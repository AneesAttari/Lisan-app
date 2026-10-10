package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuiz365Generator
import com.example.data.model.DayProgressInfo
import com.example.data.model.QuizQuestion
import com.example.data.model.WeeklyQuizProgression
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
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
import com.example.ui.theme.EditorialTheme
import com.example.ui.theme.LightBg
import com.example.ui.viewmodel.LisanViewModel

/**
 * Quizzes Main Architecture:
 * 365 DAYS
 * → divided into 7-day weeks (Week 1 to 53)
 * → each week contains 7 DAYS (Mon..Sun)
 * → each DAY contains 10 QUIZZES
 *
 * Main Quizzes Screen: Focuses ONLY on the 7-day weekly journey.
 * Day Quiz Screen: Dedicated view displaying and running the 10 quizzes for a selected day.
 */
@Composable
fun QuizScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val currentIndex by viewModel.currentQuizIndex.collectAsState()
    val selectedOption by viewModel.selectedQuizOption.collectAsState()
    val isSubmitted by viewModel.isQuizAnswerSubmitted.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val streak by viewModel.quizStreak.collectAsState()
    val totalAnswered by viewModel.quizTotalAnswered.collectAsState()
    val dailyProgress by viewModel.dailyChallengeProgress.collectAsState()
    val weeklyProgression by viewModel.weeklyProgression.collectAsState()
    val todayAssignment by viewModel.todayAssignment.collectAsState()
    val isChallengeActive by viewModel.isDailyQuizChallengeActive.collectAsState()
    val selectedDayNumber by viewModel.selectedDailyChallengeDay.collectAsState()
    val selectedWeekNumber by viewModel.selectedQuizWeekNumber.collectAsState()

    var inDayQuizScreen by remember { mutableStateOf(false) }
    var saveConfirmationMessage by remember { mutableStateOf<String?>(null) }
    var showWeekPickerDialog by remember { mutableStateOf(false) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.ensureDailyChallengeProgressLoaded()
    }

    LaunchedEffect(isChallengeActive) {
        if (isChallengeActive) {
            inDayQuizScreen = true
        }
    }

    // 10 Quizzes for the selected day
    val dayQuestions = remember(selectedDayNumber) {
        DailyQuiz365Generator.getQuestionsForDay(selectedDayNumber)
    }

    val currentQuestion = dayQuestions.getOrNull(currentIndex)
    val isDayFinished = isSubmitted && currentIndex == dayQuestions.size - 1

    LaunchedEffect(isDayFinished) {
        if (isDayFinished) {
            showCompletionDialog = true
        }
    }

    val scrollState = rememberScrollState()

    SelectionContainer {
        Box(modifier = modifier.fillMaxSize()) {
            if (!inDayQuizScreen) {
                // ==========================================================
                // 1. MAIN QUIZZES PAGE — 7-DAY WEEKLY JOURNEY
                // ==========================================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Top Section: Title & Stats Header (Streak, Points, Badges)
                    QuizzesTopHeader(
                        streak = streak,
                        totalScore = score,
                        completedWeeksCount = weeklyProgression.completedWeeksCount,
                        totalCompletedDays = dailyProgress?.totalCompletedDays ?: 0
                    )

                    // Weekly Journey Card (7-day indicators with states)
                    WeeklyJourneyCard(
                        weeklyProgression = weeklyProgression,
                        onPrevWeek = {
                            if (weeklyProgression.weekNumber > 1) {
                                viewModel.selectQuizWeek(weeklyProgression.weekNumber - 1)
                            }
                        },
                        onNextWeek = {
                            if (weeklyProgression.weekNumber < 53) {
                                viewModel.selectQuizWeek(weeklyProgression.weekNumber + 1)
                            }
                        },
                        onOpenWeekPicker = { showWeekPickerDialog = true },
                        onDaySelected = { dayInfo ->
                            if (!dayInfo.isLocked) {
                                viewModel.startDaily10QuizChallenge(dayInfo.dayNumber)
                                inDayQuizScreen = true
                            }
                        },
                        onContinueActiveDay = {
                            viewModel.startDaily10QuizChallenge(weeklyProgression.currentActiveDayNumber)
                            inDayQuizScreen = true
                        }
                    )

                    // List of 7 DAYS for the Current Selected Week
                    WeeklyDaysListSection(
                        weeklyProgression = weeklyProgression,
                        onDayClick = { dayInfo ->
                            if (!dayInfo.isLocked) {
                                viewModel.startDaily10QuizChallenge(dayInfo.dayNumber)
                                inDayQuizScreen = true
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            } else {
                // ==========================================================
                // 2. DAY QUIZ SCREEN — DEDICATED 10-QUIZ WORKFLOW
                // ==========================================================
                DayQuizScreen(
                    dayNumber = selectedDayNumber,
                    questions = dayQuestions,
                    currentIndex = currentIndex,
                    currentQuestion = currentQuestion,
                    selectedOption = selectedOption,
                    isSubmitted = isSubmitted,
                    score = score,
                    streak = streak,
                    totalAnswered = totalAnswered,
                    saveConfirmationMessage = saveConfirmationMessage,
                    onBackToWeeklyJourney = {
                        inDayQuizScreen = false
                        viewModel.exitDaily10QuizChallenge()
                    },
                    onSelectOption = { viewModel.selectQuizOption(it) },
                    onSaveNote = { question ->
                        viewModel.saveQuizQuestionToNotes(question)
                        saveConfirmationMessage = "سؤال کوئز اور گرامر نوٹس میں محفوظ ہو گیا! 📌"
                    },
                    onNextQuestion = {
                        saveConfirmationMessage = null
                        viewModel.nextQuizQuestion()
                    },
                    onRestartQuiz = {
                        saveConfirmationMessage = null
                        viewModel.restartQuiz()
                    },
                    onFinishDay = {
                        saveConfirmationMessage = null
                        viewModel.completeDailyChallengeDay(selectedDayNumber, score)
                        val nextDay = if (selectedDayNumber < 365) selectedDayNumber + 1 else 1
                        viewModel.startDaily10QuizChallenge(nextDay)
                        inDayQuizScreen = false
                    }
                )
            }

            // Week Jump Picker Dialog
            if (showWeekPickerDialog) {
                WeekPickerBottomSheetDialog(
                    currentWeek = weeklyProgression.weekNumber,
                    activeWeek = weeklyProgression.currentActiveWeekNumber,
                    onSelectWeek = { week ->
                        viewModel.selectQuizWeek(week)
                        showWeekPickerDialog = false
                    },
                    onDismiss = { showWeekPickerDialog = false }
                )
            }

            // Day Completion Dialog
            if (showCompletionDialog && inDayQuizScreen) {
                DayCompletionCelebrationDialog(
                    dayNumber = selectedDayNumber,
                    score = score,
                    totalQuestions = dayQuestions.size,
                    onContinueNextDay = {
                        showCompletionDialog = false
                        val nextDay = if (selectedDayNumber < 365) selectedDayNumber + 1 else 1
                        viewModel.completeDailyChallengeDay(selectedDayNumber, score)
                        viewModel.startDaily10QuizChallenge(nextDay)
                    },
                    onReturnToJourney = {
                        showCompletionDialog = false
                        viewModel.completeDailyChallengeDay(selectedDayNumber, score)
                        inDayQuizScreen = false
                    }
                )
            }
        }
    }
}

// ==========================================================
// 1. TOP SECTION — COMPACT, POLISHED STATS HEADER
// ==========================================================
@Composable
private fun QuizzesTopHeader(
    streak: Int,
    totalScore: Int,
    completedWeeksCount: Int,
    totalCompletedDays: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Main Title Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Quizzes",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = EditorialTextPrimary,
                    letterSpacing = (-0.5).sp
                )
                Text(
                    text = "365-Day English Grammar Journey • 365 روزہ گرامر سفر",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = EditorialTextMuted
                )
            }

            // Overall Mastery Badge
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFBFDBFE))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(text = "🇵🇰", fontSize = 14.sp)
                    Text(
                        text = "$totalCompletedDays / 365 Days",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D4ED8)
                    )
                }
            }
        }

        // Stats Badges Row (Streak, Points, Mastered Weeks)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Streak Pill
            Surface(
                modifier = Modifier.weight(1f),
                color = Color(0xFFFFF7ED),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFFFEDD5))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Whatshot,
                        contentDescription = "Streak",
                        tint = Color(0xFFEA580C),
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "$streak Days",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9A3412)
                        )
                        Text(
                            text = "Streak",
                            fontSize = 10.sp,
                            color = Color(0xFFC2410C)
                        )
                    }
                }
            }

            // Points Pill
            Surface(
                modifier = Modifier.weight(1f),
                color = Color(0xFFF0FDF4),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFBBF7D0))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Points",
                        tint = Color(0xFF16A34A),
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "$totalScore pts",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF166534)
                        )
                        Text(
                            text = "Score",
                            fontSize = 10.sp,
                            color = Color(0xFF15803D)
                        )
                    }
                }
            }

            // Badges / Weeks Pill
            Surface(
                modifier = Modifier.weight(1f),
                color = Color(0xFFFAF5FF),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFF3E8FF))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Badges",
                        tint = Color(0xFF9333EA),
                        modifier = Modifier.size(18.dp)
                    )
                    Column {
                        Text(
                            text = "$completedWeeksCount Weeks",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF6B21A8)
                        )
                        Text(
                            text = "Mastered",
                            fontSize = 10.sp,
                            color = Color(0xFF7E22CE)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================
// 2. WEEKLY JOURNEY CARD (7 Day Indicators + State Badges)
// ==========================================================
@Composable
private fun WeeklyJourneyCard(
    weeklyProgression: WeeklyQuizProgression,
    onPrevWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onOpenWeekPicker: () -> Unit,
    onDaySelected: (DayProgressInfo) -> Unit,
    onContinueActiveDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = EditorialTheme.colors.background != LightBg
    val completedCount = weeklyProgression.completedDaysCount
    val totalDays = if (weeklyProgression.totalDaysInWeek > 0) weeklyProgression.totalDaysInWeek else 7
    val progressPercent = ((completedCount.toFloat() / totalDays.toFloat()) * 100).toInt()

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header Row with Week Switcher and Week Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Week Switcher Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = onPrevWeek,
                        enabled = weeklyProgression.weekNumber > 1,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Previous Week",
                            tint = if (weeklyProgression.weekNumber > 1) EditorialPrimary else EditorialTextMuted
                        )
                    }

                    Surface(
                        color = Color(0xFFFF9600).copy(alpha = 0.12f),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.clickable { onOpenWeekPicker() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "WEEK ${weeklyProgression.weekNumber}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD97706),
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "▾",
                                fontSize = 11.sp,
                                color = Color(0xFFD97706)
                            )
                        }
                    }

                    IconButton(
                        onClick = onNextWeek,
                        enabled = weeklyProgression.weekNumber < 53,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = "Next Week",
                            tint = if (weeklyProgression.weekNumber < 53) EditorialPrimary else EditorialTextMuted
                        )
                    }
                }

                // Current Day Subtitle / Status
                Surface(
                    color = if (weeklyProgression.isWeekCompleted) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (weeklyProgression.isWeekCompleted) Color(0xFF86EFAC) else Color(0xFFFDE68A))
                ) {
                    Text(
                        text = if (weeklyProgression.isWeekCompleted) "✓ 7/7 Completed" else "Day ${weeklyProgression.currentDayIndex} of $totalDays",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (weeklyProgression.isWeekCompleted) Color(0xFF15803D) else Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // 7 Day Step Indicators Row (Monday..Sunday)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weeklyProgression.days.forEach { dayInfo ->
                    DayIndicatorNode(
                        day = dayInfo,
                        onClick = { onDaySelected(dayInfo) }
                    )
                }
            }

            // Progress Bar (Calculated from completed DAYS, not quizzes)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Progress ($completedCount of $totalDays Days)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextSecondary
                    )
                    Text(
                        text = "$progressPercent%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF9600)
                    )
                }

                LinearProgressIndicator(
                    progress = { if (totalDays > 0) completedCount.toFloat() / totalDays.toFloat() else 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = Color(0xFFFF9600),
                    trackColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                )
            }

            // Primary Action Button
            if (!weeklyProgression.isWeekCompleted) {
                Button(
                    onClick = onContinueActiveDay,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9600),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("continue_active_day_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Continue Day ${weeklyProgression.currentActiveDayNumber} Lesson (10 Quizzes)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==========================================================
// 3. DAY INDICATOR NODE (Completed ✓, Current ●, Available, Locked 🔒)
// ==========================================================
@Composable
private fun DayIndicatorNode(
    day: DayProgressInfo,
    onClick: () -> Unit
) {
    val isDark = EditorialTheme.colors.background != LightBg

    val infiniteTransition = rememberInfiniteTransition(label = "dayPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (day.isToday) 1.08f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(enabled = !day.isLocked) { onClick() }
            .scale(if (day.isToday) pulseScale else 1f)
            .padding(vertical = 2.dp)
    ) {
        // Day Name English & Urdu (e.g., Mon / پیر)
        Text(
            text = day.dayNameEn,
            fontSize = 11.sp,
            fontWeight = if (day.isToday) FontWeight.Black else FontWeight.SemiBold,
            color = when {
                day.isCompleted -> Color(0xFF16A34A)
                day.isToday -> Color(0xFFEA580C)
                day.isLocked -> Color(0xFF94A3B8)
                else -> Color(0xFF475569)
            }
        )
        Text(
            text = day.dayNameUr,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = if (day.isToday) Color(0xFFC2410C) else Color(0xFF94A3B8)
        )

        Spacer(modifier = Modifier.height(6.dp))

        // State Badge
        val bgColor = when {
            day.isCompleted -> Color(0xFF22C55E) // Completed: Green
            day.isToday -> Color(0xFFFF9600)     // Current: Orange
            day.isLocked -> if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0) // Locked: Grey
            else -> if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)        // Available: Slate
        }

        val borderColor = when {
            day.isCompleted -> Color(0xFF16A34A)
            day.isToday -> Color(0xFFC2410C)
            day.isLocked -> if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1)
            else -> Color(0xFF94A3B8)
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(bgColor, CircleShape)
                .border(if (day.isToday) 2.dp else 1.dp, borderColor, CircleShape)
                .shadow(if (day.isToday || day.isCompleted) 3.dp else 0.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            when {
                day.isCompleted -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                day.isToday -> {
                    // Highlighted Active Bullet / Number
                    Text(
                        text = "${day.dayOfWeekIndex}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                day.isLocked -> {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                        modifier = Modifier.size(14.dp)
                    )
                }
                else -> {
                    Text(
                        text = "${day.dayOfWeekIndex}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFE2E8F0) else Color(0xFF475569)
                    )
                }
            }
        }
    }
}

