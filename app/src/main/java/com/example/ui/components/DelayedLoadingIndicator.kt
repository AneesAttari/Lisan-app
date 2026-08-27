package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * A clean, lightweight loading indicator that appears only after a short delay (default 250ms).
 * This ensures that on fast devices or instant cache hits, no distracting spinner flash occurs,
 * while slower devices communicate clear, active progress instead of appearing frozen.
 */
@Composable
fun DelayedLoadingIndicator(
    modifier: Modifier = Modifier,
    message: String = "Loading...",
    delayMillis: Long = 250L,
    indicatorColor: Color = MaterialTheme.colorScheme.primary
) {
    var showIndicator by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis)
        showIndicator = true
    }

    AnimatedVisibility(
        visible = showIndicator,
        enter = fadeIn(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = indicatorColor,
                    strokeWidth = 2.5.dp
                )
                if (message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = message,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
