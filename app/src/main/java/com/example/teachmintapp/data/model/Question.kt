package com.example.teachmintapp.data.model

import com.google.gson.annotations.SerializedName

data class QuestionResponse(
    @SerializedName("id")
    val id: String,
    @SerializedName("question")
    val question: QuestionText,
    @SerializedName("correctAnswer")
    val correctAnswer: String,
    @SerializedName("incorrectAnswers")
    val incorrectAnswers: List<String>,
    @SerializedName("difficulty")
    val difficulty: String
)

data class QuestionText(
    @SerializedName("text")
    val text: String
)

data class QuizQuestion(
    val id: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswer: String,
    val difficulty: String
)
