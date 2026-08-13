package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.*
import com.example.ui.viewmodel.*

@Composable
fun ExamModeDialog(
    viewModel: ExamViewModel,
    lisanViewModel: LisanViewModel,
    onDismiss: () -> Unit
) {
    val isGenerating by viewModel.isGeneratingExam.collectAsState()
    val examData by viewModel.examData.collectAsState()
    val examError by viewModel.examError.collectAsState()
    val examResult by viewModel.examResult.collectAsState()

    Dialog(
        onDismissRequest = {
            viewModel.clearExam()
            onDismiss()
        },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .padding(8.dp)
                .testTag("exam_mode_dialog"),
            shape = RoundedCornerShape(24.dp),
            color = EditorialSurface,
            tonalElevation = 6.dp,
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    examResult != null -> {
                        ExamResultsView(
                            result = examResult!!,
                            examData = examData!!,
                            viewModel = viewModel,
                            lisanViewModel = lisanViewModel,
                            onPracticeWeakAreas = { weakTopics ->
                                viewModel.startPracticeWeakAreas(weakTopics)
                            },
                            onRetake = {
                                viewModel.clearExam()
                            },
                            onClose = {
                                viewModel.clearExam()
                                onDismiss()
                            }
                        )
                    }

                    examData != null -> {
                        ActiveExamFlowView(
                            viewModel = viewModel,
                            examData = examData!!,
                            onClose = {
                                viewModel.clearExam()
                                onDismiss()
                            }
                        )
                    }

                    else -> {
                        ExamSetupFlowView(
                            viewModel = viewModel,
                            isGenerating = isGenerating,
                            error = examError,
                            onClose = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExamSetupFlowView(
    viewModel: ExamViewModel,
    isGenerating: Boolean,
    error: String?,
    onClose: () -> Unit
) {
    val config by viewModel.config.collectAsState()
    val availableBooks by viewModel.availableBooks.collectAsState()
    val context = LocalContext.current

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            try {
                val flags = android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flags)
            } catch (e: Exception) {
                android.util.Log.w("ExamSetupFlowView", "Could not persist URI read permission: ${e.message}")
            }
            viewModel.processUploadedPdf(context, uri)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Dialog Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = EditorialAccentPurple.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("📝", fontSize = 20.sp)
                    }
                }
                Column {
                    Text(
                        text = "EXAM MODE • انگریزی امتحان",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Text(
                        text = "انگریزی سیکھنے کے لیے خصوصی امتحان (اردو رہنمائی)",
                        fontSize = 11.sp,
                        color = EditorialTextSecondary
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.testTag("exam_setup_close_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = EditorialTextMuted
                )
            }
        }

        HorizontalDivider(color = EditorialBorder)

        // Step 1: Select Language
        Text(
            text = "STEP 1: SELECT EXAM LANGUAGE • زبان کا انتخاب",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialAccentPurple,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // English Language Chip
            val isEnglish = config.language == "English"
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp)
                    .clickable { viewModel.setLanguage("English") }
                    .testTag("language_option_english"),
                shape = RoundedCornerShape(12.dp),
                color = if (isEnglish) EditorialAccentPurple.copy(alpha = 0.12f) else EditorialContainer,
                border = BorderStroke(
                    if (isEnglish) 2.dp else 1.dp,
                    if (isEnglish) EditorialAccentPurple else EditorialBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🇬🇧", fontSize = 18.sp)
                    Text("English Exam", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EditorialPrimary)
                    Spacer(modifier = Modifier.weight(1f))
                    if (isEnglish) Icon(Icons.Default.CheckCircle, null, tint = EditorialAccentPurple, modifier = Modifier.size(16.dp))
                }
            }

            // Urdu Language Chip
            val isUrdu = config.language == "Urdu"
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(55.dp)
                    .clickable { viewModel.setLanguage("Urdu") }
                    .testTag("language_option_urdu"),
                shape = RoundedCornerShape(12.dp),
                color = if (isUrdu) EditorialAccentPurple.copy(alpha = 0.12f) else EditorialContainer,
                border = BorderStroke(
                    if (isUrdu) 2.dp else 1.dp,
                    if (isUrdu) EditorialAccentPurple else EditorialBorder
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🇵🇰", fontSize = 18.sp)
                    Text("Urdu Exam (اردو امتحان)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EditorialPrimary)
                    Spacer(modifier = Modifier.weight(1f))
                    if (isUrdu) Icon(Icons.Default.CheckCircle, null, tint = EditorialAccentPurple, modifier = Modifier.size(16.dp))
                }
            }
        }

        HorizontalDivider(color = EditorialBorder)

        // Step 2: Select Source
        Text(
            text = "STEP 2: SELECT EXAM SOURCE • ماخذ کا انتخاب",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialAccentPurple,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Source Card 1: Upload PDF / Book
            val isBookSelected = config.sourceType == ExamSourceType.BOOK_PDF
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(85.dp)
                    .clickable { viewModel.setSourceType(ExamSourceType.BOOK_PDF) }
                    .testTag("source_option_book"),
                shape = RoundedCornerShape(16.dp),
                color = if (isBookSelected) EditorialAccentPurple.copy(alpha = 0.12f) else EditorialContainer,
                border = BorderStroke(
                    if (isBookSelected) 2.dp else 1.dp,
                    if (isBookSelected) EditorialAccentPurple else EditorialBorder
                )
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("📚", fontSize = 20.sp)
                        if (isBookSelected) Icon(Icons.Default.CheckCircle, null, tint = EditorialAccentPurple, modifier = Modifier.size(16.dp))
                    }
                    Text("Upload PDF / Book", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EditorialPrimary)
                }
            }

            // Source Card 2: Things Learned
            val isLearnedSelected = config.sourceType == ExamSourceType.PREVIOUS_LEARNED
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(85.dp)
                    .clickable {
                        viewModel.setSourceType(ExamSourceType.PREVIOUS_LEARNED)
                        viewModel.scanLearnedHistory()
                    }
                    .testTag("source_option_learned"),
                shape = RoundedCornerShape(16.dp),
                color = if (isLearnedSelected) EditorialAccentPurple.copy(alpha = 0.12f) else EditorialContainer,
                border = BorderStroke(
                    if (isLearnedSelected) 2.dp else 1.dp,
                    if (isLearnedSelected) EditorialAccentPurple else EditorialBorder
                )
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("🧠", fontSize = 20.sp)
                        if (isLearnedSelected) Icon(Icons.Default.CheckCircle, null, tint = EditorialAccentPurple, modifier = Modifier.size(16.dp))
                    }
                    Text("Learned History", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EditorialPrimary)
                }
            }
        }

        // Sub-options depending on Source Selection
        if (config.sourceType == ExamSourceType.BOOK_PDF) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                
                // Obvious PDF File Pick Section
                Card(
                    colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                    border = BorderStroke(1.dp, EditorialBorder),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "PDF FILE UPLOAD • کسٹم پی ڈی ایف اپ لوڈ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = { filePickerLauncher.launch("application/pdf") },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EditorialAccentPurple.copy(alpha = 0.15f),
                                    contentColor = EditorialAccentPurple
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.UploadFile, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Select PDF File", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            
                            if (config.isProcessingPdf) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = EditorialAccentPurple, strokeWidth = 2.dp)
                                Text("Processing PDF...", fontSize = 11.sp, color = EditorialTextSecondary)
                            } else if (config.uploadedPdfName != null) {
                                Icon(Icons.Default.CheckCircle, null, tint = EditorialAccentPurple, modifier = Modifier.size(18.dp))
                                Text(
                                    text = config.uploadedPdfName!!,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialAccentPurple,
                                    maxLines = 1
                                )
                            } else {
                                Text("No custom PDF uploaded yet.", fontSize = 11.sp, color = EditorialTextMuted)
                            }
                        }

                        if (config.pdfProcessingError != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = config.pdfProcessingError!!,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }

                Text("Or select preloaded grammar book:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)

                var expandedBookDropdown by remember { mutableStateOf(false) }
                val currentBook = config.selectedBook ?: availableBooks.firstOrNull()

                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedBookDropdown = true }
                            .testTag("book_select_dropdown"),
                        shape = RoundedCornerShape(12.dp),
                        color = EditorialContainer,
                        border = BorderStroke(1.dp, EditorialBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = currentBook?.title ?: "Select Book",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = EditorialPrimary
                                )
                                Text(
                                    text = "By ${currentBook?.author ?: ""}",
                                    fontSize = 11.sp,
                                    color = EditorialTextSecondary
                                )
                            }
                            Icon(Icons.Default.ArrowDropDown, null, tint = EditorialAccentPurple)
                        }
                    }

                    DropdownMenu(
                        expanded = expandedBookDropdown,
                        onDismissRequest = { expandedBookDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(EditorialSurface)
                    ) {
                        availableBooks.forEach { book ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(book.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EditorialPrimary)
                                        Text(book.description, fontSize = 10.sp, color = EditorialTextSecondary, maxLines = 1)
                                    }
                                },
                                onClick = {
                                    viewModel.setSelectedBook(book)
                                    expandedBookDropdown = false
                                }
                            )
                            HorizontalDivider(color = EditorialBorder.copy(alpha = 0.5f))
                        }
                    }
                }

                // Chapter Selector
                val chapters = currentBook?.chapters ?: listOf("All Chapters")
                var expandedChapterDropdown by remember { mutableStateOf(false) }

                Text("Select Chapter / Focus:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedChapterDropdown = true }
                            .testTag("chapter_select_dropdown"),
                        shape = RoundedCornerShape(12.dp),
                        color = EditorialContainer,
                        border = BorderStroke(1.dp, EditorialBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = config.selectedChapter,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = EditorialPrimary
                            )
                            Icon(Icons.Default.ArrowDropDown, null, tint = EditorialAccentPurple)
                        }
                    }

                    DropdownMenu(
                        expanded = expandedChapterDropdown,
                        onDismissRequest = { expandedChapterDropdown = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(EditorialSurface)
                    ) {
                        chapters.forEach { chapter ->
                            DropdownMenuItem(
                                text = { Text(chapter, fontSize = 12.sp, color = EditorialPrimary) },
                                onClick = {
                                    viewModel.setSelectedChapter(chapter)
                                    expandedChapterDropdown = false
                                }
                            )
                        }
                    }
                }
            }
        } else {
            // Summary Card for Learned History
            Card(
                colors = CardDefaults.cardColors(containerColor = EditorialContainer),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, EditorialBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, null, tint = EditorialAccentPurple, modifier = Modifier.size(16.dp))
                        Text("DETECTED LEARNING EVIDENCE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
                    }
                    Text(
                        text = config.learnedTopicsSummary,
                        fontSize = 11.sp,
                        color = EditorialTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        HorizontalDivider(color = EditorialBorder)

        // Step 3: MCQ Configuration
        Text(
            text = "STEP 3: MCQ CONFIGURATION • ایم سی کیو کنفیگریشن",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialAccentPurple,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 50% MCQs
            val is50Percent = config.mcqPercentage == "50%"
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(65.dp)
                    .clickable { viewModel.setMcqPercentage("50%") }
                    .testTag("mcq_ratio_50"),
                shape = RoundedCornerShape(12.dp),
                color = if (is50Percent) EditorialAccentPurple.copy(alpha = 0.12f) else EditorialContainer,
                border = BorderStroke(
                    if (is50Percent) 2.dp else 1.dp,
                    if (is50Percent) EditorialAccentPurple else EditorialBorder
                )
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("50% MCQs", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EditorialPrimary)
                    Text("Half MCQ, Half conversational", fontSize = 9.sp, color = EditorialTextSecondary)
                }
            }

            // 100% MCQs
            val is100Percent = config.mcqPercentage == "100%"
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(65.dp)
                    .clickable { viewModel.setMcqPercentage("100%") }
                    .testTag("mcq_ratio_100"),
                shape = RoundedCornerShape(12.dp),
                color = if (is100Percent) EditorialAccentPurple.copy(alpha = 0.12f) else EditorialContainer,
                border = BorderStroke(
                    if (is100Percent) 2.dp else 1.dp,
                    if (is100Percent) EditorialAccentPurple else EditorialBorder
                )
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("100% MCQs", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EditorialPrimary)
                    Text("Entirely MCQs exam", fontSize = 9.sp, color = EditorialTextSecondary)
                }
            }
        }

        // Step 4: Questions Count (Conditional on 50% MCQs choice)
        if (config.mcqPercentage == "50%") {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                HorizontalDivider(color = EditorialBorder)

                Text(
                    text = "STEP 4: CONVERSATIONAL QUESTIONS COUNT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple,
                    letterSpacing = 1.sp
                )

                // Short Questions Count Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Select Short Questions count:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(20, 30, 40).forEach { count ->
                            val isSelected = config.shortCount == count
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setShortCount(count) },
                                label = { Text("$count Short Questions", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EditorialAccentPurple,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("chip_short_$count")
                            )
                        }
                    }
                }

                // Long Questions Count Selection
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Select Long Questions count:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(10, 20, 30).forEach { count ->
                            val isSelected = config.longCount == count
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setLongCount(count) },
                                label = { Text("$count Long Questions", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EditorialAccentPurple,
                                    selectedLabelColor = Color.White
                                ),
                                modifier = Modifier.testTag("chip_long_$count")
                            )
                        }
                    }
                }
            }
        } else {
            Card(
                colors = CardDefaults.cardColors(containerColor = EditorialContainer),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EditorialBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("💡", fontSize = 16.sp)
                    Text(
                        text = "Your exam is configured to be 100% MCQs (up to 25 maximum). Short and Long questions are deactivated.",
                        fontSize = 11.sp,
                        color = EditorialTextSecondary
                    )
                }
            }
        }

        HorizontalDivider(color = EditorialBorder)

        // Difficulty Level
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("مشکل کی سطح منتخب کریں (Difficulty Level):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Beginner" to "ابتدائی", "Intermediate" to "درمیانی", "Advanced" to "اعلیٰ").forEach { (diff, urduLabel) ->
                    val isSelected = config.difficulty.equals(diff, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setDifficulty(diff) },
                        label = { Text("$diff ($urduLabel)", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EditorialAccentPurple,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("chip_diff_$diff")
                    )
                }
            }
        }

        // Time Limit
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("وقت کی حد (Time Limit):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(0 to "No Limit (بغیر حد)", 5 to "5 Mins", 10 to "10 Mins", 15 to "15 Mins").forEach { (mins, label) ->
                    val isSelected = config.timeLimitMinutes == mins
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setTimeLimit(mins) },
                        label = { Text(label, fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EditorialAccentPurple,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("chip_timer_$mins")
                    )
                }
            }
        }

        if (error != null) {
            Surface(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.ErrorOutline, null, tint = MaterialTheme.colorScheme.error)
                    Text(text = error, fontSize = 11.sp, color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { viewModel.startExamWithConfig() },
            enabled = !isGenerating && !config.isProcessingPdf,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("generate_exam_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = EditorialAccentPurple,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isGenerating) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                Spacer(modifier = Modifier.width(8.dp))
                Text("انگریزی امتحان تیار ہو رہا ہے...", fontSize = 12.sp)
            } else {
                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("🚀 Generate English Exam (امتحان شروع کریں)", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ActiveExamFlowView(
    viewModel: ExamViewModel,
    examData: ExamData,
    onClose: () -> Unit
) {
    val questions = examData.questions
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()
    val remainingSecs by viewModel.remainingTimeSeconds.collectAsState()
    val isEvaluating by viewModel.isEvaluatingShortAnswer.collectAsState()

    val currentQuestion = questions.getOrNull(currentIndex) ?: return
    val currentAnswer = userAnswers[currentQuestion.id]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Exam Runtime Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = EditorialAccentPurple.copy(alpha = 0.15f),
                shape = CircleShape
            ) {
                Text(
                    text = "Question ${currentIndex + 1} of ${questions.size}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            // Countdown Timer if active
            if (remainingSecs != null) {
                val mins = remainingSecs!! / 60
                val secs = remainingSecs!! % 60
                val timerText = String.format("%02d:%02d", mins, secs)
                val isLowTime = remainingSecs!! < 60

                Surface(
                    color = if (isLowTime) Color.Red.copy(alpha = 0.15f) else EditorialContainer,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (isLowTime) Color.Red else EditorialBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = if (isLowTime) Color.Red else EditorialAccentPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = timerText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isLowTime) Color.Red else EditorialPrimary,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            TextButton(
                onClick = { viewModel.finishExam() },
                modifier = Modifier.testTag("finish_exam_early_button")
            ) {
                Text("Finish Exam", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
            }
        }

        // Linear Progress Bar
        val progress = (currentIndex + 1).toFloat() / questions.size
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
            color = EditorialAccentPurple,
            trackColor = EditorialBorder
        )

        // Question Details Card
        Card(
            colors = CardDefaults.cardColors(containerColor = EditorialContainer),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Topic Pill Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        color = EditorialAccentPurple.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "🏷️ ${currentQuestion.topic}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        color = EditorialSurface,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (currentQuestion.type == "MULTIPLE_CHOICE") "Multiple Choice" else "Short Answer",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextMuted,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                // Question Text
                Text(
                    text = currentQuestion.questionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary,
                    lineHeight = 22.sp
                )

                if (currentQuestion.sourceSnippet.isNotBlank()) {
                    Text(
                        text = "Source Ref: ${currentQuestion.sourceSnippet}",
                        fontSize = 10.sp,
                        color = EditorialTextMuted
                    )
                }
            }
        }

        // Answer Section
        if (currentQuestion.type == "MULTIPLE_CHOICE") {
            // Option Buttons
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                currentQuestion.options.forEachIndexed { index, option ->
                    val isSelected = currentAnswer?.selectedOptionIndex == index
                    val isAnswerChecked = currentAnswer?.isCorrect != null
                    val isCorrectOption = index == currentQuestion.correctAnswerIndex

                    val cardBg = when {
                        !isAnswerChecked -> if (isSelected) EditorialAccentPurple.copy(alpha = 0.15f) else EditorialSurface
                        isCorrectOption -> Color(0xFFDCFCE7) // Light Green
                        isSelected && !isCorrectOption -> Color(0xFFFEE2E2) // Light Red
                        else -> EditorialSurface
                    }

                    val borderColor = when {
                        !isAnswerChecked -> if (isSelected) EditorialAccentPurple else EditorialBorder
                        isCorrectOption -> Color(0xFF16A34A)
                        isSelected && !isCorrectOption -> Color(0xFFDC2626)
                        else -> EditorialBorder
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isAnswerChecked) {
                                viewModel.submitMultipleChoiceAnswer(currentQuestion.id, index)
                            }
                            .testTag("mcq_option_$index"),
                        shape = RoundedCornerShape(12.dp),
                        color = cardBg,
                        border = BorderStroke(if (isSelected || (isAnswerChecked && isCorrectOption)) 2.dp else 1.dp, borderColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSelected) EditorialAccentPurple else EditorialContainer,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = ('A' + index).toString(),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else EditorialTextSecondary
                                    )
                                }
                            }

                            Text(
                                text = option,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = EditorialPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            if (isAnswerChecked) {
                                if (isCorrectOption) {
                                    Icon(Icons.Default.Check, null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                                } else if (isSelected) {
                                    Icon(Icons.Default.Close, null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Short Answer Input Block
            var shortAnswerInput by remember { mutableStateOf(currentAnswer?.shortAnswerText ?: "") }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "انگریزی یا اردو میں اپنا جواب دیں (Type answer in English/Urdu):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )

                OutlinedTextField(
                    value = shortAnswerInput,
                    onValueChange = { shortAnswerInput = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("short_answer_input_field"),
                    placeholder = { Text("اپنا جواب یہاں لکھیں... Write your answer here", fontSize = 12.sp, color = EditorialTextMuted) },
                    enabled = currentAnswer == null && !isEvaluating,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EditorialAccentPurple,
                        unfocusedBorderColor = EditorialBorder
                    )
                )

                if (currentAnswer == null) {
                    Button(
                        onClick = {
                            viewModel.evaluateAndSubmitShortAnswer(currentQuestion.id, shortAnswerInput)
                        },
                        enabled = shortAnswerInput.isNotBlank() && !isEvaluating,
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("submit_short_answer_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)
                    ) {
                        if (isEvaluating) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(6.dp))
                            Text("Evaluating...", fontSize = 12.sp)
                        } else {
                            Text("Submit Answer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Feedback / Explanation Card
        if (currentAnswer != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (currentAnswer.isCorrect == true) Color(0xFFF0FDF4) else Color(0xFFFEF2F2)
                ),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, if (currentAnswer.isCorrect == true) Color(0xFF86EFAC) else Color(0xFFFCA5A5))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(if (currentAnswer.isCorrect == true) "✅ Score: ${currentAnswer.scorePercentage}%" else "❌ Score: ${currentAnswer.scorePercentage}%", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Text(
                        text = currentAnswer.feedback,
                        fontSize = 12.sp,
                        color = EditorialPrimary,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.previousQuestion() },
                enabled = currentIndex > 0,
                colors = ButtonDefaults.buttonColors(containerColor = EditorialContainer, contentColor = EditorialPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Previous", fontSize = 12.sp)
            }

            if (currentIndex < questions.size - 1) {
                Button(
                    onClick = { viewModel.nextQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("next_question_button")
                ) {
                    Text("Next Question", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(16.dp))
                }
            } else {
                Button(
                    onClick = { viewModel.finishExam() },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple, contentColor = Color.White),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("see_results_button")
                ) {
                    Text("See Results 🎉", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ExamResultsView(
    result: ExamResult,
    examData: ExamData,
    viewModel: ExamViewModel,
    lisanViewModel: LisanViewModel,
    onPracticeWeakAreas: (List<String>) -> Unit,
    onRetake: () -> Unit,
    onClose: () -> Unit
) {
    val config by viewModel.config.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Score Header
        Surface(
            color = EditorialAccentPurple.copy(alpha = 0.12f),
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("🎓", fontSize = 36.sp)
            }
        }

        Text(
            text = "Exam Completed!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialPrimary
        )

        // Grade Badge Card
        Surface(
            color = EditorialContainer,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, EditorialBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${result.percentage}%",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    color = EditorialAccentPurple
                )

                Surface(
                    color = EditorialAccentPurple,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Grade: ${result.gradeLetter}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "Total Score: ${result.totalScore} / ${result.maxScore} pts • Finished in ${result.timeTakenSeconds / 60}m ${result.timeTakenSeconds % 60}s",
                    fontSize = 11.sp,
                    color = EditorialTextSecondary
                )
            }
        }

        // Conversational Exam Injector Card (MANDATORY REQUIREMENT)
        val hasConversationalQuestions = examData.shortQuestions.isNotEmpty() || examData.longQuestions.isNotEmpty()
        if (hasConversationalQuestions) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EditorialAccentPurple.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, EditorialAccentPurple),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("💬", fontSize = 20.sp)
                        Text(
                            text = "CONVERSATIONAL QUESTIONS READY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple
                        )
                    }

                    Text(
                        text = "Your selected 50% MCQs exam has prepared ${examData.shortQuestions.size} short questions and ${examData.longQuestions.size} long questions to be administered conversationally by Lisan AI.",
                        fontSize = 12.sp,
                        color = EditorialPrimary,
                        lineHeight = 16.sp
                    )

                    Button(
                        onClick = {
                            val examLanguage = config.language
                            val sourceName = if (config.sourceType == ExamSourceType.BOOK_PDF) {
                                config.uploadedPdfName ?: config.selectedBook?.title ?: "Uploaded Document"
                            } else {
                                "Previously Learned Material"
                            }
                            val promptBuilder = StringBuilder()
                            if (examLanguage == "Urdu") {
                                promptBuilder.append("LEARNER EXAM START NOTICE • امتحانی اطلاع:\n\n")
                                promptBuilder.append("طالب علم نے اپنے امتحان کا ایم سی کیو (MCQ) حصہ مکمل کر لیا ہے!\n")
                                promptBuilder.append("اب مندرجہ ذیل سوالات گفتگو کی صورت میں ایک ایک کر کے پوچھیں، ان کے جوابات کا جائزہ لیں، اور اردو میں تفصیلی تعلیمی فیڈ بیک فراہم کریں۔\n\n")
                                promptBuilder.append("تفصیلات:\n")
                                promptBuilder.append("- امتحان کی زبان: اردو رہنمائی (Urdu)\n")
                                promptBuilder.append("- ماخذ: $sourceName\n\n")
                            } else {
                                promptBuilder.append("LEARNER EXAM START NOTICE:\n\n")
                                promptBuilder.append("The learner has completed the MCQ section of their exam!\n")
                                promptBuilder.append("Now, please conversationally administer the following Short & Long questions. Ask them one by one, evaluate the learner's replies, and provide educational feedback.\n\n")
                                promptBuilder.append("Details:\n")
                                promptBuilder.append("- Language: English\n")
                                promptBuilder.append("- Source: $sourceName\n\n")
                            }

                            if (examData.shortQuestions.isNotEmpty()) {
                                promptBuilder.append("SHORT QUESTIONS:\n")
                                examData.shortQuestions.forEachIndexed { i, q ->
                                    promptBuilder.append("${i+1}. $q\n")
                                }
                            }

                            if (examData.longQuestions.isNotEmpty()) {
                                promptBuilder.append("\nLONG QUESTIONS:\n")
                                examData.longQuestions.forEachIndexed { i, q ->
                                    promptBuilder.append("${i+1}. $q\n")
                                }
                            }

                            if (examLanguage == "Urdu") {
                                promptBuilder.append("\nبراہ کرم طالب علم سے پہلا سوال پوچھ کر امتحان کا آغاز کریں!")
                            } else {
                                promptBuilder.append("\nPlease ask the learner the first question to begin!")
                            }

                            lisanViewModel.injectSystemExamMessage(promptBuilder.toString())
                            viewModel.clearExam()
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("begin_chat_exam_button")
                    ) {
                        Icon(Icons.Default.QuestionAnswer, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("💬 Begin Conversational Phase in Chat", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Weak Topics Section
        if (result.weakTopics.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Warning, null, tint = Color(0xFFDC2626), modifier = Modifier.size(18.dp))
                        Text("WEAK TOPICS IDENTIFIED", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626))
                    }

                    Text(
                        text = result.weakTopics.joinToString(" • "),
                        fontSize = 12.sp,
                        color = EditorialPrimary,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = { onPracticeWeakAreas(result.weakTopics) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("practice_weak_areas_button")
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("🔄 Practice Weak Areas Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Strong Topics Section
        if (result.strongTopics.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF86EFAC)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                        Text("MASTERED STRENGTHS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }

                    Text(
                        text = result.strongTopics.joinToString(" • "),
                        fontSize = 12.sp,
                        color = EditorialPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.weight(1f).height(44.dp).testTag("retake_exam_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retake Exam", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onClose,
                modifier = Modifier.weight(1f).height(44.dp).testTag("close_exam_results_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
