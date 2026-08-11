package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ConversationSessionEntity
import com.example.ui.components.EditorialCard
import com.example.ui.components.ScrollToBottomFAB
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PastConversationsScreen(
    viewModel: LisanViewModel,
    onBack: () -> Unit,
    onConversationSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = EditorialTheme.colors
    val sessions by viewModel.conversationSessions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTopicFilter by remember { mutableStateOf("All") } // "All", "Grammar", "Idiom", "Pronunciation", "Revision"

    var sessionToDelete by remember { mutableStateOf<ConversationSessionEntity?>(null) }
    var sessionToRename by remember { mutableStateOf<ConversationSessionEntity?>(null) }
    var newTitleInput by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    var isLoading by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        delay(400) // Smooth loading transition
        isLoading = false
    }

    val filteredSessions = remember(sessions, searchQuery, selectedTopicFilter) {
        sessions.filter { session ->
            val matchesSearch = session.title.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault())) ||
                    session.preview.lowercase(Locale.getDefault()).contains(searchQuery.lowercase(Locale.getDefault()))
            val matchesTopic = selectedTopicFilter == "All" || session.topicLabel.equals(selectedTopicFilter, ignoreCase = true)
            matchesSearch && matchesTopic
        }
    }

    // Delete Confirmation Dialog
    if (sessionToDelete != null) {
        AlertDialog(
            onDismissRequest = { sessionToDelete = null },
            title = { Text("Delete Conversation?", fontWeight = FontWeight.Bold, color = colors.primary) },
            text = { Text("This will permanently delete this conversation and detach its content from your Tutor memory.", color = colors.textSecondary) },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Cancel", color = colors.accentPurple)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sessionToDelete?.let { viewModel.deleteConversationSession(it.id) }
                        sessionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Delete", color = Color.White)
                }
            }
        )
    }

    // Rename Dialog
    if (sessionToRename != null) {
        AlertDialog(
            onDismissRequest = { sessionToRename = null },
            title = { Text("Rename Conversation", fontWeight = FontWeight.Bold, color = colors.primary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter a new title for this discussion:", color = colors.textSecondary, fontSize = 13.sp)
                    OutlinedTextField(
                        value = newTitleInput,
                        onValueChange = { newTitleInput = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("rename_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = colors.surface,
                            unfocusedContainerColor = colors.surface,
                            focusedBorderColor = colors.accentPurple,
                            unfocusedBorderColor = colors.borderSoft
                        )
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToRename = null }) {
                    Text("Cancel", color = colors.accentPurple)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        sessionToRename?.let {
                            if (newTitleInput.isNotBlank()) {
                                viewModel.renameConversationSession(it.id, newTitleInput.trim())
                            }
                        }
                        sessionToRename = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) {
                    Text("Save", color = Color.White)
                }
            }
        )
    }

    if (isLoading) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CircularProgressIndicator(
                    color = colors.accentPurple,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(48.dp)
                )
                Text(
                    text = "Retrieving past conversations...",
                    fontSize = 14.sp,
                    color = colors.textSecondary
                )
            }
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(colors.background)
        ) {
Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 800.dp)
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header with Back button
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = colors.surface,
                        shape = CircleShape,
                        shadowElevation = 2.dp,
                        border = BorderStroke(1.dp, colors.borderSoft),
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { onBack() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = colors.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Text(
                        text = "Past Conversations",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary,
                        fontFamily = FontFamily.Serif
                    )
                }
                Text(
                    text = "Past Conversations",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Search & Filters Card
                EditorialCard(
                    containerColor = colors.surface,
                    borderColor = colors.borderSoft
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search conversations...", fontSize = 13.sp, color = colors.textMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = colors.textMuted) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = colors.textMuted)
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = colors.surface,
                                unfocusedContainerColor = colors.surface,
                                focusedBorderColor = colors.accentPurple,
                                unfocusedBorderColor = colors.borderSoft
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("conversation_search_input")
                        )

                        // Horizontally scrollable topic pills to prevent vertical squishing
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Topic:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.textMuted
                            )
                            listOf("All", "Grammar", "Idiom", "Pronunciation", "Revision", "Daily Challenge").forEach { topic ->
                                val isSelected = selectedTopicFilter == topic
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { selectedTopicFilter = topic },
                                    color = if (isSelected) colors.accentPurple else colors.pillBg,
                                    border = BorderStroke(1.dp, if (isSelected) colors.accentPurple else colors.borderSoft),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = topic,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else colors.textPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Conversations List / Empty State
                if (filteredSessions.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                color = colors.pillBg,
                                shape = CircleShape,
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.Forum,
                                        contentDescription = null,
                                        modifier = Modifier.size(32.dp),
                                        tint = colors.accentPurple
                                    )
                                }
                            }

                            Text(
                                text = if (searchQuery.isNotEmpty()) "No Matching Conversations" else "Your Learning History is Empty",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = if (searchQuery.isNotEmpty()) {
                                    "We couldn't find any conversations matching \"$searchQuery\"."
                                } else {
                                    "You haven't had any tutor discussions yet. Chat with AI Tutor to get instant explanations, and view your records here!"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = colors.textSecondary,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            if (searchQuery.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        searchQuery = ""
                                        selectedTopicFilter = "All"
                                    },
                                    border = BorderStroke(1.dp, colors.accentPurple),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(40.dp)
                                ) {
                                    Text("Clear Search", color = colors.accentPurple)
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.setSelectedTab(2) }, // Tab 2: AI Tutor Chat
                                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Start Learning", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    val listState = rememberLazyListState()
                    val coroutineScope = rememberCoroutineScope()
                    val isScrollToBottomVisible by remember {
                        derivedStateOf {
                            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
                            if (filteredSessions.isEmpty()) false
                            else {
                                lastVisibleItem == null || lastVisibleItem.index < filteredSessions.size - 1
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("past_conversations_list"),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 16.dp)
                        ) {
                            items(filteredSessions) { session ->
                            Surface(
                                color = colors.surface,
                                border = BorderStroke(1.dp, colors.borderSoft),
                                shape = RoundedCornerShape(20.dp),
                                shadowElevation = 2.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.continueConversationSession(session.id)
                                        onConversationSelected()
                                    }
                            ) {
                                Column(
                                    modifier = Modifier.padding(18.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            color = when (session.topicLabel.lowercase()) {
                                                "grammar" -> Color(0xFFEFF6FF)
                                                "idiom" -> Color(0xFFFDF2F8)
                                                "pronunciation" -> Color(0xFFECFDF5)
                                                "revision" -> Color(0xFFFFFBEB)
                                                else -> colors.pillBg
                                            },
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text(
                                                text = session.topicLabel,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = colors.textPrimary,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                maxLines = 1
                                            )
                                        }
                                        Text(
                                            text = dateFormat.format(Date(session.updatedAt)),
                                            fontSize = 11.sp,
                                            color = colors.textMuted,
                                            modifier = Modifier.weight(1f),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "${session.messageCount} messages",
                                            fontSize = 11.sp,
                                            color = colors.accentPurple,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1
                                        )
                                    }

                                    Text(
                                        text = session.title,
                                        fontSize = 17.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = colors.textPrimary
                                    )

                                    Text(
                                        text = session.preview,
                                        fontSize = 13.sp,
                                        color = colors.textSecondary,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    HorizontalDivider(
                                        color = colors.borderSoft,
                                        thickness = 1.dp,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )

                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            TextButton(
                                                onClick = {
                                                    sessionToRename = session
                                                    newTitleInput = session.title
                                                },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Edit,
                                                    contentDescription = "Rename",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = colors.textSecondary
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Rename", fontSize = 12.sp, color = colors.textSecondary)
                                            }

                                            TextButton(
                                                onClick = { sessionToDelete = session },
                                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Delete,
                                                    contentDescription = "Delete",
                                                    modifier = Modifier.size(16.dp),
                                                    tint = Color(0xFFEF4444)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Delete", fontSize = 12.sp, color = Color(0xFFEF4444))
                                            }
                                        }

                                        Button(
                                            onClick = {
                                        viewModel.continueConversationSession(session.id)
                                        onConversationSelected()
                                    },
                                            colors = ButtonDefaults.buttonColors(containerColor = colors.accentPurple),
                                            shape = RoundedCornerShape(12.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.PlayArrow,
                                                contentDescription = "Continue",
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Continue", fontSize = 13.sp, color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ScrollToBottomFAB(
                        visible = isScrollToBottomVisible,
                        onClick = {
                            coroutineScope.launch {
                                if (filteredSessions.isNotEmpty()) {
                                    listState.animateScrollToItem(filteredSessions.size - 1)
                                }
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
}
