package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
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
import com.example.data.model.PictureVocabularyItemEntity
import com.example.ui.components.PictureCardThumbnail
import com.example.ui.navigation.HashRouter
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialTheme
import com.example.ui.viewmodel.PictureVocabularyViewModel

@Composable
fun PictureQuizSessionScreen(sessionId: String, viewModel: PictureVocabularyViewModel) {
    val colors = EditorialTheme.colors
    val session by viewModel.currentSession.collectAsState()
    val questions by viewModel.sessionQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOptionId by viewModel.selectedOptionId.collectAsState()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val isCorrect by viewModel.isCorrectAnswer.collectAsState()

    var options by remember { mutableStateOf<List<PictureVocabularyItemEntity>>(emptyList()) }
    var targetItem by remember { mutableStateOf<PictureVocabularyItemEntity?>(null) }

    LaunchedEffect(questions, currentIndex) {
        if (questions.isNotEmpty() && currentIndex in questions.indices) {
            val (_, q) = questions[currentIndex]
            val items = viewModel.getItemsForQuestion(q)
            options = viewModel.ensureUniqueOptionImages(items, q)
            targetItem = viewModel.getItem(q.targetItemId)
        }
    }

    // Colors matching Duolingo aesthetic
    val duoGreen = Color(0xFF58CC02)
    val duoBlue = Color(0xFF1CB0F6)
    val duoRed = Color(0xFFFF4B4B)
    val duoLightBlue = Color(0xFFDDF4FF)
    val duoLightGreen = Color(0xFFD7FFB8)
    val duoLightRed = Color(0xFFFFDFE0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(16.dp)
    ) {
        // Top Bar: Close button, Progress bar, Hearts/Streak pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                val catId = session?.categoryId
                if (catId != null) {
                    HashRouter.navigate("#/picture-vocabulary/category/$catId")
                } else {
                    HashRouter.navigate("#/picture-vocabulary")
                }
            }) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFFAFAFAF))
            }

            // Progress Bar
            val progress = if (questions.isNotEmpty()) (currentIndex + 1).toFloat() / questions.size else 0f
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE5E5E5))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .clip(RoundedCornerShape(6.dp))
                        .background(duoGreen)
                )
            }

            // Energy / Streak Pill
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFFFF0F6),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "⚡", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "25",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFFF2D55)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Question Title
        Text(
            text = "Select the correct image",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF3C3C3C)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Target Word Bar with Audio Button
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E5E5))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { targetItem?.let { viewModel.speakWord(it.wordEnglish) } },
                    modifier = Modifier
                        .size(48.dp)
                        .background(duoBlue, CircleShape)
                ) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = targetItem?.wordEnglish ?: "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3C3C3C)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2x2 Options Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(options) { option ->
                val isSelected = selectedOptionId == option.id
                val isTarget = option.id == targetItem?.id

                val cardBorderColor = when {
                    isSubmitted && isTarget -> duoGreen
                    isSubmitted && isSelected && !isTarget -> duoRed
                    isSelected -> duoBlue
                    else -> Color(0xFFE5E5E5)
                }

                val cardBgColor = when {
                    isSubmitted && isTarget -> duoLightGreen
                    isSubmitted && isSelected && !isTarget -> duoLightRed
                    isSelected -> duoLightBlue
                    else -> Color.White
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .border(
                            width = if (isSelected || isSubmitted) 3.dp else 2.dp,
                            color = cardBorderColor,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable(enabled = !isSubmitted) {
                            viewModel.selectOption(option.id)
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = cardBgColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        PictureCardThumbnail(
                            assetKey = option.imageAssetKey,
                            imageUrl = option.getEffectiveImageUrl(),
                            word = option.wordEnglish,
                            alt = option.getEffectiveAlt(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = option.wordArabic,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected && !isSubmitted) duoBlue else Color(0xFF4B4B4B)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bottom Action Bar
        if (!isSubmitted) {
            Button(
                onClick = { viewModel.submitAnswer() },
                enabled = selectedOptionId != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = duoGreen,
                    disabledContainerColor = Color(0xFFE5E5E5)
                )
            ) {
                Text(
                    text = "CHECK",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (selectedOptionId != null) Color.White else Color(0xFFAFAFAF)
                )
            }
        } else {
            // Feedback Footer Container
            val footerBg = if (isCorrect) duoLightGreen else duoLightRed
            val footerTextColor = if (isCorrect) duoGreen else duoRed
            val buttonColor = if (isCorrect) duoGreen else duoRed

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = footerBg
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.Check else Icons.Default.Close,
                            contentDescription = null,
                            tint = footerTextColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCorrect) "Awesome!" else "Correct answer: ${targetItem?.wordEnglish}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = footerTextColor
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (currentIndex < questions.size - 1) {
                                viewModel.nextQuestion()
                            } else {
                                HashRouter.navigate("#/picture-vocabulary/completion/${session?.sessionId ?: ""}")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = buttonColor)
                    ) {
                        Text(
                            text = if (currentIndex < questions.size - 1) "CONTINUE" else "VIEW RESULTS",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

