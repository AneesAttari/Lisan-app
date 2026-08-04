package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialPrimary

@Composable
fun RevolvingAppIconCircle(
    modifier: Modifier = Modifier,
    size: Dp = 90.dp,
    iconSize: Dp = 44.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "revolving_app_icon")
    
    // Continuous rotation angle
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Gentle pulsing scale for visual elegance
    val scalePulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size(size)
            .scale(scalePulse),
        contentAlignment = Alignment.Center
    ) {
        // Revolving outer multi-color gradient ring
        Box(
            modifier = Modifier
                .fillMaxSize()
                .rotate(rotationAngle)
                .clip(CircleShape)
                .background(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            EditorialAccentPurple,
                            EditorialPrimary,
                            Color(0xFFD81B60),
                            EditorialAccentPurple.copy(alpha = 0.4f),
                            EditorialPrimary,
                            EditorialAccentPurple
                        )
                    )
                )
                .padding(3.dp)
        )

        // Inner solid cream circle holding the Lisan App emblem
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp),
            shape = CircleShape,
            color = Color(0xFFFAF6F0),
            shadowElevation = 6.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "لِسَان",
                        fontSize = (iconSize.value * 0.38).sp,
                        fontWeight = FontWeight.ExtraBold,
                        fontFamily = FontFamily.Serif,
                        color = EditorialPrimary
                    )
                    Text(
                        text = "Lisan",
                        fontSize = (iconSize.value * 0.22).sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialAccentPurple
                    )
                }
            }
        }
    }
}
