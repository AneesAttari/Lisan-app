package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyChallengeQuestion
import com.example.data.model.DailyChallengeQuestionType
import com.example.data.model.DailyChallengeState
import com.example.data.model.RevisionItem
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.components.ScrollToBottomFAB
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LisanDailyScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val localDateStr = remember {
        SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    val dailyChallengeState by viewModel.dailyChallengeState.collectAsState()
    val dailyChallengeQuestions by viewModel.dailyChallengeQuestions.collectAsState()
    val currentQuestionIndex by viewModel.currentDailyQuestionIndex.collectAsState()
    val dailyUserAnswer by viewModel.dailyUserAnswer.collectAsState()
    val dailyFeedback by viewModel.dailyFeedback.collectAsState()
    val isDailyCorrect by viewModel.isDailyCorrect.collectAsState()

    
    val dailyVerb by viewModel.dailyVerb.collectAsState()
    val isDailyVerbCompleted by viewModel.isDailyVerbCompleted.collectAsState()
    val isDailyVerbSaved by viewModel.isDailyVerbSaved.collectAsState()

    val dailyIdiom by viewModel.dailyIdiom.collectAsState()
    val isDailyIdiomCompleted by viewModel.isDailyIdiomCompleted.collectAsState()
    val isDailyIdiomSaved by viewModel.isDailyIdiomSaved.collectAsState()

    val dailyHelperChatHistory by viewModel.dailyHelperChatHistory.collectAsState()
    val isDailyHelperTyping by viewModel.isDailyHelperTyping.collectAsState()


    val isLisanDailyRewarded by viewModel.isLisanDailyRewarded.collectAsState()
    val achievements by viewModel.achievements.collectAsState()
    val revisionItems by viewModel.revisionItems.collectAsState()
    val dueRevisionCount by viewModel.dueRevisionCount.collectAsState()
    val unlockedCelebration by viewModel.unlockedCelebration.collectAsState()
    val currentlySpeaking by viewModel.activeSpeakingId.collectAsState()

    val currentQuestion = dailyChallengeQuestions.getOrNull(currentQuestionIndex)
    val isChallengeCompleted = dailyChallengeState == DailyChallengeState.COMPLETED

    // Scroll state for the entire hub page
    val scrollState = rememberScrollState()

    // Achievement unlock celebration dialog
    unlockedCelebration?.let { celebration ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissCelebration() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("مبروك! Congratulations!", color = EditorialPrimary)
                }
            },
            text = {
                Text(
                    text = celebration,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextPrimary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.dismissCelebration() },
                    modifier = Modifier.testTag("celebration_ok_button")
                ) {
                    Text("حسناً (OK)", fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = EditorialBackground
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        // --- Hub Header ---
        val selectedDailyDate by viewModel.selectedDailyDate.collectAsState()

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Lisan Daily",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Small practice. Real progress.",
                        fontSize = 15.sp,
                        color = EditorialTextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(
                    onClick = { viewModel.selectToday() },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(EditorialContainer)
                        .testTag("today_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = "Today",
                        tint = EditorialPrimary
                    )
                }
            }

            // Date navigation controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { viewModel.selectPreviousDay() },
                    modifier = Modifier.testTag("prev_date_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous Day",
                        tint = EditorialAccentPurple
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = EditorialContainer,
                    border = BorderStroke(1.dp, EditorialBorder)
                ) {
                    Text(
                        text = "📅 $selectedDailyDate",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.selectNextDay() },
                    modifier = Modifier.testTag("next_date_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next Day",
                        tint = EditorialAccentPurple
                    )
                }
            }
        }

        // --- Side-by-Side Status Summary Cards ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Challenge Status
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp)),
                color = if (isChallengeCompleted) Color(0xFFF3E5F5) else EditorialContainer,
                border = BorderStroke(1.dp, if (isChallengeCompleted) EditorialAccentPurple.copy(alpha = 0.5f) else EditorialBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Daily Challenge",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextSecondary
                        )
                        Icon(
                            imageVector = if (isChallengeCompleted) Icons.Default.CheckCircle else Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = if (isChallengeCompleted) EditorialAccentPurple else Color(0xFFFF5722),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = if (isChallengeCompleted) "Completed" else "Ready to Practice",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
            }

            // Status cards
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp)),
                color = if (isDailyVerbCompleted) Color(0xFFFFF8E1) else EditorialContainer,
                border = BorderStroke(1.dp, if (isDailyVerbCompleted) Color(0xFFFFD54F) else EditorialBorder)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Daily Verb",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextSecondary
                        )
                        Icon(
                            imageVector = if (isDailyVerbCompleted) Icons.Default.Stars else Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = if (isDailyVerbCompleted) Color(0xFFFFB300) else Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Text(
                        text = if (isDailyVerbCompleted) "Mastered" else "Ready to Learn",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
            }
        }

        // --- Completion Reward Banner ---
        if (isChallengeCompleted && isDailyVerbCompleted) {
            EditorialCard(
                containerColor = Color(0xFFE8F5E9),
                borderColor = Color(0xFF81C784).copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2E7D32).copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lisan Daily Complete! 🌟",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "You've earned 50 XP today for complete alignment!",
                            fontSize = 13.sp,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }
        }

        
        // --- 1. Daily Verb Component ---
        dailyVerb?.let { verb ->
            EditorialCard(
                borderColor = if (isDailyVerbCompleted) Color(0xFFFFD54F).copy(alpha = 0.5f) else EditorialBorder
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAILY VERB",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF9800),
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "آج کا فعل",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                // Giant Display Title
                Text(
                    text = verb.baseForm.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(6.dp))

                // English and Urdu Meanings
                Text(
                    text = "Urdu meaning:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
                Text(
                    text = verb.urduMeaning,
                    fontSize = 16.sp,
                    color = EditorialPrimary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )

                EditorialDivider()
                
                // Verb Forms Table
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EditorialContainer)
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val forms = listOf(
                        "Base form" to verb.baseForm,
                        "Third-person singular" to verb.thirdPerson,
                        "Present participle" to verb.presentParticiple,
                        "Past simple" to verb.pastSimple,
                        "Past participle" to verb.pastParticiple
                    )
                    forms.forEach { (label, value) ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(label, fontSize = 13.sp, color = EditorialTextSecondary)
                            Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                        }
                    }
                }
                
                EditorialDivider()

                // Contextual Examples
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Examples:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted
                    )
                    verb.examples.forEach { example ->
                        Column {
                            Text(
                                text = example.english,
                                fontSize = 14.sp,
                                color = EditorialTextPrimary
                            )
                            Text(
                                text = example.urdu,
                                fontSize = 14.sp,
                                color = EditorialTextSecondary
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(verb.usageNoteEn, fontSize = 13.sp, color = EditorialPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(verb.usageNoteUr, fontSize = 13.sp, color = EditorialTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action buttons: Complete & Save/Bookmark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Listen
                    IconButton(
                        onClick = { viewModel.playVerbAudio(verb.id) },
                        modifier = Modifier.size(48.dp).background(EditorialContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (currentlySpeaking == verb.id) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                            contentDescription = "Listen",
                            tint = EditorialAccentPurple
                        )
                    }

                    // Save bookmark
                    IconButton(
                        onClick = { viewModel.toggleDailyVerbSaved() },
                        modifier = Modifier.size(48.dp).testTag("save_verb_button").background(EditorialContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isDailyVerbSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Verb",
                            tint = if (isDailyVerbSaved) EditorialAccentPurple else EditorialTextSecondary
                        )
                    }
                    
                    // Practise / Note placeholder (button takes up rest of space)
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // --- Daily Idiom Component ---
        dailyIdiom?.let { idiom ->
            EditorialCard(
                borderColor = if (isDailyIdiomCompleted) Color(0xFF81C784).copy(alpha = 0.5f) else EditorialBorder
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = null,
                        tint = EditorialAccentPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "DAILY IDIOM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = "روزمرہ محاورہ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    color = EditorialAccentPurple.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = idiom.category.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = idiom.expression,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Urdu meaning:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
                Text(
                    text = idiom.urduMeaning,
                    fontSize = 16.sp,
                    color = EditorialPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "English Explanation:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
                Text(
                    text = idiom.simpleMeaning,
                    fontSize = 14.sp,
                    color = EditorialTextPrimary
                )

                EditorialDivider()

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Example Sentence:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = idiom.example,
                        fontSize = 14.sp,
                        color = EditorialTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }

                if (idiom.arabicMeaning.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFF3F2FF),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "جيسے آپ نے عربى میں سیکھا ہے (Arabic Connection):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = idiom.arabicMeaning,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.completeDailyIdiom() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDailyIdiomCompleted) Color(0xFF4CAF50) else EditorialPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isDailyIdiomCompleted,
                        modifier = Modifier.testTag("complete_idiom_button")
                    ) {
                        Icon(
                            imageVector = if (isDailyIdiomCompleted) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isDailyIdiomCompleted) "Learned Today! • سیکھ لیا" else "Mark as Learned • سیکھ لیا",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleDailyIdiomSaved() },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("save_idiom_button")
                            .background(EditorialContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isDailyIdiomSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save Idiom",
                            tint = if (isDailyIdiomSaved) EditorialAccentPurple else EditorialTextSecondary
                        )
                    }
                }
            }
        }

        // --- 2. Daily Challenge Component ---
        EditorialCard(
            borderColor = if (isChallengeCompleted) Color.Green.copy(alpha = 0.3f) else EditorialBorder
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Whatshot,
                    contentDescription = null,
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DAILY CHALLENGE",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple,
                    letterSpacing = 1.2.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            when (dailyChallengeState) {
                DailyChallengeState.LOADING -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = EditorialPrimary)
                    }
                }
                DailyChallengeState.ERROR -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = Color.Red, modifier = Modifier.size(36.dp))
                        Text("Couldn't retrieve daily grammar.", color = EditorialTextPrimary)
                        Button(
                            onClick = { viewModel.retryDailyChallenge() },
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                        ) {
                            Text("Retry")
                        }
                    }
                }
                DailyChallengeState.COMPLETED -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Excellent! You have completed today's challenge.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "شاندار! آپ نے آج کا چیلنج مکمل کر لیا ہے۔",
                            fontSize = 15.sp,
                            color = EditorialTextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {
                    if (currentQuestion != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            // Question header count
                            Text(
                                text = "Question ${currentQuestionIndex + 1} of ${dailyChallengeQuestions.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextMuted
                            )

                            // Question Title/Text
                            Text(
                                text = currentQuestion.questionText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary
                            )

                            if (!currentQuestion.urduTranslation.isNullOrBlank()) {
                                Text(
                                    text = currentQuestion.urduTranslation,
                                    fontSize = 15.sp,
                                    color = EditorialTextSecondary,
                                    textAlign = TextAlign.Right,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            EditorialDivider()

                            val isAnswered = dailyChallengeState == DailyChallengeState.ANSWERED

                            // Input fields or MCQ Options
                            if (currentQuestion.type == DailyChallengeQuestionType.MCQ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    currentQuestion.options.forEachIndexed { optIndex, option ->
                                        val isSelected = dailyUserAnswer == optIndex.toString()
                                        val isCorrectOption = optIndex == currentQuestion.correctOptionIndex
                                        val optBorderColor = when {
                                            isAnswered && isCorrectOption -> Color.Green
                                            isAnswered && isSelected && !isCorrectOption -> Color.Red
                                            isSelected -> EditorialPrimary
                                            else -> EditorialBorder
                                        }

                                        Surface(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(12.dp))
                                                .clickable(enabled = !isAnswered) {
                                                    viewModel.setDailyUserAnswer(optIndex.toString())
                                                }
                                                .testTag("mcq_option_$optIndex"),
                                            color = if (isSelected) EditorialContainer else EditorialSurface,
                                            shape = RoundedCornerShape(12.dp),
                                            border = BorderStroke(1.5.dp, optBorderColor)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(
                                                    text = "${'A' + optIndex}.",
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) EditorialPrimary else EditorialTextSecondary,
                                                    modifier = Modifier.width(24.dp)
                                                )
                                                Text(
                                                    text = option,
                                                    fontSize = 15.sp,
                                                    color = EditorialTextPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                OutlinedTextField(
                                    value = dailyUserAnswer,
                                    onValueChange = { viewModel.setDailyUserAnswer(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("answer_input"),
                                    label = { Text("Your Answer") },
                                    enabled = !isAnswered,
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    shape = RoundedCornerShape(12.dp)
                                )
                            }

                            // Dynamic Feedbacks (English, Urdu and Arabic Comparison)
                            AnimatedVisibility(visible = isAnswered) {
                                Surface(
                                    color = if (isDailyCorrect == true) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = if (isDailyCorrect == true) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                                contentDescription = null,
                                                tint = if (isDailyCorrect == true) Color(0xFF2E7D32) else Color(0xFFC62828),
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (isDailyCorrect == true) "Correct! درست" else "Incorrect! غلط",
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDailyCorrect == true) Color(0xFF2E7D32) else Color(0xFFC62828)
                                            )
                                        }

                                        HorizontalDivider(color = (if (isDailyCorrect == true) Color(0xFF2E7D32) else Color(0xFFC62828)).copy(alpha = 0.1f))

                                        // English Feedback
                                        Text(
                                            text = "English explanation:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialTextMuted
                                        )
                                        Text(
                                            text = currentQuestion.explanationEn,
                                            fontSize = 14.sp,
                                            color = EditorialTextPrimary
                                        )

                                        // Urdu Feedback
                                        Text(
                                            text = "اردو وضاحت:",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialTextMuted
                                        )
                                        Text(
                                            text = currentQuestion.explanationUr,
                                            fontSize = 15.sp,
                                            color = EditorialTextPrimary
                                        )

                                        // Arabic Reference/Comparison
                                        currentQuestion.arabicReference?.let { reference ->
                                            Text(
                                                text = "Arabic grammar reference / comparison:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EditorialTextMuted
                                            )
                                            Text(
                                                text = reference,
                                                fontSize = 14.sp,
                                                color = EditorialPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            }

                            // Submit & Next Buttons
                            Button(
                                onClick = {
                                    if (isAnswered) {
                                        viewModel.nextDailyQuestion()
                                    } else {
                                        viewModel.submitDailyAnswer()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag(if (isAnswered) "next_question_button" else "submit_answer_button"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isAnswered) EditorialAccentPurple else EditorialPrimary
                                ),
                                shape = RoundedCornerShape(12.dp),
                                enabled = dailyUserAnswer.isNotBlank()
                            ) {
                                Text(
                                    text = if (isAnswered) "Continue چیلنج جاری رکھیں" else "Check Answer جواب چیک کریں",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Daily AI Guidance ---
        EditorialCard(
            containerColor = EditorialContainer,
            borderColor = EditorialAccentPurple.copy(alpha = 0.3f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EditorialAccentPurple.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = EditorialAccentPurple
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AI Grammar Assistant Guidance",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Text(
                        text = "لسان مصنوعی ذہانت رہنمائی",
                        fontSize = 13.sp,
                        color = EditorialAccentPurple,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "For deep conversational practice, real-time error correction, and unlimited questions, chat directly with the Lisan AI Tutor — the primary mentor of your learning journey.",
                fontSize = 13.sp,
                color = EditorialTextSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    viewModel.setChatInput("Can you explain today's daily task and grammar rules?")
                    viewModel.setSelectedTab(2)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("open_tutor_from_daily_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Open Lisan AI Tutor (لسان ٹیوٹر سے بات کریں)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // --- 4. Spaced Repetition Revision Planner ---
        EditorialCard {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = null,
                        tint = EditorialAccentPurple,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "REVISION PLANNER",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        letterSpacing = 1.2.sp
                    )
                }
                if (dueRevisionCount > 0) {
                    Badge(containerColor = Color.Red, contentColor = Color.White) {
                        Text(
                            text = "$dueRevisionCount Due",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            val dueItems = revisionItems.filter { it.nextReviewAt <= System.currentTimeMillis() }

            if (dueItems.isEmpty()) {
                Text(
                    text = "All clear! You are fully caught up with your scheduled revisions. ✨",
                    fontSize = 14.sp,
                    color = EditorialTextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Spaced repetition assignments due for self-evaluation:",
                        fontSize = 13.sp,
                        color = EditorialTextSecondary
                    )
                    dueItems.take(3).forEach { item ->
                        var isExpanded by remember { mutableStateOf(false) }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EditorialContainer,
                            border = BorderStroke(1.dp, EditorialBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = item.topic,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialPrimary
                                        )
                                        Text(
                                            text = "Source: ${item.sourceType.replace("_", " ")} • Reviewed ${item.timesReviewed} times",
                                            fontSize = 11.sp,
                                            color = EditorialTextMuted
                                        )
                                    }
                                    TextButton(onClick = { isExpanded = !isExpanded }) {
                                        Text(if (isExpanded) "Hide" else "Reveal")
                                    }
                                }

                                AnimatedVisibility(visible = isExpanded) {
                                    Column(
                                        modifier = Modifier.padding(top = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        HorizontalDivider(color = EditorialBorder)
                                        Text(
                                            text = "Verify your memory of this topic. How did you do?",
                                            fontSize = 13.sp,
                                            color = EditorialTextSecondary
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Button(
                                                onClick = {
                                                    viewModel.reviewRevisionItem(item, isCorrect = false)
                                                    isExpanded = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Need Review", fontSize = 12.sp)
                                            }
                                            Button(
                                                onClick = {
                                                    viewModel.reviewRevisionItem(item, isCorrect = true)
                                                    isExpanded = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("I Know This", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- 5. Discuss with Lisan AI Tutor Quick Link ---
        EditorialCard(
            containerColor = EditorialPillBg,
            borderColor = EditorialPrimary.copy(alpha = 0.15f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EditorialPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = null,
                        tint = EditorialPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Discuss with Lisan AI Tutor",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Text(
                        text = "Need personalized context, detailed practice, or explanations?",
                        fontSize = 12.sp,
                        color = EditorialTextSecondary
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val promptContext = buildString {
                        append("Can you explain the Daily grammar concept")
                        currentQuestion?.let { append(" about '${it.topic}' ('${it.questionText}')") }
                        dailyVerb?.let { append(" or the Daily Verb '${it.baseForm}'") }
                        append("?")
                    }
                    // Trigger deep conversation inside Tutor tab
                    viewModel.setChatInput(promptContext)
                    viewModel.setSelectedTab(2) // Navigate directly to AI Tutor Screen!
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("discuss_with_tutor_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "Start Dialogue with Tutor AI",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        // --- 6. Dynamic Achievements Ribbon List ---
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "YOUR MILESTONES & ACHIEVEMENTS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextMuted,
                letterSpacing = 1.2.sp
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(achievements) { ach ->
                    val opacity = if (ach.isUnlocked) 1f else 0.5f
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (ach.isUnlocked) Color(0xFFE8F5E9) else EditorialContainer,
                        border = BorderStroke(1.dp, if (ach.isUnlocked) Color(0xFF81C784) else EditorialBorder),
                        modifier = Modifier
                            .width(160.dp)
                            .padding(vertical = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = ach.icon,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = ach.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = ach.description,
                                fontSize = 10.sp,
                                color = EditorialTextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                lineHeight = 13.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { ach.currentProgress.toFloat() / ach.maxProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp),
                                color = if (ach.isUnlocked) Color(0xFF2E7D32) else EditorialAccentPurple,
                                trackColor = EditorialBorder
                            )
                            Text(
                                text = "${ach.currentProgress}/${ach.maxProgress}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextMuted,
                                textAlign = TextAlign.Right,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

        ScrollToBottomFAB(
            scrollState = scrollState,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