// ==========================================================
// 4. LIST OF 7 DAYS FOR THE CURRENT WEEK (Clean Cards)
// ==========================================================
@Composable
private fun WeeklyDaysListSection(
    weeklyProgression: WeeklyQuizProgression,
    onDayClick: (DayProgressInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "WEEK ${weeklyProgression.weekNumber} • 7-DAY GRAMMAR JOURNEY",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    color = EditorialTextPrimary
                )
                Text(
                    text = "ہفتہ وار 7 روزہ گرامر نصاب — 10 کوئز فی دن",
                    fontSize = 11.sp,
                    color = EditorialTextMuted
                )
            }

            Surface(
                color = EditorialContainer,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, EditorialBorderSoft)
            ) {
                Text(
                    text = "7 Days",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        // The 7 Day Cards
        weeklyProgression.days.forEach { dayInfo ->
            WeeklyDayItemCard(
                day = dayInfo,
                onClick = { onDayClick(dayInfo) }
            )
        }
    }
}

@Composable
private fun WeeklyDayItemCard(
    day: DayProgressInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = EditorialTheme.colors.background != LightBg

    val cardBg = when {
        day.isCompleted -> if (isDark) Color(0xFF143823).copy(alpha = 0.6f) else Color(0xFFF0FDF4)
        day.isToday -> if (isDark) Color(0xFF33200B) else Color(0xFFFFFBEB)
        day.isLocked -> if (isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color(0xFFF8FAFC)
        else -> if (isDark) Color(0xFF1E293B) else Color.White
    }

    val borderColor = when {
        day.isCompleted -> if (isDark) Color(0xFF166534) else Color(0xFFBBF7D0)
        day.isToday -> Color(0xFFF59E0B)
        day.isLocked -> if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
        else -> if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !day.isLocked) { onClick() }
            .testTag("week_day_card_${day.dayNumber}"),
        color = cardBg,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (day.isToday) 1.5.dp else 1.dp, borderColor),
        shadowElevation = if (day.isToday) 2.dp else 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Day Number Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = when {
                    day.isCompleted -> Color(0xFF22C55E)
                    day.isToday -> Color(0xFFFF9600)
                    day.isLocked -> if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                    else -> Color(0xFF3B82F6)
                },
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (day.isCompleted) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else if (day.isLocked) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Locked",
                            tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "DAY",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                text = "${day.dayNumber}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Day Details & Grammar Topic
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Day ${day.dayNumber} — ${day.dayNameEn} (${day.dayNameUr})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (day.isLocked) EditorialTextMuted else EditorialTextPrimary
                    )

                    if (day.isToday) {
                        Surface(
                            color = Color(0xFFEA580C),
                            shape = CircleShape
                        ) {
                            Text(
                                text = "CURRENT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = "${day.grammarTopicTitle} • ${day.grammarTopicTitleUr}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (day.isLocked) EditorialTextMuted else EditorialAccentPurple,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                val progressText = when {
                    day.isCompleted -> "10 MCQs • 10/10 مکمل (Completed) • اسکور: ${day.score}/10"
                    day.isInProgress -> "10 MCQs • ${day.completedMcqsCount}/10 جاری ہے (In Progress)"
                    day.isLocked -> "10 MCQs • قفل شدہ (Locked 0/10)"
                    else -> "10 MCQs • شروع کریں (0/10 Not Started)"
                }

                Text(
                    text = progressText,
                    fontSize = 11.sp,
                    fontWeight = if (day.isInProgress) FontWeight.Bold else FontWeight.Normal,
                    color = if (day.isInProgress) Color(0xFFD97706) else EditorialTextMuted
                )
            }

            // Right Action / Status Badge
            when {
                day.isCompleted -> {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFF86EFAC))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color(0xFF15803D),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Completed (10/10)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }
                day.isInProgress -> {
                    Surface(
                        color = Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "In Progress (${day.completedMcqsCount}/10)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309)
                            )
                        }
                    }
                }
                day.isToday -> {
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9600),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (day.completedMcqsCount > 0) "Resume (${day.completedMcqsCount}/10)" else "Start (0/10)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                day.isLocked -> {
                    Text(
                        text = "Locked (0/10) 🔒",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted
                    )
                }
                else -> {
                    OutlinedButton(
                        onClick = onClick,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = "Start (0/10)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                    }
                }
            }
        }
    }
}

