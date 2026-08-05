package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GeneratingLightingGlow(
    text: String = "Thinking...",
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "generating_lighting_glow")

    // Animated progress for sweeping color effect in the bars
    val offsetProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset"
    )

    // Gentle pulsing opacity of the bars
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Smooth dynamic width breathing for line 1 (green)
    val widthProgress1 by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 0.82f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "width1"
    )

    // Smooth dynamic width breathing for line 2 (blue)
    val widthProgress2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "width2"
    )

    // Light green and light blue colors
    val lightGreen = Color(0xFFA5D6A7) // Light Green
    val lightBlue = Color(0xFF90CAF9)  // Light Blue

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("generating_lighting_glow_container"),
        color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant, // Theme-aware background
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Spinning icon
            RevolvingAppIconCircle(
                size = 32.dp,
                iconSize = 14.dp
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Thinking text strictly in English
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware high contrast color
                )
                
                // First animated line: Light green glowing bar with smooth width transition
                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthProgress1)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    lightGreen.copy(alpha = glowAlpha * 0.6f),
                                    lightGreen.copy(alpha = glowAlpha),
                                    lightGreen.copy(alpha = glowAlpha * 0.4f)
                                ),
                                start = androidx.compose.ui.geometry.Offset(offsetProgress, 0f),
                                end = androidx.compose.ui.geometry.Offset(offsetProgress + 350f, 0f)
                            )
                        )
                )

                // Second animated line: Light blue glowing bar with smooth width transition
                Box(
                    modifier = Modifier
                        .fillMaxWidth(widthProgress2)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    lightBlue.copy(alpha = glowAlpha * 0.6f),
                                    lightBlue.copy(alpha = glowAlpha),
                                    lightBlue.copy(alpha = glowAlpha * 0.4f)
                                ),
                                start = androidx.compose.ui.geometry.Offset(offsetProgress, 0f),
                                end = androidx.compose.ui.geometry.Offset(offsetProgress + 350f, 0f)
                            )
                        )
                )
            }
        }
    }
}
