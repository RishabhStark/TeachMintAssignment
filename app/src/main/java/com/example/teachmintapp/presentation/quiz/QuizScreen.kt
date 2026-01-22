package com.example.teachmintapp.presentation.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teachmintapp.presentation.quiz.QuizContract.Intent
import com.example.teachmintapp.presentation.quiz.QuizContract.State

@Composable
fun QuizScreen(
    state: State.QuizInProgress,
    onIntent: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val question = state.questions[state.currentQuestionIndex]
    val answerResult = state.answers[state.currentQuestionIndex]
    var selectedAnswer by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(state.currentQuestionIndex) {
        selectedAnswer = null
    }

    LaunchedEffect(state.remainingTime) {
        // Update UI when timer changes
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${state.currentQuestionIndex + 1} of ${state.questions.size}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${((state.currentQuestionIndex + 1) * 100 / state.questions.size)}%",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }

        LinearProgressIndicator(
            progress = { (state.currentQuestionIndex + 1).toFloat() / state.questions.size },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )

        // Timer
        TimerDisplay(
            remainingTime = state.remainingTime,
            isRunning = state.isTimerRunning,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Question
        Text(
            text = question.questionText,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Options
        question.options.forEach { option ->
            val isSelected = selectedAnswer == option || answerResult?.selectedAnswer == option
            val isCorrect = option == question.correctAnswer
            val isIncorrect = answerResult != null && option == answerResult.selectedAnswer && !answerResult.isCorrect

            val backgroundColor = when {
                state.isQuestionLocked && isCorrect -> Color(0xFF4CAF50).copy(alpha = 0.3f)
                state.isQuestionLocked && isIncorrect -> Color(0xFFF44336).copy(alpha = 0.3f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            val borderColor = when {
                state.isQuestionLocked && isCorrect -> Color(0xFF4CAF50)
                state.isQuestionLocked && isIncorrect -> Color(0xFFF44336)
                isSelected -> MaterialTheme.colorScheme.primary
                else -> Color.Transparent
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(backgroundColor)
                    .border(
                        width = 2.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(
                        enabled = !state.isQuestionLocked,
                        onClick = {
                            selectedAnswer = option
                            onIntent(Intent.SelectAnswer(option))
                        }
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = option,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Feedback
        if (state.isQuestionLocked && answerResult != null) {
            val feedbackText = if (answerResult.isCorrect) {
                "Correct! ✓"
            } else {
                "Incorrect. The correct answer is: ${answerResult.correctAnswer}"
            }
            val feedbackColor = if (answerResult.isCorrect) {
                Color(0xFF4CAF50)
            } else {
                Color(0xFFF44336)
            }

            Text(
                text = feedbackText,
                style = MaterialTheme.typography.titleMedium,
                color = feedbackColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                textAlign = TextAlign.Center
            )
        }

        // Submit button
        if (!state.isQuestionLocked) {
            Button(
                onClick = { onIntent(Intent.SubmitAnswer) },
                enabled = selectedAnswer != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = "Submit Answer",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun TimerDisplay(
    remainingTime: Long,
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val seconds = (remainingTime / 1000).coerceAtLeast(0)
    val progress = (remainingTime / 10_000f).coerceIn(0f, 1f)

    val color = when {
        progress < 0.3f -> Color(0xFFF44336)
        progress < 0.6f -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Time Remaining",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "${seconds}s",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color
        )
    }
}
