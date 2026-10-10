package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurriculumData
import com.example.ui.components.DailyGrammarCard
import com.example.ui.components.LessonMcqSection
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialComparisonGrid
import com.example.ui.components.EditorialDarkPracticeBox
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.components.EditorialMixedText
import com.example.ui.components.GeneratingLightingGlow
import com.example.ui.components.UserAvatar
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialBorder
import com.example.ui.theme.EditorialBorderSoft
import com.example.ui.theme.EditorialContainer
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
    val authState by viewModel.authState.collectAsState()
    val activeLesson by viewModel.activeLesson.collectAsState()
    val isGenerating by viewModel.isGeneratingLesson.collectAsState()
    val userQuizAnswer by viewModel.userQuizAnswer.collectAsState()
    val quizFeedback by viewModel.quizFeedback.collectAsState()
    val isCheckingQuiz by viewModel.isCheckingQuiz.collectAsState()
    val userNameState by viewModel.userName.collectAsState()
    val completedTopicIds by viewModel.completedTopicIds.collectAsState()

    val completedCount = completedTopicIds.size
    val totalTopics = CurriculumData.topics.size.coerceAtLeast(1)
    val progressRatio = (completedCount.toFloat() / totalTopics).coerceIn(0f, 1f)
    val progressPercentage = (progressRatio * 100).toInt()

    SelectionContainer {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Progress Bar Overview (Just beneath the lessons top header)
            item(key = "progress_overview") {
                EditorialCard(
                    modifier = Modifier.clickable { viewModel.setSelectedTab(9) },
                    containerColor = EditorialAccentPurple.copy(alpha = 0.05f),
                    borderColor = EditorialAccentPurple.copy(alpha = 0.25f),
                    showBottomAccent = true
                ) {
                    Column(
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
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = "Progress Bar",
                                    tint = EditorialAccentPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "MY PROGRESS",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    color = EditorialTextMuted
                                )
                            }
                            Text(
                                text = "$completedCount/$totalTopics Topics ($progressPercentage%)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple
                            )
                        }

                        LinearProgressIndicator(
                            progress = { progressRatio },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = EditorialAccentPurple,
                            trackColor = EditorialAccentPurple.copy(alpha = 0.15f)
                        )
                    }
                }
            }

            // Daily Grammar Card (Featured at top)
            item(key = "daily_grammar_card") {
                DailyGrammarCard(viewModel = viewModel)
            }

            // Horizontal Topics Bar
            item(key = "topics_bar") {
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
                                text = topic.titleUr,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) EditorialSurface else EditorialTextPrimary,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            if (isGenerating) {
                item(key = "generating_glow") {
                    GeneratingLightingGlow()
                }
            } else if (activeLesson != null) {
                val lesson = activeLesson!!

                // Main Editorial Lesson Card
                item(key = "lesson_content_${lesson.topicTitleEn}") {
                    val isSavedFlow = remember(lesson.topicTitleEn) { viewModel.isSaved("Lesson", lesson.topicTitleEn) }
                    val isLikedFlow = remember(lesson.topicTitleEn) { viewModel.isLiked("Lesson", lesson.topicTitleEn) }
                    val isSaved by isSavedFlow.collectAsState(initial = false)
                    val isLiked by isLikedFlow.collectAsState(initial = false)

                    EditorialCard {
                        // Topic Title row & Save button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = lesson.topicTitleUr,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary
                                )
                                Text(
                                    text = lesson.topicTitleEn,
                                    fontSize = 14.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Normal,
                                    fontStyle = FontStyle.Italic,
                                    color = EditorialAccentPurple
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val currentlySpeaking by viewModel.activeSpeakingId.collectAsState()
                                val lessonSpeechId = "lesson_${lesson.topicTitleEn}"
                                val isLessonSpeaking = currentlySpeaking == lessonSpeechId

                                IconButton(onClick = {
                                    if (isLessonSpeaking) {
                                        viewModel.stopSpeech()
                                    } else {
                                        val textToSpeak = "${lesson.topicTitleUr}. ${lesson.ruleUrdu}. ${lesson.arabicGrammarComparison}"
                                        viewModel.speak(textToSpeak, lessonSpeechId)
                                    }
                                }) {
                                    Icon(
                                        imageVector = if (isLessonSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Listen to Lesson",
                                        tint = if (isLessonSpeaking) Color.Red else EditorialAccentPurple
                                    )
                                }

                                IconButton(onClick = {
                                    val fullText = """
                                        |【 TOPIC / موضوع 】
                                        |${lesson.topicTitleEn} (${lesson.topicTitleUr})
                                        |
                                        |【 ENGLISH RULE 】
                                        |${lesson.englishRule}
                                        |
                                        |【 URDU RULE (قانون گرامر) 】
                                        |${lesson.ruleUrdu}
                                        |
                                        |【 ARABIC GRAMMAR COMPARISON (موازنہ مع العربية) 】
                                        |${lesson.arabicGrammarComparison}
                                        |
                                        |【 SIMILARITIES 】
                                        |${lesson.similarities.joinToString("\n") { "• $it" }}
                                        |
                                        |【 DIFFERENCES 】
                                        |${lesson.differences.joinToString("\n") { "• $it" }}
                                        |
                                        |【 EXAMPLES (مثالیں) 】
                                        |${lesson.examples.joinToString("\n\n") { "• English: ${it.english}\n  Urdu: ${it.urdu}\n  Note: ${it.note}" }}
                                        |
                                        |【 COMMON MISTAKE (عام غلطی) 】
                                        |${lesson.commonMistake}
                                    """.trimMargin()
                                    viewModel.toggleLike(
                                        com.example.data.model.LikedItem(
                                            id = "like_lesson_${lesson.topicTitleEn}",
                                            contentType = "Lesson",
                                            contentId = lesson.topicTitleEn,
                                            title = lesson.topicTitleEn,
                                            preview = fullText
                                        )
                                    )
                                }) {
                                    Icon(
                                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Like",
                                        tint = if (isLiked) Color.Red else EditorialTextMuted
                                    )
                                }

                                IconButton(onClick = {
                                    val fullText = """
                                        |【 TOPIC / موضوع 】
                                        |${lesson.topicTitleEn} (${lesson.topicTitleUr})
                                        |
                                        |【 ENGLISH RULE 】
                                        |${lesson.englishRule}
                                        |
                                        |【 URDU RULE (قانون گرامر) 】
                                        |${lesson.ruleUrdu}
                                        |
                                        |【 ARABIC GRAMMAR COMPARISON (موازنہ مع العربية) 】
                                        |${lesson.arabicGrammarComparison}
                                        |
                                        |【 SIMILARITIES 】
                                        |${lesson.similarities.joinToString("\n") { "• $it" }}
                                        |
                                        |【 DIFFERENCES 】
                                        |${lesson.differences.joinToString("\n") { "• $it" }}
                                        |
                                        |【 EXAMPLES (مثالیں) 】
                                        |${lesson.examples.joinToString("\n\n") { "• English: ${it.english}\n  Urdu: ${it.urdu}\n  Note: ${it.note}" }}
                                        |
                                        |【 COMMON MISTAKE (عام غلطی) 】
                                        |${lesson.commonMistake}
                                    """.trimMargin()
                                    viewModel.toggleLibrarySave(
                                        com.example.data.model.LibraryItem(
                                            id = "lesson_${lesson.topicTitleEn}",
                                            contentType = "Lesson",
                                            contentId = lesson.topicTitleEn,
                                            title = lesson.topicTitleEn,
                                            preview = fullText,
                                            payloadJson = null
                                        )
                                    )
                                }) {
                                    Icon(
                                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Save to Library",
                                        tint = if (isSaved) EditorialPrimary else EditorialTextMuted
                                    )
                                }
                            }
                        }

                        EditorialDivider()

                        // 1. Urdu Explanation & English Rule
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            EditorialSectionTitle("۱. گرامر کا قانون (The Rule)")
                            EditorialMixedText(
                                text = lesson.ruleUrdu,
                                fontSize = 15.sp,
                                lineHeight = 24.sp,
                                color = EditorialTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            EditorialMixedText(
                                text = lesson.englishRule,
                                fontSize = 13.sp,
                                lineHeight = 20.sp,
                                color = EditorialTextSecondary
                            )
                        }

                        EditorialDivider()

                        // 2. Likewise in Arabic...
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            EditorialSectionTitle("۲. مثل ذلك في العربية (عربی موازنہ)")
                            EditorialMixedText(
                                text = lesson.arabicGrammarComparison,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Serif,
                                fontStyle = FontStyle.Normal,
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
                                        text = "عام غلطی (COMMON MISTAKE)",
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
                        EditorialSectionTitle("۳. مثالیں (Bilingual Examples)")
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
                                            text = example.urdu,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialPrimary
                                        )
                                        Text(
                                            text = example.english,
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontStyle = FontStyle.Italic,
                                            color = EditorialAccentPurple
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = example.note,
                                            fontSize = 13.sp,
                                            color = EditorialTextSecondary,
                                            lineHeight = 18.sp
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

                        // 5. 10 MCQs Quiz Section
                        if (lesson.mcqs.isNotEmpty()) {
                            EditorialDivider()
                            LessonMcqSection(
                                topicId = selectedTopicId,
                                mcqs = lesson.mcqs,
                                viewModel = viewModel
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        val context = androidx.compose.ui.platform.LocalContext.current
                        androidx.compose.material3.Button(
                            onClick = {
                                viewModel.saveLessonNote(lesson)
                                android.widget.Toast.makeText(context, "Lesson summary note saved! (نوٹ محفوظ ہو گیا)", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save Summary Note (نوٹ محفوظ کریں)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

