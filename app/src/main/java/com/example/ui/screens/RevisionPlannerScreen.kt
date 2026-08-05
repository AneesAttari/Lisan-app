package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun RevisionPlannerScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Review Summary Header
        EditorialCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REVISION PLANNER • دہرائی کا منصوبہ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                        color = EditorialTextMuted
                    )
                    Text(
                        text = "آپ کے لیے تیار دہرائی کا شیڈول",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
                Icon(
                    imageVector = Icons.Default.EventNote,
                    contentDescription = null,
                    tint = EditorialAccentPurple,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RevisionStatBox(
                    count = "12",
                    label = "Due Today",
                    color = EditorialAccentPurple,
                    modifier = Modifier.weight(1f)
                )
                RevisionStatBox(
                    count = "5",
                    label = "Overdue",
                    color = Color.Red,
                    modifier = Modifier.weight(1f)
                )
                RevisionStatBox(
                    count = "45",
                    label = "Learned",
                    color = EditorialPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 2. Start Review Session Button
        Button(
            onClick = { /* Start review logic */ },
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("START REVIEW SESSION • دہرائی شروع کریں", fontWeight = FontWeight.Bold)
        }

        // 3. Upcoming Review Schedule
        EditorialSectionTitle(text = "REVIEW SCHEDULE")
        
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ScheduleItem("Verb 'To Be'", "Today", "High Priority")
            ScheduleItem("Articles", "Today", "Medium Priority")
            ScheduleItem("Present Simple", "Tomorrow", "Low Priority")
            ScheduleItem("Past Simple", "3 Days", "Planned")
        }

        // 4. Learning History
        EditorialSectionTitle(text = "LEARNING HISTORY")
        LearningHistoryCard()
    }
}

@Composable
private fun RevisionStatBox(count: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = color.copy(alpha = 0.08f),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = label, fontSize = 10.sp, color = EditorialTextMuted)
        }
    }
}

@Composable
private fun ScheduleItem(topic: String, time: String, priority: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = EditorialSurface,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, EditorialBorder)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    color = EditorialPillBg,
                    shape = CircleShape,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = EditorialPrimary, modifier = Modifier.size(18.dp))
                    }
                }
                Column {
                    Text(text = topic, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
                    Text(text = priority, fontSize = 11.sp, color = EditorialTextMuted)
                }
            }
            Text(text = time, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple)
        }
    }
}

@Composable
private fun LearningHistoryCard() {
    EditorialCard {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Icon(Icons.Default.History, contentDescription = null, tint = EditorialTextMuted)
            Text("View Detailed Learning History", fontSize = 14.sp, color = EditorialTextPrimary)
        }
    }
}
