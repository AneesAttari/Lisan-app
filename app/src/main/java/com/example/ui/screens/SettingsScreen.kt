package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.data.repository.UserSettings
import com.example.ui.components.EditorialCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val learnerProfile by viewModel.learnerProfile.collectAsState()
    val isOnline by viewModel.isNetworkAvailable.collectAsState()
    val ttsCurrentlySpeaking by viewModel.ttsManager.currentlySpeaking.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Dialog States
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showTimePickerModal by remember { mutableStateOf(false) }
    var showResetDefaultsDialog by remember { mutableStateOf(false) }
    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showDeleteAllDataDialog by remember { mutableStateOf(false) }
    var deleteConfirmationInput by remember { mutableStateOf("") }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    // Mic Permission launcher
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Microphone permission granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Microphone permission required for voice input.", Toast.LENGTH_SHORT).show()
        }
    }

    // Notification Permission launcher (Android 13+)
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
        if (isGranted) {
            Toast.makeText(context, "Notification permission granted!", Toast.LENGTH_SHORT).show()
        }
    }

    fun matchesQuery(vararg keywords: String): Boolean {
        val q = searchQuery.trim().lowercase()
        if (q.isEmpty()) return true
        return keywords.any { it.lowercase().contains(q) }
    }

    // --- DIALOGS ---

    // 1. Edit Profile Dialog
    if (showEditProfileDialog) {
        var tempName by remember { mutableStateOf(userSettings.displayName) }
        var tempLevel by remember { mutableStateOf(userSettings.learningLevel) }
        var tempNativeLang by remember { mutableStateOf(userSettings.nativeLanguage) }
        var tempGoal by remember { mutableStateOf(userSettings.learningGoal) }
        var tempMinutes by remember { mutableStateOf(userSettings.dailyGoalMinutes) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Learner Profile", fontWeight = FontWeight.Bold, color = EditorialPrimary) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("English Proficiency Level", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Beginner", "Elementary", "Intermediate", "Advanced").forEach { lvl ->
                            SettingsPillChip(
                                selected = tempLevel == lvl,
                                onClick = { tempLevel = lvl },
                                label = lvl
                            )
                        }
                    }

                    Text("Native Language", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf("Urdu", "Arabic").forEach { lang ->
                            SettingsPillChip(
                                selected = tempNativeLang == lang,
                                onClick = { tempNativeLang = lang },
                                label = lang
                            )
                        }
                    }

                    Text("Primary Learning Goal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf(
                            "Improve grammar",
                            "Improve speaking",
                            "Improve pronunciation",
                            "Prepare for exams",
                            "Workplace English",
                            "Everyday conversation"
                        ).forEach { goalOption ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { tempGoal = goalOption }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = tempGoal == goalOption, onClick = { tempGoal = goalOption })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(goalOption, fontSize = 13.sp)
                            }
                        }
                    }

                    Text("Daily Learning Goal (Minutes)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(5, 10, 15, 20, 30).forEach { mins ->
                            SettingsPillChip(
                                selected = tempMinutes == mins,
                                onClick = { tempMinutes = mins },
                                label = "${mins}m"
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalName = tempName.ifBlank { "Learner" }
                        viewModel.updateUserSettings { current ->
                            current.copy(
                                displayName = finalName,
                                learningLevel = tempLevel,
                                nativeLanguage = tempNativeLang,
                                learningGoal = tempGoal,
                                dailyGoalMinutes = tempMinutes
                            )
                        }
                        showEditProfileDialog = false
                        Toast.makeText(context, "Learner profile updated!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 2. Time Picker Dialog
    if (showTimePickerModal) {
        val timePickerState = rememberTimePickerState(
            initialHour = userSettings.reminderHour,
            initialMinute = userSettings.reminderMinute
        )

        AlertDialog(
            onDismissRequest = { showTimePickerModal = false },
            title = { Text("Set Daily Reminder Time", fontWeight = FontWeight.Bold) },
            text = {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TimePicker(state = timePickerState)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserSettings { current ->
                            current.copy(
                                reminderHour = timePickerState.hour,
                                reminderMinute = timePickerState.minute
                            )
                        }
                        showTimePickerModal = false
                        val formattedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        Toast.makeText(context, "Reminder set for $formattedTime daily", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)
                ) {
                    Text("Save Time")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 3. Reset Defaults Dialog
    if (showResetDefaultsDialog) {
        AlertDialog(
            onDismissRequest = { showResetDefaultsDialog = false },
            title = { Text("Reset All Preferences?", fontWeight = FontWeight.Bold) },
            text = { Text("This will restore all app preferences, theme modes, voice controls, and learning goals to their default values.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetUserSettings()
                        showResetDefaultsDialog = false
                        Toast.makeText(context, "Settings restored to defaults", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDefaultsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 4. Clear Cache Dialog
    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            title = { Text("Clear Cached Lessons?", fontWeight = FontWeight.Bold) },
            text = { Text("This will remove locally cached lesson files to free up space. Your saved notes and bookmarked idioms will NOT be affected.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearLessonCache {
                            showClearCacheDialog = false
                            Toast.makeText(context, "Lesson cache cleared successfully", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)
                ) {
                    Text("Clear Cache")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 5. Delete All Data Dialog (Destructive)
    if (showDeleteAllDataDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteAllDataDialog = false
                deleteConfirmationInput = ""
            },
            title = { Text("⚠️ Delete All Learning Data?", fontWeight = FontWeight.Bold, color = Color(0xFFC62828)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "This will permanently erase all chat history, saved notes, bookmarked idioms, pronunciation scores, and daily challenge progress. This action cannot be undone.",
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Type 'DELETE' below to confirm:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = deleteConfirmationInput,
                        onValueChange = { deleteConfirmationInput = it },
                        singleLine = true,
                        placeholder = { Text("DELETE") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = deleteConfirmationInput.trim() == "DELETE",
                    onClick = {
                        viewModel.deleteAllDataAndReset {
                            showDeleteAllDataDialog = false
                            deleteConfirmationInput = ""
                            Toast.makeText(context, "All learning data has been cleared.", Toast.LENGTH_LONG).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC62828),
                        disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
                    )
                ) {
                    Text("Permanently Delete All")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteAllDataDialog = false
                    deleteConfirmationInput = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // 6. Help Dialog
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("Lisan Grammar • Help & How-to", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("• Comparative Lessons:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Learn English grammar topics by comparing them directly with similar rules in Modern Standard Arabic.", fontSize = 12.sp)

                    Text("• Sentence Analyzer:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Type or speak any English sentence to get deep tense decomposition, word-by-word grammar breakdowns, and Arabic comparison.", fontSize = 12.sp)

                    Text("• AI Tutor Chat:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Ask questions in English or Urdu. The AI tutor replies in clear English with supportive Urdu explanations and speech audio.", fontSize = 12.sp)

                    Text("• Pronunciation Practice:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Speak phrases into your microphone to receive instant phonetic evaluation, word-level scoring, and audio feedback.", fontSize = 12.sp)

                    Text("• Offline Support:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("When internet is disconnected, Lisan uses local grammar tables and offline cached lessons seamlessly.", fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showHelpDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)) {
                    Text("Got it")
                }
            }
        )
    }

    // 7. Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Lisan Grammar respects your privacy. All your learning progress, notes, and chat interactions are stored locally on your device or in your secure account database. We do not sell or share personal learning data with third parties. Speech recognition inputs are processed strictly for real-time pronunciation evaluation.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)) {
                    Text("Close")
                }
            }
        )
    }

    // 8. Terms Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Use", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "By using Lisan Grammar, you agree to use the educational tools and AI services for personal language learning. All translation comparisons and AI responses are crafted to guide learners respectfully. All intellectual content is protected.",
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)) {
                    Text("Close")
                }
            }
        )
    }

    // 9. Open Source Licenses Dialog
    if (showLicensesDialog) {
        AlertDialog(
            onDismissRequest = { showLicensesDialog = false },
            title = { Text("Open Source Licenses", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("• Jetpack Compose & Material 3 (Apache 2.0)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• AndroidX Room & DataStore Preferences (Apache 2.0)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• Kotlin Coroutines & Serialization (Apache 2.0)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• Retrofit & OkHttp (Apache 2.0)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("• Coil Image Loader (Apache 2.0)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            },
            confirmButton = {
                Button(onClick = { showLicensesDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)) {
                    Text("Close")
                }
            }
        )
    }

    // --- MAIN SCREEN BODY ---
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Summary Card: Learner Profile Overview
        EditorialCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showEditProfileDialog = true }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar Badge
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(EditorialAccentPurple.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userSettings.displayName.take(1).uppercase(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = userSettings.displayName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                        Surface(
                            color = EditorialAccentPurple.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = userSettings.learningLevel,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Native Language: ${userSettings.nativeLanguage} • Goal: ${userSettings.learningGoal}",
                        fontSize = 12.sp,
                        color = EditorialTheme.colors.textSecondary
                    )
                    Text(
                        text = "Keep going! Practice ${userSettings.dailyGoalMinutes} minutes today.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = EditorialAccentPurple
                    )
                }

                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Real-Time Settings Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search settings (voice, theme, notifications, offline)...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EditorialTheme.colors.textSecondary) },
            trailingIcon = if (searchQuery.isNotEmpty()) {
                { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = "Clear search") } }
            } else null,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EditorialAccentPurple,
                unfocusedBorderColor = EditorialTheme.colors.border
            )
        )

        // SECTION A: Learner Profile & Goals
        if (matchesQuery("profile", "learner", "name", "level", "language", "goal", "minutes", "urdu", "arabic")) {
            SettingsCategoryCard(
                title = "1. Learner Profile & Goals",
                subtitle = "Manage your English proficiency level, native language, and study goals.",
                icon = Icons.Default.Person
            ) {
                Text("English Proficiency Level", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Beginner", "Elementary", "Intermediate", "Upper Intermediate", "Advanced").forEach { lvl ->
                        SettingsPillChip(
                            selected = userSettings.learningLevel == lvl,
                            onClick = { viewModel.updateUserSettings { it.copy(learningLevel = lvl) } },
                            label = lvl
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Native Language", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Urdu", "Arabic").forEach { lang ->
                        SettingsPillChip(
                            selected = userSettings.nativeLanguage == lang,
                            onClick = { viewModel.updateUserSettings { it.copy(nativeLanguage = lang) } },
                            label = lang
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Daily Practice Goal", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(5, 10, 15, 20, 30).forEach { mins ->
                        SettingsPillChip(
                            selected = userSettings.dailyGoalMinutes == mins,
                            onClick = { viewModel.updateUserSettings { it.copy(dailyGoalMinutes = mins) } },
                            label = "${mins} mins"
                        )
                    }
                }
            }
        }

        // SECTION B: Appearance & Theme
        if (matchesQuery("appearance", "theme", "dark", "light", "system", "dynamic", "color", "font", "size", "motion")) {
            SettingsCategoryCard(
                title = "2. Appearance & Theme",
                subtitle = "Customize colors, theme mode, typography scale, and visual effects.",
                icon = Icons.Default.Palette
            ) {
                Text("Theme Mode", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("System", "Light", "Dark").forEach { mode ->
                        SettingsPillChip(
                            selected = userSettings.themeMode == mode,
                            onClick = { viewModel.updateUserSettings { it.copy(themeMode = mode) } },
                            label = mode,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    SettingsSwitchRow(
                        title = "Dynamic Colors (Material You)",
                        subtitle = "Harmonize app colors with your device wallpaper.",
                        checked = userSettings.dynamicColor,
                        onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(dynamicColor = valOn) } }
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Font Size", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Small", "Default", "Large", "Extra Large").forEach { sizeOpt ->
                        SettingsPillChip(
                            selected = userSettings.fontSize == sizeOpt,
                            onClick = { viewModel.updateUserSettings { it.copy(fontSize = sizeOpt) } },
                            label = sizeOpt
                        )
                    }
                }

                SettingsSwitchRow(
                    title = "Reduce Motion",
                    subtitle = "Disable heavy screen transitions and animations.",
                    checked = userSettings.reduceMotion,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(reduceMotion = valOn) } }
                )

                Divider(color = EditorialTheme.colors.divider, modifier = Modifier.padding(vertical = 12.dp))

                Surface(
                    onClick = { com.example.ui.navigation.HashRouter.navigate("#/chat-theme") },
                    color = EditorialAccentPurple.copy(alpha = 0.06f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth().testTag("chat_theme_row_settings")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = EditorialAccentPurple,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Chat Theme & Wallpaper",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary
                            )
                            Text(
                                text = "Change chat background theme or choose custom photo",
                                fontSize = 11.sp,
                                color = EditorialTheme.colors.textSecondary
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = EditorialAccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // SECTION C: Language & Learning Preferences
        if (matchesQuery("language", "explanation", "arabic", "comparison", "roman", "tab", "difficulty", "adapt")) {
            SettingsCategoryCard(
                title = "3. Language & Learning Preferences",
                subtitle = "Configure explanation languages, Arabic grammar rules, and tab defaults.",
                icon = Icons.Default.Language
            ) {
                Text("Lesson Explanation Language", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("English", "Urdu", "English + Urdu").forEach { langOpt ->
                        SettingsPillChip(
                            selected = userSettings.explanationLanguage == langOpt,
                            onClick = { viewModel.updateUserSettings { it.copy(explanationLanguage = langOpt) } },
                            label = langOpt
                        )
                    }
                }

                SettingsSwitchRow(
                    title = "Show Arabic Grammar Comparison",
                    subtitle = "Compare English grammar rules directly with Arabic counterparts.",
                    checked = userSettings.showArabicGrammarComparison,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(showArabicGrammarComparison = valOn) } }
                )

                SettingsSwitchRow(
                    title = "Roman Urdu Assistance",
                    subtitle = "Include Romanized Urdu text alongside nastaliq script.",
                    checked = userSettings.romanUrduAssistance,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(romanUrduAssistance = valOn) } }
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text("Default Startup Screen", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Learn", "Analyze", "Tutor", "Idioms", "Library").forEach { tab ->
                        SettingsPillChip(
                            selected = userSettings.defaultLearningTab == tab,
                            onClick = { viewModel.updateUserSettings { it.copy(defaultLearningTab = tab) } },
                            label = tab
                        )
                    }
                }

                SettingsSwitchRow(
                    title = "Adapt Content to My Level",
                    subtitle = "Automatically adjust AI practice complexity based on your score.",
                    checked = userSettings.adaptDifficulty,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(adaptDifficulty = valOn) } }
                )
            }
        }

        // SECTION D: Speech, Listening & Pronunciation
        if (matchesQuery("speech", "listening", "pronunciation", "tts", "voice", "sample", "pitch", "rate", "mic", "feedback")) {
            SettingsCategoryCard(
                title = "4. Speech, Listening & Pronunciation",
                subtitle = "Tune TTS teacher voice, speech rate, microphone permissions, and practice feedback.",
                icon = Icons.Default.Mic
            ) {
                Text("Teacher Voice Preset", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("Young Voice", "Mature Voice", "Gentle Voice").forEach { speaker ->
                        SettingsPillChip(
                            selected = userSettings.selectedSpeaker == speaker,
                            onClick = {
                                viewModel.updateUserSettings { it.copy(selectedSpeaker = speaker) }
                                viewModel.ttsManager.applySpeakerPreset(speaker)
                            },
                            label = speaker,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Voice Sample Playback Button
                OutlinedButton(
                    onClick = {
                        val sampleText = "Welcome to Lisan Grammar. Welcome to your English learning journey."
                        viewModel.ttsManager.speak(sampleText)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (ttsCurrentlySpeaking != null) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (ttsCurrentlySpeaking != null) "Playing Voice Sample..." else "Play Voice Sample", fontSize = 13.sp)
                }

                // Speech Rate Slider
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Speech Rate (Speed)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EditorialPrimary)
                        Text("${String.format("%.2f", userSettings.speechRate)}x", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
                    }
                    Slider(
                        value = userSettings.speechRate,
                        onValueChange = { rate ->
                            viewModel.updateUserSettings { it.copy(speechRate = rate) }
                            viewModel.ttsManager.setSpeechRate(rate)
                        },
                        valueRange = 0.5f..2.0f,
                        colors = SliderDefaults.colors(thumbColor = EditorialAccentPurple, activeTrackColor = EditorialAccentPurple)
                    )
                }

                // Pitch Slider
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Voice Pitch", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EditorialPrimary)
                        Text("${String.format("%.2f", userSettings.pitch)}x", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
                    }
                    Slider(
                        value = userSettings.pitch,
                        onValueChange = { p ->
                            viewModel.updateUserSettings { it.copy(pitch = p) }
                            viewModel.ttsManager.setPitch(p)
                        },
                        valueRange = 0.5f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = EditorialAccentPurple, activeTrackColor = EditorialAccentPurple)
                    )
                }

                SettingsSwitchRow(
                    title = "Auto-Play Pronunciation",
                    subtitle = "Automatically speak target phrases when entering practice screens.",
                    checked = userSettings.autoPlayPronunciation,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(autoPlayPronunciation = valOn) } }
                )

                // Microphone Permission Card
                Surface(
                    color = if (hasMicPermission) EditorialAccentPurple.copy(alpha = 0.08f) else EditorialAccentPurple.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (hasMicPermission) EditorialAccentPurple.copy(alpha = 0.3f) else EditorialAccentPurple.copy(alpha = 0.25f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Microphone Permission", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                            Text(
                                if (hasMicPermission) "Status: Allowed for voice input" else "Status: Not granted",
                                fontSize = 11.sp,
                                color = EditorialTheme.colors.textSecondary
                            )
                        }
                        if (!hasMicPermission) {
                            Button(
                                onClick = {
                                    micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Grant Permission", fontSize = 11.sp)
                            }
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EditorialAccentPurple)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Pronunciation Feedback Style", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Supportive", "Detailed", "Concise").forEach { style ->
                        SettingsPillChip(
                            selected = userSettings.pronunciationFeedbackStyle == style,
                            onClick = { viewModel.updateUserSettings { it.copy(pronunciationFeedbackStyle = style) } },
                            label = style
                        )
                    }
                }
            }
        }

        // SECTION E: Notifications & Daily Reminders
        if (matchesQuery("notification", "reminder", "daily", "time", "picker", "challenge", "message", "workmanager")) {
            SettingsCategoryCard(
                title = "5. Notifications & Daily Learning",
                subtitle = "Schedule daily practice alerts, challenge notifications, and quiet hours.",
                icon = Icons.Default.Notifications
            ) {
                SettingsSwitchRow(
                    title = "Daily Practice Reminder",
                    subtitle = "Receive periodic reminders to complete your daily lesson.",
                    checked = userSettings.dailyReminderEnabled,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(dailyReminderEnabled = valOn) } }
                )

                if (userSettings.dailyReminderEnabled) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showTimePickerModal = true }
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Reminder Time", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EditorialPrimary)
                            Text(
                                String.format("%02d:%02d daily", userSettings.reminderHour, userSettings.reminderMinute),
                                fontSize = 12.sp,
                                color = EditorialTheme.colors.textSecondary
                            )
                        }
                        OutlinedButton(onClick = { showTimePickerModal = true }, shape = RoundedCornerShape(8.dp)) {
                            Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Set Time", fontSize = 12.sp)
                        }
                    }
                }

                SettingsSwitchRow(
                    title = "Daily Challenge Reminder",
                    subtitle = "Get notified when today's Lisan Daily grammar question is ready.",
                    checked = userSettings.dailyChallengeReminderEnabled,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(dailyChallengeReminderEnabled = valOn) } }
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) {
                    Surface(
                        color = EditorialAccentPurple.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.25f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Post Notifications Permission",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Required to send daily practice and challenge reminders.",
                                    fontSize = 11.sp,
                                    color = EditorialTheme.colors.textSecondary
                                )
                            }
                            Button(
                                onClick = { notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                                colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Allow", fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text("Reminder Message Style", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf("Gentle", "Motivational", "Minimal").forEach { style ->
                        SettingsPillChip(
                            selected = userSettings.reminderMessageStyle == style,
                            onClick = { viewModel.updateUserSettings { it.copy(reminderMessageStyle = style) } },
                            label = style
                        )
                    }
                }
            }
        }

        // SECTION F: Data, Storage & Offline Mode
        if (matchesQuery("data", "storage", "offline", "cache", "export", "delete", "clear", "fallback", "network")) {
            SettingsCategoryCard(
                title = "6. Data & Offline Storage",
                subtitle = "Manage local storage, offline grammar engine, export backups, or clear data.",
                icon = Icons.Default.Storage
            ) {
                // Connection State Card
                Surface(
                    color = if (isOnline) Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isOnline) Color(0xFF81C784) else Color(0xFFFFB74D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isOnline) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (isOnline) Color(0xFF2E7D32) else Color(0xFFE65100)
                        )
                        Column {
                            Text(
                                if (isOnline) "Status: Online (Connected to AI Cloud)" else "Status: Offline (Local Engine Active)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isOnline) Color(0xFF1B5E20) else Color(0xFFBF360C)
                            )
                            Text(
                                "When offline, Lisan seamlessly uses cached lessons and local grammar rules.",
                                fontSize = 11.sp,
                                color = EditorialTheme.colors.textSecondary
                            )
                        }
                    }
                }

                SettingsSwitchRow(
                    title = "Auto-Use Offline Fallback",
                    subtitle = "Automatically route to local grammar engine if network is slow or offline.",
                    checked = userSettings.autoOfflineFallback,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(autoOfflineFallback = valOn) } }
                )

                HorizontalDivider(color = EditorialTheme.colors.border, modifier = Modifier.padding(vertical = 4.dp))

                // Cache & Data Buttons
                OutlinedButton(
                    onClick = { showClearCacheDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Cached Lessons")
                }

                OutlinedButton(
                    onClick = {
                        viewModel.exportLearningData { exportedFile ->
                            if (exportedFile != null && exportedFile.exists()) {
                                try {
                                    val uri = FileProvider.getUriForFile(
                                        context,
                                        "${context.packageName}.fileprovider",
                                        exportedFile
                                    )
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/json"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Export Lisan Learning Data"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Export saved to: ${exportedFile.name}", Toast.LENGTH_LONG).show()
                                }
                            } else {
                                Toast.makeText(context, "Failed to export data.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export Learning Data (JSON)")
                }

                Button(
                    onClick = { showDeleteAllDataDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete All Learning Data")
                }
            }
        }

        // SECTION G: Privacy & AI
        if (matchesQuery("privacy", "ai", "conversation", "history", "analytics", "policy", "terms")) {
            SettingsCategoryCard(
                title = "7. Privacy & AI Controls",
                subtitle = "Manage conversation history storage, analytics consent, and terms.",
                icon = Icons.Default.Security
            ) {
                SettingsSwitchRow(
                    title = "Save Conversation History",
                    subtitle = "Store AI tutor chat logs in local Room database.",
                    checked = userSettings.saveConversationHistory,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(saveConversationHistory = valOn) } }
                )

                SettingsSwitchRow(
                    title = "Improve Lisan AI (Analytics Consent)",
                    subtitle = "Allow anonymous diagnostic learning stats to improve grammar models.",
                    checked = userSettings.analyticsConsent,
                    onCheckedChange = { valOn -> viewModel.updateUserSettings { it.copy(analyticsConsent = valOn) } }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { showPrivacyDialog = true }) {
                        Text("Privacy Policy", fontSize = 12.sp, color = EditorialAccentPurple)
                    }
                    TextButton(onClick = { showTermsDialog = true }) {
                        Text("Terms of Use", fontSize = 12.sp, color = EditorialAccentPurple)
                    }
                }
            }
        }



        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SettingsCategoryCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    EditorialCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(EditorialAccentPurple.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = EditorialAccentPurple, modifier = Modifier.size(20.dp))
                }
                Column {
                    Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    Text(text = subtitle, fontSize = 11.sp, color = EditorialTheme.colors.textSecondary)
                }
            }
            HorizontalDivider(color = EditorialTheme.colors.border)
            content()
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EditorialPrimary)
            Text(text = subtitle, fontSize = 11.sp, color = EditorialTheme.colors.textSecondary, lineHeight = 14.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = EditorialAccentPurple,
                uncheckedThumbColor = EditorialTheme.colors.textMuted,
                uncheckedTrackColor = EditorialTheme.colors.container
            )
        )
    }
}

@Composable
fun SettingsPillChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) EditorialAccentPurple else Color(0xFFF1F5F9))
            .border(
                width = 1.dp,
                color = if (selected) EditorialAccentPurple else Color(0xFFCBD5E1),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color.White else EditorialPrimary,
            maxLines = 1
        )
    }
}
