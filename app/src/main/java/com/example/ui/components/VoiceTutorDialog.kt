package com.example.ui.components

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
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
    var selectedLanguage by remember { mutableStateOf("ur-PK") }
    var userSpeechText by remember { mutableStateOf("") }
    var tutorSpeechText by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isMuted by remember { mutableStateOf(false) }
    var manualInputText by remember { mutableStateOf("") }
    var rmsVolume by remember { mutableFloatStateOf(0f) }
    var silenceDurationMode by remember { mutableStateOf("normal") }
    val isSpeechRecognizerAvailable = remember { SpeechRecognizer.isRecognitionAvailable(context) }
    var hasAttemptedAutoSystemFallback by remember { mutableStateOf(false) }
    var showUrduKeyboard by remember { mutableStateOf(false) }

    // TTS & STT instances
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var speechRecognizer by remember { mutableStateOf<SpeechRecognizer?>(null) }
    
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

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            voiceState = VoiceState.ERROR
            errorMessage = "آڈیو ریکارڈنگ کی اجازت درکار ہے۔"
        }
    }

    // Forward declaration of launchSystemSpeechDialog to allow use in launcher and onError
    var launchSystemSpeechDialog: () -> Unit = {}

    // System Speech Overlay Launcher (Native Voice Dialog)
    val speechIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = matches?.firstOrNull()
            if (!text.isNullOrBlank()) {
                userSpeechText = text
                voiceState = VoiceState.THINKING
                viewModel.sendChatMessage(text)
                errorMessage = ""
            } else {
                voiceState = VoiceState.INACTIVE
                errorMessage = "لم يتم الحصول على صوت واضح من نافذة جوجل.\n(گوگل وائس پاپ اپ سے آواز ریکارڈ نہیں ہو سکی۔ نیچے لکھ کر کوشش کریں۔)"
            }
        } else if (result.resultCode == Activity.RESULT_CANCELED) {
            voiceState = VoiceState.INACTIVE
            errorMessage = ""
        } else {
            voiceState = VoiceState.INACTIVE
            errorMessage = "تم إغلاق نافذة الصوت. يمكنك استخدام الكلمات السريعة أو الكتابة أدناه.\n(وائس پاپ اپ منسوخ یا بند ہو گیا۔ نیچے ٹائپ کریں یا چپس استعمال کریں۔)"
        }
    }

    // Helper functions for TTS
    val speakOut: (String) -> Unit = { text ->
        if (!isMuted && textToSpeech != null) {
            voiceState = VoiceState.SPEAKING
            tutorSpeechText = text
            
            val cleanText = cleanMarkdownForSpeech(text)
            val locale = detectLanguageForSpeech(cleanText)
            
            try {
                val res = textToSpeech?.setLanguage(locale)
                if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.forLanguageTag("ur"))
                }
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Error setting language to $locale", e)
            }

            textToSpeech?.setPitch(1.0f)
            textToSpeech?.setSpeechRate(1.15f)

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TUTOR_SPEECH")
            }
            textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "TUTOR_SPEECH")
        }
    }

    val stopSpeaking: () -> Unit = {
        textToSpeech?.stop()
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
                if (userSpeechText.isBlank() || userSpeechText == "سن رہا ہے...") {
                    userSpeechText = "سن رہا ہے..."
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

                // If partial speech was already recognized, submit it directly to the AI tutor instead of failing
                if (userSpeechText.isNotBlank() && userSpeechText != "سن رہا ہے...") {
                    Log.d("VoiceTutor", "Error $error occurred but partial speech was captured: '$userSpeechText'. Sending to tutor.")
                    val capturedText = userSpeechText
                    voiceState = VoiceState.THINKING
                    viewModel.sendChatMessage(capturedText)
                    return
                }

                if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    voiceState = VoiceState.INACTIVE
                    if (userSpeechText == "سن رہا ہے...") {
                        userSpeechText = ""
                    }
                    errorMessage = "لم يتم سماع الصوت بشكل واضح. يرجى المحاولة مجدداً، أو اضغط زر گوگل وائس پاپ اپ، أو اكتب أدناه.\n(آواز سنائی نہیں دی۔ دوبارہ کوشش کریں یا نیچے لکھیں۔)"
                } else if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY || error == SpeechRecognizer.ERROR_CLIENT) {
                    voiceState = VoiceState.INACTIVE
                    errorMessage = "الخدمة الصوتية مشغولة حالياً (Busy/Client Error #$error).\nيرجى الضغط على زر الميكروفون مجدداً أو الكتابة أدناه.\n(وائس سروس مصروف یا ری سیٹ درکار ہے۔ مائیک دوبارہ دبائیں یا نیچے لکھیں۔)"
                } else if ((error == 11 || error == 5 || error == 4 || error == 10 || error == 12) && !hasAttemptedAutoSystemFallback) {
                    Log.d("VoiceTutor", "Network/Server error $error encountered. Auto launching system speech dialog.")
                    hasAttemptedAutoSystemFallback = true
                    voiceState = VoiceState.INACTIVE
                    launchSystemSpeechDialog()
                } else {
                    voiceState = VoiceState.ERROR
                    errorMessage = when (error) {
                        SpeechRecognizer.ERROR_AUDIO -> "مشكلة في تسجيل الصوت (Audio Error #1).\n(آڈیو ریکارڈنگ کا مسئلہ)"
                        SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "لم يتم منح إذن الميكروفون (Permission Required #3).\n(مائیک کی اجازت درکار ہے)"
                        SpeechRecognizer.ERROR_NETWORK -> "مشكلة في الاتصال بالإنترنت (Network Error #4).\nتعذر الاتصال بخوادم الصوت من Google. (نیٹ ورک منقطع ہے)"
                        SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "⚠️ تجاوز وقت الشبكة في خدمة الصوت (Network Timeout - Error #5).\nخدمة Google الصوتية لم تستجب في الوقت المحدد (Timed Out).\nيمكنك تجربة نافذة Google الصوتية، أو إدخال النص أدناه وسيقوم المعلم بالرد عليك بصوت عالٍ!\n(گوگل وائس کا نیٹ ورک ٹائم آؤٹ ہو گیا ہے - Error 5۔ آپ سسٹم وائس پاپ اپ یا نیچے ٹائپ کریں، ٹیوٹر بول کر جواب دے گا!)"
                        10 -> "تجاوز حد الطلبات على خدمة الصوت (Error 10: Too Many Requests).\nيرجى الكتابة أدناه بدلاً من ذلك. (بہت زیادہ درخواستیں۔ نیچے ٹائپ کریں)"
                        11 -> "انقطع الاتصال بخادم الصوت (Google Speech Server Disconnected - Error 11).\nخدمة التعرف على الصوت لجوجل غير متوفرة أونلاين على هذا المحاكي.\nيمكنك كتابة رسالتك في المربع أدناه أو النقر على العبارات السريعة وسيقوم المعلم بالرد عليك بصوت عالٍ!\n(گوگل وائس سرور منقطع ہے - Error 11۔ آپ نیچے ٹائپ کریں یا چپس استعمال کریں، ٹیوٹر آپ کو آواز میں جواب دے گا!)"
                        12 -> "⚠️ اللغة المحددة غير مدعومة أوفلاين أو غير مثبتة على هذا الجهاز (Language Not Supported - Error #12).\nيرجى استخدام لوحة المفاتيح، أو العبارات السريعة، أو الاتصال بالإنترنت.\n(منتخبہ زبان اس ڈیوائس پر آف لائن دستیاب نہیں ہے۔ آپ نیچے ٹائپ کریں یا چپس استعمال کریں!)"
                        else -> "خطأ في التعرف على الصوت (Code #$error).\nيرجى إعادة المحاولة أو إدخال النص أدناه. (آواز کی شناخت کی خرابی #$error)"
                    }
                }
            }

            override fun onResults(results: Bundle?) {
                rmsVolume = 0f
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: if (userSpeechText.isNotBlank() && userSpeechText != "سن رہا ہے...") userSpeechText else null
                if (!text.isNullOrBlank()) {
                    userSpeechText = text
                    voiceState = VoiceState.THINKING
                    viewModel.sendChatMessage(text)
                } else {
                    voiceState = VoiceState.INACTIVE
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
        } else {
            stopSpeaking()
            userSpeechText = ""
            errorMessage = ""
            voiceState = VoiceState.LISTENING
            rmsVolume = 0f

            // Cleanly re-initialize SpeechRecognizer on every start button click to ensure fresh microphone stream
            val recognizer = initSpeechRecognizer()

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
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLanguage)
                putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US", "ur-PK", "ar-SA"))
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.applicationContext.packageName)
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, completeSilenceMillis)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, possiblyCompleteSilenceMillis)
                putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS, 2000L)
            }

            try {
                recognizer.startListening(intent)
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Error starting speech recognition", e)
                voiceState = VoiceState.ERROR
                errorMessage = "سپیچ ریکوگنیشن شروع نہیں ہوسکی۔"
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

    launchSystemSpeechDialog = {
        stopSpeaking()
        stopListening()
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLanguage)
            putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("en-US", "ur-PK", "ar-SA"))
            putExtra(RecognizerIntent.EXTRA_PROMPT, "بولیں... (Speak now)")
        }
        try {
            speechIntentLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e("VoiceTutor", "System speech activity not available", e)
            errorMessage = "سسٹم میں وائس ڈائیلاگ دستیاب نہیں ہے۔"
        }
    }

    // Initialize TTS and SpeechRecognizer
    DisposableEffect(Unit) {
        // Init SpeechRecognizer using applicationContext for binding reliability
        initSpeechRecognizer()

        // Init TextToSpeech
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val localeUrdu = Locale.forLanguageTag("ur-PK")
                val result = tts?.setLanguage(localeUrdu)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w("VoiceTutor", "ur-PK not supported by TTS, trying ur")
                    tts?.setLanguage(Locale.forLanguageTag("ur"))
                }
                
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.15f)

                tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        Log.d("VoiceTutor", "TTS started: $utteranceId")
                    }

                    override fun onDone(utteranceId: String?) {
                        Log.d("VoiceTutor", "TTS finished: $utteranceId")
                        voiceState = VoiceState.INACTIVE
                    }

                    override fun onError(utteranceId: String?) {
                        Log.e("VoiceTutor", "TTS error: $utteranceId")
                        voiceState = VoiceState.INACTIVE
                    }
                })
            } else {
                Log.e("VoiceTutor", "TTS initialization failed")
            }
        }
        textToSpeech = tts

        onDispose {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
            tts?.stop()
            tts?.shutdown()
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
            errorMessage = "خدمة التعرف على الصوت غير متوفرة على هذا الجهاز. يمكنك استخدام الكتابة للتواصل!\n(اس ڈیوائس پر آواز کی شناخت کی سروس دستیاب نہیں ہے۔ آپ لکھ کر گفتگو کر سکتے ہیں!)"
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
                        text = "لسان وائس ٹیوٹر",
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
                        Triple("ur-PK", "اردو", "Urdu"),
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
                        text = "وقفہ کی حد (Speech Pause Tolerance):",
                        fontSize = 11.sp,
                        color = EditorialTextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val modes = listOf(
                            Pair("short", "تیز (1.8s)"),
                            Pair("normal", "معمولی (3.5s)"),
                            Pair("long", "طویل (5.5s)"),
                            Pair("extra_long", "بہت طویل (8s)")
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
                            VoiceState.INACTIVE -> "بولنے کے لیے ٹیپ کریں\nTap to Speak"
                            VoiceState.LISTENING -> "میں سن رہا ہوں...\nListening..."
                            VoiceState.THINKING -> "سوچ رہا ہوں...\nThinking..."
                            VoiceState.SPEAKING -> "بول رہا ہوں...\nSpeaking..."
                            VoiceState.ERROR -> "مسئلہ پیش آگیا\nError"
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
                                text = "آپ نے کہا (You said):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple,
                                style = TextStyle(textDirection = TextDirection.Ltr)
                            )
                            Text(
                                text = if (userSpeechText.isNotBlank()) userSpeechText else "بولنا شروع کریں...",
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
                                text = "ٹیوٹر کا جواب (Tutor):",
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.align(Alignment.CenterStart)
                                    ) {
                                        RevolvingAppIconCircle(
                                            size = 28.dp,
                                            iconSize = 12.dp
                                        )
                                        Text(
                                            text = "استاد تقابلی جواب پر غور کر رہے ہیں...",
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Serif,
                                            fontStyle = FontStyle.Italic,
                                            color = EditorialPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                } else {
                                    EditorialMixedText(
                                        text = if (tutorSpeechText.isNotBlank()) tutorSpeechText else "میں آپ کا انتظار کر رہا ہوں...",
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
                        "كيف حالك يا أستاذ؟",
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
                        text = "کتابت (Fallback Text Entry):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    TextButton(
                        onClick = { showUrduKeyboard = !showUrduKeyboard },
                        colors = ButtonDefaults.textButtonColors(contentColor = EditorialAccentPurple),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("toggle_urdu_keyboard_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Urdu Keyboard",
                            modifier = Modifier.size(16.dp).padding(end = 4.dp)
                        )
                        Text(
                            text = if (showUrduKeyboard) "کی بورڈ چھپائیں (Hide)" else "اردو کی بورڈ (Urdu Keyboard)",
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
                            text = "یا یہاں ٹائپ کریں (ٹیوٹر بول کر جواب دے گا)...",
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

                if (showUrduKeyboard) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .testTag("urdu_virtual_keyboard"),
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
                            val row1 = listOf("آ", "ا", "ب", "پ", "ت", "ٹ", "ث", "ج", "چ", "ح", "خ")
                            val row2 = listOf("د", "ڈ", "ذ", "ر", "ڑ", "ز", "ژ", "س", "ش", "ص", "ض")
                            val row3 = listOf("ط", "ظ", "ع", "غ", "ف", "ق", "ک", "گ", "ل", "م", "ن")
                            val row4 = listOf("و", "ہ", "ھ", "ء", "ی", "ے", "ں", "؟", "،", "۔")

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
                                    Text("مٹائیں (Clear)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                    Text("فاصلہ (Space)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                                Button(
                                    onClick = {
                                        errorMessage = ""
                                        voiceState = VoiceState.INACTIVE
                                        startListening()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFC81E1E),
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("دوبارہ مائیک (Retry)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = {
                                        errorMessage = ""
                                        voiceState = VoiceState.INACTIVE
                                        launchSystemSpeechDialog()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EditorialPrimary,
                                        contentColor = Color.White
                                    ),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("گوگل وائس پاپ اپ (Google Pop-up)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                                    text = if (voiceState == VoiceState.LISTENING) "سننا بند کریں" else "مائیک ریکارڈنگ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = launchSystemSpeechDialog,
                            modifier = Modifier
                                .weight(1f)
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
                                    contentDescription = "System Speech Input"
                                )
                                Text(
                                    text = "گوگل وائس پاپ اپ",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
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
                                    text = if (isMuted) "ٹیوٹر کی آواز کھولیں (Unmute Tutor)" else "ٹیوٹر کی آواز بند کریں (Mute Tutor)",
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
                            text = "گفتگو ختم کریں (Close Session)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

fun cleanMarkdownForSpeech(text: String): String {
    return text
        .replace(Regex("\\*\\*|\\*|`|_|#"), "")
        .replace(Regex("💡|📚|✨|🎯|✅|❌|❓|❗"), "")
        .replace(Regex("\\[([^\\]]+)\\]\\([^\\)]+\\)"), "$1")
}

fun detectLanguageForSpeech(text: String): Locale {
    var englishCount = 0
    var rtlCount = 0
    
    for (char in text) {
        val code = char.code
        if (char in 'a'..'z' || char in 'A'..'Z') {
            englishCount++
        } else if ((code in 0x0600..0x06FF) || (code in 0x0750..0x077F) || (code in 0x08A0..0x08FF) || (code in 0xFB50..0xFDFF) || (code in 0xFE70..0xFEFF)) {
            rtlCount++
        }
    }
    
    return if (rtlCount > 0) {
        Locale.forLanguageTag("ur-PK")
    } else {
        Locale.ENGLISH
    }
}

