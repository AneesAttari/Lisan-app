package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

data class RevisionCard(
    val id: Int,
    val topic: String,
    val questionEn: String,
    val questionUr: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanationUr: String,
    val priority: String,
    val scheduleTime: String
)

private val comprehensiveRevisionCards = listOf(
    RevisionCard(
        id = 1,
        topic = "Verb 'To Be'",
        questionEn = "Choose the correct verb: She ___ a brilliant English student.",
        questionUr = "صحیح فعل کا انتخاب کریں: وہ انگریزی کی ایک ذہین طالب علم ہے۔",
        options = listOf("am", "is", "are", "be"),
        correctIndex = 1,
        explanationUr = "واحد ضمیر (She) کے ساتھ فعل 'is' استعمال ہوتا ہے۔",
        priority = "High Priority",
        scheduleTime = "Today"
    ),
    RevisionCard(
        id = 2,
        topic = "Articles",
        questionEn = "Choose the correct article: He bought ___ apple from the market.",
        questionUr = "صحیح آرٹیکل منتخب کریں: اس نے بازار سے ایک سیب خریدا۔",
        options = listOf("a", "an", "the", "no article"),
        correctIndex = 1,
        explanationUr = "حرفِ علت (Vowel sound 'a') سے شروع ہونے والے اسم کے ساتھ 'an' لگتا ہے۔",
        priority = "High Priority",
        scheduleTime = "Today"
    ),
    RevisionCard(
        id = 3,
        topic = "Present Simple",
        questionEn = "Which sentence is grammatically correct for daily habits?",
        questionUr = "روزه مرہ کی عادت کے لیے کون سا جملہ گرامر کے لحاظ سے درست ہے؟",
        options = listOf("He go to school daily", "He goes to school daily", "He going to school daily", "He is go to school"),
        correctIndex = 1,
        explanationUr = "Present Simple میں He/She/It کے ساتھ فعل میں s یا es کا اضافہ ہوتا ہے۔",
        priority = "Medium Priority",
        scheduleTime = "Today"
    ),
    RevisionCard(
        id = 4,
        topic = "Past Simple",
        questionEn = "Complete: They ___ a new car last week.",
        questionUr = "مکمل کریں: انہوں نے پچھلے ہفتے ایک نئی گاڑی خریدی۔",
        options = listOf("buy", "buys", "bought", "buying"),
        correctIndex = 2,
        explanationUr = "ماضی مطلق (Past Simple) میں فعل کی دوسری فارم (bought) استعمال ہوتی ہے۔",
        priority = "Medium Priority",
        scheduleTime = "Tomorrow"
    ),
    RevisionCard(
        id = 5,
        topic = "Prepositions",
        questionEn = "Choose the correct preposition: We live ___ Lahore.",
        questionUr = "صحیح حرفِ جار منتخب کریں: ہم لاہور میں رہتے ہیں۔",
        options = listOf("on", "at", "in", "by"),
        correctIndex = 2,
        explanationUr = "بڑے شہروں اور ممالک کے ساتھ حرفِ جار 'in' استعمال ہوتا ہے۔",
        priority = "Low Priority",
        scheduleTime = "3 Days"
    ),
    RevisionCard(
        id = 6,
        topic = "Present Continuous",
        questionEn = "Complete: Look! The children ___ football in the ground.",
        questionUr = "مکمل کریں: دیکھو! بچے میدان میں فٹ بال کھیل رہے ہیں۔",
        options = listOf("play", "plays", "are playing", "played"),
        correctIndex = 2,
        explanationUr = "موجودہ جاری کام کے لیے are + verb-ing استعمال ہوتا ہے۔",
        priority = "Planned",
        scheduleTime = "4 Days"
    ),
    RevisionCard(
        id = 7,
        topic = "Possession (Has / Have)",
        questionEn = "Choose the correct verb: Ali ___ three books.",
        questionUr = "صحیح فعل منتخب کریں: علی کے پاس تین کتابیں ہیں۔",
        options = listOf("have", "has", "had", "having"),
        correctIndex = 1,
        explanationUr = "واحد اسم (Ali) کے ساتھ ملکیت ظاہر کرنے کے لیے 'has' استعمال ہوتا ہے۔",
        priority = "Planned",
        scheduleTime = "5 Days"
    ),
    RevisionCard(
        id = 8,
        topic = "Future Simple",
        questionEn = "Complete: Tomorrow, I ___ visit my grandparents.",
        questionUr = "مکمل کریں: کل میں اپنے دادا دادی سے ملنے جاؤں گا۔",
        options = listOf("will", "do", "did", "have"),
        correctIndex = 0,
        explanationUr = "آئندہ زمانے (Future Simple) کے لیے 'will' استعمال کیا جاتا ہے۔",
        priority = "Planned",
        scheduleTime = "6 Days"
    )
)

