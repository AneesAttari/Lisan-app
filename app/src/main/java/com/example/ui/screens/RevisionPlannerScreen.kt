package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.model.DashboardSummary
import com.example.data.model.AnswerResult
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun RevisionPlannerScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val currentSession by viewModel.currentSession.collectAsState()

    Box(modifier = modifier.fillMaxSize()) {
        if (currentSession != null) {
            RevisionSessionScreen(viewModel = viewModel)
        } else {
            RevisionPlannerDashboard(viewModel = viewModel)
        }
    }
}

@Composable
fun RevisionPlannerDashboard(
    viewModel: LisanViewModel
) {
    val dashboardSummary by viewModel.dashboardSummary.collectAsState()
    val topicProgressList by viewModel.topicProgress.collectAsState()

    // Trigger dashboard refresh on composition
    LaunchedEffect(Unit) {
        viewModel.loadRevisionDashboard()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Header Summary Card
        item {
            EditorialCard(
                containerColor = EditorialAccentPurple.copy(alpha = 0.05f),
                borderColor = EditorialAccentPurple.copy(alpha = 0.2f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "REVISION PLANNER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp,
                                color = EditorialTextMuted
                            )
                            Text(
                                text = "دہرائی کا منظم منصوبہ",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.EventNote,
                            contentDescription = null,
                            tint = EditorialAccentPurple,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "منظم دہرائی اور اسمارٹ تکرار آپ کو انگریزی گرامر پر دائمی عبور دلاتی ہے۔",
                        fontSize = 13.sp,
                        color = EditorialTextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress indicators
                    val summary = dashboardSummary ?: DashboardSummary(0, 0, 0, 0, 0, 0)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Grammar Mastery",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EditorialTextPrimary
                        )
                        Text(
                            text = "${summary.overallMastery}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { summary.overallMastery / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = EditorialAccentPurple,
                        trackColor = EditorialAccentPurple.copy(alpha = 0.1f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DashboardStatBox(
                            count = "${summary.dueCount}",
                            label = "Due Today",
                            subLabel = "دہرائی ضروری",
                            color = EditorialAccentPurple,
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatBox(
                            count = "${summary.weakCount}",
                            label = "Weak Areas",
                            subLabel = "کمزور قواعد",
                            color = Color(0xFFDC2626),
                            modifier = Modifier.weight(1f)
                        )
                        DashboardStatBox(
                            count = "${summary.masteredCount}",
                            label = "Mastered",
                            subLabel = "کامل عبور",
                            color = Color(0xFF059669),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 2. Main Revision Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.startRevisionSession("mixed", null, 10) },
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("start_mixed_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mixed Revision", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                val summary = dashboardSummary
                val dueCount = summary?.dueCount ?: 0
                Button(
                    onClick = { viewModel.startRevisionSession("today", null, 10) },
                    enabled = dueCount > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .testTag("start_due_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EditorialAccentPurple,
                        disabledContainerColor = EditorialAccentPurple.copy(alpha = 0.2f)
                    )
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = if (dueCount > 0) Color.White else Color.Gray)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Review Due ($dueCount)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }

        // 3. Topics List Header
        item {
            EditorialSectionTitle(text = "REVISION TOPICS • گرامر کے مکمل ابواب")
        }

        // 4. Topics List
        val activeTopics = listOf(
            // Present Tenses (30 Quizzes Each)
            Pair("present_simple", Pair("Present Simple Tense", "فعلِ حال سادہ • 30 Quizzes")),
            Pair("present_continuous", Pair("Present Continuous Tense", "فعلِ حال جاری • 30 Quizzes")),
            Pair("present_perfect", Pair("Present Perfect Tense", "فعلِ حال مکمل • 30 Quizzes")),
            Pair("present_perfect_continuous", Pair("Present Perfect Continuous", "فعلِ حال مکمل جاری • 30 Quizzes")),

            // Past Tenses (30 Quizzes Each)
            Pair("past_simple", Pair("Past Simple Tense", "فعلِ ماضی سادہ • 30 Quizzes")),
            Pair("past_continuous", Pair("Past Continuous Tense", "فعلِ ماضی جاری • 30 Quizzes")),
            Pair("past_perfect", Pair("Past Perfect Tense", "فعلِ ماضی مکمل • 30 Quizzes")),
            Pair("past_perfect_continuous", Pair("Past Perfect Continuous", "فعلِ ماضی مکمل جاری • 30 Quizzes")),

            // Future Tenses (30 Quizzes Each)
            Pair("future_simple", Pair("Future Simple Tense", "فعلِ مستقبل سادہ • 30 Quizzes")),
            Pair("future_continuous", Pair("Future Continuous Tense", "فعلِ مستقبل جاری • 30 Quizzes")),
            Pair("future_perfect", Pair("Future Perfect Tense", "فعلِ مستقبل مکمل • 30 Quizzes")),
            Pair("future_perfect_continuous", Pair("Future Perfect Continuous", "فعلِ مستقبل مکمل جاری • 30 Quizzes")),

            // Core Grammar Mastery Modules (20-30 Quizzes Each)
            Pair("direct_indirect_speech", Pair("Direct & Indirect Speech", "روایتِ لفظی و معنوی • 30 Quizzes")),
            Pair("articles_a_an_the", Pair("Definite & Indefinite Articles", "حروفِ تنکیر و تعریف • 30 Quizzes")),
            Pair("active_passive_voice", Pair("Active & Passive Voice", "فعل معروف و مجہول • 30 Quizzes")),
            Pair("flexible_phrasal_verbs", Pair("Flexible & Phrasal Verbs", "حسبِ ضرورت افعالِ مرکب • 30 Quizzes"))
        )

        items(activeTopics) { (topicId, names) ->
            val (titleEn, titleUr) = names
            val progress = topicProgressList.find { it.topicId == topicId }
            
            TopicRevisionCard(
                topicId = topicId,
                titleEn = titleEn,
                titleUr = titleUr,
                progress = progress,
                onStartSession = {
                    viewModel.startRevisionSession("topic", topicId, 10)
                }
            )
        }
    }
}

@Composable
fun DashboardStatBox(
    count: String,
    label: String,
    subLabel: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(color.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
            .border(1.dp, color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary
            )
            Text(
                text = subLabel,
                fontSize = 10.sp,
                color = EditorialTextSecondary
            )
        }
    }
}

@Composable
fun TopicRevisionCard(
    topicId: String,
    titleEn: String,
    titleUr: String,
    progress: TopicProgressEntity?,
    onStartSession: () -> Unit
) {
    EditorialCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = titleEn,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Text(
                        text = titleUr,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = EditorialTextSecondary
                    )
                }
                
                Box(
                    modifier = Modifier
                        .background(Color(0xFF059669).copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "100 Qs",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            val mastery = progress?.overallMastery ?: 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Topic Mastery", fontSize = 12.sp, color = EditorialTextSecondary)
                Text(text = "$mastery%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { mastery / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EditorialPrimary,
                trackColor = EditorialPrimary.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // mini stats
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MiniStat(count = "${progress?.dueCount ?: 0}", label = "Due", color = EditorialAccentPurple)
                    MiniStat(count = "${progress?.weakCount ?: 0}", label = "Weak", color = Color(0xFFDC2626))
                    MiniStat(count = "${progress?.unseenCount ?: 100}", label = "Unseen", color = Color.Gray)
                }

                Button(
                    onClick = onStartSession,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("start_topic_${topicId}"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                ) {
                    Text("Practice", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun MiniStat(count: String, label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "$count $label",
            fontSize = 11.sp,
            color = EditorialTextSecondary
        )
    }
}

@Composable
fun LockedTopicCard(titleEn: String, titleUr: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, EditorialBorder.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleEn,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
                Text(
                    text = titleUr,
                    fontSize = 13.sp,
                    color = EditorialTextMuted
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Coming Soon",
                    tint = EditorialTextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Seeding Bank",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
            }
        }
    }
}

// ====================================
// REVISION SESSION SCREEN (ACTIVE MCQ)
// ====================================

@Composable
fun RevisionSessionScreen(
    viewModel: LisanViewModel
) {
    val currentSession by viewModel.currentSession.collectAsState()
    val activeQuestions by viewModel.activeSessionQuestions.collectAsState()
    val currentIdx by viewModel.currentQuestionIndex.collectAsState()
    val selectedOptionIdx by viewModel.selectedOptionIndex.collectAsState()
    val answerResult by viewModel.answerResult.collectAsState()
    val isCompleted by viewModel.isSessionCompleted.collectAsState()

    val session = currentSession ?: return

    if (isCompleted) {
        RevisionSessionCompletionScreen(
            session = session,
            onExit = { viewModel.exitRevisionSession() }
        )
    } else if (activeQuestions.isNotEmpty() && currentIdx < activeQuestions.size) {
        val (question, _) = activeQuestions[currentIdx]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // 1. Session Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.exitRevisionSession() },
                    modifier = Modifier.testTag("exit_session_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit Session")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (session.sessionType) {
                            "topic" -> "Grammar Topic Revision"
                            "mixed" -> "Mixed Grammar Review"
                            "today" -> "Spaced Repetition Review"
                            else -> "Revision Session"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple
                    )
                    Text(
                        text = "Question ${currentIdx + 1} of ${activeQuestions.size}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentIdx + 1) / activeQuestions.size.toFloat() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EditorialAccentPurple,
                trackColor = EditorialAccentPurple.copy(alpha = 0.1f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Interactive MCQ Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    
                    // Question Card
                    EditorialCard(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(18.dp)
                        ) {
                            Text(
                                text = question.stem,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            
                            question.context?.let { ctx ->
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = ctx,
                                    fontSize = 15.sp,
                                    color = EditorialTextSecondary,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    // Options list
                    val options = question.getOptions()
                    options.forEachIndexed { index, optionText ->
                        val isSelected = selectedOptionIdx == index
                        val hasAnswered = answerResult != null
                        val isThisCorrect = answerResult?.correctOptionIndex == index
                        
                        val optionLetter = when (index) {
                            0 -> "A"
                            1 -> "B"
                            2 -> "C"
                            else -> "D"
                        }

                        val containerColor = when {
                            hasAnswered && isThisCorrect -> Color(0xFFD1FAE5) // light green
                            hasAnswered && isSelected && !isThisCorrect -> Color(0xFFFEE2E2) // light red
                            isSelected -> EditorialPillBg
                            else -> MaterialTheme.colorScheme.surface
                        }

                        val borderColor = when {
                            hasAnswered && isThisCorrect -> Color(0xFF059669)
                            hasAnswered && isSelected && !isThisCorrect -> Color(0xFFDC2626)
                            isSelected -> EditorialPrimary
                            else -> EditorialBorder
                        }

                        val icon = when {
                            hasAnswered && isThisCorrect -> Icons.Default.CheckCircle
                            hasAnswered && isSelected && !isThisCorrect -> Icons.Default.Cancel
                            else -> null
                        }

                        val iconColor = if (isThisCorrect) Color(0xFF059669) else Color(0xFFDC2626)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(containerColor)
                                .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                                .clickable(enabled = !hasAnswered) {
                                    viewModel.submitRevisionAnswer(question.id, index)
                                }
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                                .testTag("option_button_$index"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Letter bubble
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(
                                        if (isSelected) EditorialPrimary else EditorialBorder.copy(alpha = 0.5f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionLetter,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else EditorialTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optionText,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EditorialTextPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            icon?.let {
                                Icon(imageVector = it, contentDescription = null, tint = iconColor)
                            }
                        }
                    }

                    // 3. Post-Answer Feedback
                    answerResult?.let { res ->
                        FeedbackPanel(res = res)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Action Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (answerResult != null) {
                    val isLast = currentIdx == activeQuestions.size - 1
                    Button(
                        onClick = { viewModel.nextRevisionQuestion() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag(if (isLast) "complete_session_button" else "next_question_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                    ) {
                        Text(
                            text = if (isLast) "Complete Session" else "Next Question",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.NavigateNext, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun FeedbackPanel(res: AnswerResult) {
    EditorialCard(
        containerColor = if (res.isCorrect) Color(0xFFF0FDF4) else Color(0xFFFEF2F2),
        borderColor = if (res.isCorrect) Color(0xFFBBF7D0) else Color(0xFFFECACA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (res.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = null,
                    tint = if (res.isCorrect) Color(0xFF059669) else Color(0xFFDC2626)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (res.isCorrect) "صحیح جواب! (Correct)" else "غلط جواب! (Incorrect)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (res.isCorrect) Color(0xFF059669) else Color(0xFFDC2626)
                )
            }

            Text(
                text = res.explanationEnglish,
                fontSize = 14.sp,
                color = EditorialTextPrimary,
                lineHeight = 20.sp
            )

            HorizontalDivider(color = EditorialBorder.copy(alpha = 0.5f))

            Text(
                text = res.explanationUrdu,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = EditorialTextPrimary,
                lineHeight = 26.sp,
                textAlign = TextAlign.Right,
                style = TextStyle(textDirection = TextDirection.Rtl),
                modifier = Modifier.fillMaxWidth()
            )

            if (res.explanationArabic.isNotBlank()) {
                HorizontalDivider(color = EditorialBorder.copy(alpha = 0.5f))
                Text(
                    text = "موازنة مع اللغة العربية (Arabic Comparison):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple
                )
                Text(
                    text = res.explanationArabic,
                    fontSize = 13.sp,
                    color = EditorialTextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
fun RevisionSessionCompletionScreen(
    session: RevisionSessionEntity,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF059669).copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFF059669),
                    modifier = Modifier.size(48.dp)
                )
            }

            Text(
                text = "Revision Complete!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialPrimary
            )

            Text(
                text = "شاندار دہرائی! گرامر کے قوانین اب آپ کے ذہن میں مزید پختہ ہو گئے ہیں۔",
                fontSize = 15.sp,
                color = EditorialTextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            EditorialCard {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "CORRECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(text = "${session.scoreCorrect}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                    }
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(EditorialBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "INCORRECT", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(text = "${session.scoreIncorrect}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(EditorialBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        val pct = if (session.actualQuestionCount > 0) (session.scoreCorrect * 100) / session.actualQuestionCount else 0
                        Text(text = "SCORE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Text(text = "$pct%", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onExit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("exit_session_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
            ) {
                Text("Return to Planner", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}
