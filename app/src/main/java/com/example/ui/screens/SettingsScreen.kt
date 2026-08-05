package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialSurface
import com.example.ui.viewmodel.LisanViewModel
import com.example.utils.ServerVoice

@Composable
fun SettingsScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val learnerProfile by viewModel.learnerProfile.collectAsState()
    val selectedVoiceEn by viewModel.ttsManager.selectedVoiceIdEn.collectAsState()
    val selectedVoiceAr by viewModel.ttsManager.selectedVoiceIdAr.collectAsState()
    val selectedVoiceUr by viewModel.ttsManager.selectedVoiceIdUr.collectAsState()
    
    val availableVoices by viewModel.ttsManager.availableVoices.collectAsState()

    val currentApiKey by viewModel.geminiApiKey.collectAsState()
    var apiKeyInput by remember(currentApiKey) { mutableStateOf(currentApiKey) }
    var keySaveStatus by remember { mutableStateOf<String?>(null) }
    var memoryStatusMessage by remember { mutableStateOf<String?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    var showEnglishDropdown by remember { mutableStateOf(false) }
    var showArabicDropdown by remember { mutableStateOf(false) }
    var showUrduDropdown by remember { mutableStateOf(false) }

    val englishVoices = availableVoices.filter { it.languageCode == "en-US" }
    val arabicVoices = availableVoices.filter { it.languageCode == "ar-XA" }
    val urduVoices = availableVoices.filter { it.languageCode == "ur-PK" }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "App & API Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialPrimary
        )

        // Gemini API Key Card
        EditorialCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "GEMINI API KEY CONFIGURATION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple,
                    letterSpacing = 1.2.sp
                )

                Text(
                    text = "Enter or update your Gemini API key (supports AIza... and AQ... keys):",
                    fontSize = 13.sp,
                    color = EditorialPrimary
                )

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = {
                        apiKeyInput = it
                        keySaveStatus = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Gemini API Key") },
                    placeholder = { Text("AQ.Ab8RN... or AIzaSy...") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Button(
                    onClick = {
                        viewModel.setGeminiApiKey(apiKeyInput)
                        keySaveStatus = "API Key saved successfully!"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save API Key")
                }

                if (keySaveStatus != null) {
                    Text(
                        text = keySaveStatus!!,
                        color = Color(0xFF2E7D32),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Automatic Long-Term Learning Memory & Privacy Card
        EditorialCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = EditorialAccentPurple
                        )
                        Text(
                            text = "AUTOMATIC LEARNING MEMORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple,
                            letterSpacing = 1.2.sp
                        )
                    }
                    Switch(
                        checked = learnerProfile.isMemoryEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.toggleMemoryEnabled(enabled)
                            memoryStatusMessage = if (enabled) "Learning memory enabled." else "Learning memory disabled."
                        }
                    )
                }

                Text(
                    text = "Tutor AI automatically updates your learning profile after questions, quizzes, challenges, and sentence analyses to personalize every answer.",
                    fontSize = 13.sp,
                    color = EditorialPrimary
                )

                Divider(color = EditorialPrimary.copy(alpha = 0.1f))

                // Learner Profile Summary Details
                Text(
                    text = "LEARNER PROFILE SUMMARY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Estimated Level:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text("${learnerProfile.learningLevel.overall} (${(learnerProfile.learningLevel.confidence * 100).toInt()}% confidence)", fontSize = 13.sp, color = EditorialAccentPurple)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Practice Accuracy:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    val acc = if (learnerProfile.totalQuestionsAnswered > 0) {
                        "${((learnerProfile.correctAnswersCount.toFloat() / learnerProfile.totalQuestionsAnswered) * 100).toInt()}% (${learnerProfile.correctAnswersCount}/${learnerProfile.totalQuestionsAnswered})"
                    } else "0% (0 answered)"
                    Text(acc, fontSize = 13.sp)
                }

                if (learnerProfile.commonMistakes.isNotEmpty()) {
                    Text("Detected Weak Areas:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    learnerProfile.commonMistakes.take(3).forEach { mistake ->
                        Text("• ${mistake.topic}: ${mistake.pattern}", fontSize = 12.sp, color = EditorialPrimary.copy(alpha = 0.8f))
                    }
                }

                if (learnerProfile.usefulMemories.isNotEmpty()) {
                    Text("Active Memory Highlights:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    learnerProfile.usefulMemories.take(4).forEach { memory ->
                        Text("• $memory", fontSize = 12.sp, color = EditorialPrimary.copy(alpha = 0.8f))
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                OutlinedButton(
                    onClick = { showClearConfirmDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Red)
                ) {
                    Icon(Icons.Default.DeleteForever, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Learning History")
                }

                if (memoryStatusMessage != null) {
                    Text(
                        text = memoryStatusMessage!!,
                        color = EditorialAccentPurple,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (showClearConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showClearConfirmDialog = false },
                title = { Text("Clear Learning History?") },
                text = { Text("This will safely reset your learning profile, estimated level, and mistake memory. Saved notes, bookmarked items, and idioms will be preserved.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearLearningHistory()
                            showClearConfirmDialog = false
                            memoryStatusMessage = "Learning history cleared safely (saved items preserved)."
                        }
                    ) {
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

        Text(
            text = "Cloud Voice Settings (Admin)",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialPrimary
        )

        EditorialCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // English Voice Dropdown
                Text("English Voice", fontWeight = FontWeight.Bold)
                Box {
                    Button(
                        onClick = { showEnglishDropdown = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialSurface, contentColor = EditorialPrimary),
                        border = BorderStroke(1.dp, EditorialPrimary)
                    ) {
                        val currentName = getVoiceLabel(selectedVoiceEn, englishVoices) ?: "Default English"
                        Text(currentName)
                    }

                    DropdownMenu(
                        expanded = showEnglishDropdown,
                        onDismissRequest = { showEnglishDropdown = false }
                    ) {
                        englishVoices.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text(voice.name) },
                                onClick = {
                                    viewModel.ttsManager.setEnglishVoice(voice.id)
                                    showEnglishDropdown = false
                                }
                            )
                        }
                    }
                }

                // Arabic Voice Dropdown
                Text("Arabic Voice", fontWeight = FontWeight.Bold)
                Box {
                    Button(
                        onClick = { showArabicDropdown = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialSurface, contentColor = EditorialPrimary),
                        border = BorderStroke(1.dp, EditorialPrimary)
                    ) {
                        val currentName = getVoiceLabel(selectedVoiceAr, arabicVoices) ?: "Default Arabic"
                        Text(currentName)
                    }

                    DropdownMenu(
                        expanded = showArabicDropdown,
                        onDismissRequest = { showArabicDropdown = false }
                    ) {
                        arabicVoices.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text(voice.name) },
                                onClick = {
                                    viewModel.ttsManager.setArabicVoice(voice.id)
                                    showArabicDropdown = false
                                }
                            )
                        }
                    }
                }
                
                // Urdu Voice Dropdown
                Text("Urdu Voice", fontWeight = FontWeight.Bold)
                Box {
                    Button(
                        onClick = { showUrduDropdown = true },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialSurface, contentColor = EditorialPrimary),
                        border = BorderStroke(1.dp, EditorialPrimary)
                    ) {
                        val currentName = getVoiceLabel(selectedVoiceUr, urduVoices) ?: "Default Urdu"
                        Text(currentName)
                    }

                    DropdownMenu(
                        expanded = showUrduDropdown,
                        onDismissRequest = { showUrduDropdown = false }
                    ) {
                        urduVoices.forEach { voice ->
                            DropdownMenuItem(
                                text = { Text(voice.name) },
                                onClick = {
                                    viewModel.ttsManager.setUrduVoice(voice.id)
                                    showUrduDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    val testText = "English Grammar: The Present Continuous tense is used for ongoing actions right now. For example, I am speaking.\n" +
                    "Urdu Translation: اردو میں، ہم 'رہا ہے' کا استعمال کرتے ہیں، جیسے 'میں بول رہا ہوں'۔\n" +
                    "Arabic Reference: كما تعلمت في اللغة العربية، نستخدم الفعل المضارع المستمر.\n" +
                    "Numbers: 1, 2, 3, 4, 5, 100.\n" +
                    "Punctuation & Long Sentence: Hello! How are you today? This is a longer sentence designed to test the natural pausing, intonation, and clarity of the selected server-side text-to-speech voice across multiple languages."
                    
                    viewModel.ttsManager.speak(testText)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)
            ) {
                Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Test All Voices")
            }

            Button(
                onClick = {
                    viewModel.ttsManager.setEnglishVoice("en-US-Standard-D")
                    viewModel.ttsManager.setArabicVoice("ar-XA-Standard-B")
                    viewModel.ttsManager.setUrduVoice("ur-PK-Wavenet-B")
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset")
            }
        }
    }
}

private fun getVoiceLabel(selectedId: String?, voices: List<ServerVoice>): String? {
    if (selectedId.isNullOrBlank()) return null
    val voice = voices.find { it.id == selectedId }
    return voice?.name
}
