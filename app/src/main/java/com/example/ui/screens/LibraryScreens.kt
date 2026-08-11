package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LibraryItem
import com.example.ui.components.EditorialCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun UnifiedLibraryScreen(viewModel: LisanViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Saved • محفوظ", "Liked • پسندیدہ")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = EditorialSurface,
            contentColor = EditorialPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = EditorialPrimary
                )
            },
            divider = { HorizontalDivider(color = EditorialBorder) }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        when (selectedTabIndex) {
            0 -> SavedItemsTab(viewModel)
            1 -> LikedItemsTab(viewModel)
        }
    }
}

@Composable
private fun SavedItemsTab(viewModel: LisanViewModel) {
    val items by viewModel.allLibraryItems.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }
    var openedItem by remember { mutableStateOf<LibraryItem?>(null) }
    
    val filters = listOf("All", "Lessons", "Idioms", "Analysis", "Tutor Responses", "Quizzes")
    
    val filteredItems = if (selectedFilter == "All") {
        items
    } else {
        items.filter { it.contentType == mapFilterToType(selectedFilter) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filters) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = EditorialPrimary,
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedFilter == filter,
                        borderColor = EditorialBorder,
                        selectedBorderColor = EditorialPrimary
                    )
                )
            }
        }
        
        if (filteredItems.isEmpty()) {
            EmptyLibraryState("No saved items yet. Save items while learning or chatting to view them offline!")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredItems) { item ->
                    LibraryItemCard(
                        item = item,
                        onOpen = { openedItem = item },
                        onRemove = { viewModel.toggleLibrarySave(item) }
                    )
                }
            }
        }
    }

    openedItem = openedItem?.let { item ->
        LibraryItemDetailDialog(
            item = item,
            onDismiss = { openedItem = null }
        )
        item
    }
}

@Composable
private fun LibraryItemDetailDialog(
    item: LibraryItem,
    onDismiss: () -> Unit
) {
    val sdf = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())
    val dateStr = sdf.format(Date(item.savedTimestamp))

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Surface(
                    color = EditorialPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.contentType.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextPrimary
                )
                Text(
                    text = "Saved on: $dateStr (Offline)",
                    fontSize = 11.sp,
                    color = EditorialTextMuted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 520.dp)
            ) {
                HorizontalDivider(color = EditorialBorderSoft)
                Spacer(modifier = Modifier.height(12.dp))
                val scrollState = rememberScrollState()
                Box(modifier = Modifier.weight(1f, fill = false)) {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        Text(
                            text = item.preview ?: "No additional details available.",
                            fontSize = 15.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Close", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = EditorialSurface,
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun LikedItemsTab(viewModel: LisanViewModel) {
    val items by viewModel.allLikedItems.collectAsState()
    var openedLikedItem by remember { mutableStateOf<com.example.data.model.LikedItem?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (items.isEmpty()) {
            EmptyLibraryState("No liked items yet. Tap the like button on AI tutor responses or lessons to bookmark them here!")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items) { item ->
                    EditorialCard(
                        modifier = Modifier.clickable { openedLikedItem = item }
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.contentType.uppercase(),
                                    fontSize = 10.sp,
                                    color = EditorialTextMuted,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.title,
                                    fontSize = 16.sp,
                                    color = EditorialTextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                item.preview?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = it,
                                        fontSize = 13.sp,
                                        color = EditorialTextSecondary,
                                        maxLines = 2,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                            IconButton(onClick = { viewModel.toggleLike(item) }) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Unlike",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    openedLikedItem?.let { item ->
        val sdf = SimpleDateFormat("d MMM yyyy, HH:mm", Locale.getDefault())
        val dateStr = sdf.format(Date(item.likedTimestamp))

        AlertDialog(
            onDismissRequest = { openedLikedItem = null },
            title = {
                Column {
                    Surface(
                        color = Color.Red.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = item.contentType.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = Color.Red,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextPrimary
                    )
                    Text(
                        text = "Liked on: $dateStr (Offline)",
                        fontSize = 11.sp,
                        color = EditorialTextMuted,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 520.dp)
                ) {
                    HorizontalDivider(color = EditorialBorderSoft)
                    Spacer(modifier = Modifier.height(12.dp))
                    val scrollState = rememberScrollState()
                    Box(modifier = Modifier.weight(1f, fill = false)) {
                        Column(modifier = Modifier.verticalScroll(scrollState)) {
                            Text(
                                text = item.preview ?: "No additional details available for this liked item.",
                                fontSize = 15.sp,
                                color = EditorialTextSecondary,
                                lineHeight = 24.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { openedLikedItem = null },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Close", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = EditorialSurface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun EmptyLibraryState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.LibraryBooks,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = EditorialBorder
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = message, color = EditorialTextMuted, fontSize = 14.sp)
        }
    }
}

@Composable
fun LibraryItemCard(
    item: LibraryItem,
    onOpen: () -> Unit,
    onRemove: () -> Unit
) {
    val sdf = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
    val dateStr = sdf.format(Date(item.savedTimestamp))
    
    val icon = when(item.contentType) {
        "Lesson" -> "📘"
        "Idiom" -> "💬"
        "Analysis" -> "🔍"
        "Quiz" -> "🎯"
        else -> "📝"
    }

    EditorialCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = EditorialPrimary.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.contentType.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(text = dateStr, fontSize = 10.sp, color = EditorialTextMuted)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = item.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary,
                lineHeight = 22.sp
            )
            
            item.preview?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = EditorialTextSecondary,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp),
                    lineHeight = 18.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Open", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                OutlinedButton(
                    onClick = onRemove,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EditorialBorder),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.Red.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Remove", fontSize = 12.sp, color = Color.Red.copy(alpha = 0.7f), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun mapFilterToType(filter: String): String = when(filter) {
    "Lessons" -> "Lesson"
    "Idioms" -> "Idiom"
    "Analysis" -> "Analysis"
    "Tutor Responses" -> "Tutor Response"
    "Quizzes" -> "Quiz"
    else -> ""
}

