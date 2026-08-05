package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuizQuestion
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialMixedText
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialBorder
import com.example.ui.theme.EditorialBorderAccent
import com.example.ui.theme.EditorialBorderSoft
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialPillBg
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialSurface
import com.example.ui.theme.EditorialTextMuted
import com.example.ui.theme.EditorialTextPrimary
import com.example.ui.theme.EditorialTextSecondary
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun QuizScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val categoryFilter by viewModel.quizCategoryFilter.collectAsState()
    val currentIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedOption by viewModel.selectedQuizOption.collectAsState()
    val isSubmitted by viewModel.isQuizAnswerSubmitted.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val streak by viewModel.quizStreak.collectAsState()
    val totalAnswered by viewModel.quizTotalAnswered.collectAsState()

    val questions = viewModel.getFilteredQuestions()
    val currentQuestion = questions.getOrNull(currentIndex)

    var saveConfirmationMessage by remember { mutableStateOf<String?>(null) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Tense Category Filter Pills
        TenseFilterBar(
            selectedCategory = categoryFilter,
            onSelectCategory = { viewModel.setQuizCategoryFilter(it) }
        )

        // 2. Score Banner & Progress Bar
        ScoreProgressBanner(
            score = score,
            totalAnswered = totalAnswered,
            streak = streak,
            currentIndex = currentIndex + 1,
            totalQuestions = questions.size
        )

        if (currentQuestion != null) {
            // 3. Question Card
            QuestionCard(
                question = currentQuestion,
                currentIndex = currentIndex + 1,
                totalQuestions = questions.size,
                selectedOption = selectedOption,
                isSubmitted = isSubmitted,
                onSelectOption = { viewModel.selectQuizOption(it) }
            )

            // 4. Immediate Feedback Panel (Revealed upon selecting an option)
            AnimatedVisibility(
                visible = isSubmitted,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                exit = fadeOut()
            ) {
                FeedbackCard(
                    question = currentQuestion,
                    selectedOptionIndex = selectedOption,
                    onSaveNote = {
                        viewModel.saveQuizQuestionToNotes(currentQuestion)
                        saveConfirmationMessage = "سؤال کوئز اور گرامر موازنہ کامیابی کے ساتھ نوٹس میں محفوظ ہو گیا! 📌"
                    }
                )
            }

            // Save Confirmation Toast/Badge
            if (saveConfirmationMessage != null) {
                Surface(
                    color = EditorialAccentPurple.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = saveConfirmationMessage!!,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // 5. Navigation Controls (Next Question / Restart)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.restartQuiz() },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EditorialBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("restart_quiz_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "کوئز دوبارہ شروع کریں",
                        tint = EditorialPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "دوبارہ شروع کریں",
                        color = EditorialPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (currentIndex < questions.size - 1) {
                    Button(
                        onClick = {
                            saveConfirmationMessage = null
                            viewModel.nextQuizQuestion()
                        },
                        enabled = isSubmitted,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EditorialPrimary,
                            disabledContainerColor = EditorialPrimary.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                            .testTag("next_question_button")
                    ) {
                        Text(
                            text = "اگلا سوال",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "السؤال التالي",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    // Quiz Completed State Button
                    Button(
                        onClick = {
                            saveConfirmationMessage = null
                            viewModel.restartQuiz()
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                            .testTag("finish_quiz_button")
                    ) {
                        Text(
                            text = "ختم کریں اور نیا چکر 🏆",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // Empty / Completed State
            EditorialCard {
                Text(
                    text = "🎉 بہت خوب! آپ نے اس سیکشن کے تمام سوالات مکمل کر لیے ہیں۔",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { viewModel.restartQuiz() },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("دوبارہ شروع کریں")
                }
            }
        }
    }
}

@Composable
private fun TenseFilterBar(
    selectedCategory: String,
    onSelectCategory: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "ALL" to "تمام اسباق (All)",
        "PRESENT" to "فعل حال (Present)",
        "PAST" to "فعل ماضی (Past)",
        "FUTURE" to "فعل مستقبل (Future)"
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { (code, label) ->
            val isSelected = selectedCategory == code
            Surface(
                shape = CircleShape,
                color = if (isSelected) EditorialPrimary else EditorialSurface,
                border = BorderStroke(1.dp, if (isSelected) EditorialPrimary else EditorialBorder),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelectCategory(code) }
                    .testTag("filter_pill_$code")
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else EditorialTextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun ScoreProgressBanner(
    score: Int,
    totalAnswered: Int,
    streak: Int,
    currentIndex: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        color = EditorialContainer,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, EditorialBorderAccent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PROGRESS • التقدم",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = "سوال $currentIndex از $totalQuestions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Streak Pill
                    if (streak > 0) {
                        Surface(
                            color = Color(0xFFFFF3E0),
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFFFFB74D))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Whatshot,
                                    contentDescription = "Streak",
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$streak متتالي",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                        }
                    }

                    // Score Pill
                    Surface(
                        color = EditorialPillBg,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, EditorialBorder)
                    ) {
                        Text(
                            text = "النتيجة: $score / $totalAnswered",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Progress bar
            val progress = if (totalQuestions > 0) currentIndex.toFloat() / totalQuestions else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = EditorialAccentPurple,
                trackColor = EditorialBorderSoft
            )
        }
    }
}

@Composable
private fun QuestionCard(
    question: QuizQuestion,
    currentIndex: Int,
    totalQuestions: Int,
    selectedOption: Int?,
    isSubmitted: Boolean,
    onSelectOption: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    EditorialCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Tense Tag Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = EditorialPillBg,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${question.tenseTitleEn} • ${question.tenseTitleAr}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = EditorialAccentPurple.copy(alpha = 0.15f),
                    shape = CircleShape
                ) {
                    Text(
                        text = question.level,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            // Main Question Prompt
            Text(
                text = question.questionText,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = EditorialPrimary,
                lineHeight = 28.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Arabic Context Translation Box
            Surface(
                color = EditorialContainer,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EditorialBorderAccent),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "💡 اردو ترجمہ اور سیاق: \"${question.arabicTranslation}\"",
                    fontSize = 13.sp,
                    color = EditorialTextSecondary,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(12.dp)
                )
            }

            EditorialDivider()

            EditorialSectionTitle(text = "SELECT THE CORRECT TENSE OPTION")

            // 4 Multiple Choice Options
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.forEachIndexed { index, optionText ->
                    OptionButton(
                        optionIndex = index,
                        optionText = optionText,
                        correctOptionIndex = question.correctOptionIndex,
                        selectedOptionIndex = selectedOption,
                        isSubmitted = isSubmitted,
                        onSelect = { onSelectOption(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionButton(
    optionIndex: Int,
    optionText: String,
    correctOptionIndex: Int,
    selectedOptionIndex: Int?,
    isSubmitted: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCorrectOption = optionIndex == correctOptionIndex
    val isUserSelected = optionIndex == selectedOptionIndex

    val backgroundColor: Color
    val borderColor: Color
    val textColor: Color

    if (!isSubmitted) {
        backgroundColor = EditorialContainer
        borderColor = EditorialBorder
        textColor = EditorialTextPrimary
    } else {
        if (isCorrectOption) {
            // Highlight correct answer in green
            backgroundColor = Color(0xFFE8F5E9)
            borderColor = Color(0xFF2E7D32)
            textColor = Color(0xFF1B5E20)
        } else if (isUserSelected) {
            // User selected wrong answer - soft red
            backgroundColor = Color(0xFFFFEBEE)
            borderColor = Color(0xFFC62828)
            textColor = Color(0xFFB71C1C)
        } else {
            // Other unselected incorrect options
            backgroundColor = EditorialContainer.copy(alpha = 0.5f)
            borderColor = EditorialBorder.copy(alpha = 0.4f)
            textColor = EditorialTextMuted
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isSubmitted) { onSelect() }
            .testTag("quiz_option_$optionIndex"),
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSubmitted && (isCorrectOption || isUserSelected)) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Option Letter Badge (A, B, C, D)
                val letter = ('A' + optionIndex).toString()
                Surface(
                    color = if (isSubmitted && isCorrectOption) Color(0xFF2E7D32) else EditorialPillBg,
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = letter,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSubmitted && isCorrectOption) Color.White else EditorialPrimary
                        )
                    }
                }

                Text(
                    text = optionText,
                    fontSize = 15.sp,
                    fontWeight = if (isSubmitted && (isCorrectOption || isUserSelected)) FontWeight.Bold else FontWeight.Medium,
                    color = textColor
                )
            }

            // Outcome Icon
            if (isSubmitted) {
                if (isCorrectOption) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "درست",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(22.dp)
                    )
                } else if (isUserSelected) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "غلط",
                        tint = Color(0xFFC62828),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeedbackCard(
    question: QuizQuestion,
    selectedOptionIndex: Int?,
    onSaveNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isCorrect = selectedOptionIndex == question.correctOptionIndex

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        color = EditorialSurface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(
            2.dp,
            if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Outcome Header Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = null,
                            tint = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isCorrect) "درست جواب! بہت خوب 👏" else "جواب درست نہیں ہے ❌",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) Color(0xFF1B5E20) else Color(0xFFB71C1C)
                        )
                    }
                }

                // Save to Notes Button
                IconButton(
                    onClick = onSaveNote,
                    modifier = Modifier.testTag("save_quiz_note_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "نوٹس میں محفوظ کریں",
                        tint = EditorialAccentPurple
                    )
                }
            }

            if (!isCorrect) {
                Text(
                    text = "نمونہ درست جواب ہے: \"${question.options[question.correctOptionIndex]}\"",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )
            }

            EditorialDivider()

            // Detailed Arabic Comparative Rule Explanation
            EditorialSectionTitle(text = "TEACHER EXPLANATION")

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "English explanation:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
                Text(
                    text = question.explanationEn,
                    fontSize = 14.sp,
                    color = EditorialTextPrimary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "اردو وضاحت:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
                Text(
                    text = question.explanationUr,
                    fontSize = 15.sp,
                    color = EditorialTextPrimary,
                    lineHeight = 22.sp
                )

                question.arabicReference?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Arabic grammar reference / comparison:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted
                    )
                    Surface(
                        color = EditorialContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = it,
                            fontSize = 14.sp,
                            color = EditorialPrimary,
                            modifier = Modifier.padding(12.dp),
                            lineHeight = 20.sp
                        )
                    }
                }

                question.extraExample?.let {
                    Spacer(modifier = Modifier.height(8.dp))
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

            // Common Mistake Alert
            Surface(
                color = Color(0xFFFFF8E1),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFFE082)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "⚠️ COMMON MISTAKE • عام غلطی جس سے پرہیز ضروری ہے",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100),
                        letterSpacing = 1.0.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = question.commonMistake,
                        fontSize = 13.sp,
                        color = Color(0xFF4E342E),
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
