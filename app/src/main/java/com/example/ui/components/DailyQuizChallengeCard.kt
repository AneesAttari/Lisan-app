package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyQuizAssignmentEntity
import com.example.data.model.DailyQuizChallengeProgressEntity
import com.example.ui.theme.*

@Composable
fun DailyQuizChallengeCard(
    progress: DailyQuizChallengeProgressEntity?,
    assignment: DailyQuizAssignmentEntity?,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    EditorialCard(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(16.dp)) {
            Text("6-Month Daily Quiz Challenge", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary)
            
            if (progress != null) {
                Text("Day ${progress.totalCompletedDays + 1} of 180", fontSize = 14.sp)
                // TODO: Add Progress Bars
            }

            Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) {
                Text(if (assignment?.completed == true) "Continue Today's Quiz" else "Start Today's Quiz")
            }
        }
    }
}
