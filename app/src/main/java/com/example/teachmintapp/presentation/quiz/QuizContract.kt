package com.example.teachmintapp.presentation.quiz

import com.example.teachmintapp.data.model.QuizQuestion
import com.example.teachmintapp.domain.model.AnswerResult

object QuizContract {
    
    sealed class State {
        object Initial : State()
        object Loading : State()
        data class Error(val message: String) : State()
        data class QuizInProgress(
            val questions: List<QuizQuestion>,
            val currentQuestionIndex: Int,
            val remainingTime: Long,
            val answers: Map<Int, AnswerResult>,
            val isTimerRunning: Boolean,
            val isQuestionLocked: Boolean
        ) : State()
        data class QuizCompleted(
            val totalQuestions: Int,
            val correctAnswers: Int,
            val answers: List<AnswerResult>
        ) : State()
    }
    
    sealed class Intent {
        object StartQuiz : Intent()
        data class SelectAnswer(val answer: String) : Intent()
        object SubmitAnswer : Intent()
        object TimerExpired : Intent()
        object MoveToNextQuestion : Intent()
        object Retry : Intent()
        object PauseTimer : Intent()
        object ResumeTimer : Intent()
    }
}
