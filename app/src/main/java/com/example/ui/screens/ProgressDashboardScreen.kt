package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.OutlinedFlag
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurriculumData
import com.example.data.model.GrammarTopic
import com.example.data.model.LearnerProfile
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialMixedText
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialBorder
import com.example.ui.theme.EditorialBorderAccent
import com.example.ui.theme.EditorialBorderSoft
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialPillBg
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialSurface
import com.example.ui.theme.EditorialTextMuted
import com.example.ui.theme.EditorialTextPrimary
import com.example.ui.theme.EditorialTextSecondary
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun ProgressDashboardScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val completedTopicIds by viewModel.completedTopicIds.collectAsState()
    val userStats by viewModel.userStats.collectAsState()
    val learnerProfile by viewModel.learnerProfile.collectAsState()
    val last7DaysActivity by viewModel.last7DaysActivity.collectAsState()
    val overallAccuracyPct by viewModel.overallAccuracyPct.collectAsState()
    val proficiencyLevel by viewModel.proficiencyLevel.collectAsState()

    val allTopics = CurriculumData.topics
    val totalTopicsCount = allTopics.size
    val completedCount = completedTopicIds.size
    val overallMasteryPct = if (totalTopicsCount > 0) (completedCount * 100) / totalTopicsCount else 0
    val quizAccuracyPct = overallAccuracyPct
    val streak = userStats?.currentLearningStreak ?: viewModel.quizStreak.collectAsState().value

    var selectedFilterCategory by remember { mutableStateOf("ALL") }
    var reportSaveMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.ensureProgressDashboardLoaded()
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Honest Empty/Zero State Banner if no activity yet
        if (completedCount == 0 && quizAccuracyPct == 0 && last7DaysActivity.sumOf { it.second } == 0) {
            Surface(
                color = EditorialPillBg,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, EditorialBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "📊 پیش رفت کا جائزہ (Progress Dashboard)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Text(
                        text = "ابھی تک کوئی سیکھنے کی سرگرمی ریکارڈ نہیں ہوئی۔ اسباق مکمل کریں، کوئز حل کریں یا ٹیوٹر سے بات کریں تاکہ یہاں آپ کی حقیقی پیش رفت اور ہفتہ وار چارٹس ظاہر ہوں۔",
                        fontSize = 12.sp,
                        color = EditorialTextSecondary
                    )
                }
            }
        }

        // 1. Overall Mastery & KPI Header Cards
        OverallMasteryKpiCard(
            masteryPct = overallMasteryPct,
            completedCount = completedCount,
            totalCount = totalTopicsCount,
            quizAccuracyPct = quizAccuracyPct,
            streak = streak,
            levelLabel = proficiencyLevel.labelUr
        )

        // 2. Weekly Progress Chart & Streak Tracker Card
        WeeklyProgressTrackerCard(
            last7DaysActivity = last7DaysActivity,
            completedCount = completedCount,
            streak = streak,
            quizAccuracyPct = quizAccuracyPct
        )

        // 3. Data Visualization Summary Bar Chart (Canvas)
        CategoryComparisonBarChartCard(
            completedTopicIds = completedTopicIds,
            allTopics = allTopics,
            learnerProfile = learnerProfile,
            quizAccuracyPct = quizAccuracyPct
        )

        // 3. Category Filter & Topic Breakdown List
        EditorialSectionTitle(text = "TOPIC MASTERY BREAKDOWN")

        CategoryFilterBar(
            selectedCategory = selectedFilterCategory,
            onSelect = { selectedFilterCategory = it }
        )

        val filteredTopics = remember(selectedFilterCategory, completedTopicIds) {
            when (selectedFilterCategory) {
                "PRESENT" -> allTopics.filter { it.titleEn.contains("Present", ignoreCase = true) || it.id.contains("present") }
                "PAST" -> allTopics.filter { it.titleEn.contains("Past", ignoreCase = true) || it.id.contains("past") }
                "FUTURE" -> allTopics.filter { it.titleEn.contains("Future", ignoreCase = true) || it.id.contains("future") }
                else -> allTopics
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            filteredTopics.forEach { topic ->
                val isDone = completedTopicIds.contains(topic.id)
                TopicMasteryCard(
                    topic = topic,
                    isCompleted = isDone,
                    learnerProfile = learnerProfile,
                    onToggle = { viewModel.toggleTopicCompleted(topic.id) },
                    onPractice = {
                        viewModel.selectTopic(topic.id)
                        viewModel.setSelectedTab(0) // Switch to Lessons tab
                    }
                )
            }
        }

        // 4. Save/Export Progress Summary Report Button
        if (reportSaveMessage != null) {
            Surface(
                color = EditorialAccentPurple.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = reportSaveMessage!!,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        Button(
            onClick = {
                viewModel.saveProgressSummaryNote(
                    completedCount = completedCount,
                    totalTopics = totalTopicsCount,
                    masteryPercentage = overallMasteryPct,
                    quizAccuracyPercentage = quizAccuracyPct
                )
                reportSaveMessage = "پروگریس رپورٹ نوٹس میں محفوظ کر لی گئی ہے! 📌"
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("save_progress_report_button")
        ) {
            Icon(
                imageVector = Icons.Default.Bookmark,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "پیش رفت کی رپورٹ نوٹس میں محفوظ کریں",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OverallMasteryKpiCard(
    masteryPct: Int,
    completedCount: Int,
    totalCount: Int,
    quizAccuracyPct: Int,
    streak: Int,
    levelLabel: String,
    modifier: Modifier = Modifier
) {
    EditorialCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MASTERY DASHBOARD • پیش رفت کی لوح",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = "گرامر میں مہارت اور پیش رفت",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }

                Surface(
                    color = EditorialPillBg,
                    shape = CircleShape
                ) {
                    Text(
                        text = levelLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Radial Donut Gauge Chart
                RadialDonutGauge(
                    percentage = masteryPct,
                    modifier = Modifier.size(110.dp)
                )

                // KPI Metrics Column
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiMetricRow(
                        icon = Icons.Default.CheckCircle,
                        iconTint = EditorialPrimary,
                        label = "مکمل شدہ اسباق",
                        value = "$completedCount / $totalCount موضوعات"
                    )

                    KpiMetricRow(
                        icon = Icons.Default.Psychology,
                        iconTint = EditorialAccentPurple,
                        label = "کوئز میں درستگی",
                        value = "$quizAccuracyPct%"
                    )

                    KpiMetricRow(
                        icon = Icons.Default.Whatshot,
                        iconTint = Color(0xFFE65100),
                        label = "مسلسل تسلسل",
                        value = "$streak مسلسل درست جوابات"
                    )
                }
            }
        }
    }
}

@Composable
private fun RadialDonutGauge(
    percentage: Int,
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(percentage) {
        animatedProgress.animateTo(
            targetValue = percentage / 100f,
            animationSpec = tween(durationMillis = 1000)
        )
    }

    val trackColor = EditorialBorderSoft
    val accentColor = EditorialAccentPurple
    val primaryColor = EditorialPrimary

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val radius = diameter / 2
            val center = Offset(size.width / 2, size.height / 2)
            val topLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2, radius * 2)

            // Background Track Arc
            drawArc(
                color = trackColor,
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active Sweeping Gradient Arc
            val sweepAngle = 270f * animatedProgress.value
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(accentColor, primaryColor, accentColor),
                    center = center
                ),
                startAngle = 135f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$percentage%",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialPrimary
            )
            Text(
                text = "مہارت",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = EditorialTextMuted
            )
        }
    }
}

