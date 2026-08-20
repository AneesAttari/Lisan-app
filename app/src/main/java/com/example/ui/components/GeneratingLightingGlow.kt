package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GeneratingLightingGlow(
    language: String = "English",
    modifier: Modifier = Modifier
) {
    val text = when (language) {
        "Urdu" -> "لسان سوچ رہا ہے"
        "Arabic" -> "لسان يفكّر"
        else -> "Lisan is thinking"
    }

    val infiniteTransition = rememberInfiniteTransition(label = "lisan_thinking_animation")

    // Gentle pulsing/breathing scale and alpha for the gradient orb
    val orbAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_alpha"
    )

    val orbScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb_scale"
    )

    // Subtle container surface alpha breathing
    val surfaceAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "surface_alpha"
    )

    // Staggered bounce/fade for 3 dots
    val dot1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.2f at 0
                1.0f at 300
                0.2f at 600
                0.2f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot1"
    )

    val dot2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.2f at 0
                0.2f at 300
                1.0f at 600
                0.2f at 900
                0.2f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot2"
    )

    val dot3Alpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0.2f at 0
                0.2f at 600
                1.0f at 900
                0.2f at 1200
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "dot3"
    )

    // Soft gradient brush for the orb (lavender & pale blue)
    val orbBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFC7D2FE), // Light lavender
            Color(0xFF93C5FD), // Pale blue
            Color.Transparent
        )
    )

    // Light lavender tinted background for the bubble
    val bubbleBgColor = Color(0xFFF3F2FF) // Soft tinted background
    val textColor = Color(0xFF4F46E5) // Muted indigo for premium styling

    Surface(
        color = bubbleBgColor,
        shape = RoundedCornerShape(
            topStart = 20.dp,
            topEnd = 20.dp,
            bottomStart = 4.dp,
            bottomEnd = 20.dp
        ),
        modifier = modifier
            .widthIn(max = 280.dp)
            .alpha(surfaceAlpha)
            .testTag("generating_lighting_glow_container")
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Small soft lavender/pale blue gradient orb with breathing scale
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .graphicsLayer(scaleX = orbScale, scaleY = orbScale)
                        .alpha(orbAlpha)
                        .background(brush = orbBrush, shape = CircleShape)
                )

                Text(
                    text = text,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }

            // Exactly three small animated dots below the label using muted indigo/lavender
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 24.dp) // align under text, past the orb
            ) {
                val dotColor = Color(0xFF818CF8) // Muted lavender/indigo shade
                Box(modifier = Modifier.size(5.dp).alpha(dot1Alpha).background(dotColor, CircleShape))
                Box(modifier = Modifier.size(5.dp).alpha(dot2Alpha).background(dotColor, CircleShape))
                Box(modifier = Modifier.size(5.dp).alpha(dot3Alpha).background(dotColor, CircleShape))
            }
        }
    }
}
