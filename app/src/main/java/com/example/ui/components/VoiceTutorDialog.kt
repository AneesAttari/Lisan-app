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
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.Offset
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
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.example.data.model.Content
import com.example.data.model.Part
import com.example.data.remote.RetrofitClient
import androidx.compose.ui.graphics.drawscope.clipPath
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

private data class OrbPalette(
    val color1: Color,
    val color2: Color,
    val color3: Color,
    val color4: Color
)

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

@Composable
fun FlutterGlowingSphere(
    pulseScale: Float,
    connectionStatus: ConnectionStateStatus,
    rmsVolume: Float,
    voiceSessionState: VoiceSessionState,
    isIntenseMode: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "living_orb_anim")
    
    // Smooth monotonic time for animation shifts
    val rawTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat() * 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "raw_time"
    )

    val speedMultiplier = if (isIntenseMode) 0.85f else 0.35f
    val animatedSpeed by animateFloatAsState(
        targetValue = speedMultiplier,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "animated_speed"
    )

    val (skyTopColor, skyBottomColor, cloudShadowColor, cloudHighlightColor) = when {
        connectionStatus == ConnectionStateStatus.ERROR || voiceSessionState == VoiceSessionState.ERROR -> {
            Quad(
                Color(0xFF991B1B), // Dark crimson red sky top
                Color(0xFFDC2626), // Lighter red horizon
                Color(0xFF7F1D1D), // Dark red shadow
                Color(0xFFFCA5A5)  // Soft red cloud caps
            )
        }
        voiceSessionState == VoiceSessionState.PROCESSING -> {
            Quad(
                Color(0xFF5B21B6), // Deep violet sky top
                Color(0xFF7C3AED), // Lavender horizon
                Color(0xFF4C1D95), // Cosmic violet shadow
                Color(0xFFDDD6FE)  // Bright lilac cloud caps
            )
        }
        else -> {
            Quad(
                Color(0xFF3B82F6), // Rich sky blue top
                Color(0xFF7CB0FF), // Soft horizon blue
                Color(0xFFB0CDFC), // Periwinkle cloud underside shadow
                Color(0xFFFFFFFF)  // Pure cloud white top
            )
        }
    }

    val animatedSkyTop by animateColorAsState(targetValue = skyTopColor, animationSpec = tween(durationMillis = 800), label = "st")
    val animatedSkyBottom by animateColorAsState(targetValue = skyBottomColor, animationSpec = tween(durationMillis = 800), label = "sb")
    val animatedShadow by animateColorAsState(targetValue = cloudShadowColor, animationSpec = tween(durationMillis = 800), label = "sh")
    val animatedHighlight by animateColorAsState(targetValue = cloudHighlightColor, animationSpec = tween(durationMillis = 800), label = "hl")

    val baseScale = when (voiceSessionState) {
        VoiceSessionState.LISTENING -> 1.03f + (rmsVolume * 0.15f)
        VoiceSessionState.SPEAKING, VoiceSessionState.REPLYING -> 1.04f
        VoiceSessionState.PROCESSING -> 0.98f
        else -> 0.97f
    }

    val speechEnvelope = if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
        (0.45f * kotlin.math.sin(rawTime * 2.8f) + 
         0.35f * kotlin.math.sin(rawTime * 5.2f) + 
         0.20f * kotlin.math.cos(rawTime * 7.6f)).coerceIn(0f, 1f)
    } else {
        0f
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .scale(baseScale * pulseScale)
            .size(240.dp)
            .testTag("lisan_voice_orb_container")
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val width = size.width
            val height = size.height
            val centerX = width / 2f
            val centerY = height / 2f
            val R = kotlin.math.min(width, height) / 2.2f // Orb radius

            // 1. Outer Atmospheric Aura Glow
            val auraRadius = R * 1.35f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        animatedSkyTop.copy(alpha = if (voiceSessionState == VoiceSessionState.LISTENING) 0.35f + (rmsVolume * 0.25f) else 0.20f),
                        animatedSkyBottom.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    center = Offset(centerX, centerY),
                    radius = auraRadius
                ),
                radius = auraRadius,
                center = Offset(centerX, centerY)
            )

            // 2. Circular Sphere Clipping Mask
            val clipPath = Path().apply {
                addOval(
                    androidx.compose.ui.geometry.Rect(
                        centerX - R,
                        centerY - R,
                        centerX + R,
                        centerY + R
                    )
                )
            }

            clipPath(clipPath) {
                // 3. Clear Sky Blue Background Gradient
                drawCircle(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            animatedSkyTop,
                            animatedSkyBottom
                        ),
                        startY = centerY - R,
                        endY = centerY + R
                    ),
                    radius = R,
                    center = Offset(centerX, centerY)
                )

                // 4. Calculate Motion Offsets and Steam Flow (Driven by left-to-right streaming)
                val timeFactor = rawTime * animatedSpeed
                val flowX = (timeFactor * 35f) % (R * 2.4f) - (R * 1.2f)
                val secondaryFlowX = ((timeFactor * 22f) + R * 0.7f) % (R * 2.4f) - (R * 1.2f)

                val pulseBoost = 1.0f + (rmsVolume * 0.15f) + (speechEnvelope * 0.12f)

                // 5. Lower Place Steam Path (Strictly bounded to lower half, wavy top edge)
                val steamTopY = centerY - (R * 0.02f)
                val steamPath = Path().apply {
                    moveTo(centerX - R - 10f, centerY + R + 10f)
                    lineTo(centerX - R - 10f, steamTopY)
                    
                    val steps = 30
                    val stepWidth = (R * 2.2f) / steps
                    for (i in 0..steps) {
                        val x = (centerX - R * 1.1f) + i * stepWidth
                        val wave1 = kotlin.math.sin((i.toFloat() / steps) * 3.5f * Math.PI.toFloat() + timeFactor * 1.8f) * (R * 0.06f)
                        val wave2 = kotlin.math.cos((i.toFloat() / steps) * 2.0f * Math.PI.toFloat() - timeFactor * 1.2f) * (R * 0.04f)
                        val y = steamTopY + wave1 + wave2
                        lineTo(x, y)
                    }
                    
                    lineTo(centerX + R + 10f, steamTopY)
                    lineTo(centerX + R + 10f, centerY + R + 10f)
                    close()
                }

                // Render dense steam filled inside the lower region path
                clipPath(steamPath) {
                    // Base dense white steam filling the bottom
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                animatedHighlight.copy(alpha = 0.82f),
                                animatedHighlight.copy(alpha = 0.96f),
                                animatedHighlight
                            ),
                            startY = steamTopY,
                            endY = centerY + R
                        ),
                        topLeft = Offset(centerX - R, steamTopY - R * 0.1f),
                        size = androidx.compose.ui.geometry.Size(R * 2f, R * 1.2f)
                    )

                    // Stream 1: Dense steam origin coming from the left
                    val originX = centerX - R * 0.5f + flowX * 0.3f
                    val originY = centerY + R * 0.35f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                animatedHighlight,
                                animatedHighlight.copy(alpha = 0.90f),
                                animatedShadow.copy(alpha = 0.30f),
                                Color.Transparent
                            ),
                            center = Offset(originX, originY),
                            radius = R * 0.75f * pulseBoost
                        ),
                        radius = R * 0.75f * pulseBoost,
                        center = Offset(originX, originY)
                    )

                    // Stream 2: Horizontal vapor roll streaming across the lower middle
                    val midStreamX = centerX + flowX
                    val midStreamY = centerY + R * 0.25f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                animatedHighlight,
                                animatedHighlight.copy(alpha = 0.85f),
                                animatedShadow.copy(alpha = 0.20f),
                                Color.Transparent
                            ),
                            center = Offset(midStreamX, midStreamY),
                            radius = R * 0.65f * pulseBoost
                        ),
                        radius = R * 0.65f * pulseBoost,
                        center = Offset(midStreamX, midStreamY)
                    )

                    // Stream 3: Secondary trailing steam wave from left
                    val tailStreamX = centerX + secondaryFlowX
                    val tailStreamY = centerY + R * 0.45f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                animatedHighlight,
                                animatedHighlight.copy(alpha = 0.88f),
                                animatedShadow.copy(alpha = 0.25f),
                                Color.Transparent
                            ),
                            center = Offset(tailStreamX, tailStreamY),
                            radius = R * 0.58f * pulseBoost
                        ),
                        radius = R * 0.58f * pulseBoost,
                        center = Offset(tailStreamX, tailStreamY)
                    )
                }

                // 6. Soft mist feather blend along the steam horizon line
                drawPath(
                    path = steamPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            animatedHighlight.copy(alpha = 0.60f),
                            Color.Transparent
                        ),
                        startY = steamTopY - R * 0.05f,
                        endY = steamTopY + R * 0.15f
                    )
                )

                // 7. Glassmorphic Surface Reflection Overlay
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.30f),
                            Color.White.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(centerX - R * 0.35f, centerY - R * 0.35f),
                        radius = R * 1.2f
                    ),
                    radius = R,
                    center = Offset(centerX, centerY)
                )
            }
        }
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
        VoiceInputLanguage.ARABIC -> "ar-SA"
        else -> "en-US"
    }

    var connectionStatus by remember { mutableStateOf(ConnectionStateStatus.CONNECTING) }
    var connectionErrorMessage by remember { mutableStateOf("") }
    var isIntenseMode by remember { mutableStateOf(false) }

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
    var bargeInJob by remember { mutableStateOf<Job?>(null) }
    var textStreamingJob by remember { mutableStateOf<Job?>(null) }
    var accumulatedSpeechText by remember { mutableStateOf("") }
    var turnEndDebounceJob by remember { mutableStateOf<Job?>(null) }
    var errorRetryCount by remember { mutableIntStateOf(0) }
    var isRecognizerListening by remember { mutableStateOf(false) }
    var consecutiveNoSpeechCount by remember { mutableIntStateOf(0) }

    val animateTextProgressively: (String, kotlinx.coroutines.CoroutineScope) -> Unit = { text, scope ->
        textStreamingJob?.cancel()
        val words = text.split(" ")
        tutorSpeechText = ""
        textStreamingJob = scope.launch(Dispatchers.Main) {
            for (i in words.indices) {
                if (voiceSessionState != VoiceSessionState.SPEAKING && voiceSessionState != VoiceSessionState.REPLYING) {
                    break
                }
                tutorSpeechText = words.take(i + 1).joinToString(" ")
                delay(180) // Standard speaking pace (~150-200 wpm)
            }
            if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
                tutorSpeechText = text
            }
        }
    }

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
        bargeInJob?.cancel()
        textStreamingJob?.cancel()
        viewModel.ttsManager.stop()
        if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
            viewModel.setVoiceSessionState(VoiceSessionState.READY)
        }
    }

    val handleNoSpeechDetected: () -> Unit = {
        listeningTimeoutJob?.cancel()
        rmsVolume = 0f
        userSpeechText = ""
        consecutiveNoSpeechCount++
        
        if (consecutiveNoSpeechCount <= 2) {
            val fallbackText = "I couldn't hear your voice. Please try speaking again."
            errorMessage = fallbackText
            tutorSpeechText = ""
            if (!isMuted) {
                viewModel.setVoiceSessionState(VoiceSessionState.SPEAKING)
                viewModel.ttsManager.speak(fallbackText, speed = currentSpeed)
                animateTextProgressively(fallbackText, scope)
            } else {
                tutorSpeechText = fallbackText
                viewModel.setVoiceSessionState(VoiceSessionState.READY)
            }
        } else {
            val stopMessage = "Voice session paused. Tap the microphone when you are ready to speak."
            errorMessage = stopMessage
            tutorSpeechText = ""
            viewModel.setVoiceSessionState(VoiceSessionState.READY)
            if (consecutiveNoSpeechCount == 3 && !isMuted) {
                viewModel.ttsManager.speak(stopMessage, speed = currentSpeed)
                animateTextProgressively(stopMessage, scope)
            }
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
                        tutorSpeechText = "" // Clear for word-by-word streaming
                        val modelContent = Content(role = "model", parts = listOf(Part(text = reply)))
                        voiceHistory = updatedHistory + modelContent

                        if (!isMuted) {
                            viewModel.setVoiceSessionState(VoiceSessionState.SPEAKING)
                            Log.d("VoiceTutorDialog", "Telling TTS Manager to speak")
                            viewModel.ttsManager.speak(reply, speed = currentSpeed)
                            animateTextProgressively(cleanReply, this)
                        } else {
                            Log.d("VoiceTutorDialog", "Muted, not speaking")
                            tutorSpeechText = cleanReply
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

    var startListeningInternalRef: (Boolean) -> Unit = {}

    val initSpeechRecognizer: () -> SpeechRecognizer? = {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.e("VoiceTutor", "Error releasing speech recognizer", e)
        }
        isRecognizerListening = false

        var recognizer: SpeechRecognizer? = null
        try {
            if (SpeechRecognizer.isRecognitionAvailable(context)) {
                recognizer = SpeechRecognizer.createSpeechRecognizer(context.applicationContext)
                recognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        isRecognizerListening = true
                        if (voiceSessionState != VoiceSessionState.SPEAKING && voiceSessionState != VoiceSessionState.REPLYING) {
                            viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
                        }
                    }

                    override fun onBeginningOfSpeech() {
                        isRecognizerListening = true
                        listeningTimeoutJob?.cancel() // Speech started, cancel timeout
                        turnEndDebounceJob?.cancel() // Speech continues, cancel previous debounce
                        errorRetryCount = 0 // Reset error retries
                        if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
                            Log.d("VoiceTutor", "Barge-in onBeginningOfSpeech detected! Interrupting.")
                            stopSpeaking()
                            viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
                            accumulatedSpeechText = ""
                            userSpeechText = "Listening..."
                        } else {
                            if (userSpeechText.isBlank() || userSpeechText == "Listening...") {
                                userSpeechText = "Listening..."
                            }
                        }
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        if (rmsdB > -2f) {
                            rmsVolume = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        }
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        isRecognizerListening = false
                        listeningTimeoutJob?.cancel()
                        rmsVolume = 0f
                        // DO NOT transition to PROCESSING here. This allows natural silence/pauses.
                    }

                    override fun onError(error: Int) {
                        isRecognizerListening = false
                        Log.e("VoiceTutor", "Speech recognizer error: $error")
                        val errorDescription = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check your microphone."
                            SpeechRecognizer.ERROR_CLIENT -> "Client-side error."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                            SpeechRecognizer.ERROR_NETWORK -> "Network error. Please check your internet connection."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network timeout."
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognition service is busy."
                            SpeechRecognizer.ERROR_SERVER -> "Server error."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected."
                            else -> "Speech recognition error ($error)."
                        }
                        
                        listeningTimeoutJob?.cancel()
                        rmsVolume = 0f

                        if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            if (accumulatedSpeechText.isNotBlank()) {
                                Log.d("VoiceTutor", "No match/timeout but we have accumulated text: $accumulatedSpeechText. Restarting recognizer.")
                                if (voiceSessionState == VoiceSessionState.LISTENING) {
                                    startListeningInternalRef(true)
                                }
                            } else {
                                handleNoSpeechDetected()
                            }
                        } else {
                            // Technical error: do NOT call launchGoogleSpeechInput()!
                            // Recover inside Lisan AI safely up to 3 times
                            if (errorRetryCount < 3) {
                                errorRetryCount++
                                Log.d("VoiceTutor", "Technical error $error, retry count $errorRetryCount. Re-triggering listening.")
                                scope.launch {
                                    delay(1000)
                                    if (voiceSessionState == VoiceSessionState.LISTENING) {
                                        startListeningInternalRef(true)
                                    }
                                }
                            } else {
                                Log.e("VoiceTutor", "Exceeded max retries. Setting state to READY.")
                                errorMessage = "Voice recognition paused. Tap the microphone to try again."
                                viewModel.setVoiceSessionState(VoiceSessionState.READY)
                            }
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        isRecognizerListening = false
                        listeningTimeoutJob?.cancel()
                        rmsVolume = 0f
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val newTextSegment = matches?.firstOrNull() ?: ""
                        
                        if (newTextSegment.isNotBlank()) {
                            consecutiveNoSpeechCount = 0
                            if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
                                Log.d("VoiceTutor", "Barge-in onResults detected! Interrupting.")
                                stopSpeaking()
                                viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
                            }
                            
                            // Accumulate the speech input segment
                            accumulatedSpeechText = if (accumulatedSpeechText.isBlank()) {
                                newTextSegment
                            } else {
                                "$accumulatedSpeechText $newTextSegment"
                            }
                            userSpeechText = accumulatedSpeechText
                            
                            // Trigger/Reset turn-end debounce job
                            turnEndDebounceJob?.cancel()
                            turnEndDebounceJob = scope.launch(Dispatchers.Main) {
                                delay(2000) // 2 seconds of silence means they're actually done speaking!
                                val finalSpeech = accumulatedSpeechText.trim()
                                accumulatedSpeechText = ""
                                if (finalSpeech.isNotBlank()) {
                                    sendVoiceInput(finalSpeech)
                                } else {
                                    handleNoSpeechDetected()
                                }
                            }
                            
                            // Immediately restart listening so they can keep speaking
                            if (voiceSessionState == VoiceSessionState.LISTENING) {
                                startListeningInternalRef(true)
                            }
                        } else {
                            if (accumulatedSpeechText.isNotBlank()) {
                                if (voiceSessionState == VoiceSessionState.LISTENING) {
                                    startListeningInternalRef(true)
                                }
                            } else {
                                handleNoSpeechDetected()
                            }
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partialText = matches?.firstOrNull()
                        if (!partialText.isNullOrBlank()) {
                            consecutiveNoSpeechCount = 0
                            if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
                                Log.d("VoiceTutor", "Barge-in onPartialResults detected! Interrupting.")
                                stopSpeaking()
                                viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
                            }
                            
                            // Show live partial results alongside the accumulated text
                            userSpeechText = if (accumulatedSpeechText.isBlank()) {
                                partialText
                            } else {
                                "$accumulatedSpeechText $partialText"
                            }
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
        turnEndDebounceJob?.cancel()
        accumulatedSpeechText = ""
        isRecognizerListening = false
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

    LaunchedEffect(isMuted) {
        if (isMuted) {
            stopSpeaking()
            stopListening()
        }
    }

    val startListeningInternal: (Boolean) -> Unit = { isBargeIn ->
        if (isMuted) {
            // Do not start speech input if muted
        } else if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            errorMessage = "Microphone permission is required for speech input."
        } else if (!isNetworkAvailable(context)) {
            viewModel.setVoiceSessionState(VoiceSessionState.ERROR)
            errorMessage = "Voice feature requires an internet connection."
        } else if (isRecognizerListening) {
            Log.d("VoiceTutor", "SpeechRecognizer is already listening. Ignoring start command.")
        } else {
            if (!isBargeIn) {
                stopSpeaking()
                accumulatedSpeechText = ""
                turnEndDebounceJob?.cancel()
                userSpeechText = ""
                errorMessage = ""
                viewModel.setVoiceSessionState(VoiceSessionState.LISTENING)
            }
            rmsVolume = 0f

            // Start an 8-second timeout if they haven't spoken anything yet
            if (accumulatedSpeechText.isBlank()) {
                listeningTimeoutJob?.cancel()
                listeningTimeoutJob = scope.launch(Dispatchers.Main) {
                    delay(8000) // 8 seconds timeout
                    if (voiceSessionState == VoiceSessionState.LISTENING && accumulatedSpeechText.isBlank()) {
                        handleNoSpeechDetected()
                    }
                }
            }

            try {
                val recognizer = initSpeechRecognizer()
                if (recognizer == null) {
                    listeningTimeoutJob?.cancel()
                    if (!isBargeIn) {
                        viewModel.setVoiceSessionState(VoiceSessionState.READY)
                    }
                    errorMessage = "Speech recognition is not fully supported on this device. Please use keyboard typing instead."
                } else {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, selectedLanguage)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, selectedLanguage)
                        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.applicationContext.packageName)
                        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
                        // Extra silence configuration
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                        putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                    }

                    try {
                        recognizer.startListening(intent)
                    } catch (e: Exception) {
                        Log.e("VoiceTutor", "Error starting speech recognition", e)
                        isRecognizerListening = false
                        listeningTimeoutJob?.cancel()
                        errorMessage = "Microphone is busy. Please try again."
                        if (!isBargeIn) {
                            viewModel.setVoiceSessionState(VoiceSessionState.READY)
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("VoiceTutor", "Microphone or Speech Recognition setup failed", e)
                isRecognizerListening = false
                listeningTimeoutJob?.cancel()
                if (!isBargeIn) {
                    viewModel.setVoiceSessionState(VoiceSessionState.READY)
                }
                errorMessage = "Failed to start microphone. Please try again."
            }
        }
    }

    startListeningInternalRef = startListeningInternal

    val startListening: () -> Unit = {
        consecutiveNoSpeechCount = 0
        startListeningInternal(false)
    }

    launchGoogleSpeechInput = {
        Log.d("VoiceTutor", "launchGoogleSpeechInput called but disabled to avoid external UI popup")
        handleNoSpeechDetected()
    }

    var wasSpeaking by remember { mutableStateOf(false) }

    LaunchedEffect(currentlySpeakingText) {
        if (currentlySpeakingText != null) {
            wasSpeaking = true
            bargeInJob?.cancel()
            // Stop listening while speaking to prevent microphone conflicts
            stopListening()
        } else if (wasSpeaking) {
            wasSpeaking = false
            bargeInJob?.cancel()
            if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
                viewModel.setVoiceSessionState(VoiceSessionState.READY)
                if (autoListenEnabled && !isMuted && hasAudioPermission) {
                    startListeningInternal(false)
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

    val dotFloat by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "thinking_dots"
    )
    val thinkingDots = ".".repeat(dotFloat.toInt().coerceIn(0, 3))

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
                window.setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding(),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
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
                    // Glowing Pulsing Sphere (ChatGPT-style fBm Fluid Multi-Layered Orb)
                    FlutterGlowingSphere(
                        pulseScale = pulseScale,
                        connectionStatus = connectionStatus,
                        rmsVolume = rmsVolume,
                        voiceSessionState = voiceSessionState,
                        isIntenseMode = isIntenseMode,
                        modifier = Modifier
                            .padding(bottom = 24.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (connectionStatus == ConnectionStateStatus.CONNECTED) {
                                    if (voiceSessionState == VoiceSessionState.SPEAKING || voiceSessionState == VoiceSessionState.REPLYING) {
                                        stopSpeaking()
                                        startListeningInternal(false)
                                    } else if (voiceSessionState == VoiceSessionState.LISTENING) {
                                        stopListening()
                                    } else if (voiceSessionState == VoiceSessionState.READY) {
                                        startListeningInternal(false)
                                    }
                                }
                            }
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
                                VoiceSessionState.PROCESSING -> "Thinking" + thinkingDots
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

                    Spacer(modifier = Modifier.height(12.dp))

                    FilterChip(
                        selected = isIntenseMode,
                        onClick = { isIntenseMode = !isIntenseMode },
                        label = { 
                            Text(
                                text = if (isIntenseMode) "Intense Orb Mode" else "Standard Orb Mode",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB).copy(alpha = 0.15f),
                            selectedLabelColor = Color(0xFF2563EB),
                            selectedLeadingIconColor = Color(0xFF2563EB),
                            containerColor = Color.Transparent,
                            labelColor = Color(0xFF757575)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isIntenseMode,
                            borderColor = Color(0xFFCBD5E1),
                            selectedBorderColor = Color(0xFF2563EB)
                        )
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

                    // Speaker toggle button
                    IconButton(
                        onClick = {
                            isMuted = !isMuted
                        },
                        enabled = (connectionStatus == ConnectionStateStatus.CONNECTED)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Mute/Unmute Speaker",
                            tint = if (isMuted) Color.Red else Color(0xFF3F51B5)
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
                        enabled = (connectionStatus == ConnectionStateStatus.CONNECTED && !isMuted)
                    ) {
                        Icon(
                            imageVector = if (isMuted) Icons.Default.MicOff else (if (voiceSessionState == VoiceSessionState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic),
                            contentDescription = "Microphone",
                            tint = if (isMuted) Color.Gray else (if (voiceSessionState == VoiceSessionState.LISTENING) Color.Red else Color(0xFF3F51B5))
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
