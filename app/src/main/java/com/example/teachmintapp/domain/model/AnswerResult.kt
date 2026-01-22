package com.example.teachmintapp.domain.model

data class AnswerResult(
    val questionId: String,
    val selectedAnswer: String,
    val isCorrect: Boolean,
    val correctAnswer: String
)