@Composable
fun RevisionPlannerScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var showRevisionDialog by remember { mutableStateOf(false) }
    var selectedTopicFilter by remember { mutableStateOf<String?>(null) }
    var targetedCardToReview by remember { mutableStateOf<RevisionCard?>(null) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var reviewedCount by remember { mutableIntStateOf(0) }
    var scheduleList by remember { mutableStateOf(comprehensiveRevisionCards) }
    var activeTab by remember { mutableIntStateOf(0) } // 0: All, 1: Due Today, 2: Mastered

    val filteredSchedule = when (activeTab) {
        1 -> scheduleList.filter { it.scheduleTime == "Today" }
        2 -> scheduleList.filter { it.priority == "Low Priority" || it.priority == "Planned" }
        else -> scheduleList
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Review Summary Header
        EditorialCard(
            containerColor = EditorialAccentPurple.copy(alpha = 0.05f),
            borderColor = EditorialAccentPurple.copy(alpha = 0.2f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "REVISION PLANNER",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.0.sp,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = "دہرائی کا منصوبہ اور نظام",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "منظم دہرائی آپ کو انگریزی گرامر پر کامل عبور دلاتی ہے۔",
                        fontSize = 13.sp,
                        color = EditorialTextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = null,
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RevisionStatBox(
                    count = "${scheduleList.count { it.scheduleTime == "Today" }}",
                    label = "Due Today",
                    color = EditorialAccentPurple,
                    modifier = Modifier.weight(1f)
                )
                RevisionStatBox(
                    count = "$reviewedCount",
                    label = "Reviewed",
                    color = EditorialPrimary,
                    modifier = Modifier.weight(1f)
                )
                RevisionStatBox(
                    count = "${45 + reviewedCount}",
                    label = "Mastered",
                    color = Color(0xFF059669),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2. Action Controls (Start Session & Refresh Schedule)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    targetedCardToReview = null
                    showRevisionDialog = true
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start Revision", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            OutlinedButton(
                onClick = {
                    scheduleList = scheduleList.shuffled()
                },
                modifier = Modifier
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, EditorialBorder),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialPrimary)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Shuffle", modifier = Modifier.size(20.dp))
            }
        }

        // 3. Filter Tabs
        ScrollableTabRow(
            selectedTabIndex = activeTab,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            divider = {}
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("All Topics (${scheduleList.size})", fontWeight = FontWeight.Medium) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Due Today (${scheduleList.count { it.scheduleTime == "Today" }})", fontWeight = FontWeight.Medium) }
            )
            Tab(
                selected = activeTab == 2,
                onClick = { activeTab = 2 },
                text = { Text("Upcoming (${scheduleList.count { it.scheduleTime != "Today" }})", fontWeight = FontWeight.Medium) }
            )
        }

        // 4. Review Schedule List (All items fully clickable)
        EditorialSectionTitle(text = "REVIEW SCHEDULE & TOPICS")

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            filteredSchedule.forEach { card ->
                ScheduleItemCard(
                    card = card,
                    onClick = {
                        targetedCardToReview = card
                        showRevisionDialog = true
                    }
                )
            }
        }

        // 5. Learning History (Clickable)
        EditorialSectionTitle(text = "LEARNING HISTORY & STATS")
        LearningHistoryCard(
            reviewedCount = reviewedCount,
            onClick = { showHistoryDialog = true }
        )
    }

    // Revision Session Dialog
    if (showRevisionDialog) {
        val sessionCards = if (targetedCardToReview != null) {
            listOf(targetedCardToReview!!)
        } else {
            scheduleList.filter { it.scheduleTime == "Today" }.ifEmpty { scheduleList }
        }

        RevisionSessionDialog(
            cards = sessionCards,
            onDismiss = {
                showRevisionDialog = false
                targetedCardToReview = null
            },
            onCompleted = { count ->
                reviewedCount += count
                showRevisionDialog = false
                targetedCardToReview = null
            }
        )
    }

    // History Details Dialog
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Learning History & Analytics", fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    IconButton(onClick = { showHistoryDialog = false }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = EditorialTextMuted)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("Total Review Sessions Completed: $reviewedCount", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialTextPrimary)
                    Text("Grammar Mastery Level: Advanced Beginner (A2)", fontSize = 13.sp, color = EditorialTextSecondary)
                    Text("Consistency Streak: 5 Days Active 🔥", fontSize = 13.sp, color = Color(0xFF059669))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Keep practicing daily to reinforce Arabic-English grammar comparative patterns.", fontSize = 12.sp, color = EditorialTextMuted)
                }
            },
            confirmButton = {
                Button(
                    onClick = { showHistoryDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                ) {
                    Text("Close")
                }
            },
            containerColor = EditorialSurface
        )
    }
}

