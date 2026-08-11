package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
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
            .background(Color(0xFF020617)) // Modern deep dark canvas
            .padding(16.dp)
    ) {
        // Header Card with bottom blue gradient accent
        EditorialCard(
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0xFF0F172A),
            borderColor = Color(0xFF334155),
            showBottomAccent = true,
            accentColor = Color(0xFF2563EB)
        ) {
            Column(
                modifier = Modifier.padding(4.dp)
            ) {
                Text(
                    text = "Learn with Pictures • تصویری ذخیرہ الفاظ",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Build English vocabulary through rich images, native audio pronunciation, and smart interactive quizzes.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        startQuizTriggered = true
                        viewModel.startMixedQuiz()
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Start Mixed Practice / ملا جلا مشق شروع کریں", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Vocabulary Categories (${categories.size} Topics)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
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
                EditorialCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            HashRouter.navigate("#/picture-vocabulary/category/${category.id}")
                        },
                    containerColor = Color(0xFF0F172A),
                    borderColor = Color(0xFF1E293B),
                    showBottomAccent = true,
                    accentColor = Color(0xFF3B82F6)
                ) {
                    Column(modifier = Modifier.padding(2.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            PictureCardThumbnail(
                                assetKey = category.id,
                                modifier = Modifier.size(44.dp)
                            )
                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "26 words",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = category.displayNameEnglish,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = category.displayNameArabic,
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}
