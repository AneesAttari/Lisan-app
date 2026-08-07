package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.data.model.Content
import com.example.data.model.Part
import com.example.data.remote.RetrofitClient
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import com.example.ui.viewmodel.VoiceInputLanguage
import com.example.ui.viewmodel.VoiceSessionState
import kotlinx.coroutines.launch

private fun isLineRtl(text: String): Boolean {
    return text.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' || it in '\u08A0'..'\u08FF' || it in '\uFB50'..'\uFDFF' || it in '\uFE70'..'\uFEFF' }
}

@Composable
fun LisanAnimatedOrb(
    state: VoiceSessionState,
    rmsVolume: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_animation")

    // Fluid rotation angle for in-place movement
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "orb_rotation"
    )

    // Gentle breathing scale in place
    val breatheScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_breathe"
    )

    val scale = when (state) {
        VoiceSessionState.LISTENING -> 1.0f + (rmsVolume * 0.16f)
        VoiceSessionState.SPEAKING, VoiceSessionState.REPLYING -> breatheScale * 1.05f
        VoiceSessionState.PROCESSING -> breatheScale * 0.98f
        else -> breatheScale
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .size(230.dp)
            .testTag("lisan_animated_orb_container")
    ) {
        // Outer ambient halo
        val haloAlpha = if (state == VoiceSessionState.LISTENING) 0.25f + (rmsVolume * 0.2f) else 0.15f
        Box(
            modifier = Modifier
                .size(290.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF3B82F6).copy(alpha = haloAlpha),
                            Color(0xFF60A5FA).copy(alpha = haloAlpha * 0.4f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Fluid middle rotation glow layer
        Box(
            modifier = Modifier
                .size(250.dp)
                .rotate(rotationAngle)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color(0xFF2563EB).copy(alpha = 0.35f),
                            Color(0xFF60A5FA).copy(alpha = 0.65f),
                            Color(0xFF93C5FD).copy(alpha = 0.35f),
                            Color(0xFF1D4ED8).copy(alpha = 0.55f),
                            Color(0xFF2563EB).copy(alpha = 0.35f)
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Core living blue sphere with radial gradient
        val orbGradient = Brush.radialGradient(
            colors = when (state) {
                VoiceSessionState.ERROR -> listOf(
                    Color(0xFFFECACA),
                    Color(0xFFEF4444),
                    Color(0xFF991B1B)
                )
                VoiceSessionState.PROCESSING -> listOf(
                    Color(0xFFFFFFFF),
                    Color(0xFFBFDBFE),
                    Color(0xFF3B82F6),
                    Color(0xFF1D4ED8)
                )
                else -> listOf(
                    Color(0xFFFFFFFF), // Bright center core
                    Color(0xFF93C5FD), // Soft sky blue
                    Color(0xFF3B82F6), // Rich indigo blue
                    Color(0xFF1E40AF)  // Deep midnight blue
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(-rotationAngle * 0.5f)
                .background(brush = orbGradient, shape = CircleShape)
                .border(
                    BorderStroke(1.5.dp, Color.White.copy(alpha = 0.65f)),
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun VoiceTutorDialog(
    viewModel: LisanViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val activeLanguage by viewModel.activeLanguage.collectAsState()
    val isStudyModeOn by viewModel.isStudyModeOn.collectAsState()
    val voiceSessionState by viewModel.voiceSessionState.collectAsState()
    val selectedVoiceLanguage by viewModel.activeVoiceLanguage.collectAsState()
    val selectedLanguage = when (selectedVoiceLanguage) {
        VoiceInputLanguage.URDU -> "ur-PK"
        VoiceInputLanguage.ENGLISH -> "en-US"
        else -> "en-US"
    }

    // ISOLATED VOICE TUTOR HISTORY STATE
    var voiceHistory by remember { mutableStateOf<List<Content>>(emptyList()) }
    var userSpeechText by remember { mutableStateOf("") }
    var tutorSpeechText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isMuted by remember { mutableStateOf(false) }
    var manualInputText by remember { mutableStateOf("") }
    var rmsVolume by remember { mutableFloatStateOf(0f) }
    var autoListenEnabled by remember { mutableStateOf(true) }
    var currentSpeed by remember { mutableFloatStateOf(1.0f) }

    var showSettingsMenu by remember { mutableStateOf(false) }
    var showPlusMenu by remember { mutableStateOf(false) }
    var showEndSessionConfirmation by remember { mutableStateOf(false) }
    var showPronunciationDialog by remember { mutableStateOf(false) }
    var showVoiceSelectionDialog by remember { mutableStateOf(false) }
    var showFirstTimeVoiceDialog by remember {
        mutableStateOf(
            !context.getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
                .getBoolean("voice_selection_prompt_shown", false)
        )
    }

    val isSpeechRecognizerAvailable = remember { SpeechRecognizer.isRecognitionAvailable(context) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    val currentlySpeakingText by viewModel.ttsManager.currentlySpeaking.collectAsState()

    LaunchedEffect(currentlySpeakingText) {
        if (currentlySpeakingText == null && voiceSessionState == VoiceSessionState.SPEAKING) {
            viewModel.setVoiceSessionState(VoiceSessionState.READY)
            if (autoListenEnabled && !isMuted) {
                speechRecognizer?.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH))
                viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
            }
        }
    }

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        val welcome = "Hello! Welcome to Lisan AI Tutor. I am ready to practice English and Arabic with you!"
        viewModel.setVoiceSessionState(VoiceSessionState.SPEAKING)
        viewModel.ttsManager.speak(welcome)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            viewModel.setVoiceSessionState(VoiceSessionState.ERROR)
            errorMessage = "Microphone permission is required for speech input."
        }
    }

    val isNetworkAvailable: (Context) -> Boolean = { ctx ->
        val connectivityManager = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectivityManager != null) {
            val activeNetwork = connectivityManager.activeNetwork
            if (activeNetwork != null) {
                val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else false
        } else false
    }

    val stopSpeaking: () -> Unit = {
        viewModel.ttsManager.stop()
        if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
            viewModel.setVoiceSessionState(VoiceSessionState.READY)
        }
    }

    val sendVoiceInput: (String) -> Unit = { text ->
        if (text.isNotBlank()) {
            userSpeechText = text
            manualInputText = ""
            viewModel.setVoiceSessionState(VoiceSessionState.PROCESSING)
            errorMessage = ""

            val userContent = Content(role = "user", parts = listOf(Part(text = text)))
            val updatedHistory = voiceHistory + userContent
            voiceHistory = updatedHistory

            scope.launch {
                try {
                    val profileSummary = viewModel.learnerProfileManager.buildPersonalizedPromptContext()
                    val reply = RetrofitClient.getVoiceTutorResponse(
                        history = updatedHistory,
                        userName = viewModel.userName.value,
                        learnerProfileSummary = profileSummary
                    )
                    
                    val cleanReply = reply.replace(Regex("\\[(pause_short|pause_long|pitch_high|pitch_low|pitch_normal)\\]"), "").replace(Regex("\\s+"), " ").trim()
                    Log.d("VoiceTutorDialog", "Received tutor reply: $cleanReply")
                    tutorSpeechText = cleanReply
                    
                    val modelContent = Content(role = "model", parts = listOf(Part(text = reply)))
                    voiceHistory = updatedHistory + modelContent

                    if (!isMuted) {
                        viewModel.setVoiceSessionState(VoiceSessionState.SPEAKING)
                        Log.d("VoiceTutorDialog", "Telling TTS Manager to speak")
                        viewModel.ttsManager.speak(reply, speed = currentSpeed)
                    } else {
                        Log.d("VoiceTutorDialog", "Muted, not speaking")
                        viewModel.setVoiceSessionState(VoiceSessionState.READY)
                    }
                } catch (e: Exception) {
                    errorMessage = "Could not connect to voice tutor. Please try again."
                    viewModel.setVoiceSessionState(VoiceSessionState.READY)
                }
            }
        }
    }

    var launchGoogleSpeechInput: () -> Unit = {}

    val speechIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = matches?.firstOrNull()
            if (!text.isNullOrBlank()) {
                sendVoiceInput(text)
            } else {
                viewModel.setVoiceSessionState(VoiceSessionState.READY)
                errorMessage = "I could not understand that. Please try again."
            }
        } else {
            viewModel.setVoiceSessionState(VoiceSessionState.READY)
        }
    }

    val initSpeechRecognizer: () -> SpeechRecognizer = {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("VoiceTutor", "Error releasing speech recognizer", e)
        }

        val recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
            }

            override fun onBeginningOfSpeech() {
                if (userSpeechText.isBlank()) {
                    userSpeechText = "Listening..."
                }
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (rmsdB > -2f) {
                    rmsVolume = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                rmsVolume = 0f
                if (voiceSessionState == VoiceSessionState.LISTENING) {
                    viewModel.setVoiceSessionState(VoiceSessionState.PROCESSING)
                }
            }

            override fun onError(error: Int) {
                rmsVolume = 0f
                viewModel.setVoiceSessionState(VoiceSessionState.READY)
                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    errorMessage = "I could not understand that. Please try again."
                } else if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    errorMessage = "No speech was detected. Please try again."
                }
            }

            override fun onResults(results: Bundle?) {
                rmsVolume = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: if (userSpeechText.isNotBlank() && userSpeechText != "Listening...") userSpeechText else null
                if (!text.isNullOrBlank()) {
                    sendVoiceInput(text)
                } else {
                    viewModel.setVoiceSessionState(VoiceSessionState.READY)
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    userSpeechText = text
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })
        speechRecognizer = recognizer
        recognizer
    }

    val startListening: () -> Unit = {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            errorMessage = "Microphone permission is required for speech input."
        } else if (!isNetworkAvailable(context)) {
            viewModel.setVoiceSessionState(VoiceSessionState.ERROR)
            errorMessage = "Voice feature requires an internet connection."
        } else {
            stopSpeaking()
            userSpeechText = ""
            errorMessage = ""
            viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
            rmsVolume = 0f

            val recognizer = speechRecognizer ?: initSpeechRecognizer()
            try {
                recognizer.cancel()
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Error cancelling recognizer", e)
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLanguage)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.applicationContext.packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            try {
                recognizer.startListening(intent)
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Error starting speech recognition", e)
                launchGoogleSpeechInput()
            }
        }
    }

    val stopListening: () -> Unit = {
        speechRecognizer?.stopListening()
        rmsVolume = 0f
        if (voiceSessionState == VoiceSessionState.LISTENING) {
            viewModel.setVoiceSessionState(VoiceSessionState.READY)
        }
    }

    launchGoogleSpeechInput = {
        stopSpeaking()
        stopListening()

        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            errorMessage = "Microphone permission is required."
        } else if (!isNetworkAvailable(context)) {
            viewModel.setVoiceSessionState(VoiceSessionState.ERROR)
            errorMessage = "Voice feature requires an internet connection."
        } else {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Lisan Tutor")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            try {
                errorMessage = ""
                speechIntentLauncher.launch(intent)
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Google speech activity failed", e)
                viewModel.setVoiceSessionState(VoiceSessionState.ERROR)
                errorMessage = "Google speech recognition is unavailable on this device."
            }
        }
    }

    DisposableEffect(Unit) {
        initSpeechRecognizer()
        onDispose {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        }
    }

    if (showEndSessionConfirmation) {
        AlertDialog(
            onDismissRequest = { showEndSessionConfirmation = false },
            title = { Text("End Voice Session?", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = { Text("Are you sure you want to exit the voice conversation session?", fontSize = 14.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        showEndSessionConfirmation = false
                        stopSpeaking()
                        stopListening()
                        onDismissRequest()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                ) {
                    Text("End Session", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndSessionConfirmation = false }) {
                    Text("Cancel", color = EditorialPrimary)
                }
            }
        )
    }

    if (showFirstTimeVoiceDialog) {
        PremiumVoiceSelectionDialog(
            viewModel = viewModel,
            onDismiss = {
                showFirstTimeVoiceDialog = false
                context.getSharedPreferences("lisan_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("voice_selection_prompt_shown", true)
                    .apply()
            }
        )
    }

    if (showVoiceSelectionDialog) {
        PremiumVoiceSelectionDialog(
            viewModel = viewModel,
            onDismiss = { showVoiceSelectionDialog = false }
        )
    }

    Dialog(
        onDismissRequest = {
            stopSpeaking()
            stopListening()
            onDismissRequest()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFFF8FAFC)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // TOP BAR
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            onClick = {
                                stopSpeaking()
                                stopListening()
                                onDismissRequest()
                            },
                            color = Color.White,
                            shape = CircleShape,
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = EditorialPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                    // Center Title Pill with Status
                        Surface(
                            color = Color(0xFFEEF2F6),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier.height(38.dp)
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
                                            color = when(voiceSessionState) {
                                                VoiceSessionState.LISTENING -> Color(0xFFEF4444) // Red
                                                VoiceSessionState.PROCESSING -> Color(0xFFF59E0B) // Amber
                                                VoiceSessionState.SPEAKING, VoiceSessionState.REPLYING -> Color(0xFF6366F1) // Indigo
                                                else -> Color(0xFF10B981) // Emerald
                                            },
                                            shape = CircleShape
                                        )
                                )
                                Text(
                                    text = "Lisan Voice Tutor",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }

                        // Settings 3-dots Button & Voice Button Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Voice Button Pill
                            Surface(
                                onClick = { showVoiceSelectionDialog = true },
                                color = EditorialAccentPurple.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.2f)),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Change Voice",
                                        tint = EditorialAccentPurple,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "Voice",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialAccentPurple
                                    )
                                }
                            }

                            Box {
                                Surface(
                                    onClick = { showSettingsMenu = !showSettingsMenu },
                                    color = Color.White,
                                    shape = CircleShape,
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MoreVert,
                                            contentDescription = "Settings",
                                            tint = EditorialPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showSettingsMenu,
                                    onDismissRequest = { showSettingsMenu = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("🎙 Change AI Voice", fontWeight = FontWeight.Bold, color = EditorialAccentPurple) },
                                        onClick = {
                                            showSettingsMenu = false
                                            showVoiceSelectionDialog = true
                                        }
                                    )
                                    HorizontalDivider(color = Color(0xFFF1F5F9))
                                Text(
                                    text = "Speech Speed",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialTextMuted,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                                DropdownMenuItem(
                                    text = { Text("1.0× (Normal)", fontWeight = if (currentSpeed == 1.0f) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        currentSpeed = 1.0f
                                        viewModel.ttsManager.setSpeechRate(1.0f)
                                        showSettingsMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("1.25× (Fast)", fontWeight = if (currentSpeed == 1.25f) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        currentSpeed = 1.25f
                                        viewModel.ttsManager.setSpeechRate(1.25f)
                                        showSettingsMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("1.5× (Speedy)", fontWeight = if (currentSpeed == 1.5f) FontWeight.Bold else FontWeight.Normal) },
                                    onClick = {
                                        currentSpeed = 1.5f
                                        viewModel.ttsManager.setSpeechRate(1.5f)
                                        showSettingsMenu = false
                                    }
                                )
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                DropdownMenuItem(
                                    text = { Text(if (isMuted) "Unmute Tutor Voice" else "Mute Tutor Voice") },
                                    onClick = {
                                        isMuted = !isMuted
                                        if (isMuted) stopSpeaking()
                                        showSettingsMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("End Session", color = Color.Red) },
                                    onClick = {
                                        showSettingsMenu = false
                                        showEndSessionConfirmation = true
                                    }
                                )
                            }
                        }
                    }
                }

                    // CENTER FLOATING ANIMATED BLUE ORB
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        LisanAnimatedOrb(
                            state = voiceSessionState,
                            rmsVolume = rmsVolume,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )

                        val labelText = when (voiceSessionState) {
                            VoiceSessionState.INACTIVE, VoiceSessionState.READY -> "Tap the microphone or type below to speak"
                            VoiceSessionState.LISTENING -> "Listening to your voice..."
                            VoiceSessionState.PROCESSING -> "Thinking..."
                            VoiceSessionState.SPEAKING, VoiceSessionState.REPLYING -> "Lisan Tutor is speaking..."
                            VoiceSessionState.ERROR -> errorMessage.ifBlank { "Connection issue. Please try again." }
                            else -> "Tap to speak"
                        }

                        Text(
                            text = labelText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (voiceSessionState == VoiceSessionState.ERROR) Color(0xFFEF4444) else EditorialTextMuted,
                            textAlign = TextAlign.Center
                        )
                    }

                    // RECENT TRANSCRIPT PREVIEW
                    if (userSpeechText.isNotBlank() || tutorSpeechText.isNotBlank()) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                if (userSpeechText.isNotBlank() && userSpeechText != "Listening...") {
                                    Text(
                                        text = "You: $userSpeechText",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EditorialPrimary
                                    )
                                }
                                if (tutorSpeechText.isNotBlank()) {
                                    if (userSpeechText.isNotBlank()) Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Tutor: $tutorSpeechText",
                                        fontSize = 13.sp,
                                        color = EditorialTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    // BOTTOM CONTROL BAR
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .imePadding(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            color = Color.White,
                            shape = RoundedCornerShape(32.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            shadowElevation = 4.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Text Field
                                OutlinedTextField(
                                    value = manualInputText,
                                    onValueChange = { manualInputText = it },
                                    placeholder = {
                                        Text(
                                            text = "Ask Lisan Tutor...",
                                            fontSize = 14.sp,
                                            color = EditorialTextMuted
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = EditorialTextMuted,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                    keyboardActions = KeyboardActions(
                                        onSend = {
                                            if (manualInputText.isNotBlank()) {
                                                sendVoiceInput(manualInputText)
                                            }
                                        }
                                    )
                                )

                                // Speaker Mute/Unmute Button
                                Surface(
                                    onClick = {
                                        isMuted = !isMuted
                                        if (isMuted) stopSpeaking()
                                    },
                                    color = if (isMuted) Color(0xFFFEE2E2) else Color(0xFFF1F5F9),
                                    shape = CircleShape,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = if (isMuted) "Unmute" else "Mute",
                                            tint = if (isMuted) Color(0xFFEF4444) else EditorialPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                // End Voice Session (Black Circle with White X)
                                Surface(
                                    onClick = {
                                        stopSpeaking()
                                        stopListening()
                                        onDismissRequest()
                                    },
                                    color = Color.Black,
                                    shape = CircleShape,
                                    modifier = Modifier.size(42.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "End Voice Session",
                                            tint = Color.White,
                                            modifier = Modifier.size(20.dp)
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
}

@Composable
fun PremiumVoiceSelectionDialog(
    viewModel: LisanViewModel,
    onDismiss: () -> Unit
) {
    val settings by viewModel.appSettings.collectAsState()
    val ttsCurrentlySpeaking by viewModel.ttsManager.currentlySpeaking.collectAsState()
    var previewSpeaker by remember { mutableStateOf<String?>(null) }
    val isSpeakingPreview = ttsCurrentlySpeaking != null && previewSpeaker != null

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(24.dp)),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "Choose your AI Voice",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Pick your preferred AI teacher style",
                            fontSize = 12.sp,
                            color = EditorialTextMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = EditorialPrimary)
                    }
                }

                // Speaker selection cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Young Voice Card
                    val isYoungSelected = settings.selectedSpeaker.contains("Young", ignoreCase = true)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateAppSettings { it.copy(selectedSpeaker = "Young Voice") }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isYoungSelected) EditorialAccentPurple.copy(alpha = 0.08f) else Color(0xFFF8FAFC)
                        ),
                        border = BorderStroke(
                            width = if (isYoungSelected) 2.dp else 1.dp,
                            color = if (isYoungSelected) EditorialAccentPurple else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🎙 Young Voice", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EditorialPrimary)
                                    if (isYoungSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EditorialAccentPurple, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text("Friendly AI Teacher", fontSize = 12.sp, color = EditorialAccentPurple, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Energetic, encouraging, and easy to follow.", fontSize = 11.sp, color = EditorialTextMuted)
                            }
                            
                            val isThisPreviewing = isSpeakingPreview && previewSpeaker?.contains("Young", ignoreCase = true) == true
                            IconButton(
                                onClick = {
                                    previewSpeaker = "Young Voice"
                                    val text = when (settings.selectedVoiceLanguage) {
                                        "Urdu" -> "ہیلو! میں آپ کا دوست اور استاد ہوں۔ آئیں اردو کے ذریعے انگریزی گرامر کو بہت آسان طریقے سے سیکھیں۔"
                                        "Arabic" -> "أهلاً بك! أنا معلمك الذكي والصديق. دعنا نتعلم قواعد اللغة الإنجليزية معاً بطريقة ممتعة وسهلة."
                                        else -> "Hey there! I am your friendly AI teacher. Let's learn English grammar together in an energetic and fun way!"
                                    }
                                    if (isThisPreviewing) {
                                        viewModel.stopSpeech()
                                        previewSpeaker = null
                                    } else {
                                        viewModel.ttsManager.applySpeakerPreset("Young Voice")
                                        viewModel.speak(text)
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if (isThisPreviewing) Color(0xFFEF4444) else EditorialAccentPurple
                                ),
                                modifier = Modifier.size(36.dp)
                            ) {
                                if (isThisPreviewing) {
                                    AiVoiceWaves(isAnimating = true, color = Color.White)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    // Mature Voice Card
                    val isMatureSelected = settings.selectedSpeaker.contains("Mature", ignoreCase = true)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                viewModel.updateAppSettings { it.copy(selectedSpeaker = "Mature Voice") }
                            },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMatureSelected) EditorialAccentPurple.copy(alpha = 0.08f) else Color(0xFFF8FAFC)
                        ),
                        border = BorderStroke(
                            width = if (isMatureSelected) 2.dp else 1.dp,
                            color = if (isMatureSelected) EditorialAccentPurple else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text("🎙 Mature Voice", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EditorialPrimary)
                                    if (isMatureSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EditorialAccentPurple, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text("Professional AI Teacher", fontSize = 12.sp, color = EditorialAccentPurple, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Calm, wise, deep, and structured approach.", fontSize = 11.sp, color = EditorialTextMuted)
                            }
                            
                            val isThisPreviewing = isSpeakingPreview && previewSpeaker?.contains("Mature", ignoreCase = true) == true
                            IconButton(
                                onClick = {
                                    previewSpeaker = "Mature Voice"
                                    val text = when (settings.selectedVoiceLanguage) {
                                        "Urdu" -> "السلام علیکم۔ میں آپ کا گرامر کا استاد ہوں۔ ہم پرسکون اور سنجیدہ انداز میں انگریزی گرامر کی تمام باریکیوں کو سمجھیں گے۔"
                                        "Arabic" -> "السلام عليكم. أنا معلمك المحترف لتعلم القواعد. سنمضي في رحلتنا خطوة بخطوة وبكل هدوء لنتقن اللغة الإنجليزية."
                                        else -> "Hello. I am your professional AI instructor. We will take a calm, structured, and deep approach to mastering English grammar."
                                    }
                                    if (isThisPreviewing) {
                                        viewModel.stopSpeech()
                                        previewSpeaker = null
                                    } else {
                                        viewModel.ttsManager.applySpeakerPreset("Mature Voice")
                                        viewModel.speak(text)
                                    }
                                },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if (isThisPreviewing) Color(0xFFEF4444) else EditorialAccentPurple
                                ),
                                modifier = Modifier.size(36.dp)
                            ) {
                                if (isThisPreviewing) {
                                    AiVoiceWaves(isAnimating = true, color = Color.White)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Language selection chips
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Select Voice Language", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            "English" to "🇬🇧 English",
                            "Urdu" to "🇵🇰 Urdu",
                            "Arabic" to "🌙 Arabic"
                        ).forEach { (langKey, display) ->
                            val isLangSelected = settings.selectedVoiceLanguage == langKey
                            FilterChip(
                                selected = isLangSelected,
                                onClick = {
                                    viewModel.updateAppSettings { it.copy(selectedVoiceLanguage = langKey) }
                                    val newLang = when (langKey) {
                                        "Urdu" -> VoiceInputLanguage.URDU
                                        "Arabic" -> VoiceInputLanguage.ARABIC
                                        else -> VoiceInputLanguage.ENGLISH
                                    }
                                    viewModel.setActiveVoiceLanguage(newLang)
                                },
                                label = { Text(display, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EditorialAccentPurple.copy(alpha = 0.15f),
                                    selectedLabelColor = EditorialAccentPurple
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Confirm button
                Button(
                    onClick = {
                        viewModel.stopSpeech()
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Active Voice", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
fun AiVoiceWaves(
    isAnimating: Boolean,
    modifier: Modifier = Modifier,
    color: Color = EditorialAccentPurple
) {
    val infiniteTransition = rememberInfiniteTransition(label = "VoiceWavesDialog")
    
    Row(
        modifier = modifier.height(24.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val count = 5
        for (i in 0 until count) {
            val duration = 400 + i * 150
            val scale by if (isAnimating) {
                infiniteTransition.animateFloat(
                    initialValue = 0.2f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(duration, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "wave_$i"
                )
            } else {
                remember { mutableStateOf(0.2f) }
            }
            
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight(scale)
                    .background(color, RoundedCornerShape(50))
            )
        }
    }
}
