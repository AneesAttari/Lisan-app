package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ui.navigation.HashRouter
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun ChatBackgroundRenderer(
    theme: String,
    bgUri: String,
    isSystemDark: Boolean,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        if (theme == "Custom" && bgUri.isNotEmpty()) {
            val painter = rememberAsyncImagePainter(
                model = Uri.parse(bgUri),
                onError = {
                    // Fallback to transparent/default if loading fails
                }
            )
            androidx.compose.foundation.Image(
                painter = painter,
                contentDescription = "Chat Background Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Soft overlay dimming layer for maximum readability of text bubbles
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        if (isSystemDark) Color.Black.copy(alpha = 0.65f)
                        else Color.White.copy(alpha = 0.65f)
                    )
            )
        } else {
            val bgColor = when (theme) {
                "Light" -> Color(0xFFF7F5FA)
                "Dark" -> Color(0xFF12131C)
                "Islamic Green" -> Color(0xFFE8F5E9)
                "Blue" -> Color(0xFFE3F2FD)
                "Warm" -> Color(0xFFFFF3E0)
                "Minimal" -> Color(0xFFFAFAFA)
                else -> Color.Transparent // Default
            }

            if (theme != "Default") {
                if (theme == "Islamic Green") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))
                                )
                            )
                    )
                } else if (theme == "Blue") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFE3F2FD), Color(0xFFBBDEFB))
                                )
                            )
                    )
                } else if (theme == "Warm") {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFFFFF3E0), Color(0xFFFFCC80))
                                )
                            )
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(bgColor)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatThemeSelectionScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userSettings by viewModel.userSettings.collectAsState()
    val isSystemDark = isSystemInDarkTheme()
    
    val currentTheme = userSettings.chatTheme
    val currentBgUri = userSettings.chatBackgroundImageUri

    // Temporary states to show direct previews when hovering/clicking on items
    var previewTheme by remember { mutableStateOf(currentTheme) }
    var previewBgUri by remember { mutableStateOf(currentBgUri) }

    // Sync preview with actual settings when they change
    LaunchedEffect(currentTheme, currentBgUri) {
        previewTheme = currentTheme
        previewBgUri = currentBgUri
    }

    var showResetDialog by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            viewModel.updateUserSettings { current ->
                current.copy(
                    chatTheme = "Custom",
                    chatBackgroundImageUri = uri.toString()
                )
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Chat Background?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove the custom background and restore the chat theme to default?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateUserSettings { current ->
                            current.copy(
                                chatTheme = "Default",
                                chatBackgroundImageUri = ""
                            )
                        }
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EditorialAccentPurple)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Back Button Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { HashRouter.goBack() }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to Settings",
                tint = EditorialAccentPurple
            )
            Text(
                text = "Back to Settings",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = EditorialAccentPurple
            )
        }

        // Live Preview Title
        Text(
            text = "Chat Background Preview",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialPrimary
        )

        // Preview Composable
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, EditorialBorder),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                ChatBackgroundRenderer(
                    theme = previewTheme,
                    bgUri = previewBgUri,
                    isSystemDark = isSystemDark
                )
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Tutor bubble preview
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.78f),
                        color = EditorialContainer,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 2.dp, bottomEnd = 12.dp),
                        border = BorderStroke(1.dp, EditorialBorder)
                    ) {
                        Text(
                            text = "Assalamu Alaikum! Welcome to Lisan. Let's compare English and Arabic grammar structures.",
                            fontSize = 11.sp,
                            color = EditorialTextPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    
                    // User bubble preview
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth(0.65f)
                            .align(Alignment.End),
                        color = EditorialPrimary,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 12.dp, bottomEnd = 2.dp)
                    ) {
                        Text(
                            text = "That sounds perfect! Can you show me an example?",
                            color = Color.White,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }

        Divider(color = EditorialDivider)

        // Custom Gallery Picker Section
        Text(
            text = "Custom Background Wallpaper",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialPrimary
        )

        Surface(
            onClick = {
                galleryLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            color = if (currentTheme == "Custom") EditorialAccentPurple.copy(alpha = 0.08f) else EditorialContainer,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(
                1.dp,
                if (currentTheme == "Custom") EditorialAccentPurple else EditorialBorder
            ),
            modifier = Modifier.fillMaxWidth().testTag("gallery_picker_row")
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PhotoLibrary,
                    contentDescription = null,
                    tint = if (currentTheme == "Custom") EditorialAccentPurple else EditorialPrimary,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Choose from Gallery",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Text(
                        text = if (currentTheme == "Custom" && currentBgUri.isNotEmpty()) "Custom gallery photo is currently set"
                               else "Use any local photo from your phone's gallery",
                        fontSize = 11.sp,
                        color = EditorialTheme.colors.textSecondary
                    )
                }
                if (currentTheme == "Custom" && currentBgUri.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = EditorialAccentPurple,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Built-in themes section
        Text(
            text = "Select Built-in Theme",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = EditorialPrimary
        )

        val themesList = listOf(
            "Default" to "Transparent default layout background",
            "Light" to "Soft elegant cream background",
            "Dark" to "Comfortable dark charcoal background",
            "Islamic Green" to "Beautiful traditional green hues",
            "Blue" to "Serene calm indigo-blue hues",
            "Warm" to "Soft warm yellow/sand gradient",
            "Minimal" to "Sleek and clean simple white slate"
        )

        themesList.forEach { (themeName, themeDesc) ->
            val isSelected = currentTheme == themeName
            
            Surface(
                onClick = {
                    viewModel.updateUserSettings { current ->
                        current.copy(
                            chatTheme = themeName,
                            chatBackgroundImageUri = ""
                        )
                    }
                },
                color = if (isSelected) EditorialAccentPurple.copy(alpha = 0.08f) else EditorialContainer,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    1.dp,
                    if (isSelected) EditorialAccentPurple else EditorialBorder
                ),
                modifier = Modifier.fillMaxWidth().testTag("theme_row_${themeName.lowercase().replace(" ", "_")}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Preview Swatch
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, EditorialBorder, CircleShape)
                    ) {
                        ChatBackgroundRenderer(
                            theme = themeName,
                            bgUri = "",
                            isSystemDark = isSystemDark
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = themeName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                        Text(
                            text = themeDesc,
                            fontSize = 11.sp,
                            color = EditorialTheme.colors.textSecondary
                        )
                    }

                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = EditorialAccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Reset to Default option
        if (currentTheme != "Default" || currentBgUri.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedButton(
                onClick = { showResetDialog = true },
                modifier = Modifier.fillMaxWidth().testTag("reset_background_button"),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFC62828)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Reset to Default Theme", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}