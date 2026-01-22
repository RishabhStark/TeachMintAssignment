package com.example.teachmintapp.data.api

import com.example.teachmintapp.data.model.QuestionResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TriviaApiService {
    @GET("v2/questions")
    suspend fun getQuestions(
        @Query("limit") limit: Int = 10
    ): List<QuestionResponse>
}
