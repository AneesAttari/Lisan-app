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
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import java.util.Locale

enum class VoiceState {
    INACTIVE,
    LISTENING,
    THINKING,
    SPEAKING,
    ERROR
}

@Composable
fun VoiceTutorDialog(
    viewModel: LisanViewModel,
    onDismissRequest: () -> Unit
) {
    val context = LocalContext.current
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val chatError by viewModel.chatError.collectAsState()

    var voiceState by remember { mutableStateOf(VoiceState.INACTIVE) }
    var selectedLanguage by remember { mutableStateOf("ar-SA") }
    var userSpeechText by remember { mutableStateOf("") }
    var tutorSpeechText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isMuted by remember { mutableStateOf(false) }
    var manualInputText by remember { mutableStateOf("") }
    var rmsVolume by remember { mutableFloatStateOf(0f) }
    var silenceDurationMode by remember { mutableStateOf("normal") }
    val isSpeechRecognizerAvailable = remember { SpeechRecognizer.isRecognitionAvailable(context) }
    var hasAttemptedAutoSystemFallback by remember { mutableStateOf(false) }
    var showArabicKeyboard by remember { mutableStateOf(false) }

    // TTS & STT instances
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    
    val currentlySpeakingText by viewModel.ttsManager.currentlySpeaking.collectAsState()

    LaunchedEffect(currentlySpeakingText) {
        if (currentlySpeakingText == null && voiceState == VoiceState.SPEAKING) {
            voiceState = VoiceState.INACTIVE
        }
    }
    
    // Remember the last processed message to prevent repeating old responses
    var lastProcessedMessageIndex by remember { mutableStateOf(-1) }
    var hasStartedAutomatically by remember { mutableStateOf(false) }

    // Audio Permission Launcher
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val openAppSettings: () -> Unit = {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("VoiceTutor", "Unable to open app settings", e)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            voiceState = VoiceState.ERROR
            errorMessage = "Microphone permission is required for speech input. Please allow it in Settings.\n(إذن الميكروفون مطلوب للإدخال الصوتي. يرجى تفعيله في الإعدادات.)"
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

    // Forward declaration of launchGoogleSpeechInput
    var launchGoogleSpeechInput: () -> Unit = {}

    // System Speech Overlay Launcher (Native Voice Dialog)
    val speechIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = matches?.firstOrNull()
            if (!text.isNullOrBlank()) {
                userSpeechText = text
                manualInputText = text
                voiceState = VoiceState.INACTIVE
                errorMessage = ""
            } else {
                voiceState = VoiceState.INACTIVE
                errorMessage = "I could not understand that. Please speak clearly and try again."
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            voiceState = VoiceState.INACTIVE
            errorMessage = "Speech input was cancelled. Please try again."
        } else {
            voiceState = VoiceState.INACTIVE
            errorMessage = "Speech input was cancelled. Please try again."
        }
    }

    // Helper functions for TTS
    val speakOut: (String) -> Unit = { text ->
        if (!isMuted) {
            voiceState = VoiceState.SPEAKING
            tutorSpeechText = text
            viewModel.ttsManager.speak(text)
        }
    }

    val stopSpeaking: () -> Unit = {
        viewModel.ttsManager.stop()
        if (voiceState == VoiceState.SPEAKING) {
            voiceState = VoiceState.INACTIVE
        }
    }

    // Speech Recognizer functions
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
                Log.d("VoiceTutor", "Ready for speech")
                voiceState = VoiceState.LISTENING
            }

            override fun onBeginningOfSpeech() {
                Log.d("VoiceTutor", "Beginning of speech")
                if (userSpeechText.isBlank() || userSpeechText == "أستمع...") {
                    userSpeechText = "أستمع..."
                }
            }

            override fun onRmsChanged(rmsdB: Float) {
                if (rmsdB > -2f) {
                    rmsVolume = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                Log.d("VoiceTutor", "End of speech")
                rmsVolume = 0f
                if (voiceState == VoiceState.LISTENING) {
                    voiceState = VoiceState.THINKING
                }
            }

            override fun onError(error: Int) {
                Log.e("VoiceTutor", "Speech recognizer error: $error")
                rmsVolume = 0f

                // If partial speech was already recognized, set it into manualInputText and userSpeechText
                if (userSpeechText.isNotBlank() && userSpeechText != "أستمع...") {
                    Log.d("VoiceTutor", "Error $error occurred but partial speech was captured: '$userSpeechText'.")
                    manualInputText = userSpeechText
                    voiceState = VoiceState.INACTIVE
                    errorMessage = ""
                    return
                }

                voiceState = VoiceState.INACTIVE

                if (error == SpeechRecognizer.ERROR_NO_MATCH) {
                    if (userSpeechText == "أستمع...") userSpeechText = ""
                    errorMessage = "I could not understand that. Please speak clearly and try again."
                } else if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    if (userSpeechText == "أستمع...") userSpeechText = ""
                    errorMessage = "No speech was detected. Please try again."
                } else if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
                    try { speechRecognizer?.cancel() } catch (e: Exception) {}
                    errorMessage = "Speech recognition is busy. Please wait a moment and try again."
                } else if (error == SpeechRecognizer.ERROR_CLIENT) {
                    try { speechRecognizer?.cancel() } catch (e: Exception) {}
                    errorMessage = "Speech recognition was interrupted. Please try again."
                } else if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                    errorMessage = "Microphone permission is required for speech input."
                } else if (error == SpeechRecognizer.ERROR_NETWORK || error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT) {
                    errorMessage = "Speech recognition needs an internet connection. Check your connection and try again."
                } else {
                    errorMessage = "Speech recognition error (Code #$error). Please try again or use Google speech input (recommended)."
                }
            }

            override fun onResults(results: Bundle?) {
                rmsVolume = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: if (userSpeechText.isNotBlank() && userSpeechText != "أستمع...") userSpeechText else null
                if (!text.isNullOrBlank()) {
                    userSpeechText = text
                    manualInputText = text
                    voiceState = VoiceState.INACTIVE
                    errorMessage = ""
                } else {
                    voiceState = VoiceState.INACTIVE
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull()
                if (!text.isNullOrBlank()) {
                    userSpeechText = text
                    manualInputText = text
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
            voiceState = VoiceState.ERROR
            errorMessage = "Speech recognition needs an internet connection. Check your connection and try again."
        } else {
            stopSpeaking()
            userSpeechText = ""
            errorMessage = ""
            voiceState = VoiceState.LISTENING
            rmsVolume = 0f

            val recognizer = speechRecognizer ?: initSpeechRecognizer()
            try {
                recognizer.cancel()
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Error cancelling previous recognizer session", e)
            }

            val selectedLanguageCode = when (selectedLanguage) {
                "en-US", "en" -> "en-US"
                "ar-SA", "ar" -> "ar-SA"
                "ar-SA", "ar" -> "ar-SA"
                else -> selectedLanguage
            }

            val completeSilenceMillis = when (silenceDurationMode) {
                "short" -> 1800L
                "normal" -> 3500L
                "long" -> 5500L
                "extra_long" -> 8000L
                else -> 3500L
            }
            val possiblyCompleteSilenceMillis = when (silenceDurationMode) {
                "short" -> 1200L
                "normal" -> 2500L
                "long" -> 4000L
                "extra_long" -> 6000L
                else -> 2500L
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguageCode)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLanguageCode)
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.applicationContext.packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, completeSilenceMillis)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, possiblyCompleteSilenceMillis)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 1500L)
            }

            try {
                recognizer.startListening(intent)
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Error starting speech recognition, fallback to Google speech input...", e)
                launchGoogleSpeechInput()
            }
        }
    }

    val stopListening: () -> Unit = {
        speechRecognizer?.stopListening()
        rmsVolume = 0f
        if (voiceState == VoiceState.LISTENING) {
            voiceState = VoiceState.INACTIVE
        }
    }

    launchGoogleSpeechInput = {
        stopSpeaking()
        stopListening()

        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            errorMessage = "Microphone permission is required for speech input."
        } else if (!isNetworkAvailable(context)) {
            voiceState = VoiceState.ERROR
            errorMessage = "Speech recognition needs an internet connection. Check your connection and try again."
        } else {
            val selectedLanguageCode = when (selectedLanguage) {
                "en-US", "en" -> "en-US"
                "ar-SA", "ar" -> "ar-SA"
                "ar-SA", "ar" -> "ar-SA"
                else -> selectedLanguage
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguageCode)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            val packageManager = context.packageManager
            val resolvedActivity = intent.resolveActivity(packageManager)
            val activities = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)

            if (resolvedActivity == null && activities.isEmpty()) {
                voiceState = VoiceState.ERROR
                errorMessage = "Google speech recognition is not available on this phone. Install or update the Google app and Speech Services by Google."
            } else {
                try {
                    errorMessage = ""
                    speechIntentLauncher.launch(intent)
                } catch (e: Exception) {
                    Log.e("VoiceTutor", "Google speech activity failed to launch", e)
                    voiceState = VoiceState.ERROR
                    errorMessage = "Google speech recognition is not available on this phone. Install or update the Google app and Speech Services by Google."
                }
            }
        }
    }

    // Initialize SpeechRecognizer
    DisposableEffect(Unit) {
        // Init SpeechRecognizer using applicationContext for binding reliability
        initSpeechRecognizer()

        onDispose {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        }
    }

    // Monitor Chat History to automatically speak incoming model responses
    LaunchedEffect(chatHistory.size, isChatLoading) {
        if (!isChatLoading && chatHistory.isNotEmpty()) {
            val lastIndex = chatHistory.size - 1
            if (lastIndex > lastProcessedMessageIndex) {
                val lastMsg = chatHistory[lastIndex]
                if (lastMsg.role == "model") {
                    lastProcessedMessageIndex = lastIndex
                    val textToSpeak = lastMsg.parts.firstOrNull()?.text
                    if (!textToSpeak.isNullOrBlank()) {
                        tutorSpeechText = textToSpeak
                        speakOut(textToSpeak)
                    }
                }
            }
        }
    }

    // Pulse animations for visual feedback
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (voiceState == VoiceState.LISTENING) 1.25f else if (voiceState == VoiceState.SPEAKING) 1.15f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = if (voiceState == VoiceState.LISTENING) 0.1f else if (voiceState == VoiceState.SPEAKING) 0.2f else 0.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val livePulseScale = if (voiceState == VoiceState.LISTENING) {
        pulseScale + (rmsVolume * 0.4f)
    } else pulseScale

    // Automatically trigger listening when dialog opens
    LaunchedEffect(hasAudioPermission, isSpeechRecognizerAvailable) {
        if (!isSpeechRecognizerAvailable) {
            voiceState = VoiceState.ERROR
            errorMessage = "خدمة التعرف على الصوت غير متوفرة على هذا الجهاز. يمكنك استخدام الكتابة للتواصل!\n(خدمة التعرف على الصوت غير متوفرة على هذا الجهاز. يمكنك استخدام الكتابة للتواصل!)"
        } else if (hasAudioPermission && !hasStartedAutomatically) {
            hasStartedAutomatically = true
            startListening()
        }
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
            modifier = Modifier
                .fillMaxSize()
                .background(EditorialSurface),
            color = EditorialSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top close bar & Mute control
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                            if (isMuted) {
                                stopSpeaking()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Mute Toggle",
                            tint = EditorialPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Text(
                        text = "المعلم الصوتي لسان",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        fontFamily = FontFamily.Serif
                    )

                    IconButton(
                        onClick = {
                            stopSpeaking()
                            stopListening()
                            onDismissRequest()
                        },
                        modifier = Modifier.testTag("close_voice_tutor_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Dialog",
                            tint = EditorialPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Language selection chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val languages = listOf(
                        Triple("ar-SA", "عربي", "Arabic"),
                        Triple("en-US", "English", "English"),
                        Triple("ar-SA", "العربية", "Arabic")
                    )
                    
                    languages.forEach { (code, nativeName, engName) ->
                        val isSelected = selectedLanguage == code
                        val chipBgColor = if (isSelected) EditorialPrimary else EditorialContainer
                        val chipTextColor = if (isSelected) Color.White else EditorialTextSecondary
                        
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(chipBgColor)
                                .clickable {
                                    if (selectedLanguage != code) {
                                        selectedLanguage = code
                                        // If currently listening, restart with the new language
                                        if (voiceState == VoiceState.LISTENING) {
                                            stopListening()
                                            // Give it a tiny delay to reset the system recognizer before restarting
                                        }
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = nativeName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = chipTextColor
                                )
                                Text(
                                    text = engName,
                                    fontSize = 8.sp,
                                    color = chipTextColor.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }

                // Silence Duration Mode Chips (Speech Pause Tolerance)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "مدة التوقف المؤقت (Speech Pause Tolerance):",
                        fontSize = 11.sp,
                        color = EditorialTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val modes = listOf(
                            Pair("short", "سريع (1.8s)"),
                            Pair("normal", "عادي (3.5s)"),
                            Pair("long", "طويل (5.5s)"),
                            Pair("extra_long", "طويل جداً (8s)")
                        )
                        modes.forEach { (modeCode, label) ->
                            val isSelected = silenceDurationMode == modeCode
                            val chipBgColor = if (isSelected) EditorialAccentPurple else EditorialContainer
                            val chipTextColor = if (isSelected) Color.White else EditorialTextSecondary
                            
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(chipBgColor)
                                    .clickable {
                                        silenceDurationMode = modeCode
                                        if (voiceState == VoiceState.LISTENING) {
                                            stopListening()
                                        }
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = chipTextColor
                                )
                            }
                        }
                    }
                }

                // Interactive Center Visualisation
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer pulsing wave
                    if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) {
                        Box(
                            modifier = Modifier
                                .size(180.dp)
                                .scale(livePulseScale)
                                .clip(CircleShape)
                                .background(
                                    if (voiceState == VoiceState.LISTENING)
                                        EditorialAccentPurple.copy(alpha = pulseAlpha)
                                    else
                                        EditorialPrimary.copy(alpha = pulseAlpha)
                                )
                        )
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .scale(livePulseScale * 0.85f)
                                .clip(CircleShape)
                                .background(
                                    if (voiceState == VoiceState.LISTENING)
                                        EditorialAccentPurple.copy(alpha = pulseAlpha + 0.1f)
                                    else
                                        EditorialPrimary.copy(alpha = pulseAlpha + 0.1f)
                                )
                        )
                    }

                    if (voiceState == VoiceState.THINKING) {
                        RevolvingAppIconCircle(
                            size = 120.dp,
                            iconSize = 48.dp,
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable {
                                    startListening()
                                }
                        )
                    } else {
                        // Interactive core ball
                        val ballColor = when (voiceState) {
                            VoiceState.INACTIVE -> EditorialContainer
                            VoiceState.LISTENING -> EditorialAccentPurple
                            VoiceState.THINKING -> EditorialTextMuted
                            VoiceState.SPEAKING -> EditorialPrimary
                            VoiceState.ERROR -> Color(0xFFB3261E)
                        }

                        val ballText = when (voiceState) {
                            VoiceState.INACTIVE -> "اضغط للتحدث\nTap to Speak"
                            VoiceState.LISTENING -> "أستمع...\nListening..."
                            VoiceState.THINKING -> "أفكر...\nThinking..."
                            VoiceState.SPEAKING -> "أتحدث...\nSpeaking..."
                            VoiceState.ERROR -> "حدث خطأ\nError"
                        }

                        val ballTextColor = if (voiceState == VoiceState.INACTIVE) EditorialTextPrimary else Color.White

                        Surface(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (voiceState == VoiceState.LISTENING) {
                                        stopListening()
                                    } else if (voiceState == VoiceState.SPEAKING) {
                                        stopSpeaking()
                                    } else {
                                        startListening()
                                    }
                                },
                            color = ballColor,
                            shape = CircleShape,
                            shadowElevation = 8.dp
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (voiceState == VoiceState.LISTENING) Icons.Default.Mic else Icons.Default.MicOff,
                                    contentDescription = "Mic Indicator",
                                    tint = ballTextColor.copy(alpha = 0.25f),
                                    modifier = Modifier.fillMaxSize()
                                )
                                Text(
                                    text = ballText,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = ballTextColor,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }

                // Transcription Panel (Live text display)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .padding(bottom = 16.dp),
                    color = EditorialContainer,
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialBorderAccent)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // User message bubble
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "قلت (You said):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple,
                                style = TextStyle(textDirection = TextDirection.Ltr)
                            )
                            Text(
                                text = if (userSpeechText.isNotBlank()) userSpeechText else "ابدأ التحدث...",
                                fontSize = 14.sp,
                                color = if (userSpeechText.isNotBlank()) EditorialTextPrimary else EditorialTextMuted,
                                modifier = Modifier.fillMaxWidth(),
                                style = LocalTextStyle.current.copy(lineHeight = 20.sp, textDirection = TextDirection.ContentOrRtl)
                            )
                        }

                        HorizontalDivider(color = EditorialBorderSoft)

                        // Tutor response bubble
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "رد المعلم (Tutor):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary,
                                style = TextStyle(textDirection = TextDirection.Ltr)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                            ) {
                                if (isChatLoading || voiceState == VoiceState.THINKING) {
                                    GeneratingLightingGlow(
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    )
                                } else {
                                    EditorialMixedText(
                                        text = if (tutorSpeechText.isNotBlank()) tutorSpeechText else "أنا في انتظارك...",
                                        fontSize = 14.sp,
                                        color = if (tutorSpeechText.isNotBlank()) EditorialTextPrimary else EditorialTextMuted,
                                        lineHeight = 20.sp,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Conversation Starters Chips
                val quickPhrases = remember {
                    listOf(
                        "السلام عليكم",
                        "كيف حالك يا معلم؟",
                        "علمني كلمة جديدة",
                        "محادثة تعارف باللغة العربية",
                        "تصحيح جملتي العربية",
                        "ترجمة: How are you today?"
                    )
                }

                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                    Text(
                        text = "عبارات سريعة (Quick Voice Starters):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp),
                        modifier = Modifier.fillMaxWidth().testTag("voice_dialog_quick_phrases_row")
                    ) {
                        items(quickPhrases) { phrase ->
                            SuggestionChip(
                                onClick = {
                                    userSpeechText = phrase
                                    voiceState = VoiceState.THINKING
                                    viewModel.sendChatMessage(phrase)
                                    stopSpeaking()
                                    stopListening()
                                },
                                label = { Text(phrase, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                                colors = SuggestionChipDefaults.suggestionChipColors(
                                    containerColor = EditorialPillBg,
                                    labelColor = EditorialPrimary
                                ),
                                border = BorderStroke(1.dp, EditorialBorderAccent)
                            )
                        }
                    }
                }

                // Keyboard Fallback Input Field (allows typing when mic is not available/working)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "إدخال نصي (Fallback Text Entry):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    TextButton(
                        onClick = { showArabicKeyboard = !showArabicKeyboard },
                        colors = ButtonDefaults.textButtonColors(contentColor = EditorialAccentPurple),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("toggle_arabic_keyboard_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Arabic Keyboard",
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                        Text(
                            text = if (showArabicKeyboard) "إخفاء لوحة المفاتيح (Hide)" else "لوحة المفاتيح العربية (Arabic Keyboard)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                OutlinedTextField(
                    value = manualInputText,
                    onValueChange = { manualInputText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .testTag("voice_dialog_fallback_input"),
                    placeholder = {
                        Text(
                            text = "أو اكتب هنا (سيجيب المعلم صوتياً)...",
                            color = EditorialTextMuted,
                            fontSize = 13.sp,
                            style = TextStyle(textDirection = TextDirection.ContentOrRtl)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (manualInputText.isNotBlank()) {
                                val textToSend = manualInputText
                                userSpeechText = textToSend
                                voiceState = VoiceState.THINKING
                                viewModel.sendChatMessage(textToSend)
                                manualInputText = ""
                                stopSpeaking()
                                stopListening()
                            }
                        }
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (manualInputText.isNotBlank()) {
                                    val textToSend = manualInputText
                                    userSpeechText = textToSend
                                    voiceState = VoiceState.THINKING
                                    viewModel.sendChatMessage(textToSend)
                                    manualInputText = ""
                                    stopSpeaking()
                                    stopListening()
                                }
                            },
                            enabled = manualInputText.isNotBlank(),
                            modifier = Modifier.testTag("voice_dialog_send_fallback_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send Text Fallback",
                                tint = if (manualInputText.isNotBlank()) EditorialPrimary else EditorialTextMuted
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = EditorialContainer,
                        unfocusedContainerColor = EditorialContainer,
                        focusedBorderColor = EditorialPrimary,
                        unfocusedBorderColor = EditorialBorderSoft,
                        focusedTextColor = EditorialTextPrimary,
                        unfocusedTextColor = EditorialTextPrimary
                    ),
                    shape = RoundedCornerShape(24.dp),
                    singleLine = true
                )

                if (showArabicKeyboard) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("arabic_virtual_keyboard"),
                        colors = CardDefaults.cardColors(
                            containerColor = EditorialContainer
                        ),
                        border = BorderStroke(1.dp, EditorialBorderAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val row1 = listOf("ض", "ص", "ث", "ق", "ف", "غ", "ع", "ه", "خ", "ح")
                            val row2 = listOf("ج", "د", "ش", "س", "ي", "ب", "ل", "ا", "ت", "ن")
                            val row3 = listOf("م", "ك", "ط", "ئ", "ء", "ؤ", "ر", "ى", "ة", "و")
                            val row4 = listOf("ز", "ظ", "؟", "،", ".")

                            // Render character rows
                            listOf(row1, row2, row3, row4).forEach { rowKeys ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
                                ) {
                                    rowKeys.forEach { key ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(0.9f)
                                                .background(Color.White, RoundedCornerShape(4.dp))
                                                .border(0.5.dp, EditorialBorder, RoundedCornerShape(4.dp))
                                                .clickable {
                                                    manualInputText += key
                                                },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = key,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EditorialPrimary
                                            )
                                        }
                                    }
                                }
                            }

                            // Control Row: Space, Backspace, Clear
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                // Clear button
                                Button(
                                    onClick = { manualInputText = "" },
                                    modifier = Modifier.weight(1.5f).height(36.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC62828),
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("مسح (Clear)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                // Space button
                                Button(
                                    onClick = { manualInputText += " " },
                                    modifier = Modifier.weight(3f).height(36.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EditorialAccentPurple,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("مسافة (Space)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }

                                // Backspace button
                                Button(
                                    onClick = {
                                        if (manualInputText.isNotEmpty()) {
                                            manualInputText = manualInputText.substring(0, manualInputText.length - 1)
                                        }
                                    },
                                    modifier = Modifier.weight(1.5f).height(36.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EditorialPrimary,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text("حذف (Del)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Error Notification (if any)
                if (voiceState == VoiceState.ERROR || errorMessage.isNotEmpty()) {
                    Surface(
                        color = Color(0xFFFDE8E8),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFF8B4B4)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .testTag("voice_error_banner")
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MicOff,
                                    contentDescription = "Mic Error",
                                    tint = Color(0xFF9B1C1C),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = errorMessage,
                                    color = Color(0xFF9B1C1C),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 16.sp
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!hasAudioPermission || errorMessage.contains("permission", ignoreCase = true) || errorMessage.contains("الميكروفون")) {
                                    Button(
                                        onClick = openAppSettings,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFC81E1E),
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                        shape = RoundedCornerShape(16.dp),
                                        modifier = Modifier.height(30.dp).testTag("open_settings_button")
                                    ) {
                                        Text("الإعدادات (Settings)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Button(
                                    onClick = {
                                        errorMessage = ""
                                        voiceState = VoiceState.INACTIVE
                                        startListening()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EditorialPrimary,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Built-in speech input", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        errorMessage = ""
                                        voiceState = VoiceState.INACTIVE
                                        launchGoogleSpeechInput()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EditorialAccentPurple,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Google speech input (recommended)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // Bottom control actions
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                if (voiceState == VoiceState.LISTENING) {
                                    stopListening()
                                } else {
                                    startListening()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("mic_toggle_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (voiceState == VoiceState.LISTENING) EditorialAccentPurple else EditorialPrimary
                            ),
                            shape = RoundedCornerShape(23.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = if (voiceState == VoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                                    contentDescription = "Mic Toggle"
                                )
                                Text(
                                    text = if (voiceState == VoiceState.LISTENING) "Stop Listening" else "Built-in speech input",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Button(
                            onClick = launchGoogleSpeechInput,
                            modifier = Modifier
                                .weight(1.2f)
                                .height(46.dp)
                                .testTag("system_voice_dialog_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EditorialAccentPurple
                            ),
                            shape = RoundedCornerShape(23.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Google speech input (recommended)"
                                )
                                Text(
                                    text = "Google speech input (recommended)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                isMuted = !isMuted
                                if (isMuted) {
                                    stopSpeaking()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("mute_tutor_voice_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isMuted) Color(0xFFC62828) else EditorialContainer,
                                contentColor = if (isMuted) Color.White else EditorialPrimary
                            ),
                            shape = RoundedCornerShape(22.dp),
                            border = if (!isMuted) androidx.compose.foundation.BorderStroke(1.dp, EditorialBorderAccent) else null
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Mute Toggle",
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = if (isMuted) "إلغاء كتم المعلم (Unmute Tutor)" else "كتم المعلم (Mute Tutor)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            stopSpeaking()
                            stopListening()
                            onDismissRequest()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("end_session_button"),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, EditorialPrimary),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = EditorialPrimary
                        )
                    ) {
                        Text(
                            text = "إنهاء الجلسة (Close Session)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

