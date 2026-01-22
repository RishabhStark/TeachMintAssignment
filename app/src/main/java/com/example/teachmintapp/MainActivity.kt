package com.example.teachmintapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.teachmintapp.presentation.quiz.QuizContract.Intent
import com.example.teachmintapp.presentation.quiz.QuizScreenContainer
import com.example.teachmintapp.presentation.quiz.QuizViewModel
import com.example.teachmintapp.ui.theme.TeachMintAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var viewModel: QuizViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TeachMintAppTheme {
                QuizAppContent(
                    onViewModelCreated = { viewModel = it }
                )
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            viewModel?.handleIntent(Intent.PauseTimer)
        }
    }

    override fun onStart() {
        super.onStart()
        if (!isChangingConfigurations) {
            viewModel?.handleIntent(Intent.ResumeTimer)
        }
    }
}

@Composable
fun QuizAppContent(
    onViewModelCreated: (QuizViewModel) -> Unit = {}
) {
    val viewModel: QuizViewModel = hiltViewModel()

    LaunchedEffect(viewModel) {
        onViewModelCreated(viewModel)
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        QuizScreenContainer(
            viewModel = viewModel,
            modifier = Modifier.fillMaxSize()
        )
    }
}