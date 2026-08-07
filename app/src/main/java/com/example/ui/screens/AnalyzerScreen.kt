package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialMixedText
import com.example.ui.components.GeneratingLightingGlow
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun AnalyzerScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val inputSentence by viewModel.analyzerInput.collectAsState()
    val analysisResult by viewModel.analyzerOutput.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val speechRate by viewModel.analyzerSpeechRate.collectAsState()
    val isNetworkAvailable by viewModel.isNetworkAvailable.collectAsState()
    val currentlySpeaking by viewModel.activeSpeakingId.collectAsState()

    val sampleSentences = listOf(
        "The boy is reading a book.",
        "My sister speaks English.",
        "These students are ready.",
        "Does Ali like tea?",
        "Open the door."
    )

    Scaffold(
        containerColor = EditorialSurface
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Input Card
            EditorialCard {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("ENTER A SENTENCE TO LISTEN & ANALYZE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
                        
                        // Speed Toggle
                        Surface(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { 
                                val newRate = if (speechRate == 1.0f) 1.2f else if (speechRate == 1.2f) 0.8f else 1.0f
                                viewModel.setAnalyzerSpeechRate(newRate) 
                            },
                            color = EditorialContainer,
                            border = BorderStroke(1.dp, EditorialBorderSoft)
                        ) {
                            Text("${speechRate}x Speed", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    TextField(
                        value = inputSentence,
                        onValueChange = { viewModel.setAnalyzerInput(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = EditorialSurface,
                            unfocusedContainerColor = EditorialSurface,
                            focusedTextColor = EditorialTextPrimary,
                            unfocusedTextColor = EditorialTextPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text("e.g. She has finished her homework.", color = EditorialTextMuted, fontSize = 13.sp) }
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.speak(inputSentence, "analyzer_input", speechRate) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = EditorialSurface, contentColor = EditorialTextSecondary), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Listen", fontSize = 12.sp)
                        }
                        Button(onClick = { viewModel.runDeepAnalyzer() }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = EditorialPrimary, contentColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Analyze", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Analysis Output
            if (!analysisResult.isNullOrBlank()) {
                EditorialCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("GRAMMATICAL ANALYSIS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted, letterSpacing = 1.4.sp)
                        TextButton(onClick = { viewModel.setAnalyzerInput("") }) { Text("Close", fontSize = 12.sp) }
                    }
                    EditorialDivider()
                    EditorialMixedText(text = analysisResult!!, fontSize = 14.sp)
                }
            }

            // Examples
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TRY AN EXAMPLE • مثال منتخب کریں", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
                sampleSentences.forEach { sentence ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).clickable { viewModel.setAnalyzerInput(sentence); viewModel.runDeepAnalyzer() },
                        color = EditorialContainer,
                        border = BorderStroke(1.dp, EditorialBorderSoft)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("“$sentence”", fontSize = 14.sp, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, color = EditorialTextPrimary)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, null, modifier = Modifier.size(16.dp), tint = EditorialTextMuted)
                                Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(16.dp), tint = EditorialTextMuted)
                            }
                        }
                    }
                }
            }
        }
    }
}
