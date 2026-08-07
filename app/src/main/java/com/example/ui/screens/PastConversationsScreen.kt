package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialMixedText
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PastConversationsScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val sessions by viewModel.conversationSessions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTopicFilter by remember { mutableStateOf("All") } // "All", "Grammar", "Idiom", "Pronunciation", "Revision"

    var sessionToDelete by remember { mutableStateOf<ConversationSessionEntity?>(null) }
    var sessionToRename by remember { mutableStateOf<ConversationSessionEntity?>(null) }
    var newTitleInput by remember { mutableStateOf("") }

    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

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
            title = { Text("Delete Conversation?", fontWeight = FontWeight.Bold, color = EditorialPrimary) },
            text = { Text("This will permanently delete this conversation and detach its content from your Tutor memory.", color = EditorialTextSecondary) },
            dismissButton = {
                TextButton(onClick = { sessionToDelete = null }) {
                    Text("Cancel", color = EditorialAccentPurple)
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
            title = { Text("Rename Conversation", fontWeight = FontWeight.Bold, color = EditorialPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter a new title for this discussion:", color = EditorialTextSecondary, fontSize = 13.sp)
                    OutlinedTextField(
                        value = newTitleInput,
                        onValueChange = { newTitleInput = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().testTag("rename_input")
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { sessionToRename = null }) {
                    Text("Cancel", color = EditorialAccentPurple)
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
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
                ) {
                    Text("Save", color = Color.White)
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFCFBF9)) // Warm-white surface
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Page Header
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Past Conversations",
                fontSize = 24.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1B4B) // Deep Indigo
            )
            Text(
                text = "Continue learning where you left off.",
                fontSize = 14.sp,
                color = EditorialTextSecondary
            )
        }

        // Search and Filter Card
        EditorialCard {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search conversations...", fontSize = 13.sp, color = EditorialTextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EditorialTextMuted) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = EditorialTextMuted)
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = EditorialAccentPurple,
                        unfocusedBorderColor = EditorialBorderSoft
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("conversation_search_input")
                )

                // Topic Filter Pills
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Topic:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
                    listOf("All", "Grammar", "Idiom", "Pronunciation", "Revision", "Daily Challenge").forEach { topic ->
                        val isSelected = selectedTopicFilter == topic
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedTopicFilter = topic },
                            color = if (isSelected) Color(0xFF818CF8) else Color(0xFFEFF6FF), // Lavender accents vs soft pale-blue
                            border = BorderStroke(1.dp, if (isSelected) Color(0xFF818CF8) else EditorialBorderSoft),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = topic,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else Color(0xFF1E1B4B),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Conversations List
        if (filteredSessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = EditorialTextMuted
                    )
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No past conversations match your search" else "No past conversations yet. Start chatting with AI Tutor!",
                        fontSize = 14.sp,
                        color = EditorialTextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("past_conversations_list"),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredSessions) { session ->
                    Surface(
                        color = Color.White,
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.continueConversationSession(session.id) }
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Surface(
                                        color = Color(0xFFEFF6FF),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            text = session.topicLabel,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E1B4B),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Text(
                                        text = dateFormat.format(Date(session.updatedAt)),
                                        fontSize = 11.sp,
                                        color = EditorialTextMuted
                                    )
                                }

                                Text(
                                    text = "${session.messageCount} messages · Tutor chat",
                                    fontSize = 11.sp,
                                    color = EditorialAccentPurple,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Text(
                                text = session.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E1B4B)
                            )

                            Text(
                                text = session.preview,
                                fontSize = 13.sp,
                                color = EditorialTextSecondary,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            EditorialDivider()

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Actions: Rename & Delete
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    TextButton(
                                        onClick = {
                                            sessionToRename = session
                                            newTitleInput = session.title
                                        },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = EditorialTextSecondary)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Rename", fontSize = 12.sp, color = EditorialTextSecondary)
                                    }

                                    TextButton(
                                        onClick = { sessionToDelete = session },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Delete", fontSize = 12.sp, color = Color.Red)
                                    }
                                }

                                // Continue Action Button
                                Button(
                                    onClick = { viewModel.continueConversationSession(session.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Continue", fontSize = 13.sp, color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