// ==========================================================
// 5. DEDICATED DAY QUIZ SCREEN (10 Quizzes of Selected Day)
// ==========================================================
@Composable
private fun DayQuizScreen(
    dayNumber: Int,
    questions: List<QuizQuestion>,
    currentIndex: Int,
    currentQuestion: QuizQuestion?,
    selectedOption: Int?,
    isSubmitted: Boolean,
    score: Int,
    streak: Int,
    totalAnswered: Int,
    saveConfirmationMessage: String?,
    onBackToWeeklyJourney: () -> Unit,
    onSelectOption: (Int) -> Unit,
    onSaveNote: (QuizQuestion) -> Unit,
    onNextQuestion: () -> Unit,
    onRestartQuiz: () -> Unit,
    onFinishDay: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Navigation & Day Title Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onBackToWeeklyJourney,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, EditorialBorder),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("back_to_weekly_journey_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Weekly Journey",
                    tint = EditorialPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Weekly Journey (ہفتہ وار سفر)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )
            }

            Surface(
                color = Color(0xFFFF9600).copy(alpha = 0.15f),
                shape = CircleShape,
                border = BorderStroke(1.dp, Color(0xFFFF9600))
            ) {
                Text(
                    text = "Day $dayNumber of 365",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        // Day Banner Info
        Surface(
            color = EditorialContainer,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, EditorialBorderSoft),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "DAY $dayNumber • DAILY 10 QUIZZES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp,
                    color = Color(0xFFD97706)
                )
                Text(
                    text = "Complete all 10 quizzes to finish today's lesson.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextPrimary
                )
                Text(
                    text = "آج کے سبق کو مکمل کرنے کے لیے تمام 10 گرامر سوالات حل کریں۔",
                    fontSize = 12.sp,
                    color = EditorialTextSecondary
                )
            }
        }

        // Score Banner & Progress Bar (Question X of 10)
        ScoreProgressBanner(
            score = score,
            totalAnswered = totalAnswered,
            streak = streak,
            currentIndex = currentIndex + 1,
            totalQuestions = questions.size
        )

        if (currentQuestion != null) {
            // Question Card
            QuestionCard(
                question = currentQuestion,
                currentIndex = currentIndex + 1,
                totalQuestions = questions.size,
                selectedOption = selectedOption,
                isSubmitted = isSubmitted,
                onSelectOption = onSelectOption
            )

            // Immediate Feedback Panel (Urdu explanation + All Options Analysis + Rules + Arabic comparison)
            AnimatedVisibility(
                visible = isSubmitted,
                enter = fadeIn() + slideInVertically(initialOffsetY = { 40 }),
                exit = fadeOut()
            ) {
                FeedbackCard(
                    question = currentQuestion,
                    selectedOptionIndex = selectedOption,
                    onSaveNote = { onSaveNote(currentQuestion) }
                )
            }

            // Save Confirmation Badge
            if (saveConfirmationMessage != null) {
                Surface(
                    color = EditorialAccentPurple.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = saveConfirmationMessage,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Navigation Controls (Next Question / Restart / Finish Day)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onRestartQuiz,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, EditorialBorder),
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("restart_quiz_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restart Day Quiz",
                        tint = EditorialPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "دوبارہ",
                        color = EditorialPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (currentIndex < questions.size - 1) {
                    Button(
                        onClick = onNextQuestion,
                        enabled = isSubmitted,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFF9600),
                            disabledContainerColor = Color(0xFFFF9600).copy(alpha = 0.4f)
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
                            contentDescription = "Next Question",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onFinishDay,
                        enabled = isSubmitted,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981),
                            disabledContainerColor = Color(0xFF10B981).copy(alpha = 0.4f)
                        ),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(52.dp)
                            .testTag("finish_day_quiz_button")
                    ) {
                        Text(
                            text = "مکمل اور دن ریکارڈ 🏆",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // All Questions Completed Card
            EditorialCard {
                Text(
                    text = "🎉 بہت خوب! آپ نے دن $dayNumber کے تمام 10 کوئز کامیابی سے مکمل کر لیے ہیں۔",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onBackToWeeklyJourney,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9600)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ہفتہ وار سفر پر واپس جائیں (Back to Weekly Journey)", color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==========================================================
// 6. SCORE & PROGRESS BANNER
// ==========================================================
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
                        color = Color(0xFFFF9600)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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

            val progress = if (totalQuestions > 0) currentIndex.toFloat() / totalQuestions else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = Color(0xFFFF9600),
                trackColor = EditorialBorderSoft
            )
        }
    }
}

