package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.viewmodel.ExamViewModel
import com.example.ui.viewmodel.ExamQuestion

@Composable
fun ExamModeDialog(
    viewModel: ExamViewModel,
    onDismiss: () -> Unit
) {
    val isGenerating by viewModel.isGeneratingExam.collectAsState()
    val examData by viewModel.examData.collectAsState()
    val examError by viewModel.examError.collectAsState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.9f)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (examData != null) {
                ActiveExamView(
                    questions = examData!!.questions,
                    onClose = { 
                        viewModel.clearExam()
                        onDismiss() 
                    }
                )
            } else {
                ExamSetupView(
                    isGenerating = isGenerating,
                    error = examError,
                    onStart = { topic -> viewModel.startExam(topic) },
                    onCancel = onDismiss,
                    onDismissError = { viewModel.dismissError() }
                )
            }
        }
    }
}

@Composable
fun ExamSetupView(
    isGenerating: Boolean,
    error: String?,
    onStart: (String) -> Unit,
    onCancel: () -> Unit,
    onDismissError: () -> Unit
) {
    var topic by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📝 Exam Mode",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Test your Arabic grammar skills!",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = topic,
            onValueChange = { topic = it },
            label = { Text("What topic would you like to be tested on?") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        
        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onCancel, enabled = !isGenerating) {
                Text("Cancel")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { 
                    if (topic.isNotBlank()) {
                        onStart(topic)
                    }
                },
                enabled = !isGenerating && topic.isNotBlank()
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Start Exam")
                }
            }
        }
    }
}

@Composable
fun ActiveExamView(
    questions: List<ExamQuestion>,
    onClose: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showExplanation by remember { mutableStateOf(false) }
    var score by remember { mutableIntStateOf(0) }

    val question = questions.getOrNull(currentIndex)

    if (question == null) {
        // Exam finished
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Exam Completed!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Your Score: $score / ${questions.size}",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onClose) {
                Text("Close")
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Question ${currentIndex + 1} of ${questions.size}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = question.text,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        question.options.forEachIndexed { index, option ->
            val isSelected = selectedOption == index
            val isCorrect = index == question.correctAnswerIndex
            
            val backgroundColor = when {
                !showExplanation -> if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                isCorrect -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
            
            Card(
                onClick = {
                    if (!showExplanation) {
                        selectedOption = index
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = null,
                        enabled = !showExplanation
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = option, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
        
        if (showExplanation) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedOption == question.correctAnswerIndex) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (selectedOption == question.correctAnswerIndex) "✅ Correct!" else "❌ Incorrect",
                        fontWeight = FontWeight.Bold,
                        color = if (selectedOption == question.correctAnswerIndex) 
                            MaterialTheme.colorScheme.onPrimaryContainer 
                        else MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.explanation,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            if (!showExplanation) {
                Button(
                    onClick = { 
                        showExplanation = true
                        if (selectedOption == question.correctAnswerIndex) {
                            score++
                        }
                    },
                    enabled = selectedOption != null
                ) {
                    Text("Check Answer")
                }
            } else {
                Button(
                    onClick = {
                        currentIndex++
                        selectedOption = null
                        showExplanation = false
                    }
                ) {
                    Text(if (currentIndex < questions.size - 1) "Next Question" else "Finish Exam")
                }
            }
        }
    }
}
