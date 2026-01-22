package com.example.teachmintapp.presentation.quiz

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.teachmintapp.presentation.quiz.QuizContract.Intent
import com.example.teachmintapp.presentation.quiz.QuizContract.State

@Composable
fun QuizScreenContainer(
    viewModel: QuizViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val currentState = state) {
        is State.Initial -> {
            StartQuizScreen(
                onStartQuiz = { viewModel.handleIntent(Intent.StartQuiz) },
                modifier = modifier
            )
        }

        is State.Loading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Loading questions...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }

        is State.Error -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = currentState.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    androidx.compose.material3.Button(
                        onClick = { viewModel.handleIntent(Intent.Retry) }
                    ) {
                        Text("Retry")
                    }
                }
            }
        }

        is State.QuizInProgress -> {
            QuizScreen(
                state = currentState,
                onIntent = viewModel::handleIntent,
                modifier = modifier
            )
        }

        is State.QuizCompleted -> {
            ScoreSummaryScreen(
                state = currentState,
                onRetry = { viewModel.handleIntent(Intent.Retry) },
                modifier = modifier
            )
        }
    }
}
