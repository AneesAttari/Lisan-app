package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.navigation.HashRouter
import com.example.ui.theme.EditorialTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashRouterBar(
    modifier: Modifier = Modifier
) {
    val colors = EditorialTheme.colors
    val currentRoute by HashRouter.currentRoute.collectAsState()
    
    // Track edit state of address bar
    var isEditing by remember { mutableStateOf(false) }
    var addressInput by remember { mutableStateOf(currentRoute) }

    // Sync input when current route changes from outside
    LaunchedEffect(currentRoute) {
        addressInput = currentRoute
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.headerBg)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Mock Browser Address Bar Style Frame
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.borderSoft),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Back Button
                IconButton(
                    onClick = { HashRouter.goBack() },
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("hash_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colors.textSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Forward / Reload Icon
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reload",
                    tint = colors.textMuted,
                    modifier = Modifier.size(16.dp)
                )

                // The Address Box
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.container)
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Protocol",
                        tint = colors.accentPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    
                    Text(
                        text = "lisan://app/",
                        fontSize = 11.sp,
                        color = colors.textMuted,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )

                    if (isEditing) {
                        BasicTextField(
                            value = addressInput,
                            onValueChange = { addressInput = it },
                            textStyle = LocalTextStyle.current.copy(
                                color = colors.textPrimary,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    HashRouter.navigate(addressInput)
                                    isEditing = false
                                }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hash_address_input")
                        )
                    } else {
                        Text(
                            text = currentRoute,
                            fontSize = 12.sp,
                            color = colors.accentPurple,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isEditing = true }
                                .testTag("hash_address_text")
                        )
                    }
                }
            }
        }

        // Three quick navigators required by user: Lesson Dashboard, AI Tutor Chat, and Sentence Analyzer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Router Quick Nav:",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textMuted
            )

            // Pill #1: Lesson Dashboard (#/lessons)
            HashRoutePill(
                label = "Dashboard",
                route = "#/lessons",
                activeRoute = currentRoute,
                onClick = { HashRouter.navigate("#/lessons") },
                modifier = Modifier.testTag("hash_lessons_button")
            )

            // Pill #2: Sentence Analyzer (#/analyzer)
            HashRoutePill(
                label = "Analyzer",
                route = "#/analyzer",
                activeRoute = currentRoute,
                onClick = { HashRouter.navigate("#/analyzer") },
                modifier = Modifier.testTag("hash_analyzer_button")
            )

            // Pill #3: AI Tutor Chat (#/tutor)
            HashRoutePill(
                label = "AI Tutor",
                route = "#/tutor",
                activeRoute = currentRoute,
                onClick = { HashRouter.navigate("#/tutor") },
                modifier = Modifier.testTag("hash_tutor_button")
            )
        }
    }
}

@Composable
private fun HashRoutePill(
    label: String,
    route: String,
    activeRoute: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = EditorialTheme.colors
    val isActive = activeRoute == route
    val bgColor by animateColorAsState(
        targetValue = if (isActive) colors.accentPurple else colors.surface,
        label = "pill_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (isActive) colors.background else colors.textPrimary,
        label = "pill_txt"
    )

    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        color = bgColor,
        border = BorderStroke(1.dp, if (isActive) colors.accentPurple else colors.borderSoft),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