@Composable
fun RevisionSessionDialog(
    cards: List<RevisionCard>,
    onDismiss: () -> Unit,
    onCompleted: (Int) -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }
    var isFinished by remember { mutableStateOf(false) }

    val currentCard = cards.getOrNull(currentIndex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isFinished) "Revision Complete! 🎉" else "Revision ${currentIndex + 1} of ${cards.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = EditorialTextMuted)
                }
            }
        },
        text = {
            if (isFinished || currentCard == null) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("ماشاء اللہ! آپ نے کامیابی سے دہرائی مکمل کر لی ہے۔", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary, textAlign = TextAlign.Center)
                    Text("Correct Answers: $score / ${cards.size}", fontSize = 14.sp, color = EditorialTextSecondary)
                    Button(
                        onClick = { onCompleted(cards.size) },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                    ) {
                        Text("Finish & Save Progress")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Topic Tag
                    Surface(
                        color = EditorialAccentPurple.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = currentCard.topic.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple,
                            letterSpacing = 1.2.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // English Question (Separate Paragraph)
                    Text(
                        text = currentCard.questionEn,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Urdu Question (Separate Paragraph on its own line)
                    Text(
                        text = currentCard.questionUr,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = EditorialTextSecondary,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Options
                    currentCard.options.forEachIndexed { idx, option ->
                        val isSelected = selectedOption == idx
                        val isCorrect = idx == currentCard.correctIndex
                        val btnBg = when {
                            isSubmitted && isCorrect -> Color(0xFFD1FAE5)
                            isSubmitted && isSelected && !isCorrect -> Color(0xFFFEE2E2)
                            isSelected -> EditorialPrimary.copy(alpha = 0.1f)
                            else -> EditorialSurface
                        }
                        val borderCol = when {
                            isSubmitted && isCorrect -> Color(0xFF059669)
                            isSubmitted && isSelected && !isCorrect -> Color(0xFFEF4444)
                            isSelected -> EditorialPrimary
                            else -> EditorialBorder
                        }

                        Surface(
                            onClick = {
                                if (!isSubmitted) selectedOption = idx
                            },
                            color = btnBg,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, borderCol),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = option,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = EditorialTextPrimary,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    // Explanation Block (Clean Urdu Explanation)
                    if (isSubmitted) {
                        Surface(
                            color = Color(0xFFF0F9FF),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, Color(0xFFBAE6FD)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "وضاحت (Explanation):",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1)
                                )
                                Text(
                                    text = currentCard.explanationUr,
                                    fontSize = 13.sp,
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isFinished && currentCard != null) {
                if (!isSubmitted) {
                    Button(
                        onClick = {
                            if (selectedOption != null) {
                                isSubmitted = true
                                if (selectedOption == currentCard.correctIndex) {
                                    score++
                                }
                            }
                        },
                        enabled = selectedOption != null,
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                    ) {
                        Text("Check Answer")
                    }
                } else {
                    Button(
                        onClick = {
                            if (currentIndex < cards.size - 1) {
                                currentIndex++
                                selectedOption = null
                                isSubmitted = false
                            } else {
                                isFinished = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                    ) {
                        Text(if (currentIndex < cards.size - 1) "Next Question" else "See Results")
                    }
                }
            }
        },
        containerColor = EditorialSurface
    )
}

@Composable
private fun RevisionStatBox(count: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 10.sp, color = EditorialTextMuted)
        }
    }
}

@Composable
private fun ScheduleItemCard(card: RevisionCard, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = EditorialSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, EditorialBorder)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    color = EditorialPillBg,
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = EditorialPrimary, modifier = Modifier.size(18.dp))
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = card.topic, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    Text(text = card.priority, fontSize = 11.sp, color = EditorialTextMuted)
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = if (card.scheduleTime == "Today") EditorialAccentPurple.copy(alpha = 0.1f) else EditorialBorder.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = card.scheduleTime,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (card.scheduleTime == "Today") EditorialAccentPurple else EditorialTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.NavigateNext,
                    contentDescription = "Open Revision",
                    tint = EditorialTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun LearningHistoryCard(reviewedCount: Int, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = EditorialSurface,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, EditorialBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = EditorialPrimary.copy(alpha = 0.1f),
                    shape = CircleShape,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.History, contentDescription = null, tint = EditorialPrimary, modifier = Modifier.size(20.dp))
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text("Learning History & Activity", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialTextPrimary)
                    Text("Total sessions reviewed today: $reviewedCount", fontSize = 12.sp, color = EditorialTextMuted)
                }
            }
            Icon(Icons.Default.NavigateNext, contentDescription = null, tint = EditorialTextMuted)
        }
    }
}
