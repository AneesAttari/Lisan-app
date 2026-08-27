package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthState
import com.example.ui.components.ScrollToBottomFAB
import com.example.ui.components.EditorialCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HelpAndFeedbackScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val authState by viewModel.authState.collectAsState()

    // Retrieve pre-filled email if user is logged in or guest
    val prefilledEmail = remember(authState) {
        when (val state = authState) {
            is AuthState.LoggedIn -> state.user.email
            is AuthState.Guest -> state.user.email
            is AuthState.LoggedOut -> ""
        }
    }

    // Tabs / Sections: 0 for FAQ, 1 for Feedback
    var selectedTabState by remember { mutableStateOf(0) }

    // Search query for FAQ
    var searchQuery by remember { mutableStateOf("") }

    // Collapsed state map for accordion FAQ
    val expandedStates = remember { mutableStateMapOf<Int, Boolean>() }

    // Feedback Form States
    var selectedCategory by remember { mutableStateOf("General Feedback") }
    var userRating by remember { mutableStateOf(0) }
    var emailInput by remember { mutableStateOf(prefilledEmail) }
    var feedbackDescription by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessModal by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    // Pre-filled FAQs
    val faqItems = listOf(
        FaqItem(
            question = "How does Lisan AI Tutor work?",
            answer = "Lisan AI Tutor is an advanced learning assistant designed specifically for Arabic speakers studying English grammar. It compares English tenses, sentence structures, and rules with their closest Arabic grammar equivalents (e.g., comparing Nominal vs Verbal sentences or Present Tense vs Al-Fi'l Al-Mudari), making learning intuitive and context-aware."
        ),
        FaqItem(
            question = "Can I learn both English and Arabic grammar?",
            answer = "Yes! While the primary focus is helping you learn English, Lisan explains the concepts by grounding them in classical and modern Arabic grammar. This dual-comparison approach reinforces your understanding of both languages simultaneously."
        ),
        FaqItem(
            question = "What should I do if the AI stops responding?",
            answer = "If the AI stops responding or you encounter connection issues, please check your internet connection. You can also try clearing the chat cache or restarting the app. Your conversations are safely persisted in your local database."
        ),
        FaqItem(
            question = "How do I save my past conversations?",
            answer = "All your active chat sessions are automatically saved in real-time to your local database. You can access them anytime via the 'Past Conversations' section in the sidebar menu."
        )
    )

    // Filtered FAQ Items
    val filteredFaqItems = faqItems.filter {
        it.question.contains(searchQuery, ignoreCase = true) ||
        it.answer.contains(searchQuery, ignoreCase = true)
    }

    // Categories list for Feedback Selectors
    val categories = listOf("Bug Report", "Feature Suggestion", "General Feedback", "Other")

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section Tabs: FAQ vs Feedback
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EditorialContainer, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TabButton(
                    title = "FAQ Guide",
                    isSelected = selectedTabState == 0,
                    onClick = { selectedTabState = 0 },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("faq_tab_button")
                )
                TabButton(
                    title = "Send Feedback",
                    isSelected = selectedTabState == 1,
                    onClick = { selectedTabState = 1 },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("feedback_tab_button")
                )
            }

            // Screen Content switcher based on Tab
            AnimatedContent(
                targetState = selectedTabState,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "HelpScreenContent"
            ) { tabIndex ->
                if (tabIndex == 0) {
                    // --- FAQ SECTION ---
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Search Bar
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search frequently asked questions...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = EditorialTextSecondary
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear search",
                                            tint = EditorialTextSecondary
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("faq_search_bar"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = EditorialTextPrimary,
                                unfocusedTextColor = EditorialTextPrimary,
                                focusedBorderColor = EditorialAccentPurple,
                                unfocusedBorderColor = EditorialBorder,
                                focusedContainerColor = EditorialSurface,
                                unfocusedContainerColor = EditorialSurface
                            ),
                            shape = RoundedCornerShape(16.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
                        )

                        // Accordion Question list
                        if (filteredFaqItems.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.HelpOutline,
                                        contentDescription = "No results",
                                        tint = EditorialTextMuted,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Text(
                                        text = "No questions found matching '$searchQuery'",
                                        color = EditorialTextSecondary,
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            filteredFaqItems.forEachIndexed { index, item ->
                                val isExpanded = expandedStates[index] ?: false
                                AccordionCard(
                                    question = item.question,
                                    answer = item.answer,
                                    isExpanded = isExpanded,
                                    onToggle = { expandedStates[index] = !isExpanded },
                                    modifier = Modifier.testTag("faq_item_$index")
                                )
                            }
                        }
                    }
                } else {
                    // --- SEND FEEDBACK SECTION ---
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        EditorialCard {
                            Text(
                                text = "Submit Feedback or Report a Bug",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialPrimary
                            )

                            // 1. Category Selector Chips
                            Text(
                                text = "Select Category",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                categories.forEach { category ->
                                    val isCatSelected = selectedCategory == category
                                    FilterChip(
                                        selected = isCatSelected,
                                        onClick = { selectedCategory = category },
                                        label = { Text(category) },
                                        leadingIcon = if (isCatSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EditorialPillBg,
                                            selectedLabelColor = EditorialPrimary,
                                            selectedLeadingIconColor = EditorialPrimary,
                                            containerColor = EditorialBackground,
                                            labelColor = EditorialTextSecondary
                                        ),
                                        modifier = Modifier.testTag("category_chip_$category")
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 2. Interactive 5-Star Rating Widget
                            Text(
                                text = "Rate Your Experience",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialTextSecondary
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                (1..5).forEach { star ->
                                    IconButton(
                                        onClick = { userRating = star },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .testTag("rating_star_$star")
                                    ) {
                                        Icon(
                                            imageVector = if (star <= userRating) Icons.Default.Star else Icons.Default.StarOutline,
                                            contentDescription = "Rate $star Stars",
                                            tint = if (star <= userRating) Color(0xFFFFB300) else EditorialTextMuted,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // 3. Optional Email Input
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = { emailInput = it },
                                label = { Text("Your Email Address (Optional)") },
                                placeholder = { Text("e.g. learner@lisan.com") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = EditorialTextSecondary
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("feedback_email_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = EditorialTextPrimary,
                                    unfocusedTextColor = EditorialTextPrimary,
                                    focusedBorderColor = EditorialAccentPurple,
                                    unfocusedBorderColor = EditorialBorder,
                                    focusedContainerColor = EditorialSurface,
                                    unfocusedContainerColor = EditorialSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                )
                            )

                            // 4. Textarea for feedback description
                            OutlinedTextField(
                                value = feedbackDescription,
                                onValueChange = { feedbackDescription = it },
                                label = { Text("Describe details...") },
                                placeholder = { Text("Tell us what happened, what you'd like to see, or any questions you have...") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .testTag("feedback_description_input"),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = EditorialTextPrimary,
                                    unfocusedTextColor = EditorialTextPrimary,
                                    focusedBorderColor = EditorialAccentPurple,
                                    unfocusedBorderColor = EditorialBorder,
                                    focusedContainerColor = EditorialSurface,
                                    unfocusedContainerColor = EditorialSurface
                                ),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
                            )

                            // 5. Submit Button with interactive loading state
                            Button(
                                onClick = {
                                    if (feedbackDescription.isBlank()) {
                                        Toast.makeText(context, "Please enter some feedback details.", Toast.LENGTH_SHORT).show()
                                        return@Button
                                    }
                                    coroutineScope.launch {
                                        isSubmitting = true
                                        viewModel.submitFeedback(
                                            subject = selectedCategory,
                                            message = "Rating: $userRating/5\nEmail: $emailInput\n\n$feedbackDescription",
                                            category = selectedCategory
                                        )
                                        delay(1000)
                                        isSubmitting = false
                                        showSuccessModal = true
                                        // Clear fields
                                        feedbackDescription = ""
                                        userRating = 0
                                    }
                                },
                                enabled = !isSubmitting,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EditorialAccentPurple,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp)
                                    .testTag("submit_feedback_button")
                            ) {
                                if (isSubmitting) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Submitting...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Send,
                                        contentDescription = "Submit",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Submit Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- CREATOR ANALYTICS & LEARNER FEEDBACK INBOX ---
            val totalLikes by viewModel.totalLikesCount.collectAsState()
            val totalDislikes by viewModel.totalDislikesCount.collectAsState()
            val feedbackList by viewModel.allFeedback.collectAsState()

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = EditorialSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, EditorialBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Creator Inbox & Analytics",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                        Surface(
                            color = EditorialAccentPurple.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Offline Sync Active",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Likes box
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFF10B981).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "$totalLikes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                                Text(text = "Total Likes 👍", fontSize = 11.sp, color = EditorialTextSecondary)
                            }
                        }

                        // Dislikes box
                        Surface(
                            modifier = Modifier.weight(1f),
                            color = Color(0xFFEF4444).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "$totalDislikes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                                Text(text = "Total Dislikes 👎", fontSize = 11.sp, color = EditorialTextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Received Learner Responses & Feedback (${feedbackList.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (feedbackList.isEmpty()) {
                        Text(
                            text = "No feedback submissions received yet. Learner ratings and comments will appear here automatically.",
                            fontSize = 12.sp,
                            color = EditorialTextMuted,
                            fontStyle = FontStyle.Italic
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            feedbackList.take(5).forEach { fb ->
                                Surface(
                                    color = EditorialSurface,
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, EditorialBorderSoft)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp).fillMaxWidth()) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = fb.subject.ifBlank { "General Feedback" },
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = EditorialAccentPurple
                                            )
                                            val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                                            Text(
                                                text = sdf.format(Date(fb.timestamp)),
                                                fontSize = 10.sp,
                                                color = EditorialTextMuted
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = fb.message,
                                            fontSize = 12.sp,
                                            color = EditorialTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = EditorialSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, EditorialBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Support & About Lisan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )

                OutlinedButton(
                    onClick = { showHelpDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Help, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("How to Use Lisan Grammar")
                }

                OutlinedButton(
                    onClick = {
                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:aneesattari67@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Lisan Grammar App Feedback (v2.4)")
                            putExtra(Intent.EXTRA_TEXT, "Hi Lisan Team,\n\nDevice: ${android.os.Build.MODEL} (Android ${android.os.Build.VERSION.RELEASE})\n\nFeedback:\n")
                        }
                        try {
                            context.startActivity(Intent.createChooser(emailIntent, "Send Feedback Email"))
                        } catch (e: Exception) {
                            Toast.makeText(context, "No email app found.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Feedback Email")
                }

                OutlinedButton(
                    onClick = {
                        val whatsappIntent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://wa.me/923171503094?text=Hi%20Lisan%20Team,%0A%0AFeedback:%0A")
                        }
                        try {
                            context.startActivity(whatsappIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not found.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Contact on WhatsApp (0317 1503094)")
                }

                OutlinedButton(
                    onClick = {
                        val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                        try {
                            context.startActivity(marketIntent)
                        } catch (e: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rate Lisan on Play Store")
                }

                TextButton(
                    onClick = { showLicensesDialog = true },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Open Source Licenses", fontSize = 12.sp, color = EditorialAccentPurple)
                }

                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Lisan Grammar • Version 2.4.0 (Build 102)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialTextSecondary)
                    Text("Designed with ❤️ for English & Arabic Grammar Learners", fontSize = 11.sp, color = EditorialTextMuted)
                }
            }
        }

        if (showHelpDialog) {
            AlertDialog(
                onDismissRequest = { showHelpDialog = false },
                title = { Text("How to Use Lisan Grammar", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("1. Learn tab: Browse comprehensive grammar lessons comparing English and Arabic structures.")
                        Text("2. AI Tutor tab: Chat with the AI tutor, ask grammar questions, practice conversations, or request sentence corrections.")
                        Text("3. Analyze tab: Paste any English sentence for detailed Arabic-English structural breakdown.")
                        Text("4. Library & Quiz tabs: Review saved bookmarks, liked items, idioms, and take interactive quizzes.")
                    }
                },
                confirmButton = {
                    Button(onClick = { showHelpDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)) {
                        Text("Got It")
                    }
                }
            )
        }

        if (showLicensesDialog) {
            AlertDialog(
                onDismissRequest = { showLicensesDialog = false },
                title = { Text("Open Source Licenses", fontWeight = FontWeight.Bold) },
                text = {
                    Text("Lisan Grammar uses Jetpack Compose, Room Database, Material 3, and Kotlin Coroutines. Licensed under Apache 2.0 / MIT licenses.")
                },
                confirmButton = {
                    Button(onClick = { showLicensesDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)) {
                        Text("Close")
                    }
                }
            )
        }

        // Success Confirmation Modal
        if (showSuccessModal) {
            AlertDialog(
                onDismissRequest = { showSuccessModal = false },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(28.dp)
                        )
                        Text("Feedback Received")
                    }
                },
                text = {
                    Text("Thank you! Your feedback has been received. Our team will review your feedback and get back to you if necessary.")
                },
                confirmButton = {
                    Button(
                        onClick = { showSuccessModal = false },
                        colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)
                    ) {
                        Text("OK")
                    }
                },
                modifier = Modifier.testTag("success_modal")
            )
        }

        ScrollToBottomFAB(
            scrollState = scrollState,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}

@Composable
fun TabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) EditorialSurface else Color.Transparent
    val textColor = if (isSelected) EditorialPrimary else EditorialTextSecondary
    val elevation = if (isSelected) 2.dp else 0.dp

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = backgroundColor,
        shadowElevation = elevation,
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp,
                color = textColor
            )
        }
    }
}

@Composable
fun AccordionCard(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp))
            .clickable { onToggle() },
        color = EditorialSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, EditorialBorderSoft)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = EditorialPrimary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = EditorialPrimary
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = EditorialBorderSoft)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = answer,
                        fontSize = 13.sp,
                        color = EditorialTextSecondary,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

data class FaqItem(val question: String, val answer: String)
