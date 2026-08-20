package com.example.ui.screens

import android.net.Uri
import com.example.ui.components.UserAvatar
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
import androidx.activity.result.PickVisualMediaRequest
import coil.compose.rememberAsyncImagePainter
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
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
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.ui.text.style.TextOverflow

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
    val isPersonalizationEnabled by viewModel.isStudyPersonalizationEnabled.collectAsState()
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
    val userSettings by viewModel.userSettings.collectAsState()

    val showApiKeyGuideModal by viewModel.showApiKeyGuideModal.collectAsState()
    if (showApiKeyGuideModal) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { viewModel.setShowApiKeyGuideModal(false) },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    com.example.ui.screens.ImplementApiKeyScreen(viewModel = viewModel)
                    
                    IconButton(
                        onClick = { viewModel.setShowApiKeyGuideModal(false) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    val showChatDetails by viewModel.isTutorChatActive.collectAsState()
    var showNameChangeDialog by remember { mutableStateOf(false) }
    var showLevelDialog by remember { mutableStateOf(false) }
    var showDailyGrammarInline by remember { mutableStateOf(false) }
    var showPlusMenu by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    var showExamModeDialog by remember { mutableStateOf(false) }
    var showPdfCreatorDialog by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }

    if (showPdfCreatorDialog) {
        com.example.ui.components.PdfCreatorDialog(
            viewModel = viewModel,
            onDismiss = { showPdfCreatorDialog = false }
        )
    }

    if (showExamModeDialog) {
        val examViewModel: com.example.ui.viewmodel.ExamViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
        com.example.ui.components.ExamModeDialog(
            viewModel = examViewModel,
            lisanViewModel = viewModel,
            onDismiss = { showExamModeDialog = false }
        )
    }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
            } catch (e: Exception) {
                android.util.Log.w("TutorChatScreen", "Could not persist URI read permission: ${e.message}")
            }
            selectedImageUri = uri
        }
    }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
            } catch (e: Exception) {
                android.util.Log.w("TutorChatScreen", "Could not persist URI read permission: ${e.message}")
            }

            val fileName = try {
                var name: String? = null
                if (uri.scheme == "content") {
                    val cursor = context.contentResolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (nameIndex != -1) name = it.getString(nameIndex)
                        }
                    }
                }
                name ?: uri.path?.substringAfterLast('/') ?: "file"
            } catch (e: Exception) {
                "file"
            }
            val mimeType = context.contentResolver.getType(uri) ?: ""
            val isDoc = mimeType.contains("pdf") || mimeType.contains("word") || mimeType.contains("presentation") || mimeType.contains("spreadsheet") || mimeType.contains("text") ||
                    fileName.endsWith(".pdf", ignoreCase = true) || fileName.endsWith(".docx", ignoreCase = true) || fileName.endsWith(".pptx", ignoreCase = true) || fileName.endsWith(".xlsx", ignoreCase = true) || fileName.endsWith(".txt", ignoreCase = true)

            if (isDoc) {
                viewModel.processSelectedDocument(uri, fileName)
            } else {
                selectedImageUri = uri
            }
        }
    }
    var showThinkAndExplainDialog by remember { mutableStateOf(false) }

    val cameraPhotoUri = remember {
        try {
            val file = java.io.File(context.cacheDir, "temp_camera_photo.jpg")
            androidx.core.content.FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            android.net.Uri.EMPTY
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && cameraPhotoUri != android.net.Uri.EMPTY) {
            selectedImageUri = cameraPhotoUri
        }
    }

    val cleanupTempCameraPhoto = {
        try {
            val file = java.io.File(context.cacheDir, "temp_camera_photo.jpg")
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            android.util.Log.e("TutorChatScreen", "Failed to delete temporary camera photo: ${e.message}")
        }
    }

    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            cleanupTempCameraPhoto()
        }
    }

    var showTutorPreferences by remember { mutableStateOf(false) }

    var imageMetadata by remember(selectedImageUri) {
        mutableStateOf<com.example.utils.ImageOptimizer.ImageMetadata?>(null)
    }
    var optimizedSizeKBs by remember(selectedImageUri, userSettings.imageCompressionPreset) {
        mutableStateOf<Long?>(null)
    }
    var optimizedWidth by remember(selectedImageUri, userSettings.imageCompressionPreset) {
        mutableStateOf<Int?>(null)
    }
    var optimizedHeight by remember(selectedImageUri, userSettings.imageCompressionPreset) {
        mutableStateOf<Int?>(null)
    }

    LaunchedEffect(selectedImageUri) {
        imageMetadata = selectedImageUri?.let { uri ->
            com.example.utils.ImageOptimizer.getImageMetadata(context, uri)
        }
    }

    LaunchedEffect(selectedImageUri, userSettings.imageCompressionPreset) {
        if (selectedImageUri != null) {
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val presetEnum = try {
                        com.example.utils.ImageOptimizer.CompressionPreset.valueOf(userSettings.imageCompressionPreset.uppercase())
                    } catch (e: Exception) {
                        com.example.utils.ImageOptimizer.CompressionPreset.BALANCED
                    }
                    val result = com.example.utils.ImageOptimizer.optimizeImage(context, selectedImageUri!!, presetEnum)
                    if (result != null) {
                        optimizedSizeKBs = result.compressedSizeBytes.toLong()
                        optimizedWidth = result.compressedWidth
                        optimizedHeight = result.compressedHeight
                    }
                } catch (e: Exception) {
                    android.util.Log.e("TutorChatScreen", "Error estimating optimized image", e)
                }
            }
        } else {
            optimizedSizeKBs = null
            optimizedWidth = null
            optimizedHeight = null
        }
    }

    
    var inputText by remember { mutableStateOf("") }
    var isCreateImageMode by remember { mutableStateOf(false) }
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
    var preSpeechText by remember { mutableStateOf("") }

    fun appendSpeechToExistingText(prefix: String, speech: String): String {
        if (prefix.isBlank()) return speech
        if (speech.isBlank()) return prefix
        return if (prefix.endsWith(" ") || speech.startsWith(" ")) {
            "$prefix$speech"
        } else {
            "$prefix $speech"
        }
    }

    val selectedVoiceLanguage by viewModel.activeVoiceLanguage.collectAsState()
    val sttLanguage = when (selectedVoiceLanguage) {
        VoiceInputLanguage.URDU -> "ur-PK"
        VoiceInputLanguage.ENGLISH -> "en-US"
        VoiceInputLanguage.ARABIC -> "ar-SA"
        VoiceInputLanguage.AUTO -> "auto"
        else -> "en-US"
    }

    DisposableEffect(Unit) {
        onDispose {
            try { activeSpeechRecognizer?.cancel() } catch (e: Exception) {}
            try { activeSpeechRecognizer?.destroy() } catch (e: Exception) {}
            activeSpeechRecognizer = null
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            preSpeechText = inputText
            startSttListening(
                context = context,
                language = sttLanguage,
                onPartial = {},
                onFinal = { text ->
                    val processed = com.example.utils.SpeechTextProcessor.processTranscribedText(
                        rawText = text,
                        targetLanguage = userSettings.selectedVoiceLanguage
                    )
                    inputText = appendSpeechToExistingText(preSpeechText, processed)
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

    LaunchedEffect(chatHistory.size, showChatDetails) {
        if (showChatDetails && chatHistory.isNotEmpty()) {
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

    val localDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val localScope = rememberCoroutineScope()

    val topKeyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
    val topFocusManager = androidx.compose.ui.platform.LocalFocusManager.current

    LaunchedEffect(localDrawerState.isOpen) {
        if (localDrawerState.isOpen) {
            topKeyboardController?.hide()
            topFocusManager.clearFocus()
        }
    }

    BackHandler(enabled = localDrawerState.isOpen) {
        localScope.launch { localDrawerState.close() }
    }

    BackHandler(enabled = showChatDetails && !localDrawerState.isOpen) {
        viewModel.saveCurrentSessionToHistory()
        viewModel.setTutorChatActive(false)
    }

    val colors = EditorialTheme.colors

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        ModalNavigationDrawer(
            drawerState = localDrawerState,
            gesturesEnabled = showChatDetails,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(340.dp),
                    drawerContainerColor = colors.background,
                    drawerTonalElevation = 6.dp
                ) {
                    TutorChatSidebar(
                        viewModel = viewModel,
                        onClose = { localScope.launch { localDrawerState.close() } },
                        onStartNewChat = {
                            viewModel.clearChat()
                            viewModel.setTutorChatActive(true)
                            localScope.launch { localDrawerState.close() }
                        },
                        onConversationSelected = { sessionId ->
                            viewModel.continueConversationSession(sessionId)
                            viewModel.setTutorChatActive(true)
                            localScope.launch { localDrawerState.close() }
                        }
                    )
                }
            }
        ) {
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
                                    onClick = { viewModel.setTutorChatActive(true) },
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
                            onClick = { viewModel.setTutorChatActive(true) }
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
                                        viewModel.setTutorChatActive(true)
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
                                viewModel.setTutorChatActive(true)
                                viewModel.sendChatMessage("Explain Simple Present Tense simply in Arabic.")
                            }
                        )

                        RecentTopicShortcutItem(
                            title = "Subject-Verb Agreement (فاعل اور فعل کی مطابقت)",
                            desc = "Understand when to use singular or plural verbs with complex subjects.",
                            onClick = {
                                viewModel.setActiveTutorMode("Explain Grammar")
                                viewModel.setTutorChatActive(true)
                                viewModel.sendChatMessage("Tell me the rules of Subject-Verb Agreement with comparisons.")
                            }
                        )

                        // Saved Notes shortcut
                        val savedNotesShape = RoundedCornerShape(16.dp)
                        Surface(
                            color = EditorialContainer,
                            shape = savedNotesShape,
                            border = BorderStroke(1.dp, EditorialBorder),
                            shadowElevation = 2.dp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(savedNotesShape)
                                .clickable { viewModel.setSelectedTab(4) } // Switch to Library
                        ) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier.padding(14.dp).padding(bottom = 6.dp),
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
                                        tint = Color(0xFF2563EB),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Bottom Accent Strip
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                                colors = listOf(
                                                    Color(0xFF3B82F6).copy(alpha = 0.3f),
                                                    Color(0xFF2563EB),
                                                    Color(0xFF1D4ED8).copy(alpha = 0.3f)
                                                )
                                            )
                                        )
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // ACTIVE CHAT STREAM VIEW
            val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
            val activeDocState by viewModel.activeDocument.collectAsState()
            val docProcessingState by viewModel.documentProcessingState.collectAsState()
            var showTutorPreferences by remember { mutableStateOf(false) }

            val hasUserSentMessage = chatHistory.any { it.role == "user" }
            val keyboardController = androidx.compose.ui.platform.LocalSoftwareKeyboardController.current
            val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
            val isImeVisible = WindowInsets.isImeVisible

            LaunchedEffect(showVoiceTutor) {
                if (showVoiceTutor) {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            }

            val totalChatItems = chatHistory.size + (if (isChatLoading) 1 else 0) + (if (chatError != null) 1 else 0)
            val lastMsgText = chatHistory.lastOrNull()?.parts?.firstOrNull()?.text ?: ""

            LaunchedEffect(Unit) {
                // Initial welcome message handled locally by ViewModel without extra network delay
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
                    .background(EditorialBackground)
            ) {
                // PREMIUM HEADER BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
// Back Button & Menu (circular)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = EditorialSurface,
                            shape = CircleShape,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, EditorialBorder),
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    viewModel.saveCurrentSessionToHistory()
                                    viewModel.setTutorChatActive(false)
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
                        
                        Surface(
                            color = EditorialSurface,
                            shape = CircleShape,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, EditorialBorder),
                            modifier = Modifier
                                .size(44.dp)
                                .clickable {
                                    localScope.launch { localDrawerState.open() }
                                }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open Chat History",
                                    tint = EditorialPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Title pill (soft rounded)
                    Surface(
                        color = EditorialSurface,
                        shape = RoundedCornerShape(20.dp),
                        shadowElevation = 1.dp,
                        border = BorderStroke(1.dp, EditorialBorder),
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
                            color = EditorialSurface,
                            shape = CircleShape,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, EditorialBorder),
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
                            color = EditorialSurface,
                            shape = CircleShape,
                            shadowElevation = 2.dp,
                            border = BorderStroke(1.dp, EditorialBorder),
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

                com.example.ui.components.DocumentProcessingStatusBanner(
                    processingState = docProcessingState,
                    onDismiss = { viewModel.resetDocumentProcessingState() },
                    onAnalyzeDocument = { doc -> viewModel.setActiveDocument(doc) }
                )

                com.example.ui.components.ActiveDocumentHeaderCard(
                    activeDocument = activeDocState,
                    onClearActiveDocument = { viewModel.setActiveDocument(null) }
                )

                // MAIN MESSAGES CONTENT CONTAINER
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    ChatBackgroundRenderer(
                        theme = userSettings.chatTheme,
                        bgUri = userSettings.chatBackgroundImageUri,
                        isSystemDark = androidx.compose.foundation.isSystemInDarkTheme()
                    )

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
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
                                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth(0.92f),
                                        color = if (isUser) EditorialPrimary else EditorialContainer,
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isUser) 16.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 16.dp
                                        ),
                                        border = if (isUser) null else BorderStroke(1.dp, EditorialBorder)
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
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
                                                    val displayableTextForAudio = cleanRecommendationTags(textContent).replace("[KEY_GUIDE_ACTION]", "").trim()
                                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                        TutorAudioControls(
                                                            text = displayableTextForAudio,
                                                            messageId = messageId,
                                                            viewModel = viewModel
                                                        )
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
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))
                                            val recommendationRoute = if (!isUser) parseRecommendationRoute(textContent) else null
                                            val containsKeyGuideAction = textContent.contains("[KEY_GUIDE_ACTION]")
                                            val displayableText = if (!isUser) {
                                                cleanRecommendationTags(textContent).replace("[KEY_GUIDE_ACTION]", "").trim()
                                            } else textContent

                                            if (isUser && message.imageUri != null) {
                                                Image(
                                                    painter = rememberAsyncImagePainter(model = message.imageUri),
                                                    contentDescription = "User Attached Image",
                                                    modifier = Modifier
                                                        .padding(vertical = 4.dp)
                                                        .sizeIn(maxWidth = 200.dp, maxHeight = 200.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                                                    contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))

                                                // If there's an active error and this is the last user message, show a "Retry" button directly on the bubble
                                                val isLastUserMsg = chatHistory.lastOrNull { it.role == "user" } == message
                                                if (isLastUserMsg && chatError != null) {
                                                    Button(
                                                        onClick = { viewModel.retryLastChatMessage() },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                                        shape = RoundedCornerShape(10.dp),
                                                        modifier = Modifier
                                                            .align(Alignment.Start)
                                                            .padding(vertical = 4.dp)
                                                            .testTag("bubble_retry_button")
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Refresh,
                                                                contentDescription = "Retry Icon",
                                                                tint = EditorialPrimary,
                                                                modifier = Modifier.size(14.dp)
                                                            )
                                                            Text(
                                                                text = "Retry (دوبارہ کوشش کریں)",
                                                                color = EditorialPrimary,
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                }
                                            }

                                            FormattedMessageContent(
                                                text = displayableText,
                                                isUser = isUser
                                            )

                                            message.generatedImageBase64?.let { base64Data ->
                                                Spacer(modifier = Modifier.height(10.dp))
                                                GeneratedImageBubbleCard(
                                                    base64Data = base64Data,
                                                    prompt = message.generatedImagePrompt ?: "Generated Image"
                                                )
                                            }

                                            message.generatedPdfUri?.let { pdfUriStr ->
                                                Spacer(modifier = Modifier.height(10.dp))
                                                PdfDocumentBubbleCard(
                                                    title = message.generatedPdfTitle ?: "Lisan Study Document",
                                                    pdfUri = Uri.parse(pdfUriStr),
                                                    onOpen = { uri -> com.example.utils.PdfCreatorHelper.openPdf(context, uri) },
                                                    onShare = { uri -> com.example.utils.PdfCreatorHelper.sharePdf(context, uri, message.generatedPdfTitle ?: "Lisan Study Guide") }
                                                )
                                            }

                                            if (!isUser) {
                                                if (containsKeyGuideAction) {
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    com.example.ui.components.ApiKeyGuideCardInline(
                                                        onOpenGuide = { viewModel.setShowApiKeyGuideModal(true) },
                                                        onSaveKey = { key -> viewModel.setUserApiKey(key) }
                                                    )
                                                }

                                                recommendationRoute?.let { route ->
                                                    Spacer(modifier = Modifier.height(8.dp))
                                                    RecommendationActionChip(
                                                        route = route,
                                                        onClick = {
                                                            com.example.ui.navigation.HashRouter.navigate(route)
                                                        }
                                                    )
                                                }
                                                Spacer(modifier = Modifier.height(8.dp))
                                                TutorMessageControls(
                                                    text = displayableText,
                                                    messageId = System.identityHashCode(message).toString(),
                                                    viewModel = viewModel
                                                )
                                            }
                                        }
                                    }

                                    // User avatar removed to maximize chat bubble width and reduce scrolling
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
                                                text = chatError ?: "Sorry, I couldn’t reach your Tutor right now. Please try again.",
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

                    // Conditional Scroll to bottom button (WhatsApp style floating over chat)
                    val coroutineScope = rememberCoroutineScope()
                    val isScrollToBottomVisible by remember {
                        derivedStateOf {
                            listState.canScrollForward
                        }
                    }

                    ScrollToBottomFAB(
                        visible = isScrollToBottomVisible,
                        onClick = {
                            coroutineScope.launch {
                                if (totalChatItems > 0) {
                                    listState.animateScrollToItem(totalChatItems - 1, scrollOffset = 10000)
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
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
                    color = EditorialSurface,
                    shadowElevation = 8.dp,
                    border = BorderStroke(1.dp, EditorialBorder),
                    modifier = Modifier
                        .fillMaxWidth()
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

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(EditorialBackground)
                        ) {
                            // Image Preview and Optimization Details
                                selectedImageUri?.let { uri ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                            .background(EditorialContainer, RoundedCornerShape(16.dp))
                                            .border(1.dp, EditorialBorder, RoundedCornerShape(16.dp))
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // 1. Image Thumbnail
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .border(1.dp, EditorialBorderAccent, RoundedCornerShape(12.dp))
                                                .background(EditorialSurface)
                                        ) {
                                            Image(
                                                painter = rememberAsyncImagePainter(model = uri),
                                                contentDescription = "Selected Image",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                                            )
                                            
                                            // Remove/Close button at top-right
                                            Box(
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .padding(4.dp)
                                                    .size(20.dp)
                                                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                                    .clickable { selectedImageUri = null }
                                                    .testTag("remove_image_button"),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove Image",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }

                                        // 2. Info & Controls Pane
                                        Column(
                                            modifier = Modifier.weight(1f),
                                            verticalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CheckCircle,
                                                    contentDescription = "Optimized",
                                                    tint = EditorialAccentPurple,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Text(
                                                    text = "Image Optimized • تم تحسين الصورة",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = EditorialPrimary
                                                )
                                            }

                                            // Sizes display
                                            if (imageMetadata != null) {
                                                val originalFormatted = com.example.utils.ImageOptimizer.formatBytes(imageMetadata!!.originalSizeBytes)
                                                val optimizedFormatted = optimizedSizeKBs?.let { com.example.utils.ImageOptimizer.formatBytes(it) } ?: "Estimating..."
                                                
                                                val reduction = if (optimizedSizeKBs != null && imageMetadata!!.originalSizeBytes > 0) {
                                                    val saved = imageMetadata!!.originalSizeBytes - optimizedSizeKBs!!
                                                    val pct = (saved.toFloat() / imageMetadata!!.originalSizeBytes.toFloat() * 100).toInt()
                                                    if (pct > 0) " (saved $pct%)" else ""
                                                } else ""

                                                Text(
                                                    text = "$originalFormatted → $optimizedFormatted$reduction",
                                                    fontSize = 10.sp,
                                                    color = EditorialTextSecondary,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }

                                            // Interactive Preset Selector
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                val presets = com.example.utils.ImageOptimizer.CompressionPreset.values()
                                                presets.forEach { preset ->
                                                    val isSelected = userSettings.imageCompressionPreset.equals(preset.name, ignoreCase = true)
                                                    Surface(
                                                        color = if (isSelected) EditorialAccentPurple else EditorialSurface,
                                                        contentColor = if (isSelected) Color.White else EditorialPrimary,
                                                        shape = RoundedCornerShape(8.dp),
                                                        border = BorderStroke(1.dp, if (isSelected) EditorialAccentPurple else EditorialBorder),
                                                        modifier = Modifier
                                                            .weight(1f)
                                                            .clickable {
                                                                viewModel.updateAppSettings { it.copy(imageCompressionPreset = preset.name) }
                                                            }
                                                    ) {
                                                        Column(
                                                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                                            horizontalAlignment = Alignment.CenterHorizontally
                                                        ) {
                                                            Text(
                                                                text = preset.displayNameEn,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                maxLines = 1
                                                            )
                                                            Text(
                                                                text = preset.displayNameAr,
                                                                fontSize = 8.sp,
                                                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else EditorialTextMuted,
                                                                maxLines = 1
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                // Create Image Mode Indicator with 'X' cancel button
                                 AnimatedVisibility(
                                    visible = isCreateImageMode,
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    Surface(
                                        color = Color(0xFFF3E8FF),
                                        shape = RoundedCornerShape(12.dp),
                                        border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.5f)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .testTag("create_image_mode_indicator")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(10.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Palette,
                                                        contentDescription = null,
                                                        tint = EditorialAccentPurple,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Text(
                                                        text = "Create Realistic Image",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = EditorialAccentPurple
                                                    )
                                                    Text(
                                                        text = "• تصویری خاکہ",
                                                        fontSize = 11.sp,
                                                        color = EditorialTextSecondary
                                                    )
                                                }
                                                IconButton(
                                                    onClick = { isCreateImageMode = false },
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .testTag("cancel_create_image_mode")
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Cancel Create Image",
                                                        tint = EditorialTextSecondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }

                                            val currentStyle by viewModel.selectedImageStyle.collectAsStateWithLifecycle()
                                            val currentRatio by viewModel.selectedImageAspectRatio.collectAsStateWithLifecycle()

                                            // Style selector chips
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text("Style:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialTextSecondary)
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .horizontalScroll(rememberScrollState()),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    com.example.utils.ImageGeneratorHelper.ImageStyle.values().forEach { style ->
                                                        val isSelected = currentStyle == style
                                                        Surface(
                                                            color = if (isSelected) EditorialAccentPurple else EditorialSurface,
                                                            contentColor = if (isSelected) Color.White else EditorialPrimary,
                                                            shape = RoundedCornerShape(12.dp),
                                                            border = BorderStroke(1.dp, if (isSelected) EditorialAccentPurple else EditorialBorder),
                                                            modifier = Modifier.clickable { viewModel.setImageStyle(style) }
                                                        ) {
                                                            Text(
                                                                text = style.label,
                                                                fontSize = 10.sp,
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // Aspect ratio selector chips
                                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                                Text("Aspect Ratio:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialTextSecondary)
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                ) {
                                                    com.example.utils.ImageGeneratorHelper.ImageAspectRatio.values().forEach { ratio ->
                                                        val isSelected = currentRatio == ratio
                                                        Surface(
                                                            color = if (isSelected) EditorialAccentPurple else EditorialSurface,
                                                            contentColor = if (isSelected) Color.White else EditorialPrimary,
                                                            shape = RoundedCornerShape(12.dp),
                                                            border = BorderStroke(1.dp, if (isSelected) EditorialAccentPurple else EditorialBorder),
                                                            modifier = Modifier
                                                                .weight(1f)
                                                                .clickable { viewModel.setImageAspectRatio(ratio) }
                                                        ) {
                                                            Text(
                                                                text = ratio.label,
                                                                fontSize = 10.sp,
                                                                textAlign = TextAlign.Center,
                                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                                modifier = Modifier.padding(vertical = 4.dp)
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // Plus Button [ + ]
                                    Box {
                                        Surface(
                                            color = EditorialContainer,
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable { showPlusMenu = !showPlusMenu }
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

                                        DropdownMenu(
                                            expanded = showPlusMenu,
                                            onDismissRequest = { showPlusMenu = false },
                                            modifier = Modifier.background(EditorialSurface)
                                        ) {
                                            // 0. Exam Mode Option
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                    ) {
                                                        Text("📝", fontSize = 16.sp)
                                                        Text(
                                                            text = "Exam Mode",
                                                            fontWeight = FontWeight.Bold,
                                                            color = EditorialPrimary
                                                        )
                                                    }
                                                },
                                                onClick = {
                                                    showPlusMenu = false
                                                    showExamModeDialog = true
                                                },
                                                modifier = Modifier.testTag("menu_option_exam_mode")
                                            )
                                            
                                            HorizontalDivider(color = EditorialBorder)
                                            
                                            // 1. Study Mode Option
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                     ) {
                                                         Icon(
                                                             imageVector = if (isStudyModeOn) Icons.Default.CheckCircle else Icons.Default.Circle,
                                                             contentDescription = null,
                                                             tint = if (isStudyModeOn) EditorialAccentPurple else EditorialPrimary,
                                                             modifier = Modifier.size(20.dp)
                                                         )
                                                         Text(
                                                             text = "Study Mode",
                                                             fontWeight = FontWeight.Bold,
                                                             color = EditorialPrimary
                                                         )
                                                     }
                                                 },
                                                 onClick = {
                                                     viewModel.setStudyMode(!isStudyModeOn)
                                                     showPlusMenu = false
                                                 },
                                                 modifier = Modifier.testTag("menu_option_study_mode")
                                             )
                                             
                                             HorizontalDivider(color = EditorialBorder)
                                             
                                             // 2. Attach Image Option
                                             DropdownMenuItem(
                                                 text = {
                                                     Row(
                                                         verticalAlignment = Alignment.CenterVertically,
                                                         horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                     ) {
                                                         Icon(
                                                             imageVector = Icons.Default.Image,
                                                             contentDescription = null,
                                                             tint = EditorialAccentPurple,
                                                             modifier = Modifier.size(20.dp)
                                                         )
                                                         Text(
                                                             text = "Photos/Gallery",
                                                             fontWeight = FontWeight.Bold,
                                                             color = EditorialPrimary
                                                         )
                                                     }
                                                 },
                                                 onClick = {
                                                     showPlusMenu = false
                                                     imagePickerLauncher.launch(
                                                         PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                                     )
                                                 },
                                                 modifier = Modifier.testTag("menu_option_attach_image")
                                             )

                                             HorizontalDivider(color = EditorialBorder)

                                             // 3. Camera Option
                                             DropdownMenuItem(
                                                 text = {
                                                     Row(
                                                         verticalAlignment = Alignment.CenterVertically,
                                                         horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                     ) {
                                                         Icon(
                                                             imageVector = Icons.Default.PhotoCamera,
                                                             contentDescription = null,
                                                             tint = EditorialAccentPurple,
                                                             modifier = Modifier.size(20.dp)
                                                         )
                                                         Text(
                                                             text = "Camera",
                                                             fontWeight = FontWeight.Bold,
                                                             color = EditorialPrimary
                                                         )
                                                     }
                                                 },
                                                 onClick = {
                                                     showPlusMenu = false
                                                     if (cameraPhotoUri != android.net.Uri.EMPTY) {
                                                         cameraLauncher.launch(cameraPhotoUri)
                                                     }
                                                 },
                                                 modifier = Modifier.testTag("menu_option_camera")
                                              )

                                              HorizontalDivider(color = EditorialBorder)

                                              // 4. Files Option
                                              DropdownMenuItem(
                                                  text = {
                                                      Row(
                                                          verticalAlignment = Alignment.CenterVertically,
                                                          horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                      ) {
                                                          Icon(
                                                              imageVector = Icons.Default.FolderOpen,
                                                              contentDescription = null,
                                                              tint = EditorialAccentPurple,
                                                              modifier = Modifier.size(20.dp)
                                                          )
                                                          Text(
                                                              text = "Files",
                                                              fontWeight = FontWeight.Bold,
                                                              color = EditorialPrimary
                                                          )
                                                      }
                                                  },
                                                  onClick = {
                                                      showPlusMenu = false
                                                      filePickerLauncher.launch("*/*")
                                                  },
                                                  modifier = Modifier.testTag("menu_option_files")
                                              )

                                              HorizontalDivider(color = EditorialBorder)

                                              // 5. Think & Explain Option
                                              DropdownMenuItem(
                                                  text = {
                                                      Row(
                                                          verticalAlignment = Alignment.CenterVertically,
                                                          horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                      ) {
                                                          Icon(
                                                              imageVector = Icons.Default.AutoAwesome,
                                                              contentDescription = null,
                                                              tint = EditorialAccentPurple,
                                                              modifier = Modifier.size(20.dp)
                                                          )
                                                          Text(
                                                              text = "Think & Explain Mode",
                                                              fontWeight = FontWeight.Bold,
                                                              color = EditorialPrimary
                                                          )
                                                      }
                                                  },
                                                  onClick = {
                                                      showPlusMenu = false
                                                      showThinkAndExplainDialog = true
                                                  },
                                                  modifier = Modifier.testTag("menu_option_think_explain")
                                              )

                                              HorizontalDivider(color = EditorialBorder)

                                              // 6. PDF Creator Option
                                              DropdownMenuItem(
                                                  text = {
                                                      Row(
                                                          verticalAlignment = Alignment.CenterVertically,
                                                          horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                      ) {
                                                          Icon(
                                                              imageVector = Icons.Default.PictureAsPdf,
                                                              contentDescription = "PDF Creator",
                                                              tint = EditorialAccentPurple,
                                                              modifier = Modifier.size(20.dp)
                                                          )
                                                          Text(
                                                              text = "PDF Creator",
                                                              fontWeight = FontWeight.Bold,
                                                              color = EditorialPrimary
                                                          )
                                                      }
                                                  },
                                                  onClick = {
                                                      showPlusMenu = false
                                                      showPdfCreatorDialog = true
                                                  },
                                                  modifier = Modifier.testTag("menu_option_pdf_creator")
                                              )

                                              HorizontalDivider(color = EditorialBorder)

                                              DropdownMenuItem(
                                                  text = {
                                                      Row(
                                                          verticalAlignment = Alignment.CenterVertically,
                                                          horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                      ) {
                                                          Icon(
                                                              imageVector = Icons.Default.Palette,
                                                              contentDescription = null,
                                                              tint = EditorialAccentPurple,
                                                              modifier = Modifier.size(20.dp)
                                                          )
                                                          Text(
                                                              text = "Create Image",
                                                              fontWeight = FontWeight.Bold,
                                                              color = EditorialPrimary
                                                          )
                                                      }
                                                  },
                                                  onClick = {
                                                      showPlusMenu = false
                                                      isCreateImageMode = true
                                                  },
                                                  modifier = Modifier.testTag("menu_option_create_image")
                                              )
                                        }
                                    }

                                    // Input Field [ Type a message… ]
                                    Surface(
                                        color = EditorialContainer,
                                        shape = RoundedCornerShape(20.dp),
                                        border = BorderStroke(1.dp, EditorialBorder),
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
                                                        fontWeight = FontWeight.Normal,
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
                                                        if ((inputText.isNotBlank() || selectedImageUri != null) && !isChatLoading && isNetworkAvailable) {
                                                            viewModel.sendChatMessage(
                                                                message = inputText,
                                                                imageUri = selectedImageUri?.toString(),
                                                                isCreateImage = isCreateImageMode
                                                            )
                                                            inputText = ""
                                                            selectedImageUri = null
                                                            isCreateImageMode = false
                                                        }
                                                    }
                                                )
                                            )
                                        }
                                    }

                                    // Microphone Button [ 🎤 ] (Turns blue when active speech recognition is listening)
                                    Surface(
                                        color = if (isSttListening) EditorialAccentPurple else EditorialContainer,
                                        shape = CircleShape,
                                        border = BorderStroke(
                                            width = if (isSttListening) 2.dp else 1.dp,
                                            color = if (isSttListening) EditorialAccentPurple else EditorialBorder
                                        ),
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clickable {
                                                keyboardController?.hide()
                                                sttErrorMessage = null
                                                if (isSttListening) {
                                                    try { activeSpeechRecognizer?.cancel() } catch (e: Exception) {}
                                                    try { activeSpeechRecognizer?.destroy() } catch (e: Exception) {}
                                                    activeSpeechRecognizer = null
                                                    isSttListening = false
                                                    isSttProcessing = false
                                                } else {
                                                    preSpeechText = inputText
                                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                                        startSttListening(
                                                            context = context,
                                                            language = sttLanguage,
                                                            onPartial = {},
                                                            onFinal = { text ->
                                                                inputText = appendSpeechToExistingText(preSpeechText, text)
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
                                                     preSpeechText = inputText
                                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                                    }
                                                }
                                            }
                                            .testTag("mic_stt_button")
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            if (isSttProcessing) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(18.dp),
                                                    color = if (isSttListening) Color.White else EditorialAccentPurple,
                                                    strokeWidth = 2.dp
                                                )
                                            } else {
                                                Icon(
                                                    imageVector = Icons.Default.Mic,
                                                    contentDescription = if (isSttListening) "Stop speech recognition" else "Start speech to text",
                                                    tint = if (isSttListening) Color.White else EditorialAccentPurple,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }

                                    if (inputText.isBlank() && selectedImageUri == null) {
                                        // Blue Conversational Voice Tutor Button [ 🎙️ ]
                                        Surface(
                                            color = EditorialAccentPurple,
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
                                            color = if (isSendEnabled) EditorialAccentPurple else EditorialBorder,
                                            shape = CircleShape,
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clickable(enabled = isSendEnabled) {
                                                    if ((inputText.isNotBlank() || selectedImageUri != null) && !isChatLoading && isNetworkAvailable) {
                                                        viewModel.sendChatMessage(
                                                            message = inputText,
                                                            imageUri = selectedImageUri?.toString(),
                                                            isCreateImage = isCreateImageMode
                                                        )
                                                        inputText = ""
                                                        selectedImageUri = null
                                                        isCreateImageMode = false
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
                                                        tint = if (isSendEnabled) Color.White else EditorialTextMuted,
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
                                    val voiceOptions = listOf(
                                        Triple("Young Voice", "Young", "نو عمر"),
                                        Triple("Mature Voice", "Mature", "بزرگ"),
                                        Triple("Gentle Voice", "Gentle", "نرم")
                                    )
                                    
                                    voiceOptions.forEach { (presetKey, titleEn, titleUr) ->
                                        val isSelected = userSettings.selectedSpeaker.contains(presetKey.substringBefore(" "), ignoreCase = true)

                                        Surface(
                                            color = if (isSelected) EditorialAccentPurple else EditorialContainer,
                                            contentColor = if (isSelected) Color.White else EditorialPrimary,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, if (isSelected) EditorialAccentPurple else EditorialBorder),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { 
                                                    viewModel.updateAppSettings { it.copy(selectedSpeaker = presetKey) }
                                                    viewModel.ttsManager.applySpeakerPreset(presetKey)
                                                }
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = titleEn,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = titleUr,
                                                    fontSize = 9.sp,
                                                    maxLines = 1,
                                                    textAlign = TextAlign.Center,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else EditorialTextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = EditorialBorder)

                            // Image Quality Settings Block
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    "Image Quality Preset • جودة الصورة",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialTextMuted
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    val presets = com.example.utils.ImageOptimizer.CompressionPreset.values()
                                    presets.forEach { preset ->
                                        val isSelected = userSettings.imageCompressionPreset.equals(preset.name, ignoreCase = true)

                                        Surface(
                                            color = if (isSelected) EditorialAccentPurple else EditorialContainer,
                                            contentColor = if (isSelected) Color.White else EditorialPrimary,
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, if (isSelected) EditorialAccentPurple else EditorialBorder),
                                            modifier = Modifier
                                                .weight(1f)
                                                .clickable { 
                                                    viewModel.updateAppSettings { it.copy(imageCompressionPreset = preset.name) }
                                                }
                                        ) {
                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.Center,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = preset.displayNameEn,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    textAlign = TextAlign.Center
                                                )
                                                Text(
                                                    text = preset.displayNameAr,
                                                    fontSize = 9.sp,
                                                    maxLines = 1,
                                                    textAlign = TextAlign.Center,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else EditorialTextMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            HorizontalDivider(color = EditorialBorder)

                            // Study Mode & Personalization Settings Block
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // 1. Study Mode Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 48.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                "Study Mode • گرامر موڈ",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EditorialPrimary
                                            )
                                            Surface(
                                                color = if (isStudyModeOn) EditorialAccentPurple else Color(0xFF94A3B8),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isStudyModeOn) Icons.Default.CheckCircle else Icons.Default.Close,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = if (isStudyModeOn) "Active" else "Inactive",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "Study Mode gives your tutor a focused teaching style and can use your selected learning progress to personalize practice.",
                                            fontSize = 11.sp,
                                            color = EditorialTextSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Switch(
                                        checked = isStudyModeOn,
                                        onCheckedChange = { viewModel.setStudyMode(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = EditorialAccentPurple
                                        ),
                                        modifier = Modifier.testTag("chat_study_mode_switch")
                                    )
                                }

                                // 2. Personalization Switch Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 48.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            "Personalize using my progress • میری کارکردگی کا استعمال",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            "Uses your real stats, streaks, lessons completed, and weak areas to customize lessons. If disabled, tutor remains strict but uses only active chat text.",
                                            fontSize = 11.sp,
                                            color = EditorialTextSecondary,
                                            lineHeight = 15.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Switch(
                                        checked = isPersonalizationEnabled,
                                        onCheckedChange = { viewModel.setStudyPersonalizationEnabled(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = EditorialAccentPurple
                                        ),
                                        modifier = Modifier.testTag("chat_personalization_switch")
                                    )
                                }
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

            if (showThinkAndExplainDialog) {
                val inputSentence by viewModel.analyzerInput.collectAsState()
                val analysisResult by viewModel.analyzerOutput.collectAsState()
                val isAnalyzing by viewModel.isAnalyzing.collectAsState()
                val lastUserMessage = chatHistory.lastOrNull { it.role == "user" }?.parts?.firstOrNull()?.text ?: ""
                val defaultSentence = lastUserMessage.ifBlank { "She has finished her homework." }

                LaunchedEffect(showThinkAndExplainDialog) {
                    if (showThinkAndExplainDialog && inputSentence.isBlank()) {
                        viewModel.setAnalyzerInput(defaultSentence)
                    }
                }

                AlertDialog(
                    onDismissRequest = { showThinkAndExplainDialog = false },
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = EditorialAccentPurple,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Think & Explain • فہم اور وضاحت",
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary,
                                fontSize = 16.sp
                            )
                        }
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                "Analyze any sentence or grammar rule below to see how the AI parses it and explains the structure.",
                                fontSize = 12.sp,
                                color = EditorialTextSecondary,
                                lineHeight = 16.sp
                            )

                            TextField(
                                value = inputSentence,
                                onValueChange = { viewModel.setAnalyzerInput(it) },
                                modifier = Modifier.fillMaxWidth().testTag("think_explain_input_field"),
                                shape = RoundedCornerShape(12.dp),
                                textStyle = TextStyle(fontSize = 13.sp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = EditorialContainer,
                                    unfocusedContainerColor = EditorialContainer,
                                    focusedTextColor = EditorialTextPrimary,
                                    unfocusedTextColor = EditorialTextPrimary,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                ),
                                placeholder = {
                                    Text(
                                        "Enter a sentence to analyze...",
                                        color = EditorialTextMuted,
                                        fontSize = 13.sp
                                    )
                                }
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.speak(inputSentence, "think_explain_speak", 1.0f) },
                                    enabled = inputSentence.isNotBlank(),
                                    modifier = Modifier.weight(1.5f).height(40.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EditorialContainer,
                                        contentColor = EditorialTextSecondary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, null, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Listen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.runDeepAnalyzer() },
                                    enabled = !isAnalyzing && inputSentence.isNotBlank(),
                                    modifier = Modifier.weight(2f).height(40.dp).testTag("think_explain_analyze_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EditorialPrimary,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    if (isAnalyzing) {
                                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Thinking...", fontSize = 11.sp)
                                    } else {
                                        Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Analyze", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (isAnalyzing) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        CircularProgressIndicator(color = EditorialAccentPurple)
                                        Text("AI is parsing the sentence structure...", fontSize = 11.sp, color = EditorialTextMuted)
                                    }
                                }
                            } else if (!analysisResult.isNullOrBlank()) {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = EditorialContainer),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .border(1.dp, EditorialBorder, RoundedCornerShape(12.dp))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            "STRUCTURE ANALYSIS & PARSING",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialTextMuted,
                                            letterSpacing = 1.0.sp,
                                            modifier = Modifier.padding(bottom = 8.dp)
                                        )

                                        EditorialDivider()
                                        Spacer(modifier = Modifier.height(8.dp))
                                        EditorialMixedText(text = analysisResult!!, fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showThinkAndExplainDialog = false }) {
                            Text("Close", color = EditorialAccentPurple, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = EditorialSurface
                )
            }
        }

        // Plus Icon Popup Menu (disabled in favor of lightweight dropdown)
        if (false) {
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
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Study Mode Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 48.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "Study Mode",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EditorialPrimary
                                            )
                                            Surface(
                                                color = if (isStudyModeOn) EditorialAccentPurple else Color(0xFF94A3B8),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = if (isStudyModeOn) Icons.Default.CheckCircle else Icons.Default.Close,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(10.dp)
                                                    )
                                                    Text(
                                                        text = if (isStudyModeOn) "Active" else "Inactive",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Enables structured grammar explanations",
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

                                HorizontalDivider(color = EditorialBorder.copy(alpha = 0.5f))

                                // Personalize Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = 48.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Personalize using progress",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialPrimary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Align tutor with your real level and weaknesses",
                                            fontSize = 11.sp,
                                            color = EditorialTextSecondary
                                        )
                                    }
                                    Switch(
                                        checked = isPersonalizationEnabled,
                                        onCheckedChange = { viewModel.setStudyPersonalizationEnabled(it) },
                                        colors = SwitchDefaults.colors(
                                            checkedThumbColor = Color.White,
                                            checkedTrackColor = EditorialAccentPurple
                                        ),
                                        modifier = Modifier.testTag("plus_personalization_switch")
                                    )
                                }
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
}
}

@Composable
fun TutorChatSidebar(
    viewModel: LisanViewModel,
    onClose: () -> Unit,
    onStartNewChat: () -> Unit,
    onConversationSelected: (String) -> Unit
) {
    val colors = EditorialTheme.colors
    val sessions by viewModel.conversationSessions.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    var sessionToDelete by remember { mutableStateOf<com.example.data.model.ConversationSessionEntity?>(null) }
    var sessionToRename by remember { mutableStateOf<com.example.data.model.ConversationSessionEntity?>(null) }
    var newTitleInput by remember { mutableStateOf("") }
    
    var showSettingsInSidebar by remember { mutableStateOf(false) }
    var showMemoryManagerDialog by remember { mutableStateOf(false) }

    val filteredSessions = remember(sessions, searchQuery) {
        sessions.filter { session ->
            session.title.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault())) ||
            session.preview.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault()))
        }
    }

    // Delete Confirmation Dialog
    if (sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Delete Conversation?", fontWeight = FontWeight.Bold, color = colors.primary) },
            text = { Text("This will permanently delete this conversation and detach its content from your Tutor memory.", color = colors.textSecondary) },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Cancel", color = colors.accentPurple)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sessionToDelete?.let { viewModel.deleteConversationSession(it.id) }
                        sessionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        )
    }

    // Rename Dialog
    if (sessionToRename != null) {
        AlertDialog(
            onDismissRequest = { sessionToRename = null },
            title = { Text("Rename Conversation", fontWeight = FontWeight.Bold, color = colors.primary) },
            text = {
                OutlinedTextField(
                    value = newTitleInput,
                    onValueChange = { newTitleInput = it },
                    label = { Text("Conversation Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accentPurple,
                        unfocusedBorderColor = colors.border
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            dismissButton = {
                TextButton(onClick = { sessionToRename = null }) {
                    Text("Cancel", color = colors.accentPurple)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sessionToRename?.let { viewModel.renameConversationSession(it.id, newTitleInput) }
                        sessionToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPurple)
                ) {
                    Text("Save", color = Color.White)
                }
            }
        )
    }

    if (showMemoryManagerDialog) {
        val learnerProfile by viewModel.learnerProfile.collectAsState()
        var newMemoryText by remember { mutableStateOf("") }
        var editingMemoryText by remember { mutableStateOf<String?>(null) }
        var editingMemoryInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showMemoryManagerDialog = false },
            title = {
                Column {
                    Text(
                        text = "Tutor Memory Manager",
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "ٹیوٹر کی یادداشت کا منتظم",
                        fontWeight = FontWeight.Bold,
                        color = colors.accentPurple,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "The tutor automatically saves facts and choices you make (like \"speak Urdu\"), and uses them across conversations. You can view, add, or edit them below.",
                        fontSize = 12.sp,
                        color = colors.textSecondary
                    )

                    // Add custom memory field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newMemoryText,
                            onValueChange = { newMemoryText = it },
                            placeholder = { Text("Add custom rule (e.g. Speak Urdu only)", fontSize = 12.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colors.accentPurple,
                                unfocusedBorderColor = colors.borderSoft
                            )
                        )
                        Button(
                            onClick = {
                                if (newMemoryText.isNotBlank()) {
                                    viewModel.learnerProfileManager.addManualMemory(newMemoryText.trim())
                                    newMemoryText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accentPurple),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Add", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(color = colors.borderSoft)

                    // Memories List
                    if (learnerProfile.usefulMemories.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No saved memories yet.",
                                fontSize = 13.sp,
                                color = colors.textMuted
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(learnerProfile.usefulMemories.size) { index ->
                                val memory = learnerProfile.usefulMemories[index]
                                val isRtl = isUrduOrArabicText(memory)
                                Surface(
                                    color = colors.pillBg,
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, colors.borderSoft),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        if (editingMemoryText == memory) {
                                            OutlinedTextField(
                                                value = editingMemoryInput,
                                                onValueChange = { editingMemoryInput = it },
                                                singleLine = true,
                                                modifier = Modifier.weight(1f),
                                                colors = OutlinedTextFieldDefaults.colors(
                                                    focusedBorderColor = colors.accentPurple,
                                                    unfocusedBorderColor = colors.borderSoft
                                                )
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                IconButton(
                                                    onClick = {
                                                        if (editingMemoryInput.isNotBlank()) {
                                                            viewModel.learnerProfileManager.updateMemory(memory, editingMemoryInput.trim())
                                                        }
                                                        editingMemoryText = null
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Check,
                                                        contentDescription = "Save",
                                                        tint = colors.accentPurple,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                                IconButton(onClick = { editingMemoryText = null }) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Cancel",
                                                        tint = colors.textSecondary,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = memory,
                                                fontSize = if (isRtl) 16.sp else 13.sp,
                                                fontWeight = if (isRtl) FontWeight.Bold else FontWeight.Normal,
                                                color = colors.textPrimary,
                                                textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                                                style = TextStyle(textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .padding(end = 6.dp)
                                            )
                                            Row {
                                                IconButton(
                                                    onClick = {
                                                        editingMemoryText = memory
                                                        editingMemoryInput = memory
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Edit,
                                                        contentDescription = "Edit",
                                                        tint = colors.textSecondary,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(4.dp))
                                                IconButton(
                                                    onClick = {
                                                        viewModel.learnerProfileManager.removeMemory(memory)
                                                    },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Delete,
                                                        contentDescription = "Delete",
                                                        tint = Color.Red,
                                                        modifier = Modifier.size(14.dp)
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
            },
            confirmButton = {
                Button(
                    onClick = { showMemoryManagerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accentPurple)
                ) {
                    Text("Close", color = Color.White)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // TOP HALF: Search & Conversation List
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lisan AI Chat",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colors.primary
                )
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = colors.textSecondary)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // New Chat Button
            Button(
                onClick = onStartNewChat,
                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("new_chat_sidebar_button"),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accentPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("New Conversation", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search conversations...", fontSize = 13.sp, color = colors.textMuted) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = colors.textSecondary, modifier = Modifier.size(18.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accentPurple,
                    unfocusedBorderColor = colors.borderSoft,
                    focusedContainerColor = colors.surface,
                    unfocusedContainerColor = colors.surface
                ),
                modifier = Modifier.fillMaxWidth().height(50.dp).testTag("sidebar_search_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Conversation list title
            Text(
                text = "History",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textMuted
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Lazy List of Sessions
            if (filteredSessions.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No matches found" else "No saved chats yet",
                        color = colors.textMuted,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().testTag("sidebar_past_conversations_list"),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredSessions) { session ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(colors.surface)
                                .clickable { onConversationSelected(session.id) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Left Chat Icon
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(colors.pillBg),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ChatBubbleOutline,
                                        contentDescription = null,
                                        tint = colors.accentPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }

                                // Text block
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = session.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = session.preview,
                                        fontSize = 11.sp,
                                        color = colors.textSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            // Options dots
                            var showMenu by remember { mutableStateOf(false) }
                            Box {
                                IconButton(
                                    onClick = { showMenu = true },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MoreVert,
                                        contentDescription = "Options",
                                        tint = colors.textSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                DropdownMenu(
                                    expanded = showMenu,
                                    onDismissRequest = { showMenu = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Rename", fontSize = 12.sp) },
                                        leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp)) },
                                        onClick = {
                                            showMenu = false
                                            sessionToRename = session
                                            newTitleInput = session.title
                                        }
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete", fontSize = 12.sp, color = Color.Red) },
                                        leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(14.dp)) },
                                        onClick = {
                                            showMenu = false
                                            sessionToDelete = session
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // BOTTOM HALF: Collapsible Settings Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = colors.container,
            border = BorderStroke(1.dp, colors.border)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Collapsible Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showSettingsInSidebar = !showSettingsInSidebar }
                        .padding(vertical = 12.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = colors.accentPurple,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Tutor Settings",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.textPrimary
                        )
                    }
                    Icon(
                        imageVector = if (showSettingsInSidebar) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Collapsible Preferences Block
                AnimatedVisibility(
                    visible = showSettingsInSidebar,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        val activeLanguage by viewModel.activeLanguage.collectAsState()
                        val isStudyModeOn by viewModel.isStudyModeOn.collectAsState()
                        val isPersonalizationEnabled by viewModel.isStudyPersonalizationEnabled.collectAsState()
                        val userSettings by viewModel.userSettings.collectAsState()

                        // Response Language
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Response Language • جواب کی زبان",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textMuted
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
                                        color = if (isSelected) colors.accentPurple else colors.pillBg,
                                        contentColor = if (isSelected) Color.White else colors.textPrimary,
                                        shape = CircleShape,
                                        border = BorderStroke(1.dp, if (isSelected) colors.accentPurple else colors.borderSoft),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { viewModel.setActiveLanguage(lang) }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = displayLangs[lang] ?: lang,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = colors.borderSoft)

                        // Voice preset
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Tutor Voice Profile • آواز کی قسم",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textMuted
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val presets = listOf("Young Voice", "Mature Voice", "Gentle Voice")
                                val displayPresets = mapOf(
                                    "Young Voice" to "Young • نو عمر",
                                    "Mature Voice" to "Mature • بزرگ",
                                    "Gentle Voice" to "Gentle • نرم"
                                )
                                
                                presets.forEach { preset ->
                                    val isSelected = userSettings.selectedSpeaker.contains(preset.substringBefore(" "), ignoreCase = true)

                                    Surface(
                                        color = if (isSelected) colors.accentPurple else colors.pillBg,
                                        contentColor = if (isSelected) Color.White else colors.textPrimary,
                                        shape = CircleShape,
                                        border = BorderStroke(1.dp, if (isSelected) colors.accentPurple else colors.borderSoft),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { 
                                                viewModel.updateAppSettings { it.copy(selectedSpeaker = preset) }
                                                viewModel.ttsManager.applySpeakerPreset(preset)
                                            }
                                    ) {
                                        Box(
                                            contentAlignment = Alignment.Center,
                                            modifier = Modifier.padding(vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = displayPresets[preset] ?: preset,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = colors.borderSoft)

                        // Study Mode Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Study Mode • گرامر موڈ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Gives focused grammar teaching.",
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                            }
                            Switch(
                                checked = isStudyModeOn,
                                onCheckedChange = { viewModel.setStudyMode(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accentPurple
                                )
                            )
                        }

                        // Personalization Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Personalize • کارکردگی",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Uses your learning stats.",
                                    fontSize = 10.sp,
                                    color = colors.textSecondary
                                )
                            }
                            Switch(
                                checked = isPersonalizationEnabled,
                                onCheckedChange = { viewModel.setStudyPersonalizationEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = colors.accentPurple
                                )
                            )
                        }

                        HorizontalDivider(color = colors.borderSoft)

                        // Tutor Memory Row
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showMemoryManagerDialog = true }
                                .padding(vertical = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = null,
                                        tint = colors.accentPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Tutor Memory • ٹیوٹر کی یادداشت",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = colors.textSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = "View and edit what the tutor remembers about you.",
                                fontSize = 10.sp,
                                color = colors.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
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
    val cardShape = RoundedCornerShape(20.dp)
    Surface(
        color = backgroundColor,
        shape = cardShape,
        border = BorderStroke(1.dp, EditorialBorder),
        shadowElevation = 3.dp,
        modifier = modifier
            .height(122.dp)
            .clip(cardShape)
            .clickable { onClick() }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
                    .padding(bottom = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
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
                            color = Color(0xFF2563EB)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = Color(0xFF2563EB),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                Text(
                    text = description,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    color = EditorialTextSecondary,
                    maxLines = 2
                )
            }

            // Bottom Accent Strip (signature blue shape at the end of the box)
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFF3B82F6).copy(alpha = 0.4f),
                                Color(0xFF2563EB),
                                Color(0xFF1D4ED8).copy(alpha = 0.4f)
                            )
                        )
                    )
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
    language: String,
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
        val recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
        onRecognizerCreated(recognizer)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            
            if (language == "auto" || language.isEmpty()) {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
                putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("en-US", "ur-PK", "ar-SA"))
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US", "ur-PK", "ar-SA"))
                if (android.os.Build.VERSION.SDK_INT >= 34) {
                    putExtra("android.speech.extra.EXTRA_ENABLE_LANGUAGE_DETECTION", true)
                    putExtra("android.speech.extra.EXTRA_ENABLE_LANGUAGE_SWITCH", "balanced")
                    putExtra("android.speech.extra.EXTRA_LANGUAGE_DETECTION_ALLOWED_LANGUAGES", arrayOf("en-US", "ur-PK", "ar-SA"))
                }
            } else {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, language)
            }
            
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.applicationContext.packageName)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
            putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2500L)
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
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val ttsManager = viewModel.ttsManager
    val currentlySpeaking by viewModel.activeSpeakingId.collectAsState()
    val isSpeaking = currentlySpeaking == messageId
    val currentSpeed by ttsManager.playbackSpeed.collectAsState()
    val isSaved by viewModel.isSaved("Tutor Response", messageId).collectAsState(initial = false)
    val isLiked by viewModel.isLiked("Tutor Response", messageId).collectAsState(initial = false)
    val isDisliked by viewModel.isDisliked("Tutor Response", messageId).collectAsState(initial = false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Copy Text
            IconButton(
                onClick = {
                    clipboardManager.setText(AnnotatedString(text))
                    Toast.makeText(context, "Copied to clipboard • کاپی ہو گیا", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("copy_button_$messageId")
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy text",
                    tint = EditorialTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Save / Favorite Bookmark
            IconButton(
                onClick = {
                    viewModel.toggleLibrarySave(
                        com.example.data.model.LibraryItem(
                            id = "chat_save_$messageId",
                            contentType = "Tutor Response",
                            contentId = messageId,
                            title = text.take(40).ifBlank { "Tutor Response" },
                            preview = text
                        )
                    )
                },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("save_button_$messageId")
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save to Library",
                    tint = if (isSaved) EditorialPrimary else EditorialTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Like (Thumbs Up)
            IconButton(
                onClick = {
                    viewModel.toggleLike(
                        com.example.data.model.LikedItem(
                            id = "like_chat_$messageId",
                            contentType = "Tutor Response",
                            contentId = messageId,
                            title = text.take(40).ifBlank { "Tutor Response" },
                            preview = text
                        )
                    )
                },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("like_button_$messageId")
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "Like response",
                    tint = if (isLiked) Color(0xFF10B981) else EditorialTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Dislike (Thumbs Down)
            IconButton(
                onClick = {
                    viewModel.toggleDislike(
                        com.example.data.model.DislikedItem(
                            id = "dislike_chat_$messageId",
                            contentType = "Tutor Response",
                            contentId = messageId,
                            title = text.take(40).ifBlank { "Tutor Response" }
                        )
                    )
                },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("dislike_button_$messageId")
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbDown,
                    contentDescription = "Dislike response",
                    tint = if (isDisliked) Color(0xFFEF4444) else EditorialTextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Regenerate
            IconButton(
                onClick = {
                    viewModel.retryLastChatMessage()
                },
                modifier = Modifier
                    .size(28.dp)
                    .testTag("regenerate_button_$messageId")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Regenerate response",
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(16.dp)
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
        color = EditorialContainer,
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
    SelectionContainer {
        if (isUser) {
            val isRtl = isUrduOrArabicText(text)
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White,
                lineHeight = 26.sp,
                textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                style = TextStyle(textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr)
            )
        } else {
            val lines = text.split("\n")

            class LineItem(
                val text: String,
                val isHeader: Boolean,
                val isSpecialExample: Boolean,
                val hasBeforeGap: Boolean
            )

            val lineItems = mutableListOf<LineItem>()
            var pendingGap = false

            for (rawLine in lines) {
                val trimmedRaw = rawLine.trim()
                if (trimmedRaw.isEmpty()) {
                    pendingGap = true
                } else {
                    val isHeader = trimmedRaw.startsWith("#")
                    val cleanLine = if (isHeader) {
                        trimmedRaw.replace(Regex("^#+\\s*"), "").trim()
                    } else {
                        trimmedRaw
                            .replace("**", "")
                            .replace("`", "")
                            .replace(">", "")
                            .trim()
                    }

                    val isSpecialExample = cleanLine.startsWith("Positive:") || cleanLine.startsWith("Negative:") || cleanLine.startsWith("Question:")
                    
                    if (cleanLine.isNotEmpty()) {
                        lineItems.add(LineItem(cleanLine, isHeader, isSpecialExample, pendingGap))
                        pendingGap = false
                    }
                }
            }

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                lineItems.forEachIndexed { index, item ->
                    val isRtl = isUrduOrArabicText(item.text)
                    
                    val topPadding = if (index == 0) {
                        0.dp
                    } else if (item.hasBeforeGap) {
                        if (item.isHeader) 10.dp else 6.dp
                    } else {
                        if (item.isHeader) 6.dp else 2.dp
                    }

                    if (item.isHeader) {
                        Text(
                            text = item.text,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isRtl) EditorialAccentPurple else EditorialPrimary,
                            lineHeight = 24.sp,
                            textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                            style = TextStyle(textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = topPadding)
                        )
                    } else if (item.isSpecialExample) {
                        Surface(
                            color = EditorialContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = topPadding)
                        ) {
                            Text(
                                text = item.text,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = EditorialPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    } else {
                        Text(
                            text = item.text,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = EditorialTextPrimary,
                            lineHeight = 26.sp,
                            textAlign = if (isRtl) TextAlign.Right else TextAlign.Left,
                            style = TextStyle(textDirection = if (isRtl) TextDirection.Rtl else TextDirection.Ltr),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = topPadding)
                        )
                    }
                }
            }
        }
    }
}

fun cleanRecommendationTags(text: String): String {
    return text.replace(Regex("\\[RECOMMEND:\\s*[^]]+\\]"), "").trim()
}

fun parseRecommendationRoute(text: String): String? {
    val match = Regex("\\[RECOMMEND:\\s*([^]]+)\\]").find(text)
    val route = match?.groupValues?.get(1)?.trim() ?: return null
    
    // Strict allowlist validation of in-app navigation routes
    val allowedRoutes = setOf(
        "#/lessons",
        "#/analyzer",
        "#/quizzes",
        "#/revision",
        "#/daily",
        "#/picture-vocabulary",
        "#/pronunciation",
        "#/idioms",
        "#/lisan-journey",
        "#/progress"
    )
    return if (allowedRoutes.contains(route)) route else null
}

@Composable
fun RecommendationActionChip(
    route: String,
    onClick: () -> Unit
) {
    val (label, icon) = when {
        route.contains("lessons") -> "Practice Lessons • اسباق کی مشق" to Icons.Default.PlayArrow
        route.contains("analyzer") -> "Analyze Sentences • جملوں کا تجزیہ" to Icons.Default.Settings
        route.contains("quizzes") -> "Take Grammar Quiz • کوئز شروع کریں" to Icons.Default.CheckCircle
        route.contains("revision") -> "Revision Planner • دہرائی کا منصوبہ" to Icons.Default.PlayArrow
        route.contains("daily") -> "Daily Challenges • روزانہ کے چیلنجز" to Icons.Default.Add
        route.contains("picture-vocabulary") -> "Picture Vocabulary • تصویری ذخیرہ الفاظ" to Icons.Default.Add
        route.contains("pronunciation") -> "Practice Pronunciation • تلفظ کی مشق" to Icons.Default.VolumeUp
        route.contains("idioms") -> "Learn Idioms • محاورے سیکھیں" to Icons.Default.PlayArrow
        route.contains("lisan-journey") -> "View Achievements • کامیابیاں دیکھیں" to Icons.Default.CheckCircle
        route.contains("progress") -> "Progress Dashboard • کارکردگی کا گراف" to Icons.Default.Settings
        else -> "Go to Section • سیکشن پر جائیں" to Icons.Default.PlayArrow
    }

    Surface(
        onClick = onClick,
        color = EditorialAccentPurple.copy(alpha = 0.08f),
        border = BorderStroke(1.2.dp, EditorialAccentPurple),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .defaultMinSize(minHeight = 48.dp)
            .testTag("recommend_chip_${route.replace("/", "_").replace("#", "")}")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EditorialAccentPurple,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialAccentPurple
            )
        }
    }
}





@Composable
fun TutorAudioControls(
    text: String,
    messageId: String,
    viewModel: com.example.ui.viewmodel.LisanViewModel
) {
    val ttsManager = viewModel.ttsManager
    val currentlySpeaking by viewModel.activeSpeakingId.collectAsState()
    val isSpeaking = currentlySpeaking == messageId
    val currentSpeed by ttsManager.playbackSpeed.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (isSpeaking) {
            Surface(
                onClick = { ttsManager.stop() },
                color = Color(0xFFEF4444),
                shape = CircleShape,
                modifier = Modifier
                    .size(24.dp)
                    .testTag("stop_speech_$messageId")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .background(Color.White, RoundedCornerShape(2.dp))
                    )
                }
            }
            
            val is075x = currentSpeed == 0.75f
            Surface(
                onClick = { ttsManager.setPlaybackSpeed(0.75f) },
                color = if (is075x) EditorialPrimary else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (is075x) EditorialPrimary else Color(0xFFCBD5E1)),
                modifier = Modifier.height(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                    Text(
                        text = "0.75×",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (is075x) Color.White else EditorialTextSecondary
                    )
                }
            }

            val is1x = currentSpeed == 1.0f
            Surface(
                onClick = { ttsManager.setPlaybackSpeed(1.0f) },
                color = if (is1x) EditorialPrimary else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (is1x) EditorialPrimary else Color(0xFFCBD5E1)),
                modifier = Modifier.height(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                    Text(
                        text = "1×",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (is1x) Color.White else EditorialTextSecondary
                    )
                }
            }

            val is125x = currentSpeed == 1.25f
            Surface(
                onClick = { ttsManager.setPlaybackSpeed(1.25f) },
                color = if (is125x) EditorialPrimary else Color.Transparent,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (is125x) EditorialPrimary else Color(0xFFCBD5E1)),
                modifier = Modifier.height(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 6.dp)) {
                    Text(
                        text = "1.25×",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (is125x) Color.White else EditorialTextSecondary
                    )
                }
            }
            
            val ttsLoadingStatus by ttsManager.ttsLoadingStatus.collectAsState()
            if (ttsLoadingStatus != null) {
                CircularProgressIndicator(
                    color = EditorialAccentPurple,
                    strokeWidth = 1.5.dp,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = ttsLoadingStatus ?: "",
                    fontSize = 10.sp,
                    color = EditorialAccentPurple,
                    fontStyle = FontStyle.Italic
                )
            }
        } else {
            IconButton(
                onClick = { viewModel.speak(text, messageId) },
                modifier = Modifier
                    .size(24.dp)
                    .testTag("speak_button_$messageId")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "Speak response",
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun savePdfToDownloads(context: android.content.Context, pdfUri: Uri, title: String) {
    try {
        val resolver = context.contentResolver
        val inputStream = resolver.openInputStream(pdfUri)
        if (inputStream == null) {
            android.widget.Toast.makeText(context, "Error reading file", android.widget.Toast.LENGTH_SHORT).show()
            return
        }
        val bytes = inputStream.readBytes()
        inputStream.close()

        val cleanTitle = title.replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val filename = "Lisan_AI_${cleanTitle}_${System.currentTimeMillis()}.pdf"

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_DOWNLOADS + "/LisanAI")
            }
            val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        outputStream.write(bytes)
                        android.widget.Toast.makeText(context, "✅ Saved to Downloads/LisanAI!", android.widget.Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }
        } else {
            val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
            val appDir = java.io.File(downloadsDir, "LisanAI").apply { mkdirs() }
            val file = java.io.File(appDir, filename)
            java.io.FileOutputStream(file).use { outputStream ->
                outputStream.write(bytes)
            }
            android.widget.Toast.makeText(context, "✅ Saved to Downloads/LisanAI!", android.widget.Toast.LENGTH_LONG).show()
            return
        }
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Failed to save PDF: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
    }
}

@Composable
fun PdfDocumentBubbleCard(
    title: String,
    pdfUri: Uri,
    onOpen: (Uri) -> Unit,
    onShare: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val cardShape = RoundedCornerShape(14.dp)
    Surface(
        shape = cardShape,
        color = Color(0xFFFEF2F2),
        border = BorderStroke(1.dp, Color(0xFFFECACA)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("chat_pdf_card")
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFDC2626),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.PictureAsPdf,
                            contentDescription = "PDF Document",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF991B1B)
                    )
                    Text(
                        text = "PDF Document Ready • دستاویز تیار ہے",
                        fontSize = 10.5.sp,
                        color = Color(0xFFB91C1C)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onOpen(pdfUri) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pdf_card_open_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Open • دیکھیں",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                OutlinedButton(
                    onClick = { onShare(pdfUri) },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFDC2626)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pdf_card_share_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Share • شیئر",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        savePdfToDownloads(context, pdfUri, title)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .border(1.dp, Color(0xFFDC2626), RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save PDF to Downloads",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun saveImageToGallery(context: android.content.Context, base64Str: String, promptName: String) {
    try {
        val bytes = android.util.Base64.decode(base64Str, android.util.Base64.DEFAULT)
        val filename = "Lisan_AI_${System.currentTimeMillis()}.png"
        
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/LisanAI")
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            if (uri != null) {
                resolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        outputStream.write(bytes)
                        android.widget.Toast.makeText(context, "✅ Image saved to Gallery!", android.widget.Toast.LENGTH_LONG).show()
                        return
                    }
                }
            }
        } else {
            val picturesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
            val appDir = java.io.File(picturesDir, "LisanAI").apply { mkdirs() }
            val file = java.io.File(appDir, filename)
            java.io.FileOutputStream(file).use { outputStream ->
                outputStream.write(bytes)
            }
            // Scan file to make it show up in gallery
            val mediaScanIntent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = android.net.Uri.fromFile(file)
            context.sendBroadcast(mediaScanIntent)
            android.widget.Toast.makeText(context, "✅ Image saved to Gallery!", android.widget.Toast.LENGTH_LONG).show()
            return
        }
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Failed to save image: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
    }
}

@Composable
fun GeneratedImageBubbleCard(
    base64Data: String,
    prompt: String,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var showFullscreenViewer by remember { mutableStateOf(false) }

    val imageBitmap = remember(base64Data) {
        try {
            val bytes = android.util.Base64.decode(base64Data, android.util.Base64.DEFAULT)
            val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFFAF5FF),
        border = BorderStroke(1.dp, Color(0xFFE9D5FF)),
        modifier = modifier
            .fillMaxWidth()
            .testTag("chat_generated_image_card")
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = null,
                        tint = EditorialAccentPurple,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Generated Image • تصویری خاکہ",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple
                    )
                }

                if (imageBitmap != null) {
                    IconButton(
                        onClick = {
                            saveImageToGallery(context, base64Data, prompt)
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Image",
                            tint = EditorialAccentPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            if (imageBitmap != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { showFullscreenViewer = true }
                ) {
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = prompt,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp)
                            .border(1.dp, Color(0xFFD8B4FE), RoundedCornerShape(10.dp)),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )

                    // Click to enlarge indicator badge
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = "Tap to view • بڑا کریں",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Unable to render generated image.",
                    fontSize = 12.sp,
                    color = Color.Red
                )
            }
        }
    }

    // Fullscreen Dialog Viewer
    if (showFullscreenViewer && imageBitmap != null) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showFullscreenViewer = false },
            properties = androidx.compose.ui.window.DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Surface(
                color = Color.Black,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Central Image
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = prompt,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .align(Alignment.Center),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )

                    // Top Custom Action Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .background(Color.Black.copy(alpha = 0.5f))
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = { showFullscreenViewer = false }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = if (prompt.length > 30) prompt.take(27) + "..." else prompt,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f).padding(horizontal = 12.dp)
                        )

                        IconButton(
                            onClick = {
                                saveImageToGallery(context, base64Data, prompt)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Save to Gallery",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

