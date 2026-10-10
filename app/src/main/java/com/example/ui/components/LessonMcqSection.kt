package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LessonMcq
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialBorder
import com.example.ui.theme.EditorialBorderSoft
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialPillBg
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialTheme
import com.example.ui.theme.LightBg
import com.example.ui.theme.EditorialSurface
import com.example.ui.theme.EditorialTextMuted
import com.example.ui.theme.EditorialTextPrimary
import com.example.ui.theme.EditorialTextSecondary
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun LessonMcqSection(
    topicId: String,
    mcqs: List<LessonMcq>,
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    if (mcqs.isEmpty()) return

    val selectedOptions by viewModel.lessonQuizSelectedOptions.collectAsState()
    val submittedMap by viewModel.lessonQuizSubmitted.collectAsState()
    val score by viewModel.lessonQuizScore.collectAsState()

    val totalCount = mcqs.size
    val answeredCount = submittedMap.size
    val isComplete = answeredCount >= totalCount

    var currentQuestionIndex by remember(topicId) { mutableIntStateOf(0) }
    var showCompletionCard by remember(topicId) { mutableStateOf(false) }

    // If all questions are completed and user re-visits or finishes
    LaunchedEffect(topicId, isComplete) {
        if (isComplete && answeredCount > 0) {
            // Keep completion card available when full quiz is done
            if (currentQuestionIndex >= totalCount - 1 && submittedMap[totalCount - 1] == true) {
                showCompletionCard = true
            }
        }
    }

    val progressRatio = ((currentQuestionIndex + (if (submittedMap[currentQuestionIndex] == true) 1 else 0)).toFloat() / totalCount.coerceAtLeast(1)).coerceIn(0f, 1f)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        EditorialSectionTitle("۵. ۱۰ امتحانی سوالات مع تفصیلی وضاحت (Lesson Mastery Quiz)")

        Text(
            text = "ہر سبق کے تمام اہم اصولوں کی جانچ کے لیے درج ذیل ۱۰ سوالات حل کریں۔ جواب منتخب کرنے پر فوری تفصیلی گرامر وضاحت پیش کی جائے گی۔",
            fontSize = 13.sp,
            color = EditorialTextSecondary,
            lineHeight = 20.sp
        )

        // Progress & Score Header Card
        Surface(
            color = EditorialContainer,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, EditorialBorderSoft)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = EditorialAccentPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Question ${currentQuestionIndex + 1} of $totalCount",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextPrimary
                        )
                    }

                    Surface(
                        color = if (isComplete) Color(0xFF2E7D32).copy(alpha = 0.15f) else EditorialPillBg,
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isComplete) Color(0xFF2E7D32).copy(alpha = 0.3f) else EditorialBorderSoft
                        )
                    ) {
                        Text(
                            text = "Score: $score / $totalCount (${((score.toFloat() / totalCount.coerceAtLeast(1)) * 100).toInt()}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isComplete) Color(0xFF2E7D32) else EditorialAccentPurple,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isComplete) Color(0xFF2E7D32) else EditorialAccentPurple,
                    trackColor = EditorialBorderSoft
                )
            }
        }

        // Completion View (when finish quiz is tapped or all answered)
        if (showCompletionCard) {
            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF81C784)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stars,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(32.dp)
                        )
                        Column {
                            Text(
                                text = "ماشاءاللہ! تمام ۱۰ سوالات مکمل",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                            Text(
                                text = "سبق کی تکمیل اور اسکور ($score / $totalCount) محفوظ ہو چکا ہے۔",
                                fontSize = 12.sp,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            viewModel.resetLessonQuiz(topicId)
                            currentQuestionIndex = 0
                            showCompletionCard = false
                        },
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF2E7D32)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("retake_lesson_quiz_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "دوبارہ حل کریں (Retake Quiz)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        } else {
            // Show ONE MCQ at a time
            val safeIndex = currentQuestionIndex.coerceIn(0, totalCount - 1)
            val mcq = mcqs[safeIndex]
            val isSubmitted = submittedMap[safeIndex] == true
            val userSelection = selectedOptions[safeIndex]
            val isCorrect = userSelection == mcq.correctOptionIndex

            Surface(
                color = EditorialSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    if (isSubmitted) {
                        if (isCorrect) Color(0xFF81C784) else Color(0xFFE57373)
                    } else {
                        EditorialBorder
                    }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("lesson_mcq_card_$safeIndex")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Header: Question Counter & Result Tag
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isSubmitted) {
                                if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            } else {
                                EditorialPillBg
                            },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Question ${safeIndex + 1} of $totalCount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSubmitted) {
                                    if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                } else {
                                    EditorialAccentPurple
                                },
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (isSubmitted) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                    contentDescription = null,
                                    tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = if (isCorrect) "✓ Correct" else "✗ Wrong",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                            }
                        }
                    }

                    // Urdu Question Prompt
                    if (mcq.questionUr.isNotBlank()) {
                        Text(
                            text = mcq.questionUr,
                            fontSize = 13.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 19.sp
                        )
                    }

                    // English Question
                    Text(
                        text = mcq.question,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EditorialPrimary,
                        lineHeight = 22.sp
                    )

                    // 4 Options
                    val isDark = isSystemInDarkTheme() || EditorialTheme.colors.background != LightBg

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        mcq.options.forEachIndexed { optIndex, optionText ->
                            val isOptionSelected = userSelection == optIndex
                            val isThisCorrect = optIndex == mcq.correctOptionIndex

                            val (optionBg, optionBorder, optionTextColor) = when {
                                isSubmitted && isThisCorrect -> Triple(
                                    if (isDark) Color(0xFF132A1C) else Color(0xFFE8F5E9),
                                    if (isDark) Color(0xFF22C55E) else Color(0xFF4CAF50),
                                    if (isDark) Color(0xFF86EFAC) else Color(0xFF1B5E20)
                                )
                                isSubmitted && isOptionSelected && !isCorrect -> Triple(
                                    if (isDark) Color(0xFF2D1418) else Color(0xFFFFEBEE),
                                    if (isDark) Color(0xFFEF4444) else Color(0xFFE53935),
                                    if (isDark) Color(0xFFFCA5A5) else Color(0xFFB71C1C)
                                )
                                !isSubmitted && isOptionSelected -> Triple(
                                    EditorialContainer,
                                    EditorialAccentPurple,
                                    EditorialPrimary
                                )
                                else -> Triple(
                                    EditorialSurface,
                                    EditorialBorderSoft,
                                    EditorialTextPrimary
                                )
                            }

                            Surface(
                                color = optionBg,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, optionBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable(enabled = !isSubmitted) {
                                        viewModel.answerLessonQuizOption(
                                            topicId = topicId,
                                            questionIndex = safeIndex,
                                            selectedOption = optIndex,
                                            correctOption = mcq.correctOptionIndex,
                                            totalQuestions = totalCount
                                        )
                                    }
                                    .testTag("mcq_${safeIndex}_option_$optIndex")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSubmitted && isThisCorrect) Color(0xFF4CAF50)
                                                else if (isSubmitted && isOptionSelected && !isCorrect) Color(0xFFE53935)
                                                else EditorialContainer
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = ('A' + optIndex).toString(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSubmitted && (isThisCorrect || (isOptionSelected && !isCorrect))) Color.White else EditorialTextSecondary
                                        )
                                    }

                                    Text(
                                        text = optionText,
                                        fontSize = 14.sp,
                                        fontWeight = if (isOptionSelected || (isSubmitted && isThisCorrect)) FontWeight.Bold else FontWeight.Normal,
                                        color = optionTextColor,
                                        modifier = Modifier.weight(1f)
                                    )

                                    if (isSubmitted && isThisCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = "Correct",
                                            tint = if (isDark) Color(0xFF4ADE80) else Color(0xFF2E7D32),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    } else if (isSubmitted && isOptionSelected && !isCorrect) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = "Incorrect",
                                            tint = if (isDark) Color(0xFFF87171) else Color(0xFFC62828),
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Feedback details & High-contrast Explanation (Revealed when submitted)
                    if (isSubmitted) {
                        // If Incorrect: Explicitly show user's selected answer and correct answer
                        if (!isCorrect) {
                            Surface(
                                color = if (isDark) Color(0xFF251417) else Color(0xFFFFEBEE),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, if (isDark) Color(0xFF7F1D1D) else Color(0xFFFFCDD2)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Cancel,
                                            contentDescription = null,
                                            tint = if (isDark) Color(0xFFF87171) else Color(0xFFC62828),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Your answer: ${if (userSelection != null && userSelection in mcq.options.indices) mcq.options[userSelection] else "None"}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isDark) Color(0xFFFCA5A5) else Color(0xFFC62828)
                                        )
                                    }
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = if (isDark) Color(0xFF4ADE80) else Color(0xFF2E7D32),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "Correct answer: ${mcq.options.getOrElse(mcq.correctOptionIndex) { "" }}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFF86EFAC) else Color(0xFF2E7D32)
                                        )
                                    }
                                }
                            }
                        }

                        // Specific High-Contrast Explanation Box
                        val explanationCardBg = when {
                            isDark && isCorrect -> Color(0xFF132A1C)
                            isDark && !isCorrect -> Color(0xFF261D12)
                            !isDark && isCorrect -> Color(0xFFF0FDF4)
                            else -> Color(0xFFFFFBEB)
                        }
                        val explanationBorderColor = when {
                            isDark && isCorrect -> Color(0xFF22C55E).copy(alpha = 0.5f)
                            isDark && !isCorrect -> Color(0xFFF59E0B).copy(alpha = 0.5f)
                            !isDark && isCorrect -> Color(0xFF86EFAC)
                            else -> Color(0xFFFDE68A)
                        }
                        val explanationHeaderColor = when {
                            isDark && isCorrect -> Color(0xFF4ADE80)
                            isDark && !isCorrect -> Color(0xFFFBBF24)
                            !isDark && isCorrect -> Color(0xFF15803D)
                            else -> Color(0xFFB45309)
                        }
                        val explanationUrduColor = if (isDark) Color(0xFFF8FAFC) else Color(0xFF0F172A)
                        val explanationEnColor = if (isDark) Color(0xFFCBD5E1) else Color(0xFF334155)

                        Surface(
                            color = explanationCardBg,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, explanationBorderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = null,
                                        tint = explanationHeaderColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "تفصیلی گرامر وضاحت • Explanation",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = explanationHeaderColor
                                    )
                                }

                                val explanationUrText = mcq.explanationUr.ifBlank {
                                    "اس سوال کا درست جواب '${mcq.options.getOrElse(mcq.correctOptionIndex) { "" }}' ہے جو کہ بنیادی انگریزی گرامر کے طے شدہ اصولوں کے عین مطابق ہے۔"
                                }
                                Text(
                                    text = explanationUrText,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = explanationUrduColor,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Right,
                                    style = TextStyle(textDirection = TextDirection.Rtl),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                val explanationEnText = mcq.explanationEn.ifBlank {
                                    "The correct answer is '${mcq.options.getOrElse(mcq.correctOptionIndex) { "" }}' based on standard English grammatical structure."
                                }
                                if (explanationEnText.isNotBlank()) {
                                    HorizontalDivider(color = explanationBorderColor.copy(alpha = 0.35f))
                                    Text(
                                        text = explanationEnText,
                                        fontSize = 13.sp,
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic,
                                        color = explanationEnColor,
                                        lineHeight = 19.sp
                                    )
                                }
                            }
                        }

                        // Next Question / Finish Quiz Button
                        if (safeIndex < totalCount - 1) {
                            Button(
                                onClick = {
                                    currentQuestionIndex = safeIndex + 1
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EditorialAccentPurple
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                                    .testTag("next_mcq_button")
                            ) {
                                Text(
                                    text = "Next Question",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = {
                                    showCompletionCard = true
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2E7D32)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp)
                                    .testTag("finish_mcq_button")
                            ) {
                                Text(
                                    text = "Finish Quiz",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
