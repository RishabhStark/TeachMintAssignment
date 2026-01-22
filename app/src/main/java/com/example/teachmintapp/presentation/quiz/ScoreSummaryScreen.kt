package com.example.teachmintapp.presentation.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teachmintapp.domain.model.AnswerResult
import com.example.teachmintapp.presentation.quiz.QuizContract.State

@Composable
fun ScoreSummaryScreen(
    state: State.QuizCompleted,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = (state.correctAnswers * 100) / state.totalQuestions
    val scoreColor = when {
        percentage >= 80 -> Color(0xFF4CAF50)
        percentage >= 60 -> Color(0xFFFF9800)
        else -> Color(0xFFF44336)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Quiz Completed!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Your Score",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "$percentage%",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = scoreColor
                )

                Text(
                    text = "${state.correctAnswers} out of ${state.totalQuestions} correct",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Text(
            text = "Answer Review",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )

        state.answers.forEachIndexed { index, answer ->
            AnswerReviewCard(
                questionNumber = index + 1,
                answerResult = answer,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Take Quiz Again",
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun AnswerReviewCard(
    questionNumber: Int,
    answerResult: AnswerResult,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (answerResult.isCorrect) {
        Color(0xFF4CAF50).copy(alpha = 0.1f)
    } else {
        Color(0xFFF44336).copy(alpha = 0.1f)
    }

    val borderColor = if (answerResult.isCorrect) {
        Color(0xFF4CAF50)
    } else {
        Color(0xFFF44336)
    }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question $questionNumber",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (answerResult.isCorrect) "✓ Correct" else "✗ Incorrect",
                    style = MaterialTheme.typography.titleSmall,
                    color = borderColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Your answer: ${answerResult.selectedAnswer}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!answerResult.isCorrect) {
                Text(
                    text = "Correct answer: ${answerResult.correctAnswer}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = borderColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
