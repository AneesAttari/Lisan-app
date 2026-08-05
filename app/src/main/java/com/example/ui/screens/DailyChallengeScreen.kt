package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyChallengeQuestionType
import com.example.data.model.DailyChallengeState
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun DailyChallengeScreen(viewModel: LisanViewModel) {
    val questions by viewModel.dailyChallengeQuestions.collectAsState()
    val currentIndex by viewModel.currentDailyQuestionIndex.collectAsState()
    val state by viewModel.dailyChallengeState.collectAsState()
    val userAnswer by viewModel.dailyUserAnswer.collectAsState()
    val feedback by viewModel.dailyFeedback.collectAsState()
    val isCorrect by viewModel.isDailyCorrect.collectAsState()
    val streak by viewModel.dailyStreak.collectAsState()
    val results by viewModel.dailyChallengeResults.collectAsState()

    val currentQuestion = questions.getOrNull(currentIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Progress Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${currentIndex + 1} of ${questions.size}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextMuted
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Streak: $streak",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )
            }
        }

        LinearProgressIndicator(
            progress = { (currentIndex.toFloat()) / (questions.size.coerceAtLeast(1)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = EditorialAccentPurple,
            trackColor = EditorialContainer,
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )

        when (state) {
            DailyChallengeState.LOADING -> {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EditorialPrimary)
                }
            }
            DailyChallengeState.ERROR -> {
                EditorialCard {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Red)
                        Text("Failed to load today's challenge.", textAlign = TextAlign.Center)
                        Button(onClick = { viewModel.retryDailyChallenge() }, colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)) {
                            Text("Retry")
                        }
                    }
                }
            }
            DailyChallengeState.COMPLETED -> {
                DailyChallengeResultView(questions.size, results.count { it.isCorrect }, streak, onRestart = { viewModel.setSelectedTab(0) })
            }
            else -> {
                if (currentQuestion != null) {
                    QuestionView(
                        question = currentQuestion,
                        userAnswer = userAnswer,
                        onAnswerChange = { viewModel.setDailyUserAnswer(it) },
                        onSubmit = { viewModel.submitDailyAnswer() },
                        isAnswered = state == DailyChallengeState.ANSWERED,
                        isCorrect = isCorrect,
                        feedback = feedback,
                        onNext = { viewModel.nextDailyQuestion() }
                    )
                }
            }
        }
    }
}

@Composable
fun QuestionView(
    question: com.example.data.model.DailyChallengeQuestion,
    userAnswer: String,
    onAnswerChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isAnswered: Boolean,
    isCorrect: Boolean?,
    feedback: String?,
    onNext: () -> Unit
) {
    EditorialCard {
        Text(
            text = question.topic.uppercase(),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialAccentPurple,
            letterSpacing = 1.5.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = question.questionText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialPrimary,
            lineHeight = 28.sp
        )

        if (!question.urduTranslation.isNullOrBlank()) {
            Text(
                text = question.urduTranslation,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                color = EditorialTextSecondary,
                modifier = Modifier.padding(top = 8.dp),
                textAlign = TextAlign.End
            )
        }

        EditorialDivider()

        when (question.type) {
            DailyChallengeQuestionType.MCQ -> {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    question.options.forEachIndexed { index, option ->
                        val isSelected = userAnswer == index.toString()
                        val isCorrectOption = index == question.correctOptionIndex
                        
                        val borderColor = when {
                            isAnswered && isCorrectOption -> Color.Green
                            isAnswered && isSelected && !isCorrectOption -> Color.Red
                            isSelected -> EditorialPrimary
                            else -> EditorialBorder
                        }

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable(enabled = !isAnswered) { onAnswerChange(index.toString()) },
                            color = if (isSelected) EditorialContainer else EditorialSurface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.5.dp, borderColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${'A' + index}.",
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) EditorialPrimary else EditorialTextSecondary,
                                    modifier = Modifier.width(28.dp)
                                )
                                Text(
                                    text = option,
                                    fontSize = 16.sp,
                                    color = EditorialTextPrimary
                                )
                            }
                        }
                    }
                }
            }
            else -> {
                OutlinedTextField(
                    value = userAnswer,
                    onValueChange = onAnswerChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your Answer") },
                    enabled = !isAnswered,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        AnimatedVisibility(visible = isAnswered) {
            Surface(
                color = if (isCorrect == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isCorrect == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect == true) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isCorrect == true) "Correct! درست" else "Incorrect! غلط",
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect == true) Color(0xFF2E7D32) else Color(0xFFC62828)
                        )
                    }

                    HorizontalDivider(color = (if (isCorrect == true) Color(0xFF2E7D32) else Color(0xFFC62828)).copy(alpha = 0.2f))

                    Text(
                        text = "English explanation:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = question.explanationEn,
                        fontSize = 14.sp,
                        color = EditorialTextPrimary
                    )

                    Text(
                        text = "اردو وضاحت:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = question.explanationUr,
                        fontSize = 15.sp,
                        color = EditorialTextPrimary
                    )

                    question.arabicReference?.let {
                        Text(
                            text = "Arabic grammar reference / comparison:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextMuted
                        )
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = EditorialPrimary
                        )
                    }

                    question.extraExample?.let {
                        Text(
                            text = "Example:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextMuted
                        )
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            color = EditorialAccentPurple
                        )
                    }
                }
            }
        }

        Button(
            onClick = { if (isAnswered) onNext() else onSubmit() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAnswered) EditorialAccentPurple else EditorialPrimary
            ),
            enabled = userAnswer.isNotBlank()
        ) {
            Text(
                text = if (isAnswered) "Continue" else "Check Answer",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DailyChallengeResultView(total: Int, correct: Int, streak: Int, onRestart: () -> Unit) {
    EditorialCard {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_teacher_avatar),
                contentDescription = "Tutor",
                modifier = Modifier
                    .size(120.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
            )
            
            Text(
                text = "Challenge Complete!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialPrimary,
                textAlign = TextAlign.Center
            )
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(120.dp)) {
                CircularProgressIndicator(
                    progress = { correct.toFloat() / total },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 12.dp,
                    color = EditorialAccentPurple,
                    trackColor = EditorialContainer
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$correct/$total",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Text("Score", fontSize = 12.sp, color = EditorialTextMuted)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ResultStat("Streak", "$streak 🔥")
                ResultStat("Accuracy", "${(correct * 100) / total}%")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Back to Lessons")
            }
        }
    }
}

@Composable
fun ResultStat(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
        Text(text = label, fontSize = 12.sp, color = EditorialTextMuted)
    }
}
