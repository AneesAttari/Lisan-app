package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.PronunciationItem
import com.example.data.model.PronunciationHistoryItem
import com.example.data.repository.PronunciationPracticeRepository
import com.example.ui.theme.EditorialTheme
import com.example.ui.viewmodel.LisanViewModel
import com.example.utils.WordStatus
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PronunciationPracticeScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val colors = EditorialTheme.colors
    val context = LocalContext.current

    val currentCategory by viewModel.pronunciationCategory.collectAsState()
    val searchQuery by viewModel.pronunciationSearchQuery.collectAsState()
    val selectedItem by viewModel.selectedPronunciationItem.collectAsState()
    val customText by viewModel.customPronunciationText.collectAsState()
    val evaluationResult by viewModel.pronunciationEvaluationResult.collectAsState()
    val isRecording by viewModel.isRecordingPronunciation.collectAsState()
    val activeSpeakingId by viewModel.activeSpeakingId.collectAsState()
    val history by viewModel.pronunciationHistory.collectAsState()

    // Recording permission request launcher
    var hasRecordingPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasRecordingPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasRecordingPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Load list items based on active tab
    val practiceItems = remember(currentCategory) {
        when (currentCategory) {
            "word" -> PronunciationPracticeRepository.words
            "idiom" -> PronunciationPracticeRepository.idioms
            "sentence" -> PronunciationPracticeRepository.sentences
            else -> emptyList()
        }
    }

    // Filter practice items
    val filteredItems = remember(practiceItems, searchQuery) {
        if (searchQuery.isBlank()) {
            practiceItems
        } else {
            practiceItems.filter {
                it.text.contains(searchQuery, ignoreCase = true) ||
                it.meaning.contains(searchQuery, ignoreCase = true) ||
                it.translation.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(16.dp)
    ) {
        // Teacher Welcome Header Message
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            color = colors.container,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, colors.borderSoft)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = colors.accentPurple.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Hearing,
                            contentDescription = null,
                            tint = colors.accentPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Welcome to Lisan Pronunciation Practice! Learn correct pronunciation by listening with TTS and speaking with immediate feedback.",
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Segmented Tab Controls
        val tabs = listOf(
            Triple("custom", "Custom", "category_tab_custom"),
            Triple("word", "Words", "category_tab_word"),
            Triple("idiom", "Idioms", "category_tab_idiom"),
            Triple("sentence", "Sentences", "category_tab_sentence"),
            Triple("history", "History", "category_tab_history")
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tabs.forEach { (catId, label, tag) ->
                val isSelected = currentCategory == catId
                Surface(
                    modifier = Modifier
                        .testTag(tag)
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { viewModel.setPronunciationCategory(catId) },
                    color = if (isSelected) colors.accentPurple else colors.container,
                    border = BorderStroke(1.dp, if (isSelected) colors.accentPurple else colors.borderSoft),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else colors.textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        Divider(color = colors.divider, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

        // Active content based on Category
        if (currentCategory == "history") {
            // HISTORY TAB
            HistoryTabContent(
                history = history,
                onDelete = { viewModel.deletePronunciationHistoryItem(it) },
                onClearAll = { viewModel.clearPronunciationHistory() }
            )
        } else if (currentCategory == "custom") {
            // CUSTOM PRACTICE TAB
            CustomPracticeContent(
                customText = customText,
                onTextChange = { viewModel.setCustomPronunciationText(it) },
                isRecording = isRecording,
                activeSpeakingId = activeSpeakingId,
                evaluationResult = evaluationResult,
                onPlayTts = { viewModel.playPronunciationTts(customText, "custom_practice") },
                onRecord = {
                    if (!hasRecordingPermission) {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        if (isRecording) {
                            viewModel.stopListeningForPronunciation()
                        } else {
                            viewModel.startListeningForPronunciation(customText, "custom")
                        }
                    }
                }
            )
        } else {
            // BROWSE WORDS/IDIOMS/SENTENCES TABS
            Row(modifier = Modifier.fillMaxSize()) {
                // Left Column: List of items (Width: 40%)
                Column(
                    modifier = Modifier
                        .weight(0.42f)
                        .fillMaxHeight()
                        .padding(end = 8.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.setPronunciationSearchQuery(it) },
                        placeholder = { Text("Search...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp)) },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { viewModel.setPronunciationSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                }
                            }
                        } else null,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colors.surface,
                            unfocusedContainerColor = colors.surface,
                            focusedBorderColor = colors.accentPurple,
                            unfocusedBorderColor = colors.borderSoft
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("pronunciation_search_input"),
                        textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(colors.surface, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No items found",
                                fontSize = 11.sp,
                                color = colors.textMuted,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            items(filteredItems, key = { it.id }) { item ->
                                val isSelected = selectedItem?.id == item.id
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { viewModel.selectPronunciationItem(item) },
                                    color = if (isSelected) colors.accentPurple.copy(alpha = 0.08f) else colors.container,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) colors.accentPurple else colors.borderSoft
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = item.text,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) colors.accentPurple else colors.textPrimary
                                        )
                                        if (!item.phonetic.isNullOrBlank()) {
                                            Text(
                                                text = item.phonetic,
                                                fontSize = 10.sp,
                                                color = colors.textMuted,
                                                modifier = Modifier.padding(top = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Right Column: Active Card & Feedback Panel (Width: 58%)
                Column(
                    modifier = Modifier
                        .weight(0.58f)
                        .fillMaxHeight()
                        .padding(start = 8.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (selectedItem == null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            color = colors.surface,
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, colors.borderSoft)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                                    contentDescription = null,
                                    tint = colors.textMuted,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Select a practice item from the list to begin listening and recording your practice.",
                                    fontSize = 11.sp,
                                    color = colors.textSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    } else {
                        val item = selectedItem!!
                        ActivePracticeCard(
                            item = item,
                            isRecording = isRecording,
                            activeSpeakingId = activeSpeakingId,
                            evaluationResult = evaluationResult,
                            onPlayTts = { viewModel.playPronunciationTts(item.text, item.id) },
                            onRecord = {
                                if (!hasRecordingPermission) {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                } else {
                                    if (isRecording) {
                                        viewModel.stopListeningForPronunciation()
                                    } else {
                                        viewModel.startListeningForPronunciation(item.text, item.category)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActivePracticeCard(
    item: PronunciationItem,
    isRecording: Boolean,
    activeSpeakingId: String?,
    evaluationResult: com.example.utils.PronunciationResult?,
    onPlayTts: () -> Unit,
    onRecord: () -> Unit
) {
    val colors = EditorialTheme.colors
    val isSpeaking = activeSpeakingId == item.id

    // Recording animation (pulser scale)
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        color = colors.container,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Category & Level Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = colors.accentPurple.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.category.uppercase(Locale.getDefault()),
                        fontSize = 9.sp,
                        color = colors.accentPurple,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    color = colors.pillBg,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.difficulty,
                        fontSize = 9.sp,
                        color = colors.textSecondary,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Text Target to Pronounce
            Text(
                text = item.text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = colors.primary,
                lineHeight = 24.sp
            )

            if (!item.phonetic.isNullOrBlank()) {
                Surface(
                    color = colors.surface,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, colors.borderSoft),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "[ ${item.phonetic} ]",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = colors.textMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Urdu Translation with Right-to-Left alignment
            Surface(
                color = colors.surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, colors.borderSoft)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = "ترجمہ (Translation):",
                        fontSize = 9.sp,
                        color = colors.textMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.translation,
                        fontSize = 13.sp,
                        color = colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Right,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Definition & Meaning
            Text(
                text = "Meaning:",
                fontSize = 10.sp,
                color = colors.textMuted,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = item.meaning,
                fontSize = 11.sp,
                color = colors.textSecondary,
                lineHeight = 15.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Example sentence
            Text(
                text = "Example Sentence:",
                fontSize = 10.sp,
                color = colors.textMuted,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "\"${item.example}\"",
                fontSize = 11.sp,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = colors.textPrimary,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // TTS Play and Recording Button Panel
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // TTS Audio Play Button
                Button(
                    onClick = onPlayTts,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSpeaking) colors.accentPurple.copy(alpha = 0.15f) else colors.container,
                        contentColor = colors.accentPurple
                    ),
                    border = BorderStroke(1.dp, colors.accentPurple),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .testTag("play_tts_button")
                ) {
                    Icon(
                        imageVector = if (isSpeaking) Icons.Default.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Listen correct pronunciation",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isSpeaking) "Stop" else "Listen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                // Microphone Speak Recording Button
                Button(
                    onClick = onRecord,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRecording) Color.Red else colors.accentPurple,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .height(40.dp)
                        .scale(if (isRecording) pulseScale else 1.0f)
                        .testTag("record_pronunciation_button")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Speak and record",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isRecording) "Listening..." else "Record", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Interactive evaluation result feedback details
    if (evaluationResult != null) {
        PronunciationResultPanel(result = evaluationResult)
    }
}

@Composable
fun PronunciationResultPanel(result: com.example.utils.PronunciationResult) {
    val colors = EditorialTheme.colors

    // Score badge color based on evaluation score
    val scoreColor = when {
        result.score >= 90 -> Color(0xFF2E7D32) // Deep green
        result.score >= 75 -> Color(0xFF4CAF50) // Green
        result.score >= 50 -> Color(0xFFF57C00) // Orange/Warning
        else -> Color(0xFFD32F2F) // Red/Error
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .animateContentSize(),
        color = colors.container,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Overall score title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pronunciation Feedback",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = colors.textPrimary
                )

                Surface(
                    color = scoreColor,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Score: ${result.score}/100",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Teacher's personalized simple Arabic support comment
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = colors.surface,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, colors.borderSoft)
            ) {
                Text(
                    text = result.teacherComment,
                    fontSize = 11.sp,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(10.dp),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Word alignment header
            Text(
                text = "Word-by-word Analysis:",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textMuted
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Word by word feedback wrapping labels flow layout
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                // Simple wrapping logic for labels using row or box
                // We'll group them into small chunks of rows or do a flow behavior
                val chunkedWords = result.feedbackWords.chunked(3)
                chunkedWords.forEach { rowWords ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        rowWords.forEach { wordFb ->
                            val borderCol = when (wordFb.status) {
                                WordStatus.MATCH -> Color(0xFF4CAF50)
                                WordStatus.INCORRECT -> Color(0xFFD32F2F)
                                WordStatus.MISSING -> Color.Gray
                                WordStatus.EXTRA -> Color(0xFFFF9800)
                            }
                            val bgCol = borderCol.copy(alpha = 0.05f)

                            Surface(
                                color = bgCol,
                                border = BorderStroke(1.dp, borderCol),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Column(
                                    modifier = Modifier.padding(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = wordFb.word,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (wordFb.status == WordStatus.MISSING) colors.textMuted else colors.textPrimary,
                                        textAlign = TextAlign.Center
                                    )
                                    if (wordFb.detail.isNotBlank()) {
                                        Text(
                                            text = wordFb.detail,
                                            fontSize = 7.sp,
                                            color = colors.textMuted,
                                            textAlign = TextAlign.Center
                                        )
                                    } else {
                                        Text(
                                            text = wordFb.status.name,
                                            fontSize = 7.sp,
                                            color = borderCol,
                                            fontWeight = FontWeight.SemiBold,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metric breakdown summary row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                MetricIndicator(count = result.matchCount, label = "Correct", color = Color(0xFF4CAF50))
                MetricIndicator(count = result.incorrectCount, label = "Incorrect", color = Color(0xFFD32F2F))
                MetricIndicator(count = result.missingCount, label = "Missing", color = Color.Gray)
                MetricIndicator(count = result.extraCount, label = "Extra", color = Color(0xFFFF9800))
            }
        }
    }
}

@Composable
fun MetricIndicator(count: Int, label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Text(
            text = "$label: $count",
            fontSize = 9.sp,
            color = EditorialTheme.colors.textSecondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun CustomPracticeContent(
    customText: String,
    onTextChange: (String) -> Unit,
    isRecording: Boolean,
    activeSpeakingId: String?,
    evaluationResult: com.example.utils.PronunciationResult?,
    onPlayTts: () -> Unit,
    onRecord: () -> Unit
) {
    val colors = EditorialTheme.colors
    val isSpeaking = activeSpeakingId == "custom_practice"

    // Infinite pulse scale
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse_custom")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale_custom"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            color = colors.container,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, colors.border)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Pronunciation Practice • مشقِ تلفظ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.primary
                )
                Text(
                    text = "Type or paste any English word, idiom, or sentence below to practice its correct pronunciation.\n\nApp on Uptodown: https://lisangrammar.uptodown.com/android",
                    fontSize = 10.sp,
                    color = colors.textSecondary,
                    lineHeight = 14.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Custom phrase edit text field
                OutlinedTextField(
                    value = customText,
                    onValueChange = onTextChange,
                    placeholder = { Text("Enter custom English text to practice (e.g. \"The weather is delightful today.\")", fontSize = 12.sp) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colors.surface,
                        unfocusedContainerColor = colors.surface,
                        focusedBorderColor = colors.accentPurple,
                        unfocusedBorderColor = colors.borderSoft
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("custom_text_input"),
                    textStyle = LocalTextStyle.current.copy(fontSize = 13.sp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Custom control buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Play TTS Audio button
                    Button(
                        onClick = onPlayTts,
                        enabled = customText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSpeaking) colors.accentPurple.copy(alpha = 0.15f) else colors.container,
                            contentColor = colors.accentPurple
                        ),
                        border = BorderStroke(1.dp, colors.accentPurple),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .testTag("play_tts_button")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeMute else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Listen correct custom text pronunciation",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isSpeaking) "Stop" else "Listen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Speak record button
                    Button(
                        onClick = onRecord,
                        enabled = customText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRecording) Color.Red else colors.accentPurple,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .height(40.dp)
                            .scale(if (isRecording) pulseScale else 1.0f)
                            .testTag("record_pronunciation_button")
                    ) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Speak and record custom text",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isRecording) "Listening..." else "Record", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (evaluationResult != null) {
            PronunciationResultPanel(result = evaluationResult)
        }
    }
}

@Composable
fun HistoryTabContent(
    history: List<PronunciationHistoryItem>,
    onDelete: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val colors = EditorialTheme.colors
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault()) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Practice Attempt History",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = colors.textPrimary
            )

            if (history.isNotEmpty()) {
                TextButton(
                    onClick = onClearAll,
                    modifier = Modifier.testTag("clear_history_button")
                ) {
                    Icon(Icons.Default.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Red)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Clear All", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colors.container, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HistoryToggleOff,
                        contentDescription = null,
                        tint = colors.textMuted,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your practice history is empty. Attempt any word, idiom, or sentence practice to save and track your score progress here!",
                        fontSize = 11.sp,
                        color = colors.textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(history, key = { it.id }) { log ->
                    val badgeColor = when {
                        log.score >= 90 -> Color(0xFF2E7D32)
                        log.score >= 75 -> Color(0xFF4CAF50)
                        log.score >= 50 -> Color(0xFFF57C00)
                        else -> Color(0xFFD32F2F)
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = colors.container,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, colors.borderSoft)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    color = badgeColor,
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Score: ${log.score}/100",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = dateFormat.format(Date(log.timestamp)),
                                        fontSize = 9.sp,
                                        color = colors.textMuted
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = { onDelete(log.id) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete entry",
                                            tint = colors.textMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "Target: ${log.targetText}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )

                            if (log.spokenText.isNotBlank()) {
                                Text(
                                    text = "Spoken: \"${log.spokenText}\"",
                                    fontSize = 11.sp,
                                    color = colors.textSecondary,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = colors.surface,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = log.accuracyFeedback,
                                    fontSize = 10.sp,
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(6.dp),
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
