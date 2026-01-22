package com.example.teachmintapp.data.repository

import com.example.teachmintapp.data.api.TriviaApiService
import com.example.teachmintapp.data.model.QuizQuestion
import com.example.teachmintapp.data.model.QuestionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface QuizRepository {
    suspend fun fetchQuestions(): Result<List<QuizQuestion>>
}

class QuizRepositoryImpl @Inject constructor(
    private val apiService: TriviaApiService
) : QuizRepository {

    override suspend fun fetchQuestions(): Result<List<QuizQuestion>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getQuestions(limit = 10)
                val quizQuestions = response.map { it.toQuizQuestion() }
                Result.success(quizQuestions)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun QuestionResponse.toQuizQuestion(): QuizQuestion {
        val allOptions = (incorrectAnswers + correctAnswer).shuffled()
        return QuizQuestion(
            id = id,
            questionText = question.text,
            options = allOptions,
            correctAnswer = correctAnswer,
            difficulty = difficulty
        )
    }
}