// ==========================================================
// 7. QUESTION CARD (Fill in the Blank, Urdu RTL, MCQ Options)
// ==========================================================
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
    val isDark = EditorialTheme.colors.background != LightBg
    var showHint by remember(question.id) { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        color = EditorialSurface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, EditorialBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category & Level Tags
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFFF9600).copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = question.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        letterSpacing = 1.sp
                    )
                }

                Surface(
                    color = EditorialContainer,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, EditorialBorderSoft)
                ) {
                    Text(
                        text = question.level,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = EditorialTextMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Tense Title Subtitle & Non-revealing Hint Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${question.tenseTitleEn} • ${question.tenseTitleAr}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple,
                    modifier = Modifier.weight(1f)
                )

                if (!isSubmitted) {
                    Surface(
                        color = if (showHint) Color(0xFFFEF3C7) else EditorialContainer,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (showHint) Color(0xFFFDE68A) else EditorialBorderSoft),
                        modifier = Modifier.clickable { showHint = !showHint }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = "💡", fontSize = 11.sp)
                            Text(
                                text = if (showHint) "اشارہ چھپائیں" else "اشارہ (Hint)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (showHint) Color(0xFFB45309) else EditorialTextSecondary
                            )
                        }
                    }
                }
            }

            // Non-revealing Pedagogical Hint Card (Active Thinking without revealing target answer)
            AnimatedVisibility(visible = showHint && !isSubmitted) {
                Surface(
                    color = if (isDark) Color(0xFF2E2415) else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF78350F) else Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎯", fontSize = 14.sp)
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = com.example.data.model.EducationalExplanationEngine.getPedagogicalHint(question),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (isDark) Color(0xFFFEF3C7) else Color(0xFF92400E),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }

            // English Question Sentence
            Surface(
                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "FILL IN THE BLANK",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = question.questionText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFFFFFFFF) else EditorialTextPrimary,
                        lineHeight = 26.sp
                    )
                }
            }

            // Urdu Translation Banner (RTL)
            Surface(
                color = if (isDark) Color(0xFF1B2838) else Color(0xFFEFF6FF),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF2563EB).copy(alpha = 0.3f) else Color(0xFFBFDBFE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ترجمہ:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = question.arabicTranslation,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color(0xFFFFFFFF) else EditorialTextPrimary,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // MCQ Options Header
            Text(
                text = "CHOOSE THE CORRECT ANSWER",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextMuted,
                letterSpacing = 1.sp
            )

            // MCQ Options
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.forEachIndexed { index, optionText ->
                    QuizOptionRow(
                        optionIndex = index,
                        optionText = optionText,
                        isSelected = selectedOption == index,
                        isSubmitted = isSubmitted,
                        isCorrect = index == question.correctOptionIndex,
                        onSelect = { if (!isSubmitted) onSelectOption(index) }
                    )
                }
            }
        }
    }
}

