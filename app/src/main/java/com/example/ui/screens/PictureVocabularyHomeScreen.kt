package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import com.example.ui.components.PictureCardThumbnail
import com.example.ui.navigation.HashRouter
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialTheme
import com.example.ui.viewmodel.PictureVocabularyViewModel

@Composable
fun PictureVocabularyHomeScreen(viewModel: PictureVocabularyViewModel) {
    val categories by viewModel.categories.collectAsState()
    val sessionId by viewModel.currentSessionId.collectAsState()
    val colors = EditorialTheme.colors
    var startQuizTriggered by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.clearCurrentSession()
    }

    LaunchedEffect(sessionId, startQuizTriggered) {
        if (startQuizTriggered && sessionId != null) {
            HashRouter.navigate("#/picture-vocabulary/quiz/$sessionId")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = EditorialPrimary,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Text(
                    text = "Learn with Pictures",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Build English vocabulary through pictures, audio pronunciation, and interactive quizzes.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        startQuizTriggered = true
                        viewModel.startMixedQuiz()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = EditorialPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Start Mixed Practice", color = EditorialPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Vocabulary Categories (${categories.size} Topics)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                Card(
                    modifier = CardDefaults.cardElevation().let {
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                HashRouter.navigate("#/picture-vocabulary/category/${category.id}")
                            }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PictureCardThumbnail(
                                assetKey = category.id,
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = "26 words",
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = category.displayNameEnglish,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = category.displayNameArabic,
                            fontSize = 13.sp,
                            color = colors.textSecondary
                        )
                    }
                }
            }
        }
    }
}
