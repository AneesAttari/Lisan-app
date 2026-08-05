package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun AchievementsScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val streak by viewModel.dailyStreak.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Achievements Header
        EditorialCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACHIEVEMENTS • کامیابیاں",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = "آپ کے تمغے اور ریکارڈز",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        // 2. Streak Milestone
        StreakMilestoneCard(streak)

        // 3. Badges Grid
        EditorialSectionTitle(text = "UNLOCKED BADGES")
        BadgeGrid()

        // 4. Milestone Progress Bars
        EditorialSectionTitle(text = "NEXT GOALS")
        MilestoneProgressBar(label = "Master 50 Topics", progress = 0.8f, current = "40/50")
        MilestoneProgressBar(label = "Answer 500 Questions", progress = 0.3f, current = "150/500")
        MilestoneProgressBar(label = "30 Day Streak", progress = streak / 30f, current = "$streak/30")
    }
}

@Composable
private fun StreakMilestoneCard(streak: Int) {
    EditorialCard(containerColor = Color(0xFFFFF3E0), borderColor = Color(0xFFFFB74D).copy(alpha = 0.5f)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                color = Color(0xFFFFB74D).copy(alpha = 0.2f),
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Whatshot, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(32.dp))
                }
            }
            Column {
                Text(text = "روزانہ کا تسلسل (Daily Streak)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                Text(text = "آپ نے مسلسل $streak دن پڑھائی کی ہے!", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
            }
        }
    }
}

@Composable
private fun BadgeGrid() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BadgeItem("Early Bird", Icons.Default.WbTwilight, Color(0xFF90CAF9), Modifier.weight(1f))
            BadgeItem("Grammar Pro", Icons.Default.AutoStories, Color(0xFFA5D6A7), Modifier.weight(1f))
            BadgeItem("Quiz Master", Icons.Default.FactCheck, Color(0xFFCE93D8), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BadgeItem("Fast Learner", Icons.Default.Bolt, Color(0xFFFFCC80), Modifier.weight(1f))
            BadgeItem("Consistent", Icons.Default.TrendingUp, Color(0xFFB0BEC5), Modifier.weight(1f))
            BadgeItem("Translator", Icons.Default.Translate, Color(0xFFF48FB1), Modifier.weight(1f))
        }
    }
}

@Composable
private fun BadgeItem(name: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = EditorialSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, EditorialBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                color = color.copy(alpha = 0.15f),
                shape = CircleShape,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Text(text = name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
        }
    }
}

@Composable
private fun MilestoneProgressBar(label: String, progress: Float, current: String) {
    EditorialCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                Text(text = current, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
            }
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = EditorialAccentPurple,
                trackColor = EditorialContainer
            )
        }
    }
}
