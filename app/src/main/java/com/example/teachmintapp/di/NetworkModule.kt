package com.example.teachmintapp.di

import com.example.teachmintapp.data.api.ApiClient
import com.example.teachmintapp.data.api.TriviaApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideTriviaApiService(): TriviaApiService {
        return ApiClient.triviaApiService
    }
}