// ==========================================================
// 8. QUIZ OPTION ROW
// ==========================================================
@Composable
private fun QuizOptionRow(
    optionIndex: Int,
    optionText: String,
    isSelected: Boolean,
    isSubmitted: Boolean,
    isCorrect: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = EditorialTheme.colors.background != LightBg
    val optionLetters = listOf("A", "B", "C", "D")
    val letter = optionLetters.getOrElse(optionIndex) { "${optionIndex + 1}" }

    val bgColor: Color
    val borderColor: Color
    val textColor: Color
    val badgeBg: Color
    val badgeTextColor: Color

    if (isSubmitted) {
        when {
            isCorrect -> {
                bgColor = if (isDark) Color(0xFF143823) else Color(0xFFE8F5E9)
                borderColor = if (isDark) Color(0xFF2E7D32) else Color(0xFF4CAF50)
                textColor = if (isDark) Color(0xFF81C784) else Color(0xFF1B5E20)
                badgeBg = if (isDark) Color(0xFF2E7D32) else Color(0xFF4CAF50)
                badgeTextColor = Color.White
            }
            isSelected && !isCorrect -> {
                bgColor = if (isDark) Color(0xFF381414) else Color(0xFFFFEBEE)
                borderColor = if (isDark) Color(0xFFC62828) else Color(0xFFEF5350)
                textColor = if (isDark) Color(0xFFE57373) else Color(0xFFB71C1C)
                badgeBg = if (isDark) Color(0xFFC62828) else Color(0xFFEF5350)
                badgeTextColor = Color.White
            }
            else -> {
                bgColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color(0xFFF8FAFC)
                borderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                textColor = EditorialTextMuted
                badgeBg = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                badgeTextColor = EditorialTextMuted
            }
        }
    } else {
        if (isSelected) {
            bgColor = Color(0xFFFF9600).copy(alpha = 0.12f)
            borderColor = Color(0xFFFF9600)
            textColor = Color(0xFFD97706)
            badgeBg = Color(0xFFFF9600)
            badgeTextColor = Color.White
        } else {
            bgColor = if (isDark) Color(0xFF1E293B) else Color(0xFFFAFAFA)
            borderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
            textColor = if (isDark) Color(0xFFFFFFFF) else EditorialTextPrimary
            badgeBg = if (isDark) Color(0xFF334155) else Color(0xFFF1F5F9)
            badgeTextColor = if (isDark) Color(0xFFCBD5E1) else EditorialTextSecondary
        }
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(enabled = !isSubmitted) { onSelect() }
            .testTag("quiz_option_$optionIndex"),
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected || (isSubmitted && isCorrect)) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = badgeBg,
                modifier = Modifier.size(32.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = letter,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeTextColor
                    )
                }
            }

            Text(
                text = optionText,
                fontSize = 15.sp,
                fontWeight = if (isSelected || (isSubmitted && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                color = textColor,
                modifier = Modifier.weight(1f)
            )

            if (isSubmitted) {
                if (isCorrect) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Correct",
                        tint = if (isDark) Color(0xFF4ADE80) else Color(0xFF2E7D32),
                        modifier = Modifier.size(22.dp)
                    )
                } else if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Wrong",
                        tint = if (isDark) Color(0xFFF87171) else Color(0xFFC62828),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

// ==========================================================
// 9. FEEDBACK CARD (Deep Option Analysis • Rule • Solving Method • Arabic Comparison)
// ==========================================================
@Composable
private fun FeedbackCard(
    question: QuizQuestion,
    selectedOptionIndex: Int?,
    onSaveNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = EditorialTheme.colors.background != LightBg
    val isCorrect = selectedOptionIndex == question.correctOptionIndex
    val correctAnswerText = question.options.getOrElse(question.correctOptionIndex) { "" }
    val safeSelectedIndex = selectedOptionIndex ?: question.correctOptionIndex

    val chosenOptionVerdict = com.example.data.model.EducationalExplanationEngine.getOptionSpecificFeedback(question, safeSelectedIndex)
    val grammarRule = com.example.data.model.EducationalExplanationEngine.getGrammarRule(question)
    val solvingMethod = com.example.data.model.EducationalExplanationEngine.getConceptSolvingMethod(question)
    val arabicComparison = com.example.data.model.EducationalExplanationEngine.getArabicGrammarComparison(question)

    var showAllOptionsBreakdown by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(22.dp)),
        color = EditorialSurface,
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(
            1.5.dp,
            if (isCorrect) (if (isDark) Color(0xFF2E7D32) else Color(0xFF4CAF50))
            else (if (isDark) Color(0xFFC62828) else Color(0xFFEF5350))
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Result Header Badge (✓ Correct / ✗ Wrong)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isCorrect) (if (isDark) Color(0xFF143823) else Color(0xFFE8F5E9)) else (if (isDark) Color(0xFF381414) else Color(0xFFFFEBEE)),
                    shape = CircleShape,
                    border = BorderStroke(
                        1.dp,
                        if (isCorrect) (if (isDark) Color(0xFF2E7D32) else Color(0xFF81C784)) else (if (isDark) Color(0xFFC62828) else Color(0xFFE57373))
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                            contentDescription = if (isCorrect) "Correct" else "Wrong",
                            tint = if (isCorrect) (if (isDark) Color(0xFF4ADE80) else Color(0xFF2E7D32)) else (if (isDark) Color(0xFFF87171) else Color(0xFFC62828)),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isCorrect) "✓ درست جواب! (Correct)" else "✗ جواب درست نہیں ہے (Incorrect)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) (if (isDark) Color(0xFF86EFAC) else Color(0xFF1B5E20)) else (if (isDark) Color(0xFFFCA5A5) else Color(0xFFB71C1C))
                        )
                    }
                }

                IconButton(
                    onClick = onSaveNote,
                    modifier = Modifier.testTag("save_question_note_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = "محفوظ کریں (Save Note)",
                        tint = EditorialPrimary
                    )
                }
            }

            // Explicit Correct Answer Banner if user chose wrong option
            if (!isCorrect) {
                Surface(
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "درست جواب:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextSecondary
                        )
                        Text(
                            text = correctAnswerText,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF4ADE80) else Color(0xFF1B5E20)
                        )
                    }
                }
            }

            EditorialDivider()

            // 2. WHY: Option-Specific Analysis
            Surface(
                color = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🎯", fontSize = 13.sp)
                        Text(
                            text = "1. WHY • منتخب کردہ آپشن کا تجزیہ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                    }

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = chosenOptionVerdict,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = EditorialTextPrimary,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Toggle Button for All 4 Options Breakdown
                    Surface(
                        color = EditorialContainer,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, EditorialBorderSoft),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showAllOptionsBreakdown = !showAllOptionsBreakdown }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (showAllOptionsBreakdown) "تمام 4 آپشنز کا موازنہ چھپائیں ▴" else "🔍 تمام 4 آپشنز کا تفصیلی موازنہ دیکھیں (A, B, C, D) ▾",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple
                            )
                        }
                    }

                    // All 4 Options Explanations Breakdown
                    AnimatedVisibility(visible = showAllOptionsBreakdown) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 6.dp)
                        ) {
                            val letters = listOf("A", "B", "C", "D")
                            question.options.forEachIndexed { optIdx, optText ->
                                val isOptCorrect = optIdx == question.correctOptionIndex
                                val optFeedback = com.example.data.model.EducationalExplanationEngine.getOptionSpecificFeedback(question, optIdx)

                                Surface(
                                    color = if (isOptCorrect) (if (isDark) Color(0xFF143823).copy(alpha = 0.5f) else Color(0xFFF0FDF4))
                                    else (if (isDark) Color(0xFF0F172A) else Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, if (isOptCorrect) (if (isDark) Color(0xFF166534) else Color(0xFF86EFAC)) else (if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0))),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                color = if (isOptCorrect) Color(0xFF22C55E) else Color(0xFF64748B),
                                                shape = CircleShape,
                                                modifier = Modifier.size(20.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text(
                                                        text = letters.getOrElse(optIdx) { "${optIdx + 1}" },
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            Text(
                                                text = optText,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isOptCorrect) (if (isDark) Color(0xFF86EFAC) else Color(0xFF15803D)) else EditorialTextPrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                            Text(
                                                text = optFeedback,
                                                fontSize = 12.sp,
                                                color = EditorialTextSecondary,
                                                lineHeight = 20.sp,
                                                textAlign = TextAlign.Start,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. RULE: Authoritative Grammar Rule
            Surface(
                color = if (isDark) Color(0xFF1E1B4B) else Color(0xFFEEF2FF),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF3730A3) else Color(0xFFC7D2FE)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "📜", fontSize = 13.sp)
                        Text(
                            text = "2. RULE • گرامر کا بنیادی قانون",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4F46E5)
                        )
                    }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = grammarRule,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) Color(0xFFE0E7FF) else Color(0xFF1E1B4B),
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 4. HOW TO SOLVE: Step-by-Step Method
            Surface(
                color = if (isDark) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (isDark) Color(0xFF1E293B) else Color(0xFFCBD5E1)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = "🪜", fontSize = 13.sp)
                        Text(
                            text = "3. HOW TO SOLVE • مرحلہ وار حل کرنے کا طریقہ",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                    }
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Text(
                            text = solvingMethod,
                            fontSize = 13.sp,
                            color = EditorialTextPrimary,
                            lineHeight = 22.sp,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // 5. EXAMPLE: Extra Example & Translation if available
            question.extraExample?.takeIf { it.isNotBlank() }?.let { exText ->
                Surface(
                    color = if (isDark) Color(0xFF2E1065) else Color(0xFFF3E8FF),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF581C87) else Color(0xFFE9D5FF)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "📚", fontSize = 13.sp)
                            Text(
                                text = "4. EXAMPLE • مزید وضاحتی مثال",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple
                            )
                        }
                        Text(
                            text = exText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isDark) Color(0xFFF3E8FF) else Color(0xFF581C87),
                            lineHeight = 22.sp
                        )
                    }
                }
            }

            // 6. COMMON MISTAKE: Pitfall Alert
            if (question.commonMistake.isNotBlank()) {
                Surface(
                    color = if (isDark) Color(0xFF271A0C) else Color(0xFFFFFBEB),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF78350F) else Color(0xFFFDE68A)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(48.dp)
                                .background(Color(0xFFF59E0B), shape = RoundedCornerShape(2.dp))
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "⚠️ 5. COMMON MISTAKE • عام غلطی سے بچیں",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFFFBBF24) else Color(0xFFD97706),
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                Text(
                                    text = question.commonMistake,
                                    fontSize = 13.sp,
                                    color = if (isDark) Color(0xFFFEF3C7) else Color(0xFF92400E),
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // 7. ARABIC GRAMMAR COMPARISON (🏛️ المقارنة مع قواعد اللغة العربية)
            if (arabicComparison.isNotBlank()) {
                Surface(
                    color = if (isDark) Color(0xFF1E1B4B) else Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isDark) Color(0xFF3730A3) else Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "🏛️", fontSize = 13.sp)
                            Text(
                                text = "ARABIC GRAMMAR COMPARISON • عربی گرامر سے موازنہ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF818CF8) else Color(0xFF166534),
                                letterSpacing = 0.5.sp
                            )
                        }
                        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                            Text(
                                text = arabicComparison,
                                fontSize = 13.sp,
                                color = if (isDark) Color(0xFFE0E7FF) else Color(0xFF14532D),
                                lineHeight = 22.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================================
// 10. DIALOGS (Week Picker & Day Completion Celebration)
// ==========================================================
@Composable
private fun WeekPickerBottomSheetDialog(
    currentWeek: Int,
    activeWeek: Int,
    onSelectWeek: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text(
                    text = "Select Week (1 to 53)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "365-Day Journey divided into 7-day weeks",
                    fontSize = 12.sp,
                    color = EditorialTextMuted
                )
            }
        },
        text = {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .height(300.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..53).forEach { weekNum ->
                    val startDay = (weekNum - 1) * 7 + 1
                    val endDay = minOf(weekNum * 7, 365)
                    val isSelected = weekNum == currentWeek
                    val isActiveWeek = weekNum == activeWeek

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onSelectWeek(weekNum) },
                        color = if (isSelected) Color(0xFFFF9600).copy(alpha = 0.15f) else Color.Transparent,
                        border = BorderStroke(1.dp, if (isSelected) Color(0xFFFF9600) else Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Week $weekNum",
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color(0xFFD97706) else EditorialTextPrimary
                                )
                                Text(
                                    text = "Days $startDay–$endDay",
                                    fontSize = 11.sp,
                                    color = EditorialTextMuted
                                )
                            }

                            if (isActiveWeek) {
                                Surface(
                                    color = Color(0xFFEA580C),
                                    shape = CircleShape
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("بند کریں (Close)", color = EditorialPrimary)
            }
        }
    )
}

