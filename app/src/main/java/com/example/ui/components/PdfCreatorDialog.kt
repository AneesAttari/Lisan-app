package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.IdiomRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import com.example.utils.PdfCreatorHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class PdfExportMode(val title: String, val icon: ImageVector, val urduLabel: String) {
    CUSTOM_TOPIC("Topic Guide", Icons.Default.AutoAwesome, "کسی بھی موضوع پر"),
    EXAM("Exam Paper", Icons.Default.School, "امتحانی پرچہ"),
    CHAT_TRANSCRIPT("Chat Export", Icons.Default.Forum, "گفتگو محفوظ کریں"),
    STUDY_NOTES("Study Notes", Icons.Default.EditNote, "نوٹس اور قواعد"),
    VOCABULARY("Vocab Sheet", Icons.Default.MenuBook, "الفاظ و محاورات"),
    WORKSHEET("Worksheet", Icons.Default.Assignment, "مشقی پرچہ")
}

private fun parseVocabItems(text: String): List<PdfCreatorHelper.PdfBlock.VocabCard> {
    val items = mutableListOf<PdfCreatorHelper.PdfBlock.VocabCard>()
    val regex = """<vocab_item>([\s\S]*?)</vocab_item>""".toRegex()
    val wordRegex = """<word>([\s\S]*?)</word>""".toRegex()
    val posRegex = """<pos>([\s\S]*?)</pos>""".toRegex()
    val meaningRegex = """<meaning>([\s\S]*?)</meaning>""".toRegex()
    val exampleEnRegex = """<example_en>([\s\S]*?)</example_en>""".toRegex()
    val exampleUrRegex = """<example_ur>([\s\S]*?)</example_ur>""".toRegex()

    for (match in regex.findAll(text)) {
        val blockText = match.groupValues[1]
        val word = wordRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: ""
        val pos = posRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: "Noun"
        val meaning = meaningRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: ""
        val exampleEn = exampleEnRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: ""
        val exampleUr = exampleUrRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: ""
        if (word.isNotBlank() && meaning.isNotBlank()) {
            items.add(PdfCreatorHelper.PdfBlock.VocabCard(word, pos, meaning, exampleEn, exampleUr))
        }
    }
    return items
}

