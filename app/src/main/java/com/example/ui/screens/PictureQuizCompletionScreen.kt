package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PictureVocabularySessionEntity
import com.example.ui.navigation.HashRouter
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialTheme
import com.example.ui.viewmodel.PictureVocabularyViewModel

@Composable
fun PictureQuizCompletionScreen(sessionId: String, viewModel: PictureVocabularyViewModel) {
    val colors = EditorialTheme.colors
    val session by viewModel.currentSession.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(80.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Quiz Completed!",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Great job practicing your picture vocabulary.",
            fontSize = 15.sp,
            color = colors.textSecondary
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = colors.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Total Questions", color = colors.textSecondary)
                    Text(text = "${session?.actualQuestionCount ?: 26}", fontWeight = FontWeight.Bold, color = colors.textPrimary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Correct Answers", color = colors.textSecondary)
                    Text(text = "${session?.correctCount ?: 0}", fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Incorrect Answers", color = colors.textSecondary)
                    Text(text = "${session?.incorrectCount ?: 0}", fontWeight = FontWeight.Bold, color = Color(0xFFE53935))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                HashRouter.navigate("#/picture-vocabulary")
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary)
        ) {
            Text(text = "Back to Learn with Pictures", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
