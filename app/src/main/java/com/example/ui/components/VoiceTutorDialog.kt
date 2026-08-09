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
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
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
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.content.ContextCompat
import com.example.data.model.Content
import com.example.data.model.Part
import com.example.data.remote.RetrofitClient
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import com.example.ui.viewmodel.VoiceInputLanguage
import com.example.ui.viewmodel.VoiceSessionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay

private fun isLineRtl(text: String): Boolean {
    return text.any { it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' || it in '\u08A0'..'\u08FF' || it in '\uFB50'..'\uFDFF' || it in '\uFE70'..'\uFEFF' }
}

@Composable
fun LisanAnimatedOrb(
    state: VoiceSessionState,
    rmsVolume: Float,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
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

    val orbSize = if (isCompact) 110.dp else 210.dp
    val haloSize = if (isCompact) 140.dp else 270.dp

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(scale)
            .size(orbSize)
            .testTag("lisan_animated_orb_container")
    ) {
        // Outer ambient halo
        val haloAlpha = if (state == VoiceSessionState.LISTENING) 0.25f + (rmsVolume * 0.2f) else 0.15f
        Box(
            modifier = Modifier
                .size(haloSize)
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

enum class ConnectionStateStatus { IDLE, CONNECTING, CONNECTED, ERROR }

private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val activeNetwork = connectivityManager?.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

@Composable
fun FlutterGlowingSphere(
    pulseScale: Float,
    connectionStatus: ConnectionStateStatus,
    rmsVolume: Float,
    voiceSessionState: VoiceSessionState,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (connectionStatus == ConnectionStateStatus.ERROR) {
        listOf(
            0.2f to Color(0xFFFECACA),
            0.7f to Color(0xFFEF4444),
            1.0f to Color(0xFF991B1B)
        )
    } else {
        listOf(
            0.2f to Color(0xFF6B9CFF),
            0.7f to Color(0xFF1E52F6),
            1.0f to Color(0xFF0030BF)
        )
    }

    val finalScale = if (connectionStatus == ConnectionStateStatus.CONNECTED) {
        when (voiceSessionState) {
            VoiceSessionState.LISTENING -> pulseScale + (rmsVolume * 0.15f)
            VoiceSessionState.SPEAKING, VoiceSessionState.REPLYING -> pulseScale * 1.05f
            else -> pulseScale
        }
    } else {
        pulseScale
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(finalScale)
            .size(180.dp)
    ) {
        Box(
            modifier = Modifier
                .size(220.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E52F6).copy(alpha = 0.45f),
                            Color(0xFF1E52F6).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(180.dp)
                .background(
                    brush = Brush.radialGradient(
                        colorStops = gradientColors.toTypedArray()
                    ),
                    shape = CircleShape
                )
                .border(
                    BorderStroke(1.5.dp, Color.White.copy(alpha = 0.6f)),
                    shape = CircleShape
                )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
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

    var connectionStatus by remember { mutableStateOf(ConnectionStateStatus.CONNECTING) }
    var connectionErrorMessage by remember { mutableStateOf("") }

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
    var listeningTimeoutJob by remember { mutableStateOf<Job?>(null) }

    val reconnectVoiceAssistant: () -> Unit = {
        scope.launch {
            connectionStatus = ConnectionStateStatus.CONNECTING
            connectionErrorMessage = ""
            viewModel.setVoiceSessionState(VoiceSessionState.PROCESSING)
            kotlinx.coroutines.delay(2000)
            if (!isNetworkAvailable(context)) {
                connectionStatus = ConnectionStateStatus.ERROR
                connectionErrorMessage = "Connection timed out. Please retry."
                viewModel.setVoiceSessionState(VoiceSessionState.ERROR)
            } else {
                connectionStatus = ConnectionStateStatus.CONNECTED
                viewModel.setVoiceSessionState(VoiceSessionState.READY)
                val welcome = "السلام عليكم ورحمة الله وبركاته\nWelcome back to Lisan Grammar. I am ready to help you learn English grammar by comparing it with Arabic grammar."
                tutorSpeechText = welcome
                viewModel.setVoiceSessionState(VoiceSessionState.SPEAKING)
                viewModel.ttsManager.speak(welcome)
            }
        }
    }

    var showSettingsMenu by remember { mutableStateOf(false) }
    var showPlusMenu by remember { mutableStateOf(false) }
    var showEndSessionConfirmation by remember { mutableStateOf(false) }
    var showPronunciationDialog by remember { mutableStateOf(false) }
    var showVoiceSelectionDialog by remember { mutableStateOf(false) }
    var showFirstTimeVoiceDialog by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val isSpeechRecognizerAvailable = remember { SpeechRecognizer.isRecognitionAvailable(context) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    val currentlySpeakingText by viewModel.ttsManager.currentlySpeaking.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
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

    val handleNoSpeechDetected: () -> Unit = {
        listeningTimeoutJob?.cancel()
        rmsVolume = 0f
        userSpeechText = ""
        val fallbackText = "I couldn't hear your voice. Please try speaking again."
        errorMessage = fallbackText
        tutorSpeechText = fallbackText
        if (!isMuted) {
            viewModel.setVoiceSessionState(VoiceSessionState.SPEAKING)
            viewModel.ttsManager.speak(fallbackText, speed = currentSpeed)
        } else {
            viewModel.setVoiceSessionState(VoiceSessionState.READY)
        }
    }

    val sendVoiceInput: (String) -> Unit = { text ->
        val trimmed = text.trim()
        if (trimmed.isBlank() || trimmed == "Listening...") {
            handleNoSpeechDetected()
        } else {
            userSpeechText = trimmed
            manualInputText = ""
            viewModel.setVoiceSessionState(VoiceSessionState.PROCESSING)
            errorMessage = ""

            val userContent = Content(role = "user", parts = listOf(Part(text = trimmed)))
            val updatedHistory = voiceHistory + userContent
            voiceHistory = updatedHistory

            scope.launch(Dispatchers.IO) {
                try {
                    val profileSummary = if (isStudyModeOn) {
                        viewModel.buildPersonalizedStudyContext()
                    } else {
                        viewModel.learnerProfileManager.buildPersonalizedPromptContext()
                    }
                    val reply = RetrofitClient.getVoiceTutorResponse(
                        history = updatedHistory,
                        userName = viewModel.userName.value,
                        learnerProfileSummary = profileSummary,
                        isStudyModeOn = isStudyModeOn
                    )
                    
                    val cleanReply = reply.replace(Regex("\\[(pause_short|pause_long|pitch_high|pitch_low|pitch_normal)\\]"), "").replace(Regex("\\s+"), " ").trim()
                    Log.d("VoiceTutorDialog", "Received tutor reply: $cleanReply")
                    
                    withContext(Dispatchers.Main) {
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
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        handleNoSpeechDetected()
                    }
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
                handleNoSpeechDetected()
            }
        } else {
            handleNoSpeechDetected()
        }
    }

    val initSpeechRecognizer: () -> SpeechRecognizer? = {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("VoiceTutor", "Error releasing speech recognizer", e)
        }

        var recognizer: SpeechRecognizer? = null
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
                    }

                    override fun onBeginningOfSpeech() {
                        listeningTimeoutJob?.cancel() // Speech started, cancel timeout
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
                        listeningTimeoutJob?.cancel()
                        rmsVolume = 0f
                        if (voiceSessionState == VoiceSessionState.LISTENING) {
                            viewModel.setVoiceSessionState(VoiceSessionState.PROCESSING)
                        }
                    }

                    override fun onError(error: Int) {
                        Log.e("VoiceTutor", "Speech recognizer error: $error")
                        val errorDescription = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check your microphone."
                            SpeechRecognizer.ERROR_CLIENT -> "Client-side error. Google Speech Services might be updating or busy."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                            SpeechRecognizer.ERROR_NETWORK -> "Network error. Please check your internet connection."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout. Please check your connection."
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognition service is busy. Please try again."
                            SpeechRecognizer.ERROR_SERVER -> "Server error. Please try again later."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected. Please speak closer to your microphone."
                            else -> "Speech recognition error ($error). Please try again."
                        }
                        
                        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            handleNoSpeechDetected()
                        } else {
                            Log.w("VoiceTutor", "Technical speech error ($errorDescription), falling back to system speech input")
                            listeningTimeoutJob?.cancel()
                            rmsVolume = 0f
                            launchGoogleSpeechInput()
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        listeningTimeoutJob?.cancel()
                        rmsVolume = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull() ?: if (userSpeechText.isNotBlank() && userSpeechText != "Listening...") userSpeechText else null
                        if (!text.isNullOrBlank()) {
                            sendVoiceInput(text)
                        } else {
                            handleNoSpeechDetected()
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
            } else {
                Log.e("VoiceTutor", "Speech recognizer is not available on this device.")
            }
        } catch (e: Exception) {
            Log.e("VoiceTutor", "Error initializing speech recognizer", e)
        }
        speechRecognizer = recognizer
        recognizer
    }

    val stopListening: () -> Unit = {
        listeningTimeoutJob?.cancel()
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.e("VoiceTutor", "Error in stopListening", e)
        }
        rmsVolume = 0f
        if (voiceSessionState == VoiceSessionState.LISTENING) {
            viewModel.setVoiceSessionState(VoiceSessionState.READY)
        }
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

            // Start an 8-second timeout. If speech doesn't begin/result, trigger no-speech handler.
            listeningTimeoutJob?.cancel()
            listeningTimeoutJob = scope.launch(Dispatchers.Main) {
                delay(8000) // 8 seconds timeout
                if (voiceSessionState == VoiceSessionState.LISTENING) {
                    handleNoSpeechDetected()
                }
            }

            try {
                val recognizer = speechRecognizer ?: initSpeechRecognizer()
                if (recognizer == null) {
                    listeningTimeoutJob?.cancel()
                    viewModel.setVoiceSessionState(VoiceSessionState.READY)
                    errorMessage = "Speech recognition is not fully supported on this device. Please use keyboard typing instead."
                } else {
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
                        listeningTimeoutJob?.cancel()
                        launchGoogleSpeechInput()
                    }
                }
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Microphone or Speech Recognition setup failed", e)
                listeningTimeoutJob?.cancel()
                viewModel.setVoiceSessionState(VoiceSessionState.READY)
                errorMessage = "Failed to start microphone. Please try again."
            }
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

    var wasSpeaking by remember { mutableStateOf(false) }

    LaunchedEffect(currentlySpeakingText) {
        if (currentlySpeakingText != null) {
            wasSpeaking = true
        } else if (wasSpeaking) {
            wasSpeaking = false
            if (voiceSessionState == VoiceSessionState.SPEAKING) {
                viewModel.setVoiceSessionState(VoiceSessionState.READY)
                if (autoListenEnabled && !isMuted && hasAudioPermission) {
                    startListening()
                }
            }
        }
    }

    LaunchedEffect(hasAudioPermission) {
        if (hasAudioPermission && voiceSessionState == VoiceSessionState.READY && connectionStatus == ConnectionStateStatus.CONNECTED && !wasSpeaking) {
            startListening()
        }
    }

    LaunchedEffect(Unit) {
        connectionStatus = ConnectionStateStatus.CONNECTING
        kotlinx.coroutines.delay(2000)
        
        if (!isNetworkAvailable(context)) {
            connectionStatus = ConnectionStateStatus.ERROR
            connectionErrorMessage = "Connection timed out. Please retry."
            viewModel.setVoiceSessionState(VoiceSessionState.ERROR)
        } else {
            connectionStatus = ConnectionStateStatus.CONNECTED
            if (!hasAudioPermission) {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
            val welcome = "السلام عليكم ورحمة الله وبركاته\nWelcome to Lisan Grammar. I will help you learn English grammar by comparing it with Arabic grammar."
            tutorSpeechText = welcome
            viewModel.setVoiceSessionState(VoiceSessionState.SPEAKING)
            viewModel.ttsManager.speak(welcome)
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

    if (showSettingsMenu) {
        VoiceTutorSettingsDialog(
            viewModel = viewModel,
            onDismiss = { showSettingsMenu = false }
        )
    }

    BackHandler(enabled = true) {
        stopSpeaking()
        stopListening()
        onDismissRequest()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_animation")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Dialog(
        onDismissRequest = {
            stopSpeaking()
            stopListening()
            onDismissRequest()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        val view = LocalView.current
        val dialogWindowProvider = view.parent as? DialogWindowProvider
        SideEffect {
            dialogWindowProvider?.window?.let { window ->
                window.setLayout(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // APP BAR (FLUTTER SPEC)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(Color.White)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            stopSpeaking()
                            stopListening()
                            onDismissRequest()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF3F51B5)
                        )
                    }

                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    color = when (connectionStatus) {
                                        ConnectionStateStatus.CONNECTING -> Color(0xFFFFA500) // Orange
                                        ConnectionStateStatus.CONNECTED -> Color(0xFF4CAF50) // Green
                                        ConnectionStateStatus.ERROR -> Color(0xFFF44336) // Red
                                        ConnectionStateStatus.IDLE -> Color(0xFF9E9E9E) // Grey
                                    },
                                    shape = CircleShape
                                )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Lisan Voice Tutor",
                            style = TextStyle(
                                color = Color(0xFF3F51B5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }

                    IconButton(
                        onClick = { showVoiceSelectionDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Change Voice",
                            tint = Color(0xFF3F51B5)
                        )
                    }

                    IconButton(
                        onClick = { showSettingsMenu = true },
                        modifier = Modifier.testTag("voice_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Tutor Settings",
                            tint = Color(0xFF3F51B5)
                        )
                    }


                }
                HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

                // BODY SECTION
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Glowing Pulsing Sphere (Flutter Spec Radial Gradient)
                    FlutterGlowingSphere(
                        pulseScale = pulseScale,
                        connectionStatus = connectionStatus,
                        rmsVolume = rmsVolume,
                        voiceSessionState = voiceSessionState,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    // Status message
                    val labelText = when (connectionStatus) {
                        ConnectionStateStatus.CONNECTING -> "Connecting to Lisan AI..."
                        ConnectionStateStatus.ERROR -> connectionErrorMessage.ifBlank { "Connection timed out. Please retry." }
                        ConnectionStateStatus.IDLE -> "Idle"
                        ConnectionStateStatus.CONNECTED -> {
                            when (voiceSessionState) {
                                VoiceSessionState.SPEAKING, VoiceSessionState.REPLYING -> "Lisan Tutor is speaking..."
                                VoiceSessionState.LISTENING -> "Listening to your voice..."
                                VoiceSessionState.PROCESSING -> "Thinking..."
                                VoiceSessionState.ERROR -> errorMessage.ifBlank { "Connection timed out. Please retry." }
                                else -> "Lisan AI Voice Assistant"
                            }
                        }
                    }

                    Text(
                        text = labelText,
                        style = TextStyle(
                            color = if (connectionStatus == ConnectionStateStatus.ERROR || voiceSessionState == VoiceSessionState.ERROR) Color(0xFFEF4444) else Color(0xFF757575),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W500
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    // Retry Button if ConnectionStateStatus.ERROR
                    if (connectionStatus == ConnectionStateStatus.ERROR) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = reconnectVoiceAssistant,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF3F51B5), // Indigo
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Retry Connection", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Recent transcription preview (only when connected)
                    if (connectionStatus == ConnectionStateStatus.CONNECTED && (userSpeechText.isNotBlank() || tutorSpeechText.isNotBlank())) {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .padding(top = 24.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(14.dp)
                            ) {
                                if (userSpeechText.isNotBlank() && userSpeechText != "Listening...") {
                                    Text(
                                        text = "You: $userSpeechText",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = EditorialPrimary
                                    )
                                }
                                if (tutorSpeechText.isNotBlank()) {
                                    if (userSpeechText.isNotBlank() && userSpeechText != "Listening...") Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Tutor: $tutorSpeechText",
                                        fontSize = 13.sp,
                                        color = EditorialTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // BOTTOM CHAT INPUT PREVIEW BAR (FLUTTER SPEC)
                HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    // Rounded white Text Field
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = manualInputText,
                            onValueChange = { manualInputText = it },
                            placeholder = {
                                Text(
                                    text = "Type a message...",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            },
                            textStyle = TextStyle(
                                fontSize = 15.sp,
                                color = Color.Black,
                                textDirection = if (isLineRtl(manualInputText)) TextDirection.Rtl else TextDirection.Ltr
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                disabledBorderColor = Color.Transparent
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 40.dp),
                            singleLine = true,
                            enabled = (connectionStatus == ConnectionStateStatus.CONNECTED),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(
                                onSend = {
                                    if (manualInputText.isNotBlank()) {
                                        val textToSend = manualInputText
                                        manualInputText = ""
                                        sendVoiceInput(textToSend)
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }
                                }
                            )
                        )
                    }

                    // Mic toggle button
                    IconButton(
                        onClick = {
                            if (connectionStatus == ConnectionStateStatus.CONNECTED) {
                                if (voiceSessionState == VoiceSessionState.LISTENING) {
                                    stopListening()
                                } else {
                                    startListening()
                                }
                            }
                        },
                        enabled = (connectionStatus == ConnectionStateStatus.CONNECTED)
                    ) {
                        Icon(
                            imageVector = if (voiceSessionState == VoiceSessionState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = if (voiceSessionState == VoiceSessionState.LISTENING) Color.Red else Color(0xFF3F51B5)
                        )
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

@Composable
fun VoiceTutorSettingsDialog(
    viewModel: LisanViewModel,
    onDismiss: () -> Unit
) {
    val isStudyModeOn by viewModel.isStudyModeOn.collectAsState()
    val isPersonalizationEnabled by viewModel.isStudyPersonalizationEnabled.collectAsState()

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
                            "Voice Tutor Settings",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary,
                            fontFamily = FontFamily.Serif
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "Customize your AI learning experience",
                            fontSize = 12.sp,
                            color = EditorialTextMuted
                        )
                    }
                    IconButton(onClick = onDismiss, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = EditorialPrimary)
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // 1. Study Mode Toggle Row
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
                                fontSize = 15.sp,
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
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Study Mode gives your tutor a focused teaching style and can use your selected learning progress to personalize practice.",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 16.sp
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
                        modifier = Modifier.testTag("voice_study_mode_switch")
                    )
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // 2. Personalization Toggle Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Personalize using my progress",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Uses your real stats, streaks, lessons completed, and weak areas to customize lessons. If disabled, tutor remains strict but uses only active chat text.",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 16.sp
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
                        modifier = Modifier.testTag("voice_personalization_switch")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
