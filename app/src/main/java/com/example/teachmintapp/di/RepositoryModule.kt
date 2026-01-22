package com.example.teachmintapp.di

import com.example.teachmintapp.data.api.TriviaApiService
import com.example.teachmintapp.data.repository.QuizRepository
import com.example.teachmintapp.data.repository.QuizRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideQuizRepository(
        apiService: TriviaApiService
    ): QuizRepository {
        return QuizRepositoryImpl(apiService)
    }
}
