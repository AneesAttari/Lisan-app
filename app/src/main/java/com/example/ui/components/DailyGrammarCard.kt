package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun DailyGrammarCard(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val isCompleted by viewModel.isDailyCompleted.collectAsState()
    val streak by viewModel.dailyStreak.collectAsState()

    EditorialCard(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "روزانہ کا چیلنج (Daily Challenge)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialPrimary
                )
                Text(
                    text = if (isCompleted) "آج کا چیلنج مکمل! 🎉 (Today's challenge complete!)" else "روزانہ 5 سوالات کے ساتھ اپنی مہارت آزمائیں۔ (Test your skills with 5 daily questions.)",
                    fontSize = 13.sp,
                    color = EditorialTextSecondary
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
                        text = "$streak 🔥",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.setSelectedTab(5) },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isCompleted) EditorialSurface else EditorialPrimary,
                contentColor = if (isCompleted) EditorialPrimary else EditorialSurface
            ),
            border = if (isCompleted) BorderStroke(1.dp, EditorialBorder) else null
        ) {
            Text(
                text = if (isCompleted) "آج کے نتائج دیکھیں (View Today's Results)" else "آج کا چیلنج شروع کریں (Start Today's Challenge)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}
