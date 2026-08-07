package com.example.ui.screens

import com.example.ui.components.UserAvatar

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
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
import com.example.ui.viewmodel.VoiceSessionState
import com.example.ui.viewmodel.VoiceInputLanguage
import com.example.ui.viewmodel.AudioPlaybackState
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
    val currentlySpeakingText by viewModel.activeSpeakingId.collectAsState()
    val playbackSpeed by viewModel.ttsManager.playbackSpeed.collectAsState()
    val isUrduVoiceAvailable by viewModel.ttsManager.isUrduVoiceAvailable.collectAsState()
    val proficiencyLevel by viewModel.proficiencyLevel.collectAsState()
    val dailyStreak by viewModel.dailyStreak.collectAsState()
    val completedTopics by viewModel.completedTopicIds.collectAsState()
    val settings by viewModel.appSettings.collectAsState(initial = com.example.data.model.AppSettings())

    var showChatDetails by remember { mutableStateOf(false) }
    var showNameChangeDialog by remember { mutableStateOf(false) }
    var showLevelDialog by remember { mutableStateOf(false) }
    var showDailyGrammarInline by remember { mutableStateOf(false) }
    var showPlusMenu by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showTutorPreferences by remember { mutableStateOf(false) }
    
    var inputText by remember { mutableStateOf("") }
    val chatInputState by viewModel.chatInputState.collectAsState()
    LaunchedEffect(chatInputState) {
        if (chatInputState.isNotEmpty()) {
            inputText = chatInputState
            viewModel.clearChatInput()
        }
    }
    val listState = rememberLazyListState()
    var showVoiceTutor by remember { mutableStateOf(false) }
    var isSttListening by remember { mutableStateOf(false) }
    var isSttProcessing by remember { mutableStateOf(false) }
    var sttErrorMessage by remember { mutableStateOf<String?>(null) }
    var activeSpeechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startSttListening(
                context = context,
                onPartial = { inputText = it },
                onFinal = { text ->
                    inputText = text
                    isSttListening = false
                    isSttProcessing = false
                },
                onError = { errMsg ->
                    isSttListening = false
                    isSttProcessing = false
                    sttErrorMessage = "Try again or type your message. / دوبارہ کوشش کریں یا اپنا پیغام لکھیں۔"
                },
                onStarted = {
                    isSttListening = true
                    isSttProcessing = false
                },
                onRecognizerCreated = { activeSpeechRecognizer = it }
            )
        } else {
            Toast.makeText(context, "Microphone permission is required for speech recognition", Toast.LENGTH_SHORT).show()
        }
    }

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
            val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
            var showTutorPreferences by remember { mutableStateOf(false) }
            val hasUserSentMessage = chatHistory.any { it.role == "user" }
            val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
            val isImeVisible = WindowInsets.isImeVisible

            val totalChatItems = chatHistory.size + (if (isChatLoading) 1 else 0) + (if (chatError != null) 1 else 0)
            val lastMsgText = chatHistory.lastOrNull()?.parts?.firstOrNull()?.text ?: ""

            LaunchedEffect(Unit) {
                // Initial greeting if chat history is empty
                if (chatHistory.isEmpty()) {
                    kotlinx.coroutines.delay(1000) // Small delay to allow TTS setup
                    val greeting = "السلام عليكم ورحمة الله وبركاته. Welcome to Lisan Grammar. I will help you learn English grammar by comparing it with Arabic grammar."
                    viewModel.sendChatMessage(greeting)
                    speakText(greeting, "greeting_id")
                }
            }

            LaunchedEffect(totalChatItems, lastMsgText, isImeVisible) {
                if (totalChatItems > 0) {
                    listState.animateScrollToItem(totalChatItems - 1)
                }
            }

            val welcomingText = when (effectiveResponseLanguage) {
                "Urdu" -> "آپ کا دوستانہ English-learning ساتھی"
                "Arabic" -> "رفيقك الودود لتعلّم الإنجليزية"
                else -> "Your friendly English-learning companion"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFFAFAF9))
                    .imePadding()
            ) {
                // PREMIUM HEADER BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button (circular)
                    Surface(
                        color = Color.White,
                        shape = CircleShape,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        modifier = Modifier
                            .size(44.dp)
                            .clickable {
                                viewModel.saveCurrentSessionToHistory()
                                showChatDetails = false
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Hub",
                                tint = EditorialPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // Title pill (soft rounded)
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 1.dp,
                        border = BorderStroke(1.dp, Color(0xFFF3F4F6)),
                        modifier = Modifier.height(40.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = if (isStudyModeOn) EditorialAccentPurple else Color(0xFF10B981),
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = "Lisan AI Tutor",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary,
                                fontFamily = FontFamily.Serif
                            )
                        }
                    }

                    // Right Actions (New Chat & Settings)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // New Chat Button
                        Surface(
                            color = Color.White,
                            shape = CircleShape,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { viewModel.clearChat() }
                                .testTag("new_chat_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "New Chat",
                                    tint = EditorialPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Sliders/Settings Icon (circular)
                        Surface(
                            color = Color.White,
                            shape = CircleShape,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                            modifier = Modifier
                                .size(44.dp)
                                .clickable { showTutorPreferences = true }
                                .testTag("tutor_preferences_button")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Tutor Preferences",
                                    tint = EditorialAccentPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // MAIN MESSAGES CONTENT CONTAINER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (!hasUserSentMessage) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("empty_tutor_welcome_card"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            ) {
                                Text(
                                    text = "السلام عليكم ورحمة الله وبركاته",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary,
                                    textAlign = TextAlign.Center,
                                    style = TextStyle(textDirection = TextDirection.Rtl)
                                )
                                Text(
                                    text = "Welcome to Lisan Grammar. I will help you learn English grammar by comparing it with Arabic grammar.",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = EditorialTextPrimary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            items(chatHistory) { message ->
                                val isUser = message.role == "user"
                                val textContent = message.parts.firstOrNull()?.text ?: ""

                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    if (!isUser) {
                                        Box(
                                            modifier = Modifier
                                                .padding(end = 8.dp, top = 4.dp)
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(EditorialPrimary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("LS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier.weight(1f, fill = false),
                                        color = if (isUser) EditorialPrimary else Color(0xFFF3F2FF),
                                        shape = RoundedCornerShape(
                                            topStart = 20.dp,
                                            topEnd = 20.dp,
                                            bottomStart = if (isUser) 20.dp else 4.dp,
                                            bottomEnd = if (isUser) 4.dp else 20.dp
                                        ),
                                        border = if (isUser) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
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
                                                
                                                if (!isUser) {
                                                    val messageId = System.identityHashCode(message).toString()
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
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            FormattedMessageContent(
                                                text = textContent,
                                                isUser = isUser
                                            )
                                            
                                            if (!isUser) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                TutorMessageControls(
                                                    text = textContent,
                                                    messageId = System.identityHashCode(message).toString(),
                                                    viewModel = viewModel
                                                )
                                            }
                                        }
                                    }

                                    if (isUser) {
                                        val authState = viewModel.authState.value
                                        val currentUser = when (authState) {
                                            is com.example.data.model.AuthState.LoggedIn -> authState.user
                                            is com.example.data.model.AuthState.Guest -> authState.user
                                            else -> null
                                        }
                                        UserAvatar(
                                            photoPath = currentUser?.profilePhotoPath,
                                            displayName = currentUser?.displayName ?: "Learner",
                                            size = 32.dp,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                        )
                                    }
                                }
                            }

                            if (isChatLoading) {
                                item {
                                    Column(
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        GeneratingLightingGlow(language = effectiveResponseLanguage)
                                    }
                                }
                            }

                            if (chatError != null) {
                                item {
                                    Surface(
                                        color = EditorialSurface,
                                        shape = RoundedCornerShape(16.dp),
                                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("chat_error_card")
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Text(
                                                text = "Sorry, I couldn’t reach your Tutor right now. Please try again.",
                                                color = Color(0xFFB91C1C),
                                                fontSize = 14.sp,
                                                lineHeight = 20.sp,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Button(
                                                onClick = { viewModel.retryLastChatMessage() },
                                                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                                                shape = RoundedCornerShape(10.dp),
                                                modifier = Modifier.align(Alignment.End).testTag("retry_button")
                                            ) {
                                                Text("Retry (دوبارہ کوشش کریں)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Conditional Scroll to bottom button (WhatsApp style)
                val coroutineScope = rememberCoroutineScope()
                val isScrollToBottomVisible by remember {
                    derivedStateOf {
                        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                        if (totalChatItems <= 1) false
                        else {
                            lastVisibleItem == null || lastVisibleItem.index < totalChatItems - 2
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isScrollToBottomVisible,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FloatingActionButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (totalChatItems > 0) {
                                        listState.animateScrollToItem(totalChatItems - 1)
                                    }
                                }
                            },
                            containerColor = EditorialPrimary,
                            contentColor = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(16.dp)
                                .size(44.dp)
                                .testTag("scroll_to_bottom_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardDoubleArrowDown,
                                contentDescription = "Scroll to Bottom",
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                if (!isNetworkAvailable) {
                    Surface(
                        color = Color(0xFFFFF3E0),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Tutor needs an internet connection.\nاستاد کے ساتھ براہِ راست سبق کے لیے انٹرنیٹ ضروری ہے۔",
                                color = Color(0xFFE65100),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                // STICKY COMPOSER BAR (Directly above keyboard)
                Surface(
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.ime)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        sttErrorMessage?.let { errMsg ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFFEF2F2))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = errMsg,
                                    fontSize = 11.sp,
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { sttErrorMessage = null },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }

                        if (isSttListening) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                TextButton(
                                    onClick = {
                                        try { activeSpeechRecognizer?.cancel() } catch(e: Exception){}
                                        try { activeSpeechRecognizer?.destroy() } catch(e: Exception){}
                                        activeSpeechRecognizer = null
                                        isSttListening = false
                                        isSttProcessing = false
                                    }
                                ) {
                                    Text(
                                        text = "Cancel / منسوخ کریں",
                                        color = EditorialTextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color.Red, CircleShape)
                                    )
                                    Text(
                                        text = "Listening… / بولیں، میں سن رہا ہوں",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimary
                                    )
                                }

                                Button(
                                    onClick = {
                                        isSttListening = false
                                        isSttProcessing = true
                                        try {
                                            activeSpeechRecognizer?.stopListening()
                                        } catch(e: Exception) {
                                            isSttProcessing = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    shape = RoundedCornerShape(20.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Stop,
                                            contentDescription = "Stop",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = "Stop / ریکارڈنگ روکیں",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        } else if (isSttProcessing) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = EditorialAccentPurple,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Processing voice… / آپ کی آواز کو سمجھا جا رہا ہے…",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary
                                )
                            }
                        } else {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Plus Button [ + ]
                                Surface(
                                    color = Color(0xFFF3F4F6),
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable { showPlusMenu = true }
                                        .testTag("plus_menu_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Tutor Options",
                                            tint = EditorialPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Input Field [ Type a message… ]
                                Surface(
                                    color = Color(0xFFF9FAFB),
                                    shape = RoundedCornerShape(20.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                                    modifier = Modifier
                                        .weight(1f)
                                        .heightIn(min = 40.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                                    ) {
                                        val placeholderText = when (effectiveResponseLanguage) {
                                            "Urdu" -> "پیغام لکھیں…"
                                            "Arabic" -> "اكتب رسالة…"
                                            else -> "Type a message…"
                                        }

                                        TextField(
                                            value = inputText,
                                            onValueChange = {
                                                inputText = it
                                                if (sttErrorMessage != null) sttErrorMessage = null
                                            },
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
                                            modifier = Modifier
                                                .weight(1f)
                                                .testTag("chat_input_field"),
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
                                                imeAction = ImeAction.Send,
                                                autoCorrectEnabled = true
                                            ),
                                            keyboardActions = KeyboardActions(
                                                onSend = {
                                                    if (inputText.isNotBlank() && !isChatLoading && isNetworkAvailable) {
                                                        viewModel.sendChatMessage(inputText)
                                                        inputText = ""
                                                    }
                                                }
                                            )
                                        )
                                    }
                                }

                                // Microphone Button [ 🎤 ]
                                Surface(
                                    color = Color(0xFFF3F4F6),
                                    shape = CircleShape,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            keyboardController?.hide()
                                            sttErrorMessage = null
                                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                                startSttListening(
                                                    context = context,
                                                    onPartial = { inputText = it },
                                                    onFinal = { text ->
                                                        inputText = text
                                                        isSttListening = false
                                                        isSttProcessing = false
                                                    },
                                                    onError = { errMsg ->
                                                        isSttListening = false
                                                        isSttProcessing = false
                                                        sttErrorMessage = "Try again or type your message. / دوبارہ کوشش کریں یا اپنا پیغام لکھیں۔"
                                                    },
                                                    onStarted = {
                                                        isSttListening = true
                                                        isSttProcessing = false
                                                    },
                                                    onRecognizerCreated = { activeSpeechRecognizer = it }
                                                )
                                            } else {
                                                micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                            }
                                        }
                                        .testTag("mic_stt_button")
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Speech to text",
                                            tint = EditorialPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                if (inputText.isBlank()) {
                                    // Blue Conversational Voice Tutor Button [ 🎙️ ]
                                    Surface(
                                        color = Color(0xFF2563EB),
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable { showVoiceTutor = true }
                                            .testTag("blue_voice_button")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = Icons.Default.GraphicEq,
                                                contentDescription = "Live Voice Tutor",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                } else {
                                    // Send Button [ Send ]
                                    val isSendEnabled = !isChatLoading && isNetworkAvailable
                                    Surface(
                                        color = if (isSendEnabled) EditorialAccentPurple else Color(0xFFE5E7EB),
                                        shape = CircleShape,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable(enabled = isSendEnabled) {
                                                if (inputText.isNotBlank() && !isChatLoading && isNetworkAvailable) {
                                                    viewModel.sendChatMessage(inputText)
                                                    inputText = ""
                                                }
                                            }
                                            .testTag("send_button")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (isChatLoading) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(16.dp),
                                                    color = Color.White,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                                    contentDescription = "Send",
                                                    tint = if (isSendEnabled) Color.White else Color(0xFF9CA3AF),
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
            }

            // Tutor Preferences Dialog
            if (showTutorPreferences) {
                AlertDialog(
                    onDismissRequest = { showTutorPreferences = false },
                    title = {
                        Text(
                            "Tutor Preferences • ترجیحات",
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary,
                            fontSize = 16.sp
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Response Language Choice
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "Response Language • جواب کی زبان",
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

                            // Tutor Voice Profile Selection
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "Tutor Voice Profile • آواز کی قسم",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialTextMuted
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val presets = listOf("Young Voice", "Mature Voice")
                                    val displayPresets = mapOf(
                                        "Young Voice" to "Young • نو عمر",
                                        "Mature Voice" to "Mature • بزرگ"
                                    )
                                    
                                    presets.forEach { preset ->
                                        val isSelected = if (preset == "Young Voice") {
                                            settings.selectedSpeaker.contains("Young", ignoreCase = true)
                                        } else {
                                            settings.selectedSpeaker.contains("Mature", ignoreCase = true)
                                        }

                                        Surface(
                                            color = if (isSelected) EditorialAccentPurple else EditorialContainer,
                                            contentColor = if (isSelected) Color.White else EditorialPrimary,
                                            shape = CircleShape,
                                            border = BorderStroke(1.dp, if (isSelected) EditorialAccentPurple else EditorialBorder),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { 
                                                    viewModel.updateAppSettings { it.copy(selectedSpeaker = preset) }
                                                    viewModel.ttsManager.applySpeakerPreset(preset)
                                                }
                                        ) {
                                            Box(
                                                contentAlignment = Alignment.Center,
                                                modifier = Modifier.padding(vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = displayPresets[preset] ?: preset,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = EditorialBorder)

                            // Study Mode Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Study Mode • گرامر موڈ",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimary
                                    )
                                    Text(
                                        "Structured grammar explanations and exercises",
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
                                    )
                                )
                            }

                            HorizontalDivider(color = EditorialBorder)

                            // Voice Responses Toggle (TTS)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Voice Settings • آواز کی ترتیبات",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimary
                                    )
                                    Text(
                                        "Enable spoken replies from teacher automatically",
                                        fontSize = 11.sp,
                                        color = EditorialTextSecondary
                                    )
                                }
                                var voiceResponseEnabled by remember { mutableStateOf(true) }
                                Switch(
                                    checked = voiceResponseEnabled,
                                    onCheckedChange = { 
                                        voiceResponseEnabled = it
                                        if (!it) {
                                            viewModel.ttsManager.stop()
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = EditorialAccentPurple
                                    )
                                )
                            }

                            HorizontalDivider(color = EditorialBorder)

                            // Clear Conversation Option
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        showTutorPreferences = false
                                        showClearConfirmDialog = true
                                    }
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteOutline,
                                    contentDescription = "Clear Chat",
                                    tint = Color.Red
                                )
                                Column {
                                    Text(
                                        "Clear Conversation History",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Red
                                    )
                                    Text(
                                        "Delete all messages in this conversation",
                                        fontSize = 11.sp,
                                        color = EditorialTextSecondary
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showTutorPreferences = false }) {
                            Text("Close", color = EditorialAccentPurple, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = EditorialSurface
                )
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
                        text = "Quick Options",
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
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = displayLangs[lang] ?: lang,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                if (isSelected) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Selected",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                }
                                            }
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

                        HorizontalDivider(color = EditorialBorder)

                        // Start New Chat (which clears current chat)
                        Surface(
                            color = EditorialContainer,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, EditorialBorder),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showPlusMenu = false
                                    showClearConfirmDialog = true
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Chat,
                                    contentDescription = null,
                                    tint = EditorialAccentPurple,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Start New Chat",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EditorialPrimary
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

@Composable
fun LisanCalmOrb(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animation")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_float"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationY = floatOffset
            }
            .size(220.dp)
            .shadow(
                elevation = 24.dp,
                shape = CircleShape,
                clip = false,
                ambientColor = Color(0xFF6366F1).copy(alpha = 0.25f),
                spotColor = Color(0xFF3B82F6).copy(alpha = 0.35f)
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFEEF2FF), // soft lavender white
                        Color(0xFFE0E7FF), // pale lavender
                        Color(0xFFC7D2FE), // pale blue
                        Color(0xFF818CF8).copy(alpha = 0.6f) // subtle sky gradient edge
                    ),
                    radius = 330f
                ),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.6f),
                            Color.Transparent,
                            Color(0xFF6366F1).copy(alpha = 0.2f)
                        )
                    ),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun TutorMessageControls(
    text: String,
    messageId: String,
    viewModel: com.example.ui.viewmodel.LisanViewModel
) {
    val ttsManager = viewModel.ttsManager
    val currentlySpeaking by viewModel.activeSpeakingId.collectAsState()
    val isSpeaking = currentlySpeaking == messageId
    val currentSpeed by ttsManager.playbackSpeed.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSpeaking) {
            val ttsLoadingStatus by ttsManager.ttsLoadingStatus.collectAsState()
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (ttsLoadingStatus != null) {
                    Text(
                        text = ttsLoadingStatus ?: "",
                        fontSize = 10.sp,
                        color = EditorialAccentPurple,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    CircularProgressIndicator(
                        color = EditorialAccentPurple,
                        strokeWidth = 1.5.dp,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                // 1x Speed Pill
                val is1x = currentSpeed == 1.0f
                Surface(
                    onClick = {
                        ttsManager.setPlaybackSpeed(1.0f)
                    },
                    color = if (is1x) EditorialPrimary else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (is1x) EditorialPrimary else Color(0xFFCBD5E1)),
                    modifier = Modifier.height(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text(
                            text = "1×",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (is1x) Color.White else EditorialTextSecondary
                        )
                    }
                }

                // 1.25x Speed Pill
                val is125x = currentSpeed == 1.25f
                Surface(
                    onClick = {
                        ttsManager.setPlaybackSpeed(1.25f)
                    },
                    color = if (is125x) EditorialPrimary else Color.Transparent,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (is125x) EditorialPrimary else Color(0xFFCBD5E1)),
                    modifier = Modifier.height(26.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 8.dp)) {
                        Text(
                            text = "1.25×",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (is125x) Color.White else EditorialTextSecondary
                        )
                    }
                }

                // Red Circular Stop Dot
                Surface(
                    onClick = { ttsManager.stop() },
                    color = Color(0xFFEF4444),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(26.dp)
                        .testTag("stop_speech_$messageId")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color.White, RoundedCornerShape(2.dp))
                        )
                    }
                }
            }
        } else {
            IconButton(
                onClick = { viewModel.speak(text, messageId) },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("speak_button_$messageId")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Speak response",
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun GeneratingLightingGlow(language: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Surface(
        color = Color(0xFFF3F2FF),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp),
        border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = alpha)),
        modifier = Modifier.fillMaxWidth(0.6f)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(14.dp),
                strokeWidth = 2.dp,
                color = EditorialAccentPurple
            )
            Text(
                text = if (language == "Urdu") "ٹیوٹر سوچ رہا ہے…" else "Tutor is thinking…",
                fontSize = 12.sp,
                color = EditorialAccentPurple,
                fontStyle = FontStyle.Italic
            )
        }
    }
}

private fun isUrduOrArabicText(text: String): Boolean {
    var arabicCharCount = 0
    var totalLetters = 0
    for (char in text) {
        val code = char.code
        if (code in 0x0600..0x06FF || code in 0x0750..0x077F || code in 0x08A0..0x08FF || code in 0xFB50..0xFDFF || code in 0xFE70..0xFEFF) {
            arabicCharCount++
        }
        if (char.isLetter()) {
            totalLetters++
        }
    }
    return if (totalLetters > 0) {
        (arabicCharCount.toFloat() / totalLetters) > 0.25f || arabicCharCount >= 3
    } else {
        arabicCharCount > 0
    }
}

private fun cleanMarkdownSymbols(text: String): String {
    return text
        .replace("**", "")
        .replace("###", "")
        .replace("##", "")
        .replace("#", "")
        .replace("`", "")
        .replace(">", "")
        .trim()
}

@Composable
fun FormattedMessageContent(text: String, isUser: Boolean) {
    if (isUser) {
        val isRtl = isUrduOrArabicText(text)
        Text(
            text = text,
            fontSize = 15.sp,
            color = Color.White,
            lineHeight = 22.sp,
            textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
            style = TextStyle(textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr)
        )
        return
    }

    val cleanedText = cleanMarkdownSymbols(text)
    val lines = cleanedText.split("\n")

    val knownHeaders = setOf(
        "English Explanation", "Urdu Explanation", "اردو وضاحت",
        "When We Use It", "اردو میں استعمال", "Sentence Pattern",
        "Examples", "Positive Examples", "Negative Examples", "Question Examples",
        "Common Mistakes", "Arabic Grammar Connection", "Practice",
        "Lesson Title", "Simple English Explanation"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                continue
            }

            val isHeader = knownHeaders.any { header ->
                line.equals(header, ignoreCase = true) || (line.startsWith(header, ignoreCase = true) && line.length < 35)
            }

            val isRtl = isUrduOrArabicText(line)

            if (isHeader) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = line,
                    fontSize = if (isRtl) 16.sp else 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRtl) EditorialAccentPurple else EditorialPrimary,
                    textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                    style = TextStyle(textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr),
                    modifier = Modifier.fillMaxWidth()
                )
            } else if (line.startsWith("Positive:") || line.startsWith("Negative:") || line.startsWith("Question:")) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    Text(
                        text = line,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            } else {
                Text(
                    text = line,
                    fontSize = if (isRtl) 16.sp else 15.sp,
                    fontWeight = FontWeight.Normal,
                    color = EditorialTextPrimary,
                    lineHeight = if (isRtl) 25.sp else 22.sp,
                    textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                    style = TextStyle(textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

