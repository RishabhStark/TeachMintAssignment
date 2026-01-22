package com.example.teachmintapp.presentation.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.teachmintapp.data.repository.QuizRepository
import com.example.teachmintapp.domain.model.AnswerResult
import com.example.teachmintapp.presentation.quiz.QuizContract.Intent
import com.example.teachmintapp.presentation.quiz.QuizContract.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private val _state = MutableStateFlow<State>(State.Initial)
    val state: StateFlow<State> = _state.asStateFlow()

    var timerManager: TimerManager? = null
        private set
    private var selectedAnswer: String? = null
    private val answers = mutableMapOf<Int, AnswerResult>()

    init {
        timerManager = TimerManager(viewModelScope) {
            handleIntent(Intent.TimerExpired)
        }
        
        // Observe timer updates and sync with state
        viewModelScope.launch {
            timerManager?.remainingTime?.collect { remainingTime ->
                val currentState = _state.value
                if (currentState is State.QuizInProgress && !currentState.isQuestionLocked) {
                    _state.value = currentState.copy(remainingTime = remainingTime)
                }
            }
        }
    }

    fun handleIntent(intent: Intent) {
        when (intent) {
            is Intent.StartQuiz -> startQuiz()
            is Intent.SelectAnswer -> selectAnswer(intent.answer)
            is Intent.SubmitAnswer -> submitAnswer()
            is Intent.TimerExpired -> onTimerExpired()
            is Intent.MoveToNextQuestion -> moveToNextQuestion()
            is Intent.Retry -> retry()
            is Intent.PauseTimer -> pauseTimer()
            is Intent.ResumeTimer -> resumeTimer()
        }
    }

    private fun startQuiz() {
        viewModelScope.launch {
            _state.value = State.Loading
            repository.fetchQuestions().fold(
                onSuccess = { questions ->
                    if (questions.isEmpty()) {
                        _state.value = State.Error("No questions available")
                    } else {
                        answers.clear()
                        selectedAnswer = null
                        val currentState = State.QuizInProgress(
                            questions = questions,
                            currentQuestionIndex = 0,
                            remainingTime = 10_000L,
                            answers = emptyMap(),
                            isTimerRunning = true,
                            isQuestionLocked = false
                        )
                        _state.value = currentState
                        timerManager?.start(10_000L)
                    }
                },
                onFailure = { error ->
                    _state.value = State.Error(error.message ?: "Failed to load questions")
                }
            )
        }
    }

    private fun selectAnswer(answer: String) {
        val currentState = _state.value
        if (currentState is State.QuizInProgress && !currentState.isQuestionLocked) {
            selectedAnswer = answer
        }
    }

    private fun submitAnswer() {
        val currentState = _state.value
        if (currentState is State.QuizInProgress && !currentState.isQuestionLocked) {
            processAnswer()
        }
    }

    private fun onTimerExpired() {
        val currentState = _state.value
        if (currentState is State.QuizInProgress && !currentState.isQuestionLocked) {
            processAnswer()
        }
    }

    private fun processAnswer() {
        val currentState = _state.value
        if (currentState !is State.QuizInProgress) return

        timerManager?.stop()
        val question = currentState.questions[currentState.currentQuestionIndex]
        val answer = selectedAnswer ?: ""
        val isCorrect = answer == question.correctAnswer

        val answerResult = AnswerResult(
            questionId = question.id,
            selectedAnswer = answer,
            isCorrect = isCorrect,
            correctAnswer = question.correctAnswer
        )

        answers[currentState.currentQuestionIndex] = answerResult

        val updatedState = currentState.copy(
            answers = answers.toMap(),
            isTimerRunning = false,
            isQuestionLocked = true
        )
        _state.value = updatedState

        viewModelScope.launch {
            kotlinx.coroutines.delay(1500)
            moveToNextQuestion()
        }
    }

    private fun moveToNextQuestion() {
        val currentState = _state.value
        if (currentState !is State.QuizInProgress) return

        val nextIndex = currentState.currentQuestionIndex + 1
        if (nextIndex >= currentState.questions.size) {
            completeQuiz()
        } else {
            selectedAnswer = null
            val remainingTime = timerManager?.getRemainingTime() ?: 10_000L
            val updatedState = currentState.copy(
                currentQuestionIndex = nextIndex,
                remainingTime = 10_000L,
                isTimerRunning = true,
                isQuestionLocked = false
            )
            _state.value = updatedState
            timerManager?.start(10_000L)
        }
    }

    private fun completeQuiz() {
        val currentState = _state.value
        if (currentState !is State.QuizInProgress) return

        timerManager?.stop()
        val correctCount = answers.values.count { it.isCorrect }
        val completedState = State.QuizCompleted(
            totalQuestions = currentState.questions.size,
            correctAnswers = correctCount,
            answers = answers.values.toList()
        )
        _state.value = completedState
    }

    private fun retry() {
        _state.value = State.Initial
        timerManager?.stop()
        answers.clear()
        selectedAnswer = null
    }

    private fun pauseTimer() {
        val currentState = _state.value
        if (currentState is State.QuizInProgress && !currentState.isQuestionLocked) {
            timerManager?.pause()
            val remainingTime = timerManager?.getRemainingTime() ?: currentState.remainingTime
            _state.value = currentState.copy(
                remainingTime = remainingTime,
                isTimerRunning = false
            )
        }
    }

    private fun resumeTimer() {
        val currentState = _state.value
        if (currentState is State.QuizInProgress && !currentState.isQuestionLocked) {
            val remainingTime = currentState.remainingTime
            if (remainingTime > 0) {
                timerManager?.start(remainingTime)
                _state.value = currentState.copy(isTimerRunning = true)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerManager?.stop()
    }
}
