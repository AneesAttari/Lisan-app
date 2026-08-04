package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurriculumData
import com.example.data.model.LessonContent
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialComparisonGrid
import com.example.ui.components.EditorialDarkPracticeBox
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.components.EditorialMixedText
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
fun LessonScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTopicId by viewModel.selectedTopicId.collectAsState()
    val activeLesson by viewModel.activeLesson.collectAsState()
    val isGenerating by viewModel.isGeneratingLesson.collectAsState()
    val error by viewModel.lessonError.collectAsState()
    val userQuizAnswer by viewModel.userQuizAnswer.collectAsState()
    val quizFeedback by viewModel.quizFeedback.collectAsState()
    val isCheckingQuiz by viewModel.isCheckingQuiz.collectAsState()

    var isSavedToastVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Horizontal Topics Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CurriculumData.topics.forEach { topic ->
                val isSelected = topic.id == selectedTopicId
                Surface(
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { viewModel.selectTopic(topic.id) },
                    color = if (isSelected) EditorialPrimary else EditorialSurface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, if (isSelected) EditorialPrimary else EditorialBorder)
                ) {
                    Text(
                        text = topic.titleEn,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) EditorialSurface else EditorialTextPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        if (isGenerating) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = EditorialAccentPurple)
                    Text(
                        text = "Gemini کے ذریعے تقابلی سبق تیار کیا جا رہا ہے...",
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        color = EditorialTextPrimary
                    )
                }
            }
        } else if (activeLesson != null) {
            val lesson = activeLesson!!

            // Main Editorial Lesson Card
            EditorialCard {
                // Topic Title row & Save button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lesson.topicTitleAr,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple
                        )
                        Text(
                            text = lesson.topicTitleEn,
                            fontSize = 22.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = EditorialPrimary
                        )
                    }
                    Surface(
                        color = EditorialContainer,
                        shape = CircleShape,
                        border = BorderStroke(1.dp, EditorialBorderAccent),
                        modifier = Modifier
                            .clip(CircleShape)
                            .clickable {
                                viewModel.saveLessonNote(lesson)
                                isSavedToastVisible = true
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = if (isSavedToastVisible) Icons.Default.CheckCircle else Icons.Default.BookmarkBorder,
                                contentDescription = "Save",
                                tint = EditorialAccentPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = if (isSavedToastVisible) "Saved" else "Save Note",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary
                            )
                        }
                    }
                }

                EditorialDivider()

                // 1. The English Rule
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    EditorialSectionTitle("1. The English Rule")
                    EditorialMixedText(
                        text = lesson.englishRule,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = EditorialTextPrimary
                    )
                }

                EditorialDivider()

                // 2. Likewise in Arabic...
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    EditorialSectionTitle("2. Likewise in Arabic...")
                    EditorialMixedText(
                        text = lesson.arabicConnection,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 24.sp,
                        color = EditorialPrimary
                    )
                }

                // Similarities & Differences Editorial Grid
                EditorialComparisonGrid(
                    similarities = lesson.similarities,
                    differences = lesson.differences
                )

                // Common Mistake Alert Banner
                Surface(
                    color = EditorialContainer,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EditorialBorderSoft)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Caution",
                            tint = EditorialAccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "COMMON MISTAKE • عام غلطی",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.0.sp,
                                color = EditorialTextMuted
                            )
                            EditorialMixedText(
                                text = lesson.commonMistake,
                                fontSize = 13.sp,
                                color = EditorialTextPrimary,
                                lineHeight = 19.sp
                            )
                        }
                    }
                }

                EditorialDivider()

                // Examples Section
                EditorialSectionTitle("3. Bilingual Examples • تقابلی مثالیں")
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    lesson.examples.forEach { example ->
                        Surface(
                            color = EditorialSurface,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, EditorialBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = example.englishSentence,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary
                                )
                                Text(
                                    text = example.arabicTranslation,
                                    fontSize = 15.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = EditorialAccentPurple,
                                    style = TextStyle(textDirection = TextDirection.ContentOrRtl)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = example.note,
                                    fontSize = 12.sp,
                                    color = EditorialTextSecondary,
                                    lineHeight = 17.sp,
                                    style = TextStyle(textDirection = TextDirection.ContentOrRtl)
                                )
                            }
                        }
                    }
                }

                // 4. Practice Box
                EditorialDarkPracticeBox(
                    question = lesson.practiceQuestion,
                    userText = userQuizAnswer,
                    onTextChange = { viewModel.setUserQuizAnswer(it) },
                    onSubmit = { viewModel.checkPracticeAnswer() },
                    isChecking = isCheckingQuiz,
                    feedback = quizFeedback
                )
            }
        }
    }
}
