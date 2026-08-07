package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.EditorialPrimary
import com.example.ui.viewmodel.LisanViewModel
import kotlinx.coroutines.delay

@Composable
fun VoiceTutorScreen(
    viewModel: LisanViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-speak initial greeting
    LaunchedEffect(Unit) {
        viewModel.ttsManager.speak("Hello! Welcome to Lisan AI Tutor. I am ready to practice English and Arabic with you!", "greeting")
    }

    // Orb Pulse Animation (speaking)
    val isSpeaking by viewModel.activeSpeakingId.collectAsState()
    val isSpeakingNow = isSpeaking != null
    
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = if (isSpeakingNow) 0.98f else 0.95f,
        targetValue = if (isSpeakingNow) 1.12f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeakingNow) 750 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "orb_scale"
    )

    // ... (rest of the UI, using viewModel.fetchGeminiWithFallback)
    // Add logic to mic button to trigger SpeechRecognition and then call fetchGeminiWithFallback

    // Layout
    Scaffold(
        containerColor = Color(0xFF0F172A),
        topBar = {
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Surface(color = Color(0xFF1E293B), shape = CircleShape) {
                    Text("Lisan AI Ready", color = Color.White, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Default.VolumeUp, contentDescription = "Speaker", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Orb
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .background(
                            brush = Brush.radialGradient(listOf(Color(0xFF3B82F6), Color(0xFF6366F1))),
                            shape = CircleShape
                        )
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Hello! Welcome to Lisan AI Tutor. I am ready to practice English and Arabic with you!",
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            // Input Bar
            Spacer(modifier = Modifier.weight(1f))
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF1E293B), CircleShape).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { viewModel.startVoiceSearch() }, modifier = Modifier.background(EditorialPrimary, CircleShape)) {
                    Icon(Icons.Default.Mic, contentDescription = "Mic", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ask Lisan Tutor...", color = Color.Gray, modifier = Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }
}
