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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuthState
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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = EditorialBackground,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EditorialHeaderBg)
                    .border(BorderStroke(1.dp, EditorialBorderSoft))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.setSelectedTab(0) }, // Go back to Lessons screen (Tab 0)
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("help_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Lessons",
                            tint = EditorialPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Help & Feedback",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                        Text(
                            text = "We are here to help you learn better",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
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
                                        // Simulate API submission call
                                        delay(1500)
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

            // --- DIRECT SUPPORT CONTACT / QUICK ACTION BUTTONS ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Still Need Help?",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // "Email Support" button
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:support@lisangrammar.com")
                                putExtra(Intent.EXTRA_SUBJECT, "Lisan AI Tutor Support Request")
                                putExtra(Intent.EXTRA_TEXT, "Hello Lisan Team,\n\n[Please enter your issue here]\n\nEmail: $emailInput")
                            }
                            try {
                                context.startActivity(Intent.createChooser(intent, "Send Email"))
                            } catch (e: Exception) {
                                Toast.makeText(context, "No email client installed.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialAccentPurple),
                        border = BorderStroke(1.dp, EditorialAccentPurple),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("email_support_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email Support",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Email Support", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    // "Community / FAQ Guide" link
                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://lisangrammar.com/faq"))
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Cannot open browser link.", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EditorialAccentPurple),
                        border = BorderStroke(1.dp, EditorialBorder),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("community_guide_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Launch,
                            contentDescription = "Community",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FAQ Website", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
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
