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
    val achievements by viewModel.achievements.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Achievements Header (Improved)
        EditorialCard(containerColor = EditorialPrimary.copy(alpha = 0.05f), borderColor = EditorialPrimary.copy(alpha = 0.2f)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ACHIEVEMENTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.0.sp,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = "آپ کے تمغے اور ریکارڈز",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ہر سبق کے ساتھ آپ اپنی کامیابیوں کا ایک نیا سنگ میل عبور کرتے ہیں۔",
                        fontSize = 13.sp,
                        color = EditorialTextSecondary
                    )
                }
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(60.dp)
                )
            }
        }

        // 2. Streak Milestone Card
        StreakMilestoneCard(streak)

        // 3. Unlocked Badges Section
        val unlockedList = achievements.filter { it.isUnlocked }
        if (unlockedList.isNotEmpty()) {
            EditorialSectionTitle(text = "UNLOCKED BADGES (کھلے ہوئے تمغے)")
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                unlockedList.chunked(2).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { ach ->
                            Surface(
                                modifier = Modifier.weight(1f),
                                color = Color(0xFFE8F5E9),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.5.dp, Color(0xFF81C784))
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(text = ach.icon, fontSize = 32.sp)
                                    Text(
                                        text = ach.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialPrimary
                                    )
                                    Text(
                                        text = ach.description,
                                        fontSize = 11.sp,
                                        color = EditorialTextSecondary,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // 4. Milestone Progress / Locked Goals
        val lockedList = achievements.filter { !it.isUnlocked }
        if (lockedList.isNotEmpty()) {
            EditorialSectionTitle(text = "NEXT GOALS (اگلے اہداف)")
            lockedList.forEach { ach ->
                EditorialCard {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = ach.icon, fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = ach.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary
                                )
                            }
                            Text(
                                text = "${ach.currentProgress}/${ach.maxProgress}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EditorialAccentPurple
                            )
                        }
                        Text(
                            text = ach.description,
                            fontSize = 11.sp,
                            color = EditorialTextSecondary
                        )
                        LinearProgressIndicator(
                            progress = { ach.currentProgress.toFloat() / ach.maxProgress.coerceAtLeast(1) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = EditorialAccentPurple,
                            trackColor = EditorialContainer
                        )
                    }
                }
            }
        }
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
