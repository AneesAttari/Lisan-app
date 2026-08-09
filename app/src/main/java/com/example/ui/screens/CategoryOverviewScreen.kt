package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PictureVocabularyCategoryEntity
import com.example.data.model.PictureVocabularyItemEntity
import com.example.ui.components.PictureCardThumbnail
import com.example.ui.navigation.HashRouter
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialTheme
import com.example.ui.viewmodel.PictureVocabularyViewModel

@Composable
fun CategoryOverviewScreen(categoryId: String, viewModel: PictureVocabularyViewModel) {
    val categories by viewModel.categories.collectAsState()
    val category = categories.find { it.id == categoryId }
    val colors = EditorialTheme.colors
    val sessionId by viewModel.currentSessionId.collectAsState()
    var startQuizTriggered by remember { mutableStateOf(false) }

    var items by remember { mutableStateOf<List<PictureVocabularyItemEntity>>(emptyList()) }
    
    LaunchedEffect(Unit) {
        viewModel.clearCurrentSession()
    }

    LaunchedEffect(categoryId) {
        items = viewModel.getItemsListForCategory(categoryId)
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { HashRouter.navigate("#/picture-vocabulary") }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = colors.textPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = category?.displayNameEnglish ?: "Category",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )
                Text(
                    text = category?.displayNameArabic ?: "",
                    fontSize = 14.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                startQuizTriggered = true
                viewModel.startCategoryQuiz(categoryId)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Start 26-Question Picture Quiz", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Vocabulary Words (26)",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            HashRouter.navigate("#/picture-vocabulary/learn/$categoryId/${item.id}")
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = colors.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            PictureCardThumbnail(
                                assetKey = item.imageAssetKey,
                                imageUrl = item.getEffectiveImageUrl(),
                                word = item.wordEnglish,
                                alt = item.getEffectiveAlt(),
                                modifier = Modifier.size(48.dp)
                            )
                            Column {
                                Text(
                                    text = item.wordEnglish,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.wordArabic,
                                    fontSize = 14.sp,
                                    color = colors.textSecondary
                                )
                            }
                        }
                        IconButton(onClick = { viewModel.speakWord(item.wordEnglish) }) {
                            Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = EditorialPrimary)
                        }
                    }
                }
            }
        }
    }
}