@Composable
private fun DayCompletionCelebrationDialog(
    dayNumber: Int,
    score: Int,
    totalQuestions: Int,
    onContinueNextDay: () -> Unit,
    onReturnToJourney: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onReturnToJourney,
        icon = {
            Surface(
                color = Color(0xFFFEF3C7),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🏆", fontSize = 28.sp)
                }
            }
        },
        title = {
            Text(
                text = "🎉 Day $dayNumber Completed!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "بہت خوب! آپ نے آج کے تمام $totalQuestions کوئز کامیابی سے مکمل کر لیے ہیں۔",
                    fontSize = 14.sp,
                    color = EditorialTextPrimary,
                    textAlign = TextAlign.Center
                )

                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Score", fontSize = 11.sp, color = Color(0xFF166534))
                            Text(
                                text = "$score / $totalQuestions",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Reward", fontSize = 11.sp, color = Color(0xFF166534))
                            Text(
                                text = "+50 XP 💎",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }

                Text(
                    text = "اگلا دن (Day ${dayNumber + 1}) کامیابی سے کھل گیا ہے!",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onContinueNextDay,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9600))
            ) {
                Text("اگلے دن پر جائیں (Next Day)", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onReturnToJourney) {
                Text("ہفتہ وار سفر (Weekly Journey)", color = EditorialPrimary)
            }
        }
    )
}
