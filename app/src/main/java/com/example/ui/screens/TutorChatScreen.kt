package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.Content
import com.example.data.model.Part
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TutorChatScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userName by viewModel.userName.collectAsState()
    val activeTutorMode by viewModel.activeTutorMode.collectAsState()
    val activeLanguage by viewModel.activeLanguage.collectAsState()
    val isStudyModeOn by viewModel.isStudyModeOn.collectAsState()
    val detectedInputLanguage by viewModel.detectedInputLanguage.collectAsState()
    val effectiveResponseLanguage by viewModel.effectiveResponseLanguage.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val chatError by viewModel.chatError.collectAsState()
    val proficiencyLevel by viewModel.proficiencyLevel.collectAsState()
    val dailyStreak by viewModel.dailyStreak.collectAsState()
    val completedTopics by viewModel.completedTopicIds.collectAsState()

    var showChatDetails by remember { mutableStateOf(false) }
    var showNameChangeDialog by remember { mutableStateOf(false) }
    var showLevelDialog by remember { mutableStateOf(false) }
    var showDailyGrammarInline by remember { mutableStateOf(false) }
    var showPlusMenu by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showVoiceTutor by remember { mutableStateOf(false) }
    var isSttListening by remember { mutableStateOf(false) }
    var activeSpeechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSttListening(
                context = context,
                onPartial = { inputText = it },
                onFinal = {
                    inputText = it
                    isSttListening = false
                },
                onError = { errMsg ->
                    isSttListening = false
                    Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                },
                onStarted = { isSttListening = true },
                onRecognizerCreated = { activeSpeechRecognizer = it }
            )
        } else {
            Toast.makeText(context, "Microphone permission is required for speech recognition", Toast.LENGTH_SHORT).show()
        }
    }

    val currentlySpeakingText by viewModel.ttsManager.currentlySpeaking.collectAsState()

    val speakText: (String, String?) -> Unit = { text, id ->
        viewModel.ttsManager.speak(text, id)
    }

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    // Level Selector Dialogue Integration
    if (showLevelDialog) {
        LevelSelectDialog(
            currentLevel = proficiencyLevel,
            onSelect = { 
                viewModel.setProficiencyLevel(it)
                showLevelDialog = false
            },
            onDismiss = { showLevelDialog = false }
        )
    }

    // Name change dialogue
    if (showNameChangeDialog) {
        NameChangeDialog(
            currentName = userName,
            onSave = { viewModel.setUserName(it) },
            onDismiss = { showNameChangeDialog = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (!showChatDetails) {
            // LEARNING HUB PAGE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcoming and Greeting Header
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = EditorialContainer,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, EditorialBorderAccent)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Surface(
                                    color = EditorialAccentPurple,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "LISAN AI TUTOR",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "السلام عليكم ورحمة الله وبركاته، $userName",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimary,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit Name",
                                        tint = EditorialAccentPurple,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { showNameChangeDialog = true }
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Your personal guide to English mastery.",
                                    fontSize = 14.sp,
                                    color = EditorialTextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { showChatDetails = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                                    shape = RoundedCornerShape(12.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text("Start Learning", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // 4 Clear Action Hub Grid
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Action 1: Ask AI Tutor
                        ActionHubCard(
                            title = "Ask AI Tutor",
                            subtitle = "ٹیوٹر سے پوچھیں",
                            description = "انگریزی گرامر کے سوالات پوچھیں",
                            icon = Icons.Default.Chat,
                            backgroundColor = EditorialContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { showChatDetails = true }
                        )

                        // Action 2: Daily Grammar
                        ActionHubCard(
                            title = "Daily Grammar",
                            subtitle = "روزانہ گرامر اصول",
                            description = if (showDailyGrammarInline) "سبق بند کریں" else "آج کا سبق اور مشق مکمل کریں",
                            icon = Icons.Default.MenuBook,
                            backgroundColor = if (showDailyGrammarInline) EditorialAccentPurple.copy(alpha = 0.15f) else EditorialContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { showDailyGrammarInline = !showDailyGrammarInline }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Action 3: Voice Practice
                        ActionHubCard(
                            title = "Voice Practice",
                            subtitle = "بول چال کی مشق",
                            description = "ٹیوٹر کے ساتھ انگریزی گفتگو کریں",
                            icon = Icons.Default.Mic,
                            backgroundColor = EditorialContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { showVoiceTutor = true }
                        )

                        // Action 4: Quick Quiz
                        ActionHubCard(
                            title = "Quick Quiz",
                            subtitle = "مختصر کوئز",
                            description = "انگریزی گرامر کا ٹیسٹ دیں",
                            icon = Icons.Default.Assignment,
                            backgroundColor = EditorialContainer,
                            modifier = Modifier.weight(1f),
                            onClick = { viewModel.setSelectedTab(6) } // Switch to Quizzes index 6
                        )
                    }
                }

                // Inline Daily Grammar Card
                AnimatedVisibility(
                    visible = showDailyGrammarInline,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "آج کا گرامر سبق (Daily Lesson)",
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary,
                                fontSize = 14.sp
                            )
                            TextButton(onClick = { showDailyGrammarInline = false }) {
                                Text("چھپائیں (Collapse)", color = EditorialAccentPurple, fontSize = 12.sp)
                            }
                        }
                        DailyGrammarCard(viewModel = viewModel)
                    }
                }

                // Choose Tutor Mode Section
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = bidiIsolate("Tutor Modes • ٹیوٹر موڈ منتخب کریں"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val modes = listOf(
                            Pair("Explain Grammar", "گرامر سیکھیں"),
                            Pair("Correct My Sentence", "جملہ درست کریں"),
                            Pair("Practice Conversation", "بول چال مشق"),
                            Pair("Translate & Explain", "ترجمہ اور وضاحت"),
                            Pair("Give Me a Quiz", "کوئز حاصل کریں"),
                            Pair("Review My Mistakes", "میری غلطیاں دیکھیں")
                        )

                        modes.forEach { (modeTitle, modeArabic) ->
                            val isSelected = activeTutorMode == modeTitle
                            Surface(
                                color = if (isSelected) EditorialPrimary else EditorialSurface,
                                contentColor = if (isSelected) Color.White else EditorialTextPrimary,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, if (isSelected) EditorialPrimary else EditorialBorder),
                                modifier = Modifier
                                    .clickable {
                                        viewModel.setActiveTutorMode(modeTitle)
                                        showChatDetails = true
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = when (modeTitle) {
                                            "Explain Grammar" -> Icons.Default.MenuBook
                                            "Correct My Sentence" -> Icons.Default.Edit
                                            "Practice Conversation" -> Icons.Default.Forum
                                            "Translate & Explain" -> Icons.Default.Translate
                                            "Give Me a Quiz" -> Icons.Default.Assignment
                                            else -> Icons.Default.CheckCircle
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp),
                                        tint = if (isSelected) Color.White else EditorialAccentPurple
                                    )
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(modeTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        Text(modeArabic, fontSize = 9.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else EditorialTextMuted)
                                    }
                                }
                            }
                        }
                    }
                }

                // Personalization Progress Card
                EditorialCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your Progress • آپ کی کارکردگی",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary
                            )
                            Button(
                                onClick = { showLevelDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Change Level", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                        
                        EditorialDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ProgressStatsItem(
                                label = "Learning Level",
                                value = proficiencyLevel.labelEn,
                                valueArabic = proficiencyLevel.labelAr,
                                icon = Icons.Default.TrendingUp
                            )
                            ProgressStatsItem(
                                label = "Daily Streak",
                                value = "$dailyStreak Days",
                                valueArabic = "$dailyStreak أيام متتالية",
                                icon = Icons.Default.Whatshot
                            )
                            ProgressStatsItem(
                                label = "Grammar Progress",
                                value = "${completedTopics.size} Topics",
                                valueArabic = "${completedTopics.size} دروس مكتملة",
                                icon = Icons.Default.CheckCircle
                            )
                        }
                    }
                }

                // Recent Learning Section
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = bidiIsolate("Recent Learning & Shortcuts • ریسنٹ لرننگ"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        RecentTopicShortcutItem(
                            title = "Simple Present Tense (فعل حال سادہ)",
                            desc = "Learn regular habits, routines, and subject-verb agreement rules.",
                            onClick = {
                                viewModel.setActiveTutorMode("Explain Grammar")
                                showChatDetails = true
                                viewModel.sendChatMessage("Explain Simple Present Tense simply in Arabic.")
                            }
                        )

                        RecentTopicShortcutItem(
                            title = "Subject-Verb Agreement (فاعل اور فعل کی مطابقت)",
                            desc = "Understand when to use singular or plural verbs with complex subjects.",
                            onClick = {
                                viewModel.setActiveTutorMode("Explain Grammar")
                                showChatDetails = true
                                viewModel.sendChatMessage("Tell me the rules of Subject-Verb Agreement with comparisons.")
                            }
                        )

                        // Saved Notes shortcut
                        Surface(
                            color = EditorialContainer,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, EditorialBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.setSelectedTab(4) } // Switch to Library
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.BookmarkBorder,
                                        contentDescription = null,
                                        tint = EditorialPrimary
                                    )
                                    Column {
                                        Text(
                                            "View Your Saved Grammar Notes",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = EditorialPrimary
                                        )
                                        Text(
                                            "محفوظ کیے گئے اہم اصول اور اسباق دیکھیں",
                                            fontSize = 11.sp,
                                            color = EditorialTextSecondary
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = EditorialAccentPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ACTIVE CHAT STREAM VIEW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // Toolbar Header with Back Button & Clear Chat Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { showChatDetails = false },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Hub",
                                tint = EditorialPrimary
                            )
                        }
                        Column {
                            Text(
                                text = "Lisan AI Tutor",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary,
                                fontFamily = FontFamily.Serif
                            )
                            val statusLabel = when {
                                isChatLoading -> "Thinking…"
                                isSttListening -> "Listening…"
                                isStudyModeOn -> "Study Mode"
                                else -> "Friendly conversation"
                            }
                            Surface(
                                color = if (isStudyModeOn) EditorialAccentPurple.copy(alpha = 0.15f) else EditorialContainer,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = statusLabel,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isStudyModeOn) EditorialAccentPurple else EditorialTextMuted,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear Chat",
                            tint = EditorialAccentPurple
                        )
                    }
                }

                // TTS Speaking banner
                AnimatedVisibility(
                    visible = currentlySpeakingText != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("txt_speaking_stop_banner")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Speaking",
                                    tint = Color(0xFFE65100),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Lisan AI is speaking... (ٹیچر بول رہے ہیں)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFE65100)
                                )
                            }
                            Button(
                                onClick = {
                                    viewModel.ttsManager.stop()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("روکیں (Stop)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }

                // Chat Message Streams or Empty Conversation State
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    if (chatHistory.size <= 1) {
                        item {
                            Surface(
                                color = EditorialContainer,
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, EditorialBorderAccent),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .testTag("empty_tutor_welcome_card")
                            ) {
                                Column(
                                    modifier = Modifier.padding(20.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Surface(
                                        color = EditorialAccentPurple.copy(alpha = 0.15f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(56.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = EditorialAccentPurple,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "Assalamualaikum! I’m your Lisan AI Tutor.",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Ask me anything in English, اردو, العربية, or Roman Urdu.",
                                        fontSize = 12.sp,
                                        color = EditorialTextSecondary,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Try asking:",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialTextMuted
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))

                                    val promptChips = listOf(
                                        "Help me speak English",
                                        "مجھے English سکھائیں",
                                        "اشرح لي قواعد الإنجليزية",
                                        "mujhe grammar samjhao"
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        promptChips.forEach { prompt ->
                                            Surface(
                                                color = EditorialSurface,
                                                shape = RoundedCornerShape(12.dp),
                                                border = BorderStroke(1.dp, EditorialBorder),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        viewModel.sendChatMessage(prompt)
                                                    }
                                                    .testTag("prompt_chip_${prompt.take(10)}")
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.SpaceBetween
                                                ) {
                                                    Text(
                                                        text = prompt,
                                                        fontSize = 13.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = EditorialPrimary
                                                    )
                                                    Icon(
                                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                        contentDescription = null,
                                                        tint = EditorialAccentPurple,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        items(chatHistory) { message ->
                            val isUser = message.role == "user"
                            val textContent = message.parts.firstOrNull()?.text ?: ""

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                            ) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(0.85f),
                                    color = if (isUser) EditorialPrimary else EditorialSurface,
                                    shape = RoundedCornerShape(
                                        topStart = 20.dp,
                                        topEnd = 20.dp,
                                        bottomStart = if (isUser) 20.dp else 4.dp,
                                        bottomEnd = if (isUser) 4.dp else 20.dp
                                    ),
                                    border = if (isUser) null else BorderStroke(1.dp, EditorialBorder)
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (isUser) "YOU • آپ" else "TUTOR • لسان ٹیوٹر",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.2.sp,
                                                color = if (isUser) Color.White.copy(alpha = 0.7f) else EditorialAccentPurple
                                            )
                                            val messageId = System.identityHashCode(message).toString()
                                            val isThisSpeaking = currentlySpeakingText == messageId

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (!isUser) {
                                                    val isLiked by viewModel.isLiked("Tutor Response", messageId).collectAsState(initial = false)
                                                    IconButton(
                                                        onClick = {
                                                            viewModel.toggleLike(
                                                                com.example.data.model.LikedItem(
                                                                    id = "like_chat_$messageId",
                                                                    contentType = "Tutor Response",
                                                                    contentId = messageId,
                                                                    title = textContent.take(50).ifBlank { "Tutor Response" }
                                                                )
                                                            )
                                                        },
                                                        modifier = Modifier.size(24.dp).testTag("fav_button_$messageId")
                                                    ) {
                                                        Icon(
                                                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                            contentDescription = "Favorite",
                                                            tint = if (isLiked) Color.Red else EditorialAccentPurple,
                                                            modifier = Modifier.size(15.dp)
                                                        )
                                                    }
                                                }

                                                IconButton(
                                                    onClick = {
                                                        if (isThisSpeaking) {
                                                            viewModel.ttsManager.stop()
                                                        } else {
                                                            speakText(textContent, messageId)
                                                        }
                                                    },
                                                    modifier = Modifier.size(24.dp).testTag("speak_button_$messageId")
                                                ) {
                                                    Icon(
                                                        imageVector = if (isThisSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                                        contentDescription = "Speak",
                                                        tint = if (isThisSpeaking) Color.Red else if (isUser) Color.White else EditorialAccentPurple,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        EditorialMixedText(
                                            text = textContent,
                                            fontSize = 14.sp,
                                            lineHeight = 21.sp,
                                            fontFamily = if (isUser) FontFamily.Default else FontFamily.Serif,
                                            color = if (isUser) Color.White else EditorialTextPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (isChatLoading) {
                        item {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                GeneratingLightingGlow()
                                Text(
                                    "Thinking... (ٹیچر سوچ رہے ہیں)",
                                    fontSize = 11.sp,
                                    color = EditorialAccentPurple,
                                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                                )
                            }
                        }
                    }

                    if (chatError != null) {
                        item {
                            Surface(
                                color = EditorialSurface,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, Color(0xFFB3261E)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = chatError!!,
                                    color = Color(0xFFB3261E),
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }

                // BOTTOM COMPOSER / INPUT BAR
                Surface(
                    color = EditorialSurface,
                    shape = RoundedCornerShape(28.dp),
                    border = BorderStroke(1.dp, EditorialBorder),
                    shadowElevation = 2.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 6.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // [ Plus (+) Button ]
                        IconButton(
                            onClick = { showPlusMenu = true },
                            modifier = Modifier
                                .size(38.dp)
                                .testTag("plus_menu_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Tutor Options",
                                tint = EditorialPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        // [ Text Input Field ]
                        val placeholderText = when (effectiveResponseLanguage) {
                            "Urdu" -> "اپنے ٹیوٹر سے پوچھیں…"
                            "Arabic" -> "اسأل معلّمك…"
                            else -> "Ask your tutor…"
                        }

                        TextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            textStyle = TextStyle(
                                color = EditorialTextPrimary,
                                fontSize = 14.sp,
                                textDirection = TextDirection.ContentOrRtl
                            ),
                            placeholder = {
                                Text(
                                    text = bidiIsolate(placeholderText),
                                    color = EditorialTextMuted,
                                    fontSize = 13.sp,
                                    style = TextStyle(textDirection = TextDirection.ContentOrRtl)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedTextColor = EditorialTextPrimary,
                                unfocusedTextColor = EditorialTextPrimary,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            maxLines = 3,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Send
                            ),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (inputText.isNotBlank()) {
                                        viewModel.sendChatMessage(inputText)
                                        inputText = ""
                                    }
                                }
                            )
                        )

                        // [ Microphone STT Button ]
                        IconButton(
                            onClick = {
                                if (isSttListening) {
                                    activeSpeechRecognizer?.stopListening()
                                    isSttListening = false
                                } else {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                        startSttListening(
                                            context = context,
                                            onPartial = { inputText = it },
                                            onFinal = {
                                                inputText = it
                                                isSttListening = false
                                            },
                                            onError = { errMsg ->
                                                isSttListening = false
                                                Toast.makeText(context, errMsg, Toast.LENGTH_SHORT).show()
                                            },
                                            onStarted = { isSttListening = true },
                                            onRecognizerCreated = { activeSpeechRecognizer = it }
                                        )
                                    } else {
                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("mic_stt_button")
                        ) {
                            Icon(
                                imageVector = if (isSttListening) Icons.Default.MicOff else Icons.Default.Mic,
                                contentDescription = "Speech to text",
                                tint = if (isSttListening) Color.Red else EditorialTextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // [ Send Arrow Button ]
                        if (inputText.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    if (inputText.isNotBlank()) {
                                        viewModel.sendChatMessage(inputText)
                                        inputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .testTag("send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = EditorialAccentPurple,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        // [ Blue Voice Conversation Button ]
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { showVoiceTutor = true }
                                .testTag("blue_voice_button"),
                            color = Color(0xFF2563EB),
                            shape = CircleShape,
                            shadowElevation = 2.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Voice Conversation",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Plus Icon Popup Menu
        if (showPlusMenu) {
            AlertDialog(
                onDismissRequest = { showPlusMenu = false },
                confirmButton = {
                    TextButton(onClick = { showPlusMenu = false }) {
                        Text("Done", color = EditorialAccentPurple, fontWeight = FontWeight.Bold)
                    }
                },
                title = {
                    Text(
                        text = "Tutor Options",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                },
                text = {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Response Language Section
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Response Language",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val languages = listOf("Auto", "English", "Urdu", "Arabic")
                                val displayLangs = mapOf("Auto" to "Auto", "English" to "English", "Urdu" to "اردو", "Arabic" to "العربية")
                                
                                languages.forEach { lang ->
                                    val isSelected = activeLanguage.equals(lang, ignoreCase = true) || 
                                        (lang == "Urdu" && activeLanguage == "اردو") ||
                                        (lang == "Arabic" && activeLanguage == "العربية")

                                    Surface(
                                        color = if (isSelected) EditorialAccentPurple else EditorialContainer,
                                        contentColor = if (isSelected) Color.White else EditorialPrimary,
                                        shape = CircleShape,
                                        border = BorderStroke(1.dp, if (isSelected) EditorialAccentPurple else EditorialBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.setActiveLanguage(lang) }
                                            .testTag("plus_lang_$lang")
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 8.dp)
                                        ) {
                                            Text(
                                                text = displayLangs[lang] ?: lang,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = EditorialBorder)

                        // Study Mode Section
                        Surface(
                            color = EditorialContainer,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, EditorialBorderAccent),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "Study Mode",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialPrimary
                                        )
                                        Surface(
                                            color = if (isStudyModeOn) EditorialAccentPurple else EditorialTextMuted.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (isStudyModeOn) "On" else "Off",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isStudyModeOn) Color.White else EditorialTextMuted,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Provides structured lessons, grammar comparisons & practice exercises.",
                                        fontSize = 11.sp,
                                        color = EditorialTextSecondary
                                    )
                                }
                                Switch(
                                    checked = isStudyModeOn,
                                    onCheckedChange = { viewModel.setStudyMode(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = EditorialAccentPurple
                                    ),
                                    modifier = Modifier.testTag("study_mode_switch")
                                )
                            }
                        }
                    }
                },
                containerColor = EditorialSurface
            )
        }

        // Clear Chat Confirmation Dialog
        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Clear Conversation?") },
                text = { Text("Are you sure you want to clear this chat conversation history?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.clearChat()
                        showClearConfirmDialog = false
                    }) {
                        Text("Clear", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearConfirmDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Voice dialog overlay
        if (showVoiceTutor) {
            VoiceTutorDialog(
                viewModel = viewModel,
                onDismissRequest = { showVoiceTutor = false }
            )
        }
    }
}

@Composable
fun ActionHubCard(
    title: String,
    subtitle: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, EditorialBorder),
        modifier = modifier
            .height(115.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = subtitle,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple
                    )
                }
                Surface(
                    color = EditorialPrimary,
                    shape = CircleShape,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Text(
                text = description,
                fontSize = 10.sp,
                lineHeight = 13.sp,
                color = EditorialTextSecondary
            )
        }
    }
}

@Composable
fun ProgressStatsItem(
    label: String,
    value: String,
    valueArabic: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Surface(
            color = EditorialAccentPurple.copy(alpha = 0.1f),
            shape = CircleShape,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
        Text(valueArabic, fontSize = 10.sp, color = EditorialAccentPurple)
    }
}

@Composable
fun RecentTopicShortcutItem(
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Surface(
        color = EditorialSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, EditorialBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EditorialPrimary)
                Spacer(modifier = Modifier.height(2.dp))
                Text(desc, fontSize = 11.sp, color = EditorialTextSecondary)
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = EditorialAccentPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun NameChangeDialog(
    currentName: String,
    onSave: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("اپنا نام تبدیل کریں (Change Name)", fontWeight = FontWeight.Bold, color = EditorialPrimary, fontSize = 16.sp) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("براہ کرم اپنا نام درج کریں تاکہ ٹیوٹر آپ کو پکار سکے:", fontSize = 12.sp, color = EditorialTextSecondary)
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EditorialPrimary,
                        unfocusedBorderColor = EditorialBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(text)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
            ) {
                Text("محفوظ کریں", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("منسوخ کریں", color = EditorialAccentPurple)
            }
        },
        containerColor = EditorialSurface
    )
}

private fun startSttListening(
    context: android.content.Context,
    onPartial: (String) -> Unit,
    onFinal: (String) -> Unit,
    onError: (String) -> Unit,
    onStarted: () -> Unit,
    onRecognizerCreated: (SpeechRecognizer) -> Unit
) {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) {
        onError("Speech recognition not available on this device")
        return
    }
    try {
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
        onRecognizerCreated(recognizer)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { onStarted() }
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                val msg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "I couldn't hear that clearly. Please try again."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected. Please try again."
                    else -> "Speech recognition issue. Please try again."
                }
                onError(msg)
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onFinal(matches[0])
                } else {
                    onError("I couldn't hear that clearly. Please try again.")
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onPartial(matches[0])
                }
            }
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        recognizer.startListening(intent)
    } catch (e: Exception) {
        onError("Speech recognition error: ${e.message}")
    }
}
