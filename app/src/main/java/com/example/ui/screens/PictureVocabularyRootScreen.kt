package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.navigation.HashRouter
import com.example.ui.viewmodel.PictureVocabularyViewModel

@Composable
fun PictureVocabularyRootScreen(viewModel: PictureVocabularyViewModel) {
    val currentRoute by HashRouter.currentRoute.collectAsState()

    when {
        currentRoute.startsWith("#/picture-vocabulary/category/") -> {
            val categoryId = currentRoute.removePrefix("#/picture-vocabulary/category/")
            CategoryOverviewScreen(categoryId = categoryId, viewModel = viewModel)
        }
        currentRoute.startsWith("#/picture-vocabulary/learn/") -> {
            // format: #/picture-vocabulary/learn/{categoryId}/{itemId}
            val parts = currentRoute.removePrefix("#/picture-vocabulary/learn/").split("/")
            if (parts.size >= 2) {
                LearnWordScreen(categoryId = parts[0], itemId = parts[1], viewModel = viewModel)
            } else {
                PictureVocabularyHomeScreen(viewModel = viewModel)
            }
        }
        currentRoute.startsWith("#/picture-vocabulary/quiz/") -> {
            val sessionId = currentRoute.removePrefix("#/picture-vocabulary/quiz/")
            PictureQuizSessionScreen(sessionId = sessionId, viewModel = viewModel)
        }
        currentRoute.startsWith("#/picture-vocabulary/completion/") -> {
            val sessionId = currentRoute.removePrefix("#/picture-vocabulary/completion/")
            PictureQuizCompletionScreen(sessionId = sessionId, viewModel = viewModel)
        }
        else -> {
            PictureVocabularyHomeScreen(viewModel = viewModel)
        }
    }
}
