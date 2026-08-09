package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.navigation.HashRouter
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanJourneyViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LisanJourneyScreen(
    viewModel: LisanJourneyViewModel,
    modifier: Modifier = Modifier
) {
    val definitions by viewModel.activeDefinitions.collectAsState()
    val progressList by viewModel.allProgress.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val tutorSchedule by viewModel.tutorSchedule.collectAsState()
    val pendingRewards by viewModel.pendingRewards.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }
    var showScheduleDialog by remember { mutableStateOf(false) }

    val progressMap = remember(progressList) { progressList.associateBy { it.achievementId } }

    val categories = listOf(
        "ALL" to "All",
        "getting_started" to "Getting Started",
        "daily_habit" to "Daily Habits",
        "tutor" to "Tutor Chat",
        "grammar_revision" to "Grammar & Revision",
        "picture_vocabulary" to "Vocabulary",
        "streak" to "Streaks",
        "pronunciation" to "Pronunciation",
        "sentence_analyzer" to "Analyzer",
        "daily_challenge" to "Challenge"
    )

    val stats = userStats ?: UserAchievementStatsEntity()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header & Overview
            item {
                EditorialCard(
                    containerColor = EditorialPrimary.copy(alpha = 0.05f),
                    borderColor = EditorialPrimary.copy(alpha = 0.15f)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "LISAN JOURNEY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    color = EditorialTextMuted
                                )
                                Text(
                                    text = "آپ کا سیکھنے کا سفر",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialPrimary
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFFFD700).copy(alpha = 0.2f),
                                modifier = Modifier.size(52.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.EmojiEvents,
                                        contentDescription = "Trophy icon",
                                        tint = Color(0xFFD4AF37),
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "Your real progress, streaks, and achievements across the entire Lisan AI experience.",
                            fontSize = 13.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 18.sp
                        )

                        // Key Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatBox(
                                modifier = Modifier.weight(1f),
                                title = "Learning Streak",
                                value = "${stats.currentLearningStreak} Days",
                                icon = Icons.Default.Whatshot,
                                color = Color(0xFFE65100)
                            )
                            StatBox(
                                modifier = Modifier.weight(1f),
                                title = "On-Time Tutor",
                                value = "${stats.currentOnTimeTutorStreak} Streak",
                                icon = Icons.Default.Timer,
                                color = Color(0xFF1976D2)
                            )
                            StatBox(
                                modifier = Modifier.weight(1f),
                                title = "Unlocked",
                                value = "${stats.totalAchievementsUnlocked} Badges",
                                icon = Icons.Default.Verified,
                                color = Color(0xFF2E7D32)
                            )
                        }
                    }
                }
            }

            // 2. Daily Tutor Time Card
            item {
                DailyTutorTimeCard(
                    schedule = tutorSchedule,
                    onConfigureClick = { showScheduleDialog = true },
                    onStartPracticeClick = { HashRouter.navigate("#/tutor") }
                )
            }

            // 3. Pending Rewards Banner if any
            if (pendingRewards.isNotEmpty()) {
                item {
                    EditorialCard(
                        containerColor = Color(0xFFE8F5E9),
                        borderColor = Color(0xFF81C784)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color(0xFF2E7D32))
                                    Text("Unclaimed Rewards Available!", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                }
                                Text("${pendingRewards.size}", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                            }
                            pendingRewards.forEach { tx ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Reward: +${tx.amount ?: 50} Lisan Points", fontSize = 13.sp, color = Color(0xFF2E7D32))
                                    Button(
                                        onClick = { viewModel.claimReward(tx.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                        modifier = Modifier.semantics { contentDescription = "Claim reward button" }
                                    ) {
                                        Text("Claim", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Next Goals Section (Dynamic)
            item {
                EditorialSectionTitle(text = "NEXT GOALS (اگلے اہداف)")
            }

            val lockedDefs = definitions.filter { def ->
                val prog = progressMap[def.id]
                prog == null || !prog.isUnlocked
            }.take(3)

            if (lockedDefs.isNotEmpty()) {
                items(lockedDefs, key = { "next_${it.id}" }) { def ->
                    val prog = progressMap[def.id]
                    NextGoalCard(def = def, progress = prog)
                }
            } else {
                item {
                    EmptyNextGoalsState()
                }
            }

            // 5. Category Filters & Achievements Catalogue
            item {
                Spacer(modifier = Modifier.height(8.dp))
                EditorialSectionTitle(text = "ACHIEVEMENT CATALOGUE (تمغوں کی فہرست)")
                
                // Horizontal category chips
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { (key, label) ->
                        FilterChip(
                            selected = selectedFilter == key,
                            onClick = { selectedFilter = key },
                            label = { Text(label, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = EditorialPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            val filteredDefs = if (selectedFilter == "ALL") {
                definitions
            } else {
                definitions.filter { it.groupId == selectedFilter }
            }

            if (filteredDefs.isNotEmpty()) {
                items(filteredDefs, key = { it.id }) { def ->
                    val prog = progressMap[def.id]
                    AchievementCatalogueCard(def = def, progress = prog)
                }
            } else {
                item {
                    EmptyAchievementsState(
                        filterName = categories.find { it.first == selectedFilter }?.second ?: selectedFilter,
                        onResetFilter = { selectedFilter = "ALL" }
                    )
                }
            }
        }
    }

    if (showScheduleDialog) {
        DailyTutorScheduleDialog(
            currentSchedule = tutorSchedule,
            onDismiss = { showScheduleDialog = false },
            onSave = { isEnabled, start, end, days, reminders ->
                viewModel.saveTutorSchedule(isEnabled, start, end, days, reminders)
                showScheduleDialog = false
            }
        )
    }
}

@Composable
private fun StatBox(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    EditorialCard(
        modifier = modifier,
        containerColor = Color.White,
        borderColor = color.copy(alpha = 0.2f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary, textAlign = TextAlign.Center)
            Text(text = title, fontSize = 10.sp, color = EditorialTextMuted, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun DailyTutorTimeCard(
    schedule: TutorTimeScheduleEntity?,
    onConfigureClick: () -> Unit,
    onStartPracticeClick: () -> Unit
) {
    val isEnabled = schedule?.isEnabled == true
    val startTime = schedule?.startTime ?: "20:00"
    val endTime = schedule?.endTime ?: "21:00"

    EditorialCard(
        containerColor = if (isEnabled) Color(0xFFE3F2FD) else EditorialContainer,
        borderColor = if (isEnabled) Color(0xFF90CAF9) else EditorialBorder
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = if (isEnabled) Color(0xFF1565C0) else EditorialTextMuted
                    )
                    Text(
                        text = "Daily Tutor Time (یومیہ ٹیوٹر وقت)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isEnabled) Color(0xFF0D47A1) else EditorialPrimary
                    )
                }
                TextButton(onClick = onConfigureClick) {
                    Text(if (isEnabled) "Change Schedule" else "Set Up Schedule", fontSize = 12.sp)
                }
            }

            if (isEnabled) {
                Text(
                    text = "Planned Window: $startTime – $endTime",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )
                Text(
                    text = "Complete a tutor conversation during this daily window to maintain your On-Time Tutor Streak and earn bonus rewards.",
                    fontSize = 12.sp,
                    color = EditorialTextSecondary,
                    lineHeight = 16.sp
                )
                Button(
                    onClick = onStartPracticeClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                    modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Start tutor practice button" }
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start Tutor Practice Now")
                }
            } else {
                Text(
                    text = "Not configured. Set a preferred daily practice window to build an On-Time Tutor Streak.",
                    fontSize = 12.sp,
                    color = EditorialTextSecondary
                )
                OutlinedButton(
                    onClick = onConfigureClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Set Up Daily Tutor Time")
                }
            }
        }
    }
}

@Composable
private fun NextGoalCard(def: AchievementDefinitionEntity, progress: AchievementProgressEntity?) {
    val current = progress?.currentValue ?: 0L
    val target = def.targetValue
    val ratio = (current.toFloat() / target.coerceAtLeast(1L)).coerceIn(0f, 1f)

    EditorialCard {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = EditorialAccentPurple, modifier = Modifier.size(24.dp))
                    Column {
                        Text(text = def.titleEnglish, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                        if (!def.titleUrdu.isNullOrBlank()) {
                            Text(text = def.titleUrdu!!, fontSize = 11.sp, color = EditorialTextMuted)
                        }
                    }
                }
                Text(
                    text = "$current / $target",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialAccentPurple
                )
            }
            Text(
                text = def.descriptionEnglish,
                fontSize = 12.sp,
                color = EditorialTextSecondary
            )
            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = EditorialAccentPurple,
                trackColor = EditorialContainer
            )
        }
    }
}

@Composable
private fun AchievementCatalogueCard(def: AchievementDefinitionEntity, progress: AchievementProgressEntity?) {
    val isUnlocked = progress?.isUnlocked == true
    val current = progress?.currentValue ?: 0L
    val target = def.targetValue
    val ratio = (current.toFloat() / target.coerceAtLeast(1L)).coerceIn(0f, 1f)

    EditorialCard(
        containerColor = if (isUnlocked) Color(0xFFF1F8E9) else Color.White,
        borderColor = if (isUnlocked) Color(0xFFC5E1A5) else EditorialBorder
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = if (isUnlocked) Color(0xFF4CAF50).copy(alpha = 0.2f) else EditorialContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isUnlocked) Icons.Default.Verified else Icons.Default.Lock,
                        contentDescription = if (isUnlocked) "Unlocked badge" else "Locked badge",
                        tint = if (isUnlocked) Color(0xFF2E7D32) else EditorialTextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = def.titleEnglish,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Text(
                        text = if (isUnlocked) "Unlocked" else "$current / $target",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) Color(0xFF2E7D32) else EditorialTextMuted
                    )
                }
                Text(
                    text = def.descriptionEnglish,
                    fontSize = 12.sp,
                    color = EditorialTextSecondary,
                    lineHeight = 16.sp
                )
                if (!isUnlocked) {
                    Spacer(modifier = Modifier.height(2.dp))
                    LinearProgressIndicator(
                        progress = { ratio },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = EditorialPrimary,
                        trackColor = EditorialContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyTutorScheduleDialog(
    currentSchedule: TutorTimeScheduleEntity?,
    onDismiss: () -> Unit,
    onSave: (Boolean, String, String, String, Boolean) -> Unit
) {
    var isEnabled by remember { mutableStateOf(currentSchedule?.isEnabled ?: true) }
    var startTime by remember { mutableStateOf(currentSchedule?.startTime ?: "20:00") }
    var endTime by remember { mutableStateOf(currentSchedule?.endTime ?: "21:00") }
    var remindersEnabled by remember { mutableStateOf(currentSchedule?.remindersEnabled ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Daily Tutor Time") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Daily Tutor Time", fontWeight = FontWeight.Bold)
                    Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                }

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Reminders")
                    Switch(checked = remindersEnabled, onCheckedChange = { remindersEnabled = it })
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onSave(isEnabled, startTime, endTime, "[1,2,3,4,5,6,7]", remindersEnabled)
            }) {
                Text("Save Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun EmptyAchievementsState(
    filterName: String,
    onResetFilter: () -> Unit
) {
    EditorialCard(
        containerColor = EditorialContainer.copy(alpha = 0.5f),
        borderColor = EditorialBorder
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = EditorialPrimary.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.SearchOff,
                        contentDescription = null,
                        tint = EditorialPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
            Text(
                text = "No achievements found in '$filterName'",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Try selecting a different category or explore the app to unlock milestones in this section.",
                fontSize = 13.sp,
                color = EditorialTextSecondary,
                textAlign = TextAlign.Center
            )
            OutlinedButton(
                onClick = onResetFilter,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("View All Categories")
            }
        }
    }
}

@Composable
private fun EmptyNextGoalsState() {
    EditorialCard(
        containerColor = Color(0xFFF3E5F5).copy(alpha = 0.6f),
        borderColor = Color(0xFFCE93D8)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = EditorialAccentPurple, modifier = Modifier.size(32.dp))
            Text(
                text = "All Milestones Unlocked!",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialPrimary,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Incredible work! You have completed all active learning goals. Keep practicing daily to maintain your streak.",
                fontSize = 12.sp,
                color = EditorialTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
