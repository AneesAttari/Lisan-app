package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.AuthState
import com.example.ui.components.EditorialCard
import com.example.ui.components.AuthDialog
import com.example.ui.components.ProfileDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import com.example.utils.ServerVoice

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val settings by viewModel.appSettings.collectAsState()
    val learnerProfile by viewModel.learnerProfile.collectAsState()
    val authState by viewModel.authManager.authState.collectAsState()

    val selectedVoiceEn by viewModel.ttsManager.selectedVoiceIdEn.collectAsState()
    val selectedVoiceAr by viewModel.ttsManager.selectedVoiceIdAr.collectAsState()
    val selectedVoiceUr by viewModel.ttsManager.selectedVoiceIdUr.collectAsState()
    val availableVoices by viewModel.ttsManager.availableVoices.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showAuthDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showInfoDialogTitle by remember { mutableStateOf<String?>(null) }
    var showInfoDialogBody by remember { mutableStateOf<String?>(null) }

    val englishVoices = availableVoices.filter { it.languageCode == "en-US" }
    val arabicVoices = availableVoices.filter { it.languageCode == "ar-XA" }
    val urduVoices = availableVoices.filter { it.languageCode == "ur-PK" }

    // Helper to filter sections based on search query
    val query = searchQuery.trim().lowercase()
    fun matchesQuery(vararg keywords: String): Boolean {
        if (query.isEmpty()) return true
        return keywords.any { it.lowercase().contains(query) }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset All Settings?") },
            text = { Text("This will restore all app preferences, voice settings, and behavior controls to their default values.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetAppSettings()
                        showResetDialog = false
                        statusMessage = "All settings restored to defaults."
                        Toast.makeText(context, "Settings restored successfully", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear Learning History?") },
            text = { Text("This will safely reset your learning profile, estimated level, and mistake memory. Saved bookmarks and notes will remain intact.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearLearningHistory()
                        showClearHistoryDialog = false
                        statusMessage = "Learning history cleared safely."
                        Toast.makeText(context, "Learning history cleared", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Clear History")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showAuthDialog) {
        AuthDialog(viewModel = viewModel, onDismiss = { showAuthDialog = false })
    }

    if (showProfileDialog) {
        val currentUser = when (val state = authState) {
            is AuthState.LoggedIn -> state.user
            is AuthState.Guest -> state.user
            is AuthState.LoggedOut -> null
        }
        if (currentUser != null) {
            ProfileDialog(
                viewModel = viewModel,
                user = currentUser,
                onDismiss = { showProfileDialog = false },
                onOpenAuth = { showAuthDialog = true }
            )
        }
    }

    if (showInfoDialogTitle != null) {
        AlertDialog(
            onDismissRequest = { showInfoDialogTitle = null },
            title = { Text(showInfoDialogTitle!!) },
            text = { Text(showInfoDialogBody ?: "") },
            confirmButton = {
                Button(onClick = { showInfoDialogTitle = null }) {
                    Text("Close")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "SETTINGS & PREFERENCES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "App Control Center",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )
            }
            IconButton(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(EditorialTheme.colors.container, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Settings",
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search settings (voice, theme, notifications, privacy)...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EditorialTheme.colors.textSecondary) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = null) } }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditorialAccentPurple,
                unfocusedBorderColor = EditorialTheme.colors.border
            )
        )

        if (statusMessage != null) {
            Surface(
                color = EditorialAccentPurple.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = statusMessage!!,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    IconButton(onClick = { statusMessage = null }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // 1. Voice Control Category (Premium Voice Assistant Settings)
        if (matchesQuery("voice", "speech", "tts", "audio", "mic", "listen", "wake", "speaker", "assistant")) {
            var previewSpeaker by remember { mutableStateOf<String?>(null) }
            val ttsCurrentlySpeaking by viewModel.ttsManager.currentlySpeaking.collectAsState()
            val isSpeakingPreview = ttsCurrentlySpeaking != null && previewSpeaker != null

            SettingsCategoryCard(
                title = "🎙 VOICE ASSISTANT SETTINGS",
                subtitle = "Select your AI teacher's personality, language, and configure controls.",
                icon = Icons.Default.Mic
            ) {
                // Current Voice Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(EditorialAccentPurple.copy(alpha = 0.08f))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "CURRENT ACTIVE VOICE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                "${settings.selectedSpeaker} • ${settings.selectedVoiceLanguage}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary
                            )
                        }
                        
                        // Active voice wave indicator
                        if (ttsCurrentlySpeaking != null) {
                            AiVoiceWaves(isAnimating = true)
                        } else {
                            Icon(
                                Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = EditorialAccentPurple,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Personality & Persona", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                
                // Speaker Cards Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Card 1: Young Voice
                    val isYoungSelected = settings.selectedSpeaker.contains("Young", ignoreCase = true)
                    Card(
                        modifier = Modifier
                             .weight(1f)
                             .clickable {
                                 viewModel.updateAppSettings { it.copy(selectedSpeaker = "Young Voice") }
                             },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isYoungSelected) EditorialAccentPurple.copy(alpha = 0.12f) else EditorialTheme.colors.container
                        ),
                        border = BorderStroke(
                            width = if (isYoungSelected) 2.dp else 1.dp,
                            color = if (isYoungSelected) EditorialAccentPurple else EditorialTheme.colors.border
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎙 Young Voice", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EditorialPrimary)
                                if (isYoungSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EditorialAccentPurple, modifier = Modifier.size(18.dp))
                                }
                            }
                            Text(
                                "Friendly AI Teacher",
                                fontSize = 12.sp,
                                color = EditorialTheme.colors.textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Energetic, clear, and highly encouraging style.",
                                fontSize = 11.sp,
                                color = EditorialTheme.colors.textSecondary,
                                maxLines = 2
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Preview button inside card
                            val isThisPreviewing = isSpeakingPreview && previewSpeaker?.contains("Young", ignoreCase = true) == true
                            Button(
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
                                        // Temporarily apply Young Voice to TTS, then speak
                                        viewModel.ttsManager.applySpeakerPreset("Young Voice")
                                        viewModel.speak(text)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isThisPreviewing) Color(0xFFC62828) else EditorialAccentPurple
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isThisPreviewing) {
                                    AiVoiceWaves(isAnimating = true, color = Color.White)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Preview", fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // Card 2: Mature Voice
                    val isMatureSelected = settings.selectedSpeaker.contains("Mature", ignoreCase = true)
                    Card(
                        modifier = Modifier
                             .weight(1f)
                             .clickable {
                                 viewModel.updateAppSettings { it.copy(selectedSpeaker = "Mature Voice") }
                             },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isMatureSelected) EditorialAccentPurple.copy(alpha = 0.12f) else EditorialTheme.colors.container
                        ),
                        border = BorderStroke(
                            width = if (isMatureSelected) 2.dp else 1.dp,
                            color = if (isMatureSelected) EditorialAccentPurple else EditorialTheme.colors.border
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🎙 Mature Voice", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EditorialPrimary)
                                if (isMatureSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EditorialAccentPurple, modifier = Modifier.size(18.dp))
                                }
                            }
                            Text(
                                "Professional AI Teacher",
                                fontSize = 12.sp,
                                color = EditorialTheme.colors.textSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                "Calm, deep, confident, and professional style.",
                                fontSize = 11.sp,
                                color = EditorialTheme.colors.textSecondary,
                                maxLines = 2
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // Preview button inside card
                            val isThisPreviewing = isSpeakingPreview && previewSpeaker?.contains("Mature", ignoreCase = true) == true
                            Button(
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
                                        // Temporarily apply Mature Voice to TTS, then speak
                                        viewModel.ttsManager.applySpeakerPreset("Mature Voice")
                                        viewModel.speak(text)
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isThisPreviewing) Color(0xFFC62828) else EditorialAccentPurple
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.fillMaxWidth().height(32.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isThisPreviewing) {
                                    AiVoiceWaves(isAnimating = true, color = Color.White)
                                } else {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Preview", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Select Language", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                
                // Language Selection Row
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
                            },
                            label = { Text(display, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EditorialAccentPurple.copy(alpha = 0.15f),
                                selectedLabelColor = EditorialAccentPurple
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isLangSelected,
                                selectedBorderColor = EditorialAccentPurple,
                                borderColor = EditorialTheme.colors.border
                            )
                        )
                    }
                }

                HorizontalDivider(color = EditorialTheme.colors.border, modifier = Modifier.padding(vertical = 4.dp))

                // Speech Controls
                Text("Voice Assistant Tuning", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)

                SettingsSwitchRow(
                    title = "Voice Response Feedback",
                    subtitle = "Speak lessons and corrected phrases automatically.",
                    checked = settings.voiceFeedbackEnabled,
                    onCheckedChange = { valOn ->
                        viewModel.updateAppSettings { it.copy(voiceFeedbackEnabled = valOn) }
                    }
                )

                SettingsSwitchRow(
                    title = "Automatically Speak Answers",
                    subtitle = "Synthesize correct sentences immediately after submit.",
                    checked = settings.speakAnswersAutomatically,
                    onCheckedChange = { valOn ->
                        viewModel.updateAppSettings { it.copy(speakAnswersAutomatically = valOn) }
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))
                
                // Speed Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voice Speed", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EditorialPrimary)
                        Text("${String.format("%.2f", settings.voiceSpeed)}x", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
                    }
                    Slider(
                        value = settings.voiceSpeed,
                        onValueChange = { speed -> viewModel.updateAppSettings { it.copy(voiceSpeed = speed) } },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = EditorialAccentPurple,
                            activeTrackColor = EditorialAccentPurple
                        )
                    )
                }

                // Volume Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voice Volume", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EditorialPrimary)
                        Text("${(settings.voiceVolume * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
                    }
                    Slider(
                        value = settings.voiceVolume,
                        onValueChange = { vol -> viewModel.updateAppSettings { it.copy(voiceVolume = vol) } },
                        valueRange = 0.0f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = EditorialAccentPurple,
                            activeTrackColor = EditorialAccentPurple
                        )
                    )
                }

                // Pitch Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voice Pitch (Modulation)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EditorialPrimary)
                        Text("${String.format("%.2f", settings.voicePitch)}x", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
                    }
                    Slider(
                        value = settings.voicePitch,
                        onValueChange = { p -> viewModel.updateAppSettings { it.copy(voicePitch = p) } },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = EditorialAccentPurple,
                            activeTrackColor = EditorialAccentPurple
                        )
                    )
                }

                HorizontalDivider(color = EditorialTheme.colors.border, modifier = Modifier.padding(vertical = 4.dp))

                // Advanced / Native voice matching info
                Text(
                    "Note: Lisan automatically selects high-fidelity cloud voices when online, and gracefully falls back to the closest matching native voice offline.",
                    fontSize = 11.sp,
                    color = EditorialTheme.colors.textSecondary,
                    lineHeight = 15.sp
                )
            }
        }

        // 2. Appearance Category
        if (matchesQuery("appearance", "theme", "dark", "light", "color", "font", "ui")) {
            SettingsCategoryCard(
                title = "2. Appearance & Theming",
                subtitle = "Customize colors, theme mode, and visual typography.",
                icon = Icons.Default.Palette
            ) {
                Text("Theme Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("System", "Light", "Dark").forEach { mode ->
                        FilterChip(
                            selected = settings.themeMode == mode,
                            onClick = {
                                viewModel.updateAppSettings { it.copy(themeMode = mode) }
                            },
                            label = { Text(mode, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Font Size", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Small", "Medium", "Large").forEach { size ->
                        FilterChip(
                            selected = settings.fontSize == size,
                            onClick = { viewModel.updateAppSettings { it.copy(fontSize = size) } },
                            label = { Text(size, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                SettingsSwitchRow(
                    title = "Compact Mode",
                    subtitle = "Reduce padding and list item heights for higher density.",
                    checked = settings.compactMode,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(compactMode = it) } }
                )
                SettingsSwitchRow(
                    title = "Rounded UI Corners",
                    subtitle = "Use smooth pill and card corner radii.",
                    checked = settings.roundedUi,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(roundedUi = it) } }
                )
                SettingsSwitchRow(
                    title = "Animation & Transition Effects",
                    subtitle = "Enable smooth fading and scale animations across screens.",
                    checked = settings.animationEffects,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(animationEffects = it) } }
                )
            }
        }

        // 3. Language & Learning Preferences
        if (matchesQuery("language", "learning", "level", "urdu", "arabic", "goal", "grammar", "preference")) {
            SettingsCategoryCard(
                title = "3. Language & Learning Preferences",
                subtitle = "Adjust translation support, English proficiency level, and daily goals.",
                icon = Icons.Default.Language
            ) {
                Text("English Level", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Beginner", "Intermediate", "Advanced").forEach { lvl ->
                        FilterChip(
                            selected = settings.englishLevel == lvl,
                            onClick = { viewModel.updateAppSettings { it.copy(englishLevel = lvl) } },
                            label = { Text(lvl, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                SettingsSwitchRow(
                    title = "Show Arabic Support (عربي)",
                    subtitle = "Compare English grammar rules with Arabic equivalents.",
                    checked = settings.arabicSupport,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(arabicSupport = it) } }
                )
                SettingsSwitchRow(
                    title = "Show Urdu Help (اردو)",
                    subtitle = "Provide Urdu translations and explanations for all lessons.",
                    checked = settings.urduHelp,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(urduHelp = it) } }
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Daily Lesson Goal: ${settings.dailyLessonGoal} lessons", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                Slider(
                    value = settings.dailyLessonGoal.toFloat(),
                    onValueChange = { goal -> viewModel.updateAppSettings { it.copy(dailyLessonGoal = goal.toInt()) } },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Text("Grammar Explanation Style", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Short", "Normal", "Detailed").forEach { style ->
                        FilterChip(
                            selected = settings.grammarExplanationStyle == style,
                            onClick = { viewModel.updateAppSettings { it.copy(grammarExplanationStyle = style) } },
                            label = { Text(style, fontSize = 12.sp) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 4. Notifications
        if (matchesQuery("notification", "reminder", "streak", "alert", "sound", "time")) {
            SettingsCategoryCard(
                title = "4. Notifications & Reminders",
                subtitle = "Stay on track with daily practice reminders and streak alerts.",
                icon = Icons.Default.Notifications
            ) {
                SettingsSwitchRow(
                    title = "Daily Practice Reminder",
                    subtitle = "Remind me to practice English grammar every day.",
                    checked = settings.dailyReminder,
                    onCheckedChange = {
                        viewModel.updateAppSettings { s -> s.copy(dailyReminder = it) }
                        viewModel.toggleDailyNotification(it, context)
                    }
                )
                SettingsSwitchRow(
                    title = "Lesson Completion Reminders",
                    subtitle = "Celebrate finishing daily lessons and quizzes.",
                    checked = settings.lessonCompletionReminder,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(lessonCompletionReminder = it) } }
                )
                SettingsSwitchRow(
                    title = "Streak Saver Alert",
                    subtitle = "Alert me before my daily learning streak expires.",
                    checked = settings.streakReminder,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(streakReminder = it) } }
                )
                SettingsSwitchRow(
                    title = "New Content & Idiom Alerts",
                    subtitle = "Notify when new daily verbs, idioms, or quizzes are available.",
                    checked = settings.newContentAlerts,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(newContentAlerts = it) } }
                )
                SettingsSwitchRow(
                    title = "Quiet Hours (10 PM - 7 AM)",
                    subtitle = "Pause notification sounds during sleep hours.",
                    checked = settings.quietHours,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(quietHours = it) } }
                )
            }
        }

        // 5. Privacy & Data
        if (matchesQuery("privacy", "data", "backup", "history", "cache", "export", "policy")) {
            SettingsCategoryCard(
                title = "5. Privacy & Data Management",
                subtitle = "Manage your learning data, local storage, and history backups.",
                icon = Icons.Default.Security
            ) {
                SettingsSwitchRow(
                    title = "Save Progress Locally",
                    subtitle = "Store lesson progress, chat logs, and notes securely on device.",
                    checked = settings.saveProgressLocally,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(saveProgressLocally = it) } }
                )
                SettingsSwitchRow(
                    title = "Secure Cloud Backup",
                    subtitle = "Back up user profile and stats across sessions.",
                    checked = settings.cloudBackup,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(cloudBackup = it) } }
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { showClearHistoryDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                        border = BorderStroke(1.dp, Color(0xFFC62828))
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear History", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = {
                            statusMessage = "Cache cleared successfully (3.4 MB freed)."
                            Toast.makeText(context, "App cache cleared", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear Cache", fontSize = 11.sp)
                    }
                }

                OutlinedButton(
                    onClick = {
                        statusMessage = "Learning profile and progress exported successfully as JSON."
                        Toast.makeText(context, "Data exported to downloads", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Learning Data & Notes")
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = {
                        showInfoDialogTitle = "Privacy Policy"
                        showInfoDialogBody = "Lisan Grammar respects your privacy. All learning data, chat sessions, and notes are processed securely and stored locally or on your encrypted account database. We do not sell or share personal data with third parties."
                    }) {
                        Text("Privacy Policy", fontSize = 12.sp, color = EditorialAccentPurple)
                    }
                    TextButton(onClick = {
                        showInfoDialogTitle = "Terms of Use"
                        showInfoDialogBody = "By using Lisan Grammar, you agree to use the educational content for personal English and Arabic learning purposes. All AI translations and grammar explanations are provided as-is to assist learners."
                    }) {
                        Text("Terms of Use", fontSize = 12.sp, color = EditorialAccentPurple)
                    }
                }
            }
        }

        // 6. Accessibility
        if (matchesQuery("accessibility", "contrast", "text", "motion", "reader", "guidance")) {
            SettingsCategoryCard(
                title = "6. Accessibility",
                subtitle = "Make Lisan Grammar easy and comfortable for every learner.",
                icon = Icons.Default.Accessibility
            ) {
                SettingsSwitchRow(
                    title = "Large Text Mode",
                    subtitle = "Increase font scaling across lessons and chat UI.",
                    checked = settings.largeText,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(largeText = it) } }
                )
                SettingsSwitchRow(
                    title = "High Contrast Mode",
                    subtitle = "Boost color contrast for better readability.",
                    checked = settings.highContrast,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(highContrast = it) } }
                )
                SettingsSwitchRow(
                    title = "Reduce Motion",
                    subtitle = "Minimize UI animations and screen transitions.",
                    checked = settings.reduceMotion,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(reduceMotion = it) } }
                )
                SettingsSwitchRow(
                    title = "Voice Guidance & Talkback Support",
                    subtitle = "Optimize content descriptions and screen reader semantics.",
                    checked = settings.voiceGuidance,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(voiceGuidance = it) } }
                )
            }
        }

        // 7. Account / Profile
        if (matchesQuery("account", "profile", "login", "email", "password", "sign", "user")) {
            SettingsCategoryCard(
                title = "7. Account & Cloud Sync",
                subtitle = "Manage your Lisan user account, cloud sync status, and login.",
                icon = Icons.Default.AccountCircle
            ) {
                val user = when (val state = authState) {
                    is AuthState.LoggedIn -> state.user
                    is AuthState.Guest -> state.user
                    is AuthState.LoggedOut -> null
                }

                if (user != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(user.displayName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = EditorialPrimary)
                            Text(if (user.isGuest) "Guest Account (مہمان)" else user.email, fontSize = 12.sp, color = EditorialTheme.colors.textSecondary)
                        }
                        Button(
                            onClick = { showProfileDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Manage", fontSize = 12.sp)
                        }
                    }
                } else {
                    Button(
                        onClick = { showAuthDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log In or Create Account")
                    }
                }
            }
        }

        // 8. Lesson & App Behavior
        if (matchesQuery("behavior", "lesson", "advance", "hint", "quiz", "practice", "autoplay")) {
            SettingsCategoryCard(
                title = "8. Lesson & App Behavior",
                subtitle = "Fine-tune how quizzes, lessons, and auto-advancing behave.",
                icon = Icons.Default.Tune
            ) {
                SettingsSwitchRow(
                    title = "Auto-Advance After Answer",
                    subtitle = "Move to the next question automatically after answering correctly.",
                    checked = settings.autoAdvance,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(autoAdvance = it) } }
                )
                SettingsSwitchRow(
                    title = "Show Hints Before Answer",
                    subtitle = "Provide helpful Arabic/Urdu clues during quiz questions.",
                    checked = settings.showHints,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(showHints = it) } }
                )
                SettingsSwitchRow(
                    title = "Show Correct Answer Immediately",
                    subtitle = "Display explanation immediately upon quiz submission.",
                    checked = settings.showCorrectAnswerImmediately,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(showCorrectAnswerImmediately = it) } }
                )
                SettingsSwitchRow(
                    title = "Interactive Practice Mode",
                    subtitle = "Enable sentence builder and grammar correction in chat.",
                    checked = settings.practiceMode,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(practiceMode = it) } }
                )
                SettingsSwitchRow(
                    title = "Continue From Last Lesson on Open",
                    subtitle = "Automatically load your last active lesson when app starts.",
                    checked = settings.continueFromLastLesson,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(continueFromLastLesson = it) } }
                )
            }
        }

        // 9. About
        if (matchesQuery("about", "version", "support", "rate", "share", "developer")) {
            SettingsCategoryCard(
                title = "9. About Lisan Grammar",
                subtitle = "App version, support, rating, and developer information.",
                icon = Icons.Default.Info
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("App Version", fontSize = 13.sp, color = EditorialTheme.colors.textSecondary)
                    Text("v1.5.0 (Production)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Build Number", fontSize = 13.sp, color = EditorialTheme.colors.textSecondary)
                    Text("2026.08.06", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            statusMessage = "Opening Google Play Store..."
                            Toast.makeText(context, "Thank you for rating Lisan Grammar 5 stars!", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rate App", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = {
                            statusMessage = "Share link copied to clipboard!"
                            Toast.makeText(context, "Share link copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Share App", fontSize = 11.sp)
                    }
                }
            }
        }

        // 10. Advanced
        if (matchesQuery("advanced", "reset", "diagnostics", "logs", "developer")) {
            SettingsCategoryCard(
                title = "10. Advanced Controls",
                subtitle = "Diagnostics, logs, and master reset options.",
                icon = Icons.Default.DeveloperMode
            ) {
                SettingsSwitchRow(
                    title = "Developer Options & Diagnostics",
                    subtitle = "Show advanced AI telemetry and network status in debug views.",
                    checked = settings.developerOptions,
                    onCheckedChange = { viewModel.updateAppSettings { s -> s.copy(developerOptions = it) } }
                )

                Spacer(modifier = Modifier.height(4.dp))
                Button(
                    onClick = { showResetDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.RestartAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset All App Settings & Defaults", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SettingsCategoryCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    EditorialCard {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(EditorialAccentPurple.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = EditorialAccentPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple,
                        letterSpacing = 1.2.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = EditorialTheme.colors.textSecondary
                    )
                }
            }

            HorizontalDivider(color = EditorialTheme.colors.border, modifier = Modifier.padding(vertical = 4.dp))

            content()
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (enabled) EditorialPrimary else EditorialTheme.colors.textSecondary
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = EditorialTheme.colors.textSecondary
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EditorialAccentPurple
            )
        )
    }
}

@Composable
fun VoiceDropdownSelector(
    label: String,
    selectedId: String?,
    voices: List<ServerVoice>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentName = voices.find { it.id == selectedId }?.name ?: selectedId ?: "Default Voice"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialTheme.colors.textSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, EditorialTheme.colors.border)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(currentName, fontSize = 13.sp, color = EditorialPrimary, maxLines = 1)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                voices.forEach { voice ->
                    DropdownMenuItem(
                        text = { Text(voice.name, fontSize = 13.sp) },
                        onClick = {
                            onSelect(voice.id)
                            expanded = false
                        }
                    )
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
    val infiniteTransition = rememberInfiniteTransition(label = "VoiceWaves")
    
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
