package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.model.ArabicEquivalentType
import com.example.data.model.Idiom
import com.example.data.model.IdiomCategory
import com.example.data.model.IdiomLevel
import com.example.ui.viewmodel.LisanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdiomScreen(viewModel: LisanViewModel) {
    val searchQuery by viewModel.idiomSearchQuery.collectAsState()
    val filteredIdioms by viewModel.filteredIdioms.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.ensureIdiomsLoaded()
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startVoiceSearch()
        } else {
            Toast.makeText(context, "Microphone access is needed for voice search.", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setIdiomSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            placeholder = { Text("Search for ideom") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = {
                    if (isListening) {
                        viewModel.stopVoiceSearch()
                    } else {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                            viewModel.startVoiceSearch()
                        } else {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                }) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Voice Search",
                        tint = if (isListening) Color.Red else MaterialTheme.colorScheme.primary,
                        modifier = if (isListening) Modifier.graphicsLayer(scaleX = 1.2f, scaleY = 1.2f) else Modifier
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() })
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (searchQuery.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredIdioms.size} matches found",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { viewModel.setIdiomSearchQuery("") }) {
                    Text("Clear search")
                }
            }
        }

        // Popular Chips (Horizontal)
        if (searchQuery.isBlank()) {
            val popularToday = remember(filteredIdioms) { com.example.data.repository.IdiomRepository.getPopularToday5() }
            Text(
                text = "Popular today (Calendar Daily 5)",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                color = MaterialTheme.colorScheme.primary
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(popularToday) { item ->
                    FilterChip(
                        selected = false,
                        onClick = { viewModel.setIdiomSearchQuery(item.expression) },
                        label = { Text(item.expression) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Categories Row
            val selectedCategory by viewModel.selectedIdiomCategory.collectAsState()
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { viewModel.setIdiomCategoryFilter(null) },
                        label = { Text("All Categories") }
                    )
                }
                items(IdiomCategory.values()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            if (selectedCategory == category) {
                                viewModel.setIdiomCategoryFilter(null)
                            } else {
                                viewModel.setIdiomCategoryFilter(category)
                            }
                        },
                        label = { Text(category.label) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Idiom List or Empty State
        if (filteredIdioms.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SearchOff,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "No idioms found for \"$searchQuery\"",
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Try searching in English, Urdu, or by meaning.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredIdioms) { idiom ->
                    IdiomCard(idiom, viewModel)
                }
            }
        }
    }
}

@Composable
fun IdiomCard(idiom: Idiom, viewModel: LisanViewModel) {
    val currentlySpeakingId by viewModel.activeSpeakingId.collectAsState()
    val isPlaying = currentlySpeakingId == idiom.id
    val isLoading = false
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        SelectionContainer {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = idiom.expression,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        IconButton(onClick = {
                            val fullIdiomText = """
                                |${idiom.expression}
                                |Meaning: ${idiom.simpleMeaning}
                                |Urdu: ${idiom.urduMeaning}
                                |Example: ${idiom.example}
                            """.trimMargin()
                            clipboardManager.setText(AnnotatedString(fullIdiomText))
                            Toast.makeText(context, "Copied idiom to clipboard • کاپی ہو گیا", Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Idiom",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(
                            onClick = { viewModel.playIdiomAudio(idiom.id) },
                            enabled = !isLoading
                        ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.StopCircle else Icons.Default.VolumeUp,
                                contentDescription = if (isPlaying) "Stop" else "Pronounce",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    val isLiked by viewModel.isLiked("Idiom", idiom.id).collectAsState(initial = false)
                    IconButton(onClick = {
                        val fullIdiomText = """
                            |【 IDIOM / EXPRESSION 】
                            |${idiom.expression}
                            |
                            |【 MEANING 】
                            |English: ${idiom.simpleMeaning}
                            |Urdu: ${idiom.urduMeaning}
                            |Arabic: ${idiom.arabicMeaning}
                            |
                            |【 EXAMPLE 】
                            |${idiom.example}
                            |
                            |【 ORIGIN 】
                            |${idiom.origin ?: "General English idiom"}
                        """.trimMargin()
                        viewModel.toggleLike(
                            com.example.data.model.LikedItem(
                                id = "like_idiom_${idiom.id}",
                                contentType = "Idiom",
                                contentId = idiom.id,
                                title = idiom.expression,
                                preview = fullIdiomText
                            )
                        )
                    }) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (isLiked) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    val isSaved by viewModel.isSaved("Idiom", idiom.id).collectAsState(initial = false)
                    IconButton(onClick = {
                        val fullIdiomText = """
                            |【 IDIOM / EXPRESSION 】
                            |${idiom.expression}
                            |
                            |【 MEANING 】
                            |English: ${idiom.simpleMeaning}
                            |Urdu: ${idiom.urduMeaning}
                            |Arabic: ${idiom.arabicMeaning}
                            |
                            |【 EXAMPLE 】
                            |${idiom.example}
                            |
                            |【 ORIGIN 】
                            |${idiom.origin ?: "General English idiom"}
                        """.trimMargin()
                        viewModel.toggleLibrarySave(
                            com.example.data.model.LibraryItem(
                                id = "lib_idiom_${idiom.id}",
                                contentType = "Idiom",
                                contentId = idiom.id,
                                title = idiom.expression,
                                preview = fullIdiomText,
                                payloadJson = null
                            )
                        )
                    }) {
                        Icon(
                            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save to Library",
                            tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Text(
                text = idiom.simpleMeaning,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.outlineVariant)

            // Urdu
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Urdu:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = idiom.urduMeaning,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Right,
                    style = TextStyle(textDirection = TextDirection.Rtl),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Arabic
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Likewise in Arabic, you say:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = idiom.arabicMeaning,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Example Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Text("Example:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                Text(idiom.example, style = MaterialTheme.typography.bodyMedium)
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Level: ${idiom.level.label}",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
}