@Composable
private fun KpiMetricRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Surface(
            color = iconTint.copy(alpha = 0.12f),
            shape = CircleShape,
            modifier = Modifier.size(28.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = EditorialTextMuted
            )
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EditorialTextPrimary
            )
        }
    }
}

@Composable
private fun CategoryComparisonBarChartCard(
    completedTopicIds: Set<String>,
    allTopics: List<GrammarTopic>,
    learnerProfile: LearnerProfile,
    quizAccuracyPct: Int,
    modifier: Modifier = Modifier
) {
    // Calculate category statistics
    val presentTopics = allTopics.filter { it.titleEn.contains("Present", ignoreCase = true) || it.id.contains("present") }
    val pastTopics = allTopics.filter { it.titleEn.contains("Past", ignoreCase = true) || it.id.contains("past") }
    val futureTopics = allTopics.filter { it.titleEn.contains("Future", ignoreCase = true) || it.id.contains("future") }

    val presentCompleted = presentTopics.count { completedTopicIds.contains(it.id) }
    val pastCompleted = pastTopics.count { completedTopicIds.contains(it.id) }
    val futureCompleted = futureTopics.count { completedTopicIds.contains(it.id) }

    val presentPct = if (presentTopics.isNotEmpty()) (presentCompleted * 100) / presentTopics.size else 0
    val pastPct = if (pastTopics.isNotEmpty()) (pastCompleted * 100) / pastTopics.size else 0
    val futurePct = if (futureTopics.isNotEmpty()) (futureCompleted * 100) / futureTopics.size else 0

    // Real category mastery or overall accuracy if no category specific score
    val presentAcc = if (presentTopics.any { learnerProfile.topicMastery.containsKey(it.id) }) {
        val sum = presentTopics.sumOf { ((learnerProfile.topicMastery[it.id] ?: 0f) * 100).toInt() }
        sum / maxOf(1, presentTopics.size)
    } else quizAccuracyPct

    val pastAcc = if (pastTopics.any { learnerProfile.topicMastery.containsKey(it.id) }) {
        val sum = pastTopics.sumOf { ((learnerProfile.topicMastery[it.id] ?: 0f) * 100).toInt() }
        sum / maxOf(1, pastTopics.size)
    } else quizAccuracyPct

    val futureAcc = if (futureTopics.any { learnerProfile.topicMastery.containsKey(it.id) }) {
        val sum = futureTopics.sumOf { ((learnerProfile.topicMastery[it.id] ?: 0f) * 100).toInt() }
        sum / maxOf(1, futureTopics.size)
    } else quizAccuracyPct

    val textMeasurer = rememberTextMeasurer()
    val primaryColor = EditorialPrimary
    val accentColor = EditorialAccentPurple
    val borderColor = EditorialBorder
    val textMutedColor = EditorialTextMuted
    val textPrimaryColor = EditorialTextPrimary
    val textSecondaryColor = EditorialTextSecondary

    EditorialCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CATEGORY BREAKDOWN • زمانہ کے لحاظ سے موازنہ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = textMutedColor
                    )
                    Text(
                        text = "اسباق اور کوئز کی درستگی",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }

                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = "Chart",
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Legend Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .shadow(0.dp)
                            .padding(0.dp)
                            .then(Modifier)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(primaryColor)
                        }
                    }
                    Text(text = "مکمل شدہ اسباق کا تناسب", fontSize = 11.sp, color = textSecondaryColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Canvas(modifier = Modifier.size(10.dp)) {
                        drawCircle(accentColor)
                    }
                    Text(text = "کوئز میں درستگی", fontSize = 11.sp, color = textSecondaryColor)
                }
            }

            // Jetpack Compose Custom Canvas Bar Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val paddingBottom = 28.dp.toPx()
                    val paddingTop = 20.dp.toPx()
                    val paddingLeft = 36.dp.toPx()
                    val paddingRight = 16.dp.toPx()

                    val chartWidth = canvasWidth - paddingLeft - paddingRight
                    val chartHeight = canvasHeight - paddingTop - paddingBottom

                    // Draw Horizontal Dotted Grid Lines (0%, 50%, 100%)
                    val gridSteps = listOf(0, 50, 100)
                    gridSteps.forEach { stepPct ->
                        val y = canvasHeight - paddingBottom - (chartHeight * (stepPct / 100f))
                        drawLine(
                            color = borderColor.copy(alpha = 0.5f),
                            start = Offset(paddingLeft, y),
                            end = Offset(canvasWidth - paddingRight, y),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                            strokeWidth = 1.dp.toPx()
                        )

                        // Y-axis label text
                        val labelResult = textMeasurer.measure(
                            text = "$stepPct%",
                            style = TextStyle(fontSize = 9.sp, color = textMutedColor)
                        )
                        drawText(
                            textLayoutResult = labelResult,
                            topLeft = Offset(4.dp.toPx(), y - labelResult.size.height / 2)
                        )
                    }

                    // 3 Categories: Present, Past, Future
                    val categoriesData = listOf(
                        Triple("فعل حال (Present)", presentPct, presentAcc),
                        Triple("فعل ماضی (Past)", pastPct, pastAcc),
                        Triple("فعل مستقبل (Future)", futurePct, futureAcc)
                    )

                    val groupWidth = chartWidth / categoriesData.size
                    val barWidth = 16.dp.toPx()

                    categoriesData.forEachIndexed { index, (label, completionPct, accuracyValue) ->
                        val groupCenterX = paddingLeft + (index + 0.5f) * groupWidth

                        // Completion Bar (Primary Dark)
                        val bar1Height = chartHeight * (completionPct / 100f)
                        val bar1TopLeftX = groupCenterX - barWidth - 4.dp.toPx()
                        val bar1TopLeftY = canvasHeight - paddingBottom - bar1Height

                        drawRoundRect(
                            color = primaryColor,
                            topLeft = Offset(bar1TopLeftX, bar1TopLeftY),
                            size = Size(barWidth, bar1Height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Accuracy Bar (Accent Purple)
                        val bar2Height = chartHeight * (accuracyValue / 100f)
                        val bar2TopLeftX = groupCenterX + 4.dp.toPx()
                        val bar2TopLeftY = canvasHeight - paddingBottom - bar2Height

                        drawRoundRect(
                            color = accentColor,
                            topLeft = Offset(bar2TopLeftX, bar2TopLeftY),
                            size = Size(barWidth, bar2Height),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )

                        // Percentage Label above bar
                        val pctResult = textMeasurer.measure(
                            text = "$completionPct%",
                            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                        )
                        drawText(
                            textLayoutResult = pctResult,
                            topLeft = Offset(bar1TopLeftX + (barWidth - pctResult.size.width) / 2, bar1TopLeftY - pctResult.size.height - 2.dp.toPx())
                        )

                        // X-axis Category Label
                        val categoryResult = textMeasurer.measure(
                            text = label,
                            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, color = textPrimaryColor)
                        )
                        drawText(
                            textLayoutResult = categoryResult,
                            topLeft = Offset(groupCenterX - categoryResult.size.width / 2, canvasHeight - paddingBottom + 6.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterBar(
    selectedCategory: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        "ALL" to "تمام (All)",
        "PRESENT" to "فعل حال (Present)",
        "PAST" to "فعل ماضی (Past)",
        "FUTURE" to "فعل مستقبل (Future)"
    )

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (code, label) ->
            val isSelected = selectedCategory == code
            Surface(
                shape = CircleShape,
                color = if (isSelected) EditorialPrimary else EditorialSurface,
                border = BorderStroke(1.dp, if (isSelected) EditorialPrimary else EditorialBorder),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onSelect(code) }
                    .testTag("dashboard_filter_$code")
            ) {
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else EditorialTextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun TopicMasteryCard(
    topic: GrammarTopic,
    isCompleted: Boolean,
    learnerProfile: LearnerProfile,
    onToggle: () -> Unit,
    onPractice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val masteryScore = learnerProfile.topicMastery[topic.id] ?: learnerProfile.topicMastery[topic.titleEn] ?: if (isCompleted) 1.0f else 0.0f
    val statusText = if (isCompleted) "مکمل ہو گیا 🌟" else if (masteryScore > 0f) "سیکھا جا رہا ہے 📈" else "شروع نہیں ہوا ⏳"
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(16.dp)),
        color = EditorialSurface,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, if (isCompleted) EditorialAccentPurple.copy(alpha = 0.5f) else EditorialBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = isCompleted,
                        onCheckedChange = { onToggle() },
                        colors = CheckboxDefaults.colors(
                            checkedColor = EditorialPrimary,
                            uncheckedColor = EditorialBorder
                        ),
                        modifier = Modifier.testTag("topic_checkbox_${topic.id}")
                    )

                    Column {
                        Text(
                            text = topic.titleUr,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                        Text(
                            text = topic.titleEn,
                            fontSize = 12.sp,
                            color = EditorialTextMuted
                        )
                    }
                }

                // Mastery Status Tag
                Surface(
                    color = if (isCompleted) Color(0xFFE8F5E9) else EditorialPillBg,
                    shape = CircleShape
                ) {
                    Text(
                        text = statusText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCompleted) Color(0xFF1B5E20) else EditorialPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            // Topic Mastery Progress Bar
            val targetProgress = if (isCompleted) 1.0f else masteryScore.coerceIn(0.0f, 1.0f)
            LinearProgressIndicator(
                progress = { targetProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(CircleShape),
                color = if (isCompleted) Color(0xFF2E7D32) else EditorialAccentPurple,
                trackColor = EditorialBorderSoft
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "لیول: ${topic.level}",
                    fontSize = 11.sp,
                    color = EditorialTextMuted
                )

                OutlinedButton(
                    onClick = onPractice,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EditorialBorderAccent),
                    modifier = Modifier.testTag("practice_topic_${topic.id}")
                ) {
                    Text(
                        text = "سبق پڑھیں",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = EditorialPrimary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyProgressTrackerCard(
    last7DaysActivity: List<Pair<String, Int>>,
    completedCount: Int,
    streak: Int,
    quizAccuracyPct: Int,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val totalWeekLessons = last7DaysActivity.sumOf { it.second }

    EditorialCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "WEEKLY PROGRESS & STREAK • ہفتہ وار پیش رفت اور تسلسل",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = "اسباق، کوئز اسکور اور روزانہ تسلسل",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }

                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = CircleShape,
                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "$streak دن تسلسل",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            // Summary metrics mini-row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = EditorialPillBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EditorialBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "اس ہفتے پڑھے اسباق", fontSize = 10.sp, color = EditorialTextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$totalWeekLessons اسباق", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = EditorialPillBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EditorialBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "اوسط کوئز اسکور", fontSize = 10.sp, color = EditorialTextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$quizAccuracyPct%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = EditorialPillBg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, EditorialBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "مطالعہ کا تسلسل", fontSize = 10.sp, color = EditorialTextMuted)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "$streak دن 🔥", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                    }
                }
            }

            // Visual Weekly Chart (Canvas)
            val chartBorderColor = EditorialBorder
            val chartTextMuted = EditorialTextMuted
            val chartAccentPurple = EditorialAccentPurple
            val chartTextPrimary = EditorialTextPrimary

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val paddingBottom = 26.dp.toPx()
                    val paddingTop = 16.dp.toPx()
                    val paddingLeft = 32.dp.toPx()
                    val paddingRight = 16.dp.toPx()

                    val chartWidth = canvasWidth - paddingLeft - paddingRight
                    val chartHeight = canvasHeight - paddingTop - paddingBottom

                    // Horizontal grid lines
                    listOf(0, 50, 100).forEach { pct ->
                        val y = canvasHeight - paddingBottom - (chartHeight * (pct / 100f))
                        drawLine(
                            color = chartBorderColor.copy(alpha = 0.4f),
                            start = Offset(paddingLeft, y),
                            end = Offset(canvasWidth - paddingRight, y),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f),
                            strokeWidth = 1.dp.toPx()
                        )
                        val lbl = textMeasurer.measure(
                            text = "$pct%",
                            style = TextStyle(fontSize = 9.sp, color = chartTextMuted)
                        )
                        drawText(
                            textLayoutResult = lbl,
                            topLeft = Offset(4.dp.toPx(), y - lbl.size.height / 2)
                        )
                    }

                    // 7 Days actual data from last7DaysActivity
                    val daysData = if (last7DaysActivity.isNotEmpty()) {
                        last7DaysActivity.map { (dayName, count) ->
                            Triple(dayName, if (count > 0) minOf(100, count * 25 + 50) else 0, count)
                        }
                    } else {
                        listOf(
                            Triple("پیر", 0, 0),
                            Triple("منگل", 0, 0),
                            Triple("بدھ", 0, 0),
                            Triple("جمعرات", 0, 0),
                            Triple("جمعہ", 0, 0),
                            Triple("ہفتہ", 0, 0),
                            Triple("اتوار", 0, 0)
                        )
                    }

                    val colWidth = chartWidth / daysData.size
                    val barWidth = 12.dp.toPx()

                    daysData.forEachIndexed { index, (dayName, scoreVal, lessonsVal) ->
                        val centerX = paddingLeft + (index + 0.5f) * colWidth

                        // Score bar (Purple)
                        val scoreHeight = chartHeight * (scoreVal.coerceIn(20, 100) / 100f)
                        val barX = centerX - barWidth / 2
                        val barY = canvasHeight - paddingBottom - scoreHeight

                        drawRoundRect(
                            color = chartAccentPurple.copy(alpha = 0.85f),
                            topLeft = Offset(barX, barY),
                            size = Size(barWidth, scoreHeight),
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                        )

                        // Day label (Arabic)
                        val dayResult = textMeasurer.measure(
                            text = dayName,
                            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = chartTextPrimary)
                        )
                        drawText(
                            textLayoutResult = dayResult,
                            topLeft = Offset(centerX - dayResult.size.width / 2, canvasHeight - paddingBottom + 6.dp.toPx())
                        )
                    }
                }
            }

            // Motivational encouragement box
            Surface(
                color = EditorialAccentPurple.copy(alpha = 0.08f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EditorialAccentPurple.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EditorialPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "💡 استاد کا مشورہ: روزانہ مطالعہ اور کوئز کی مشق آپ کو انگریزی گرامر میں ماہر بناتی ہے۔ آپ کا ہفتہ وار تسلسل شاندار ہے!",
                        fontSize = 11.sp,
                        color = EditorialTextPrimary
                    )
                }
            }
        }
    }
}