private fun parseExerciseItems(text: String): List<PdfCreatorHelper.PdfBlock.ExerciseItem> {
    val items = mutableListOf<PdfCreatorHelper.PdfBlock.ExerciseItem>()
    val regex = """<exercise_item>([\s\S]*?)</exercise_item>""".toRegex()
    val numRegex = """<number>([\s\S]*?)</number>""".toRegex()
    val questionRegex = """<question>([\s\S]*?)</question>""".toRegex()
    val optionsRegex = """<options>([\s\S]*?)</options>""".toRegex()
    val hintRegex = """<hint>([\s\S]*?)</hint>""".toRegex()
    val answerRegex = """<answer>([\s\S]*?)</answer>""".toRegex()

    for (match in regex.findAll(text)) {
        val blockText = match.groupValues[1]
        val num = numRegex.find(blockText)?.groupValues?.get(1)?.trim()?.toIntOrNull() ?: (items.size + 1)
        val question = questionRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: ""
        val optionsStr = optionsRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: ""
        val options = optionsStr.split("|").map { it.trim() }.filter { it.isNotBlank() }
        val hint = hintRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: ""
        val answer = answerRegex.find(blockText)?.groupValues?.get(1)?.trim() ?: ""
        if (question.isNotBlank()) {
            items.add(PdfCreatorHelper.PdfBlock.ExerciseItem(num, question, options, hint, answer))
        }
    }
    return items
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfCreatorDialog(
    viewModel: LisanViewModel,
    examViewModel: com.example.ui.viewmodel.ExamViewModel? = null,
    initialMode: PdfExportMode = PdfExportMode.CUSTOM_TOPIC,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userName by viewModel.userName.collectAsState()
    val proficiencyLevel by viewModel.proficiencyLevel.collectAsState()
    val chatHistory by viewModel.chatHistory.collectAsState()
    val savedNotes by viewModel.savedNotes.collectAsState()
    val savedIdiomIds by viewModel.savedIdiomIds.collectAsState()

    var selectedMode by remember { mutableStateOf(initialMode) }
    var documentTitle by remember { mutableStateOf("Simple Present Tense • فعل حال مطلق") }
    var documentSubtitle by remember { mutableStateOf("Complete Guide with Examples, Urdu & Arabic References") }
    var authorName by remember { mutableStateOf(userName.ifBlank { "Lisan Learner" }) }
    var customTopicInput by remember {
        mutableStateOf("I want the first simple tense with examples with Urdu translation with references of Arabic")
    }
    
    // Mode-specific options
    var chatMessageRange by remember { mutableStateOf("ALL") } // ALL, LAST_5, LAST_10, LAST_20
    var customNotesContent by remember {
        mutableStateOf(
            "Topic: Present Perfect vs Simple Past\n\n" +
            "1. Rule: Present Perfect expresses actions connected with the present moment.\n" +
            "Urdu: فعل ماضی قریب (جیسے 'میں نے کام ختم کر لیا ہے')\n\n" +
            "2. Example: I have eaten breakfast. (میں ناشتہ کر چکا ہوں۔)\n\n" +
            "3. Key Difference: Use Past Simple for finished time in the past (yesterday, last night)."
        )
    }
    var includeUrdu by remember { mutableStateOf(true) }
    var includeGrammarTags by remember { mutableStateOf(true) }
    var includeTimestamps by remember { mutableStateOf(true) }
    var selectedTheme by remember { mutableStateOf(PdfCreatorHelper.PdfColorTheme.CLASSIC_EDITORIAL) }
    var isDetailedMasterclass by remember { mutableStateOf(false) }

    // Generation state
    var isGenerating by remember { mutableStateOf(false) }
    var generatedResult by remember { mutableStateOf<PdfCreatorHelper.GeneratedPdfResult?>(null) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    // Update default title when mode changes
    LaunchedEffect(selectedMode) {
        when (selectedMode) {
            PdfExportMode.CUSTOM_TOPIC -> {
                documentTitle = "Simple Present Tense • فعل حال مطلق"
                documentSubtitle = "Complete Guide with Examples, Urdu & Arabic References"
            }
            PdfExportMode.EXAM -> {
                val cfg = examViewModel?.config?.value
                val sourceStr = if (cfg?.sourceType == com.example.ui.viewmodel.ExamSourceType.BOOK_PDF) {
                    cfg.uploadedPdfName ?: cfg.selectedBook?.title ?: "English Grammar Exam"
                } else {
                    "Learned History & Topics Exam"
                }
                documentTitle = "English Exam Paper • $sourceStr"
                documentSubtitle = "Level: ${cfg?.difficulty ?: "Intermediate"} • Format: ${cfg?.mcqPercentage ?: "100%"} MCQs • ${cfg?.language ?: "English"}"
            }
            PdfExportMode.CHAT_TRANSCRIPT -> {
                documentTitle = "Tutor Conversation Transcript"
                documentSubtitle = "Interactive English-Urdu Tutoring Session"
            }
            PdfExportMode.STUDY_NOTES -> {
                documentTitle = "Custom Grammar & Study Notes"
                documentSubtitle = "Core Rules, Urdu Concepts & Examples"
            }
            PdfExportMode.VOCABULARY -> {
                documentTitle = "Essential Vocabulary & Idioms Sheet"
                documentSubtitle = "English-Urdu Bilingual Reference Guide"
            }
            PdfExportMode.WORKSHEET -> {
                documentTitle = "English Grammar Practice Worksheet"
                documentSubtitle = "Active Exercises with Urdu Guidance"
            }
        }
    }

    Dialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("pdf_creator_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = EditorialSurface,
            border = BorderStroke(1.5.dp, EditorialBorder),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // --- TOP HEADER ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = EditorialContainer,
                            border = BorderStroke(1.dp, EditorialBorderAccent),
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF Creator",
                                    tint = EditorialAccentPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Text(
                                text = "Lisan PDF Creator",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary
                            )
                            Text(
                                text = "پی ڈی ایف اسٹڈی گائیڈ تیار کریں",
                                fontSize = 11.sp,
                                color = EditorialTextMuted
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isGenerating,
                        modifier = Modifier.size(32.dp).testTag("pdf_creator_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = EditorialTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = EditorialBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // --- SCROLLABLE CONTENT BODY ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // MODE SELECTOR TABS
                    Text(
                        text = "1. SELECT DOCUMENT TYPE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        PdfExportMode.values().forEach { mode ->
                            val isSelected = selectedMode == mode
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) EditorialContainer else EditorialSurface,
                                border = BorderStroke(
                                    if (isSelected) 1.5.dp else 1.dp,
                                    if (isSelected) EditorialAccentPurple else EditorialBorder
                                ),
                                modifier = Modifier
                                    .clickable {
                                        selectedMode = mode
                                        generatedResult = null
                                    }
                                    .testTag("pdf_mode_${mode.name.lowercase()}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = mode.icon,
                                        contentDescription = null,
                                        tint = if (isSelected) EditorialAccentPurple else EditorialTextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Column {
                                        Text(
                                            text = mode.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) EditorialPrimary else EditorialTextSecondary
                                        )
                                        Text(
                                            text = mode.urduLabel,
                                            fontSize = 9.sp,
                                            color = EditorialTextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // DOCUMENT METADATA FIELDS
                    Text(
                        text = "2. DOCUMENT DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    OutlinedTextField(
                        value = documentTitle,
                        onValueChange = { documentTitle = it },
                        label = { Text("Document Title", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("pdf_title_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EditorialAccentPurple,
                            unfocusedBorderColor = EditorialBorder,
                            focusedTextColor = EditorialTextPrimary,
                            unfocusedTextColor = EditorialTextPrimary
                        )
                    )

                    OutlinedTextField(
                        value = documentSubtitle,
                        onValueChange = { documentSubtitle = it },
                        label = { Text("Subtitle / Subject", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EditorialAccentPurple,
                            unfocusedBorderColor = EditorialBorder,
                            focusedTextColor = EditorialTextPrimary,
                            unfocusedTextColor = EditorialTextPrimary
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = authorName,
                            onValueChange = { authorName = it },
                            label = { Text("Learner Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EditorialAccentPurple,
                                unfocusedBorderColor = EditorialBorder,
                                focusedTextColor = EditorialTextPrimary,
                                unfocusedTextColor = EditorialTextPrimary
                            )
                        )

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EditorialContainer,
                            border = BorderStroke(1.dp, EditorialBorder),
                            modifier = Modifier.align(Alignment.CenterVertically).padding(top = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                                Text("Level", fontSize = 9.sp, color = EditorialTextMuted)
                                Text(proficiencyLevel.labelEn, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                            }
                        }
                    }

                    // MODE-SPECIFIC CONFIGURATION
                    when (selectedMode) {
                        PdfExportMode.CUSTOM_TOPIC -> {
                            Text(
                                text = "3. SPECIFY ANY TOPIC OR PROMPT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary,
                                letterSpacing = 0.5.sp
                            )

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SuggestionChip(
                                    onClick = {
                                        customTopicInput = "I want the first simple tense with examples with Urdu translation with references of Arabic"
                                        documentTitle = "Simple Present Tense (فعل حال مطلق)"
                                        documentSubtitle = "Complete Rules, Examples, Urdu & Arabic References"
                                    },
                                    label = { Text("⚡ Simple Present (فعل حال)", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = {
                                        customTopicInput = "Explain Active and Passive voice with clear rules, examples in English, Urdu translation, and Arabic comparison"
                                        documentTitle = "Active vs Passive Voice (معروف و مجهول)"
                                        documentSubtitle = "Transformation Rules & Bilingual Analysis"
                                    },
                                    label = { Text("⚡ Active & Passive Voice", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = {
                                        customTopicInput = "Comprehensive guide on Prepositions of Place and Time with Urdu rules and Quranic Arabic references"
                                        documentTitle = "Prepositions Masterclass (حروفِ جار)"
                                        documentSubtitle = "In, On, At, By, For with Contextual Usages"
                                    },
                                    label = { Text("⚡ Prepositions Guide", fontSize = 11.sp) }
                                )
                                SuggestionChip(
                                    onClick = {
                                        customTopicInput = "Modal verbs (Can, Must, Should, May) with permission vs obligation rules and Urdu meanings"
                                        documentTitle = "Modal Verbs & Auxiliaries (افعالِ ناقصہ)"
                                        documentSubtitle = "Expressing Ability, Obligation & Advice"
                                    },
                                    label = { Text("⚡ Modal Verbs Sheet", fontSize = 11.sp) }
                                )
                            }

                            OutlinedTextField(
                                value = customTopicInput,
                                onValueChange = { customTopicInput = it },
                                label = { Text("Describe what kind of PDF document you want to create", fontSize = 11.sp) },
                                minLines = 3,
                                maxLines = 5,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pdf_custom_topic_prompt_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EditorialAccentPurple,
                                    unfocusedBorderColor = EditorialBorder
                                )
                            )
                        }

                        PdfExportMode.CHAT_TRANSCRIPT -> {
                            Text(
                                text = "3. CONVERSATION RANGE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("ALL" to "All (${chatHistory.size})", "LAST_5" to "Last 5", "LAST_10" to "Last 10", "LAST_20" to "Last 20").forEach { (key, label) ->
                                    val isSel = chatMessageRange == key
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { chatMessageRange = key },
                                        label = { Text(label, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EditorialContainer,
                                            selectedLabelColor = EditorialPrimary
                                        )
                                    )
                                }
                            }
                        }

                        PdfExportMode.STUDY_NOTES -> {
                            Text(
                                text = "3. NOTES CONTENT & QUICK TEMPLATES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary,
                                letterSpacing = 0.5.sp
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                SuggestionChip(
                                    onClick = {
                                        customNotesContent = "Tenses Summary (فعل اور زمانے)\n\n" +
                                                "1. Present Simple: Habitual actions • میں روزانہ پڑھتا ہوں (I study daily)\n" +
                                                "2. Present Continuous: Happening right now • وہ جا رہا ہے (He is going)\n" +
                                                "3. Present Perfect: Action just completed • ہم نے کھانا کھا لیا ہے (We have eaten)\n" +
                                                "4. Past Simple: Specific past event • کل بارش ہوئی (It rained yesterday)\n" +
                                                "5. Future Simple: Future intent • وہ کل آئے گا (He will come tomorrow)"
                                    },
                                    label = { Text("⚡ Tenses Sheet", fontSize = 11.sp) }
                                )

                                SuggestionChip(
                                    onClick = {
                                        customNotesContent = "Prepositions of Place & Time (حروفِ جار)\n\n" +
                                                "• IN: Enclosed spaces & long periods (in the room, in 2026, in summer)\n" +
                                                "• ON: Surfaces & specific days/dates (on the table, on Monday, on 14th August)\n" +
                                                "• AT: Precise locations & clock times (at the bus stop, at 5 PM, at night)\n" +
                                                "• UNDER / OVER: نیچے / اوپر (under the tree, over the bridge)"
                                    },
                                    label = { Text("⚡ Prepositions Guide", fontSize = 11.sp) }
                                )

                                SuggestionChip(
                                    onClick = {
                                        customNotesContent = "Modal Verbs & Urdu Equivalents (افعالِ ناقصہ)\n\n" +
                                                "• CAN: Ability / طاقت یا قابلیت (I can speak English • میں انگریزی بول سکتا ہوں)\n" +
                                                "• MUST: Strong obligation / لازمی حکم (You must study • آپ کو لازمی پڑھنا چاہیے)\n" +
                                                "• SHOULD: Advice / مشورہ (You should rest • آپ کو آرام کرنا چاہیے)\n" +
                                                "• MAY: Permission / اجازت یا امکان (May I come in? • کیا میں اندر آ سکتا ہوں؟)"
                                    },
                                    label = { Text("⚡ Modal Verbs", fontSize = 11.sp) }
                                )
                            }

                            OutlinedTextField(
                                value = customNotesContent,
                                onValueChange = { customNotesContent = it },
                                label = { Text("Study Sheet Content", fontSize = 11.sp) },
                                minLines = 5,
                                maxLines = 8,
                                modifier = Modifier.fillMaxWidth().testTag("pdf_custom_notes_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EditorialAccentPurple,
                                    unfocusedBorderColor = EditorialBorder
                                )
                            )
                        }

                        PdfExportMode.VOCABULARY -> {
                            Text(
                                text = "3. VOCABULARY DATASET",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EditorialContainer,
                                border = BorderStroke(1.dp, EditorialBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = EditorialAccentPurple)
                                    Column {
                                        Text(
                                            text = "Ready to compile ${savedIdiomIds.size.coerceAtLeast(10)} curated idioms & vocabulary flashcards",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialPrimary
                                        )
                                        Text(
                                            text = "Includes English terms, phonetic pronunciation, Urdu meanings, and real sentences.",
                                            fontSize = 10.5.sp,
                                            color = EditorialTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        PdfExportMode.EXAM -> {
                            val cfg = examViewModel?.config?.value
                            val exData = examViewModel?.examData?.value
                            Text(
                                text = "3. EXAM MODE SPECIFICATIONS & DATA",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = EditorialContainer,
                                border = BorderStroke(1.dp, EditorialBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = if (exData != null && exData.questions.isNotEmpty()) {
                                            "✅ Using Active Exam Session (${exData.questions.size} MCQs, ${exData.shortQuestions.size} Short & ${exData.longQuestions.size} Long questions)"
                                        } else {
                                            "⚡ Will generate fresh Exam Paper using your configured Exam Settings below:"
                                        },
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimary
                                    )
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("Language: ${cfg?.language ?: "English"}", fontSize = 10.sp) }
                                        )
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("Level: ${cfg?.difficulty ?: "Intermediate"}", fontSize = 10.sp) }
                                        )
                                        AssistChip(
                                            onClick = {},
                                            label = { Text("Format: ${cfg?.mcqPercentage ?: "100%"}", fontSize = 10.sp) }
                                        )
                                    }
                                    var customDirectivesText by remember(cfg?.customInstructions) { mutableStateOf(cfg?.customInstructions ?: "") }
                                    OutlinedTextField(
                                        value = customDirectivesText,
                                        onValueChange = {
                                            customDirectivesText = it
                                            examViewModel?.setCustomInstructions(it)
                                        },
                                        label = { Text("Additional Exam Directives / Custom Instructions", fontSize = 11.sp) },
                                        placeholder = { Text("e.g. Focus on Tenses, include Urdu translation for all options...", fontSize = 10.sp) },
                                        minLines = 2,
                                        maxLines = 4,
                                        modifier = Modifier.fillMaxWidth().testTag("exam_pdf_custom_directives_input"),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = EditorialAccentPurple,
                                            unfocusedBorderColor = EditorialBorder
                                        )
                                    )
                                }
                            }
                        }

                        PdfExportMode.WORKSHEET -> {
                            Text(
                                text = "3. PRACTICE WORKSHEET QUESTIONS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary,
                                letterSpacing = 0.5.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EditorialContainer,
                                border = BorderStroke(1.dp, EditorialBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(
                                        text = "📝 Auto-generates 5 standard practice exercises with multiple-choice options, Urdu hints, and an answer key at the bottom.",
                                        fontSize = 11.5.sp,
                                        color = EditorialPrimary
                                    )
                                }
                            }
                        }
                    }

                    // FORMATTING & TOGGLES
                    Text(
                        text = "4. STYLING & OPTIONS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextSecondary,
                        letterSpacing = 0.5.sp
                    )

                    // Color theme selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            PdfCreatorHelper.PdfColorTheme.CLASSIC_EDITORIAL to "Classic Navy",
                            PdfCreatorHelper.PdfColorTheme.EMERALD_STUDY to "Emerald",
                            PdfCreatorHelper.PdfColorTheme.ROYAL_PURPLE to "Royal Purple",
                            PdfCreatorHelper.PdfColorTheme.WARM_AMBER to "Warm Amber"
                        ).forEach { (theme, label) ->
                            val isSel = selectedTheme == theme
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(theme.cardBgColor),
                                border = BorderStroke(if (isSel) 2.dp else 1.dp, Color(theme.primaryColor)),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedTheme = theme }
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        color = Color(theme.primaryColor)
                                    )
                                }
                            }
                        }
                    }

                    // Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Include Urdu Translations (اردو ترجمہ)", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EditorialTextPrimary)
                            Text("Adds bilingual support to every note & dialogue", fontSize = 10.sp, color = EditorialTextMuted)
                        }
                        Switch(
                            checked = includeUrdu,
                            onCheckedChange = { includeUrdu = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EditorialPrimary, checkedTrackColor = EditorialContainer)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Grammar Tags Breakdown", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = EditorialTextPrimary)
                            Text("Labels Nouns (اسم), Verbs (فعل), and Tenses", fontSize = 10.sp, color = EditorialTextMuted)
                        }
                        Switch(
                            checked = includeGrammarTags,
                            onCheckedChange = { includeGrammarTags = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EditorialPrimary, checkedTrackColor = EditorialContainer)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Deep Dive / Masterclass Detail (تفصیلی دستاویز)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialTextPrimary)
                            Text(
                                if (isDetailedMasterclass) "✨ Active: Generating a comprehensive, multi-page masterpiece with extensive examples, formulas & worksheets"
                                else "Standard: Generating a concise, clear 1-2 page revision sheet",
                                fontSize = 10.sp,
                                color = if (isDetailedMasterclass) EditorialAccentPurple else EditorialTextMuted
                            )
                        }
                        Switch(
                            checked = isDetailedMasterclass,
                            onCheckedChange = { isDetailedMasterclass = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = EditorialPrimary, checkedTrackColor = EditorialContainer),
                            modifier = Modifier.testTag("pdf_masterclass_toggle")
                        )
                    }

                    // SUCCESS / RESULT BANNER
                    if (generatedResult != null) {
                        val res = generatedResult!!
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                            modifier = Modifier.fillMaxWidth().testTag("pdf_success_banner")
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "PDF Generated Successfully! (${res.pageCount} Pages • ${(res.fileSizeBytes / 1024).coerceAtLeast(1)} KB)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            PdfCreatorHelper.openPdf(context, res.uri)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        modifier = Modifier.weight(1f).testTag("pdf_open_button")
                                    ) {
                                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Open PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            PdfCreatorHelper.sharePdf(context, res.uri, documentTitle)
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("pdf_share_button")
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val ok = PdfCreatorHelper.savePdfToDownloads(context, res.file, res.file.name)
                                            if (ok) {
                                                Toast.makeText(context, "Saved to Downloads folder!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Could not save to Downloads.", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f).testTag("pdf_download_button")
                                    ) {
                                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = EditorialBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // --- BOTTOM ACTION BAR ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !isGenerating,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(0.4f)
                    ) {
                        Text("Cancel", fontSize = 12.sp, color = EditorialTextSecondary)
                    }

                    Button(
                        onClick = {
                            isGenerating = true
                            statusMessage = "Generating PDF..."
                            coroutineScope.launch(Dispatchers.IO) {
                                try {
                                    val metadata = PdfCreatorHelper.PdfMetadata(
                                        title = documentTitle.ifBlank { "Lisan Study Notes" },
                                        subtitle = documentSubtitle.ifBlank { "Bilingual Study Guide" },
                                        learnerName = authorName.ifBlank { "Learner" },
                                        proficiencyLevel = proficiencyLevel.labelEn,
                                        theme = selectedTheme,
                                        includeUrduTranslations = includeUrdu,
                                        includeGrammarBreakdown = includeGrammarTags,
                                        includeTimestamps = includeTimestamps
                                    )

                                    val blocks = mutableListOf<PdfCreatorHelper.PdfBlock>()

                                    val activeDoc = viewModel.activeDocument.value
                                    val hasActiveDoc = activeDoc != null && activeDoc.processingStatus == com.example.data.model.ProcessingStatus.READY.name

                                    when (selectedMode) {
                                        PdfExportMode.CUSTOM_TOPIC -> {
                                            val topicText = customTopicInput.ifBlank { documentTitle.ifBlank { "Grammar & Topic Study Guide" } }
                                            statusMessage = "Fetching real-time AI study content from Gemini..."
                                            
                                            val groundingContext = if (hasActiveDoc) {
                                                viewModel.documentRepository.retrieveGroundingContext(
                                                    activeDoc!!.documentId,
                                                    topicText,
                                                    maxChunks = 8
                                                )
                                            } else ""

                                            val promptWithGrounding = buildString {
                                                if (isDetailedMasterclass) {
                                                    append("Generate an extremely comprehensive, detailed, multi-chapter academic lesson (at least 1500 words) for: $topicText\n")
                                                    append("Divide it into multiple clear chapters using '### Chapter Name' headers.\n")
                                                    append("Provide exhaustive grammatical breakdowns, numerous examples, comparative Urdu translations, and Arabic parallel structures. Do NOT summarize or abbreviate anything.\n")
                                                } else {
                                                    append("Generate detailed study guide lesson for: $topicText\n")
                                                }
                                                if (groundingContext.isNotBlank()) {
                                                    append("\nCRITICAL SOURCE DOCUMENT GROUNDING:\n")
                                                    append(groundingContext)
                                                    append("\nMANDATE: You MUST base your study guide rules, translations, examples, and terminology strictly on the grammar, vocabulary, rules, and concepts present in the source document above.")
                                                }
                                            }

                                            val aiLessonContent = try {
                                                var responseText = ""
                                                com.example.data.remote.RetrofitClient.getTutorResponseStream(
                                                    context = context,
                                                    history = emptyList(),
                                                    mode = com.example.ui.viewmodel.TutorMode.STUDY_MODE.name,
                                                    language = "English & Urdu",
                                                    userName = authorName.ifBlank { "Learner" },
                                                    learnerProfileSummary = promptWithGrounding,
                                                    isStudyModeOn = true,
                                                    learnerLevel = proficiencyLevel.labelEn,
                                                    onChunkReceived = { chunk -> responseText += chunk }
                                                )
                                                if (responseText.isNotBlank()) responseText else topicText
                                            } catch (e: Exception) {
                                                topicText
                                            }

                                            blocks.addAll(
                                                PdfCreatorHelper.buildTopicStudyGuideBlocks(
                                                    topicTitle = documentTitle.ifBlank { topicText.take(40) },
                                                    contentBody = aiLessonContent
                                                )
                                            )
                                        }

                                        PdfExportMode.CHAT_TRANSCRIPT -> {
                                            val filteredMessages = when (chatMessageRange) {
                                                "LAST_5" -> chatHistory.takeLast(5)
                                                "LAST_10" -> chatHistory.takeLast(10)
                                                "LAST_20" -> chatHistory.takeLast(20)
                                                else -> chatHistory
                                            }
                                            if (filteredMessages.isEmpty()) {
                                                blocks.add(PdfCreatorHelper.PdfBlock.Callout("Session Notice", "No active messages in chat yet. Start a conversation with Lisan AI Tutor to export your personalized chat dialogue."))
                                            } else {
                                                blocks.addAll(PdfCreatorHelper.buildChatTranscriptBlocks(filteredMessages))
                                            }
                                        }

                                        PdfExportMode.STUDY_NOTES -> {
                                            blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("GRAMMAR RULES & SUMMARY", "Structured concepts with Urdu comparative breakdown"))
                                            val lines = customNotesContent.split("\n\n")
                                            for (paragraph in lines) {
                                                if (paragraph.startsWith("Topic:") || paragraph.startsWith("Rule:")) {
                                                    blocks.add(PdfCreatorHelper.PdfBlock.Callout("Grammar Point", paragraph, isAccent = true))
                                                } else {
                                                    blocks.add(PdfCreatorHelper.PdfBlock.Paragraph(paragraph))
                                                }
                                            }
                                            if (savedNotes.isNotEmpty()) {
                                                blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("SAVED USER NOTES", "Personal pinned notes from lessons"))
                                                for (note in savedNotes) {
                                                    val body = if (note.arabicComparisonText.isNotBlank()) {
                                                        "${note.englishText}\n\nComparative: ${note.arabicComparisonText}"
                                                    } else {
                                                        note.englishText
                                                    }
                                                    blocks.add(PdfCreatorHelper.PdfBlock.Callout("Note • ${note.titleEn}", body))
                                                }
                                            }
                                        }

                                        PdfExportMode.VOCABULARY -> {
                                            statusMessage = "Generating comparative vocabulary sheet..."
                                            val docContext = if (hasActiveDoc) {
                                                viewModel.documentRepository.retrieveGroundingContext(
                                                    activeDoc!!.documentId,
                                                    "vocabulary verbs nouns idioms",
                                                    maxChunks = 8
                                                )
                                            } else ""

                                            val prompt = buildString {
                                                val itemCount = if (isDetailedMasterclass) 20 else 10
                                                append("Generate $itemCount advanced or intermediate English vocabulary words, phrases, or verbs ")
                                                if (hasActiveDoc) {
                                                    append("extracted directly from the following source text:\n$docContext\n")
                                                } else {
                                                    append("suitable for level ${proficiencyLevel.labelEn}.\n")
                                                }
                                                append("For each vocabulary item, provide the part of speech, Urdu meaning, an English example sentence, and its Urdu translation.\n")
                                                append("Output them strictly in the following XML format, with NO conversational introduction or wrapper:\n")
                                                append("<vocab_list>\n")
                                                append("<vocab_item>\n")
                                                append("  <word>English word/phrase</word>\n")
                                                append("  <pos>Part of Speech (e.g. Noun, Verb, Idiom)</pos>\n")
                                                append("  <meaning>Urdu meaning (e.g. سمجھنا)</meaning>\n")
                                                append("  <example_en>An English sentence using the word.</example_en>\n")
                                                append("  <example_ur>Urdu translation of the example sentence.</example_ur>\n")
                                                append("</vocab_item>\n")
                                                append("... (repeat for $itemCount items)\n")
                                                append("</vocab_list>")
                                            }

                                            var responseText = ""
                                            try {
                                                com.example.data.remote.RetrofitClient.getTutorResponseStream(
                                                    context = context,
                                                    history = emptyList(),
                                                    mode = com.example.ui.viewmodel.TutorMode.STUDY_MODE.name,
                                                    language = "English",
                                                    userName = authorName.ifBlank { "Learner" },
                                                    learnerProfileSummary = prompt,
                                                    isStudyModeOn = true,
                                                    learnerLevel = proficiencyLevel.labelEn,
                                                    onChunkReceived = { chunk -> responseText += chunk }
                                                )
                                            } catch (e: Exception) {
                                                android.util.Log.e("PdfCreatorDialog", "Failed to generate dynamic vocabulary: ${e.message}")
                                            }

                                            val generatedCards = parseVocabItems(responseText)
                                            if (generatedCards.isNotEmpty()) {
                                                blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("KEY VOCABULARY & IDIOMS", "Master colloquial expressions and words with contextual examples"))
                                                blocks.addAll(generatedCards)
                                            } else {
                                                blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("KEY VOCABULARY & IDIOMS (SAMPLE)", "Master colloquial expressions and words with contextual examples"))
                                                val sampleIdioms = IdiomRepository.idioms.take(12)
                                                for (idiom in sampleIdioms) {
                                                    blocks.add(
                                                        PdfCreatorHelper.PdfBlock.VocabCard(
                                                            word = idiom.expression,
                                                            partOfSpeech = "Idiom • ${idiom.level.label}",
                                                            urduMeaning = idiom.urduMeaning,
                                                            exampleEnglish = idiom.example,
                                                            exampleUrdu = idiom.arabicMeaning
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        PdfExportMode.EXAM -> {
                                            statusMessage = "Building Exam Paper PDF with active configuration..."
                                            val cfg = examViewModel?.config?.value ?: com.example.ui.viewmodel.ExamSetupConfig()
                                            val exData = examViewModel?.examData?.value

                                            val sourceTitle = if (cfg.sourceType == com.example.ui.viewmodel.ExamSourceType.BOOK_PDF) {
                                                cfg.uploadedPdfName ?: cfg.selectedBook?.title ?: "English Grammar Book"
                                            } else {
                                                "Learned Concepts & History"
                                            }

                                            val lang = cfg.language
                                            val customInst = cfg.customInstructions

                                            if (exData != null && exData.questions.isNotEmpty()) {
                                                blocks.addAll(
                                                    PdfCreatorHelper.buildExamPaperBlocks(
                                                        sourceTitle = sourceTitle,
                                                        difficulty = cfg.difficulty,
                                                        language = lang,
                                                        timeLimitMinutes = cfg.timeLimitMinutes,
                                                        mcqPercentage = cfg.mcqPercentage,
                                                        customInstructions = customInst,
                                                        questions = exData.questions,
                                                        shortQuestions = exData.shortQuestions,
                                                        longQuestions = exData.longQuestions,
                                                        includeUrdu = includeUrdu || lang.equals("Urdu", ignoreCase = true)
                                                    )
                                                )
                                            } else {
                                                statusMessage = "Generating full exam paper with AI tutor..."
                                                val docContext = if (hasActiveDoc) {
                                                    viewModel.documentRepository.retrieveGroundingContext(
                                                        activeDoc!!.documentId,
                                                        "grammar exam questions exercises tenses prepositions verbs",
                                                        maxChunks = 8
                                                    )
                                                } else ""

                                                val prompt = buildString {
                                                    val questionCount = if (isDetailedMasterclass) 12 else 8
                                                    append("Generate a comprehensive English Exam Paper for level ${cfg.difficulty} grounded in: $sourceTitle.\n")
                                                    if (docContext.isNotBlank()) {
                                                        append("GROUNDING CONTEXT:\n$docContext\n")
                                                    }
                                                    append("Language preference: $lang.\n")
                                                    append("Format: ${cfg.mcqPercentage} MCQs.\n")
                                                    if (customInst.isNotBlank()) {
                                                        append("Learner custom directives: $customInst\n")
                                                    }
                                                    append("Provide $questionCount multiple choice questions testing English grammar, vocabulary, usage, or translation. Provide 4 options, an explanation hint in $lang, and the correct answer key.\n")
                                                    append("Output strictly in XML format:\n")
                                                    append("<worksheet_list>\n")
                                                    append("<exercise_item>\n")
                                                    append("  <number>1</number>\n")
                                                    append("  <question>Question sentence</question>\n")
                                                    append("  <options>Option A | Option B | Option C | Option D</options>\n")
                                                    append("  <hint>Explanation hint in $lang</hint>\n")
                                                    append("  <answer>Correct option text</answer>\n")
                                                    append("</exercise_item>\n")
                                                    append("</worksheet_list>")
                                                }

                                                var responseText = ""
                                                try {
                                                    com.example.data.remote.RetrofitClient.getTutorResponseStream(
                                                        context = context,
                                                        history = emptyList(),
                                                        mode = com.example.ui.viewmodel.TutorMode.STUDY_MODE.name,
                                                        language = lang,
                                                        userName = authorName.ifBlank { "Learner" },
                                                        learnerProfileSummary = prompt,
                                                        isStudyModeOn = true,
                                                        learnerLevel = cfg.difficulty,
                                                        onChunkReceived = { chunk -> responseText += chunk }
                                                    )
                                                } catch (e: Exception) {
                                                    android.util.Log.e("PdfCreatorDialog", "Failed to generate dynamic exam PDF: ${e.message}")
                                                }

                                                val generatedExercises = parseExerciseItems(responseText)
                                                if (generatedExercises.isNotEmpty()) {
                                                    blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("OFFICIAL ENGLISH EXAMINATION PAPER", "Source: $sourceTitle | Level: ${cfg.difficulty} | Time: ${if (cfg.timeLimitMinutes > 0) "${cfg.timeLimitMinutes} Mins" else "Untimed"}"))
                                                    blocks.add(
                                                        PdfCreatorHelper.PdfBlock.Callout(
                                                            "EXAM SPECIFICATIONS & INSTRUCTIONS",
                                                            "Language: $lang | Difficulty: ${cfg.difficulty} | Custom Directives: ${customInst.ifBlank { "Standard Rules" }}",
                                                            urduText = if (includeUrdu || lang.equals("Urdu", ignoreCase = true)) "تمام سوالات کو غور سے پڑھیں اور صحیح جواب منتخب کریں۔" else null,
                                                            isAccent = true
                                                        )
                                                    )
                                                    blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("SECTION A: MULTIPLE CHOICE QUESTIONS", "Select the single best option for each question"))
                                                    blocks.addAll(generatedExercises)
                                                    if (cfg.mcqPercentage == "50%") {
                                                        blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("SECTION B: CONVERSATIONAL SHORT & LONG QUESTIONS", "Answer in concise English/Urdu"))
                                                        blocks.add(PdfCreatorHelper.PdfBlock.Callout("Question B1", "Write 3 sentences using Present Perfect Tense to describe your daily achievements.", urduText = if (includeUrdu) "فعل ماضی قریب کا استعمال کرتے ہوئے ۳ جملے تحریر کریں" else null))
                                                        blocks.add(PdfCreatorHelper.PdfBlock.Callout("Question B2", "Explain the difference between 'Since' and 'For' with 2 original examples.", urduText = if (includeUrdu) "'Since' اور 'For' کے درمیان فرق مثالوں کے ساتھ بیان کریں" else null))
                                                    }
                                                } else {
                                                    blocks.addAll(
                                                        PdfCreatorHelper.buildExamPaperBlocks(
                                                            sourceTitle = sourceTitle,
                                                            difficulty = cfg.difficulty,
                                                            language = lang,
                                                            timeLimitMinutes = cfg.timeLimitMinutes,
                                                            mcqPercentage = cfg.mcqPercentage,
                                                            customInstructions = customInst,
                                                            questions = emptyList(),
                                                            shortQuestions = listOf("Explain the usage of Present Perfect vs Past Simple with examples.", "Translate into English: میں صبح سے کام کر رہا ہوں۔"),
                                                            longQuestions = listOf("Write a 150-word essay explaining the importance of correct grammar in formal communication."),
                                                            includeUrdu = includeUrdu || lang.equals("Urdu", ignoreCase = true)
                                                        )
                                                    )
                                                }
                                            }
                                        }

                                        PdfExportMode.WORKSHEET -> {
                                            statusMessage = "Generating interactive practice worksheet..."
                                            val docContext = if (hasActiveDoc) {
                                                viewModel.documentRepository.retrieveGroundingContext(
                                                    activeDoc!!.documentId,
                                                    "grammar exercises tenses verbs nouns prepositions",
                                                    maxChunks = 8
                                                )
                                            } else ""

                                            val prompt = buildString {
                                                val questionCount = if (isDetailedMasterclass) 12 else 5
                                                append("Generate a $questionCount-question multiple choice English grammar worksheet ")
                                                if (hasActiveDoc) {
                                                    append("grounded strictly in the grammar, vocabulary, rules, and concepts of this source text:\n$docContext\n")
                                                } else {
                                                    append("suitable for level ${proficiencyLevel.labelEn} on the general topic: ${documentTitle}.\n")
                                                }
                                                append("Each question must test grammar, tenses, prepositions, or vocabulary. Provide 4 options, an Urdu hint, and the correct answer key.\n")
                                                append("Output them strictly in the following XML format, with NO conversational introduction or wrapper:\n")
                                                append("<worksheet_list>\n")
                                                append("<exercise_item>\n")
                                                append("  <number>1</number>\n")
                                                append("  <question>The sentence with a blank, e.g. She _______ (write) three emails since morning.</question>\n")
                                                append("  <options>Option A | Option B | Option C | Option D</options>\n")
                                                append("  <hint>Urdu explanation hint of the rule</hint>\n")
                                                append("  <answer>The exact text of the correct option</answer>\n")
                                                append("</exercise_item>\n")
                                                append("... (repeat for questions 1 to $questionCount)\n")
                                                append("</worksheet_list>")
                                            }

                                            var responseText = ""
                                            try {
                                                com.example.data.remote.RetrofitClient.getTutorResponseStream(
                                                    context = context,
                                                    history = emptyList(),
                                                    mode = com.example.ui.viewmodel.TutorMode.STUDY_MODE.name,
                                                    language = "English & Urdu",
                                                    userName = authorName.ifBlank { "Learner" },
                                                    learnerProfileSummary = prompt,
                                                    isStudyModeOn = true,
                                                    learnerLevel = proficiencyLevel.labelEn,
                                                    onChunkReceived = { chunk -> responseText += chunk }
                                                )
                                            } catch (e: Exception) {
                                                android.util.Log.e("PdfCreatorDialog", "Failed to generate dynamic worksheet: ${e.message}")
                                            }

                                            val generatedExercises = parseExerciseItems(responseText)
                                            if (generatedExercises.isNotEmpty()) {
                                                blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("GRAMMAR EXERCISE WORKSHEET", "Answer the questions below to test your understanding"))
                                                blocks.addAll(generatedExercises)
                                                blocks.add(PdfCreatorHelper.PdfBlock.Callout("Worksheet Instructions", "Complete each exercise above, then check your answers using Lisan AI Tutor chat for detailed explanations."))
                                            } else {
                                                blocks.add(PdfCreatorHelper.PdfBlock.SectionHeader("GRAMMAR EXERCISE WORKSHEET (SAMPLE)", "Answer the questions below to test your understanding"))
                                                blocks.add(PdfCreatorHelper.PdfBlock.ExerciseItem(
                                                    number = 1,
                                                    question = "She _______ (write) three emails since morning.",
                                                    options = listOf("has written", "wrote", "is writing", "writes"),
                                                    urduHint = "صبح سے اب تک مکمل ہونے والا عمل (Present Perfect)",
                                                    answerKey = "has written"
                                                ))
                                                blocks.add(PdfCreatorHelper.PdfBlock.ExerciseItem(
                                                    number = 2,
                                                    question = "The book is _______ the table.",
                                                    options = listOf("on", "in", "at", "into"),
                                                    urduHint = "سطح پر ہونے کی صورت میں 'on' استعمال ہوتا ہے",
                                                    answerKey = "on"
                                                ))
                                                blocks.add(PdfCreatorHelper.PdfBlock.ExerciseItem(
                                                    number = 3,
                                                    question = "You _______ follow traffic rules.",
                                                    options = listOf("must", "might", "can", "could"),
                                                    urduHint = "لازمی فرض کے لیے 'must' استعمال ہوتا ہے",
                                                    answerKey = "must"
                                                ))
                                                blocks.add(PdfCreatorHelper.PdfBlock.ExerciseItem(
                                                    number = 4,
                                                    question = "Identify the Urdu grammatical category for 'Noun':",
                                                    options = listOf("اسم (Ism)", "فعل (Fi'l)", "حرف (Harf)", "صفت (Sifat)"),
                                                    urduHint = "کسی شخص، جگہ یا چیز کا نام",
                                                    answerKey = "اسم (Ism)"
                                                ))
                                                blocks.add(PdfCreatorHelper.PdfBlock.ExerciseItem(
                                                    number = 5,
                                                    question = "Yesterday, we _______ (visit) the historical museum.",
                                                    options = listOf("visited", "have visited", "had visited", "visiting"),
                                                    urduHint = "ماضی کے مخصوص وقت (Yesterday) کے لیے Past Simple آئے گا",
                                                    answerKey = "visited"
                                                ))
                                                blocks.add(PdfCreatorHelper.PdfBlock.Callout("Worksheet Instructions", "Complete each exercise above, then check your answers using Lisan AI Tutor chat for detailed explanations."))
                                            }
                                        }
                                    }

                                    val result = PdfCreatorHelper.createPdf(context, metadata, blocks)
                                    withContext(Dispatchers.Main) {
                                        isGenerating = false
                                        generatedResult = result
                                        viewModel.insertGeneratedPdfMessage(result, metadata.title, metadata.subtitle)
                                        Toast.makeText(context, "✅ PDF created successfully! (${result.pageCount} Pages)", Toast.LENGTH_LONG).show()
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        isGenerating = false
                                        statusMessage = "Error generating PDF: ${e.message}"
                                        Toast.makeText(context, "Failed to create PDF: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        },
                        enabled = !isGenerating,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                        modifier = Modifier.weight(0.6f).testTag("pdf_generate_button")
                    ) {
                        if (isGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generating...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
