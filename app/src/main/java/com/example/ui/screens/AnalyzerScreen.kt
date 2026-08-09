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
import com.example.ui.components.ScrollToBottomFAB
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

    val categorizedExamples = mapOf(
        "Present Simple" to listOf(
            "I drink tea.",
            "She speaks English.",
            "He works hard.",
            "They play football.",
            "Water boils at 100 degrees."
        ),
        "Present Continuous" to listOf(
            "I am learning English.",
            "She is reading a book.",
            "They are playing outside.",
            "It is raining today.",
            "He is working on a project."
        ),
        "Present Perfect" to listOf(
            "I have finished my work.",
            "She has seen that movie.",
            "They have visited London.",
            "He has eaten lunch.",
            "We have lived here for years."
        ),
        "Present Perfect Continuous" to listOf(
            "I have been studying for hours.",
            "She has been working here.",
            "They have been playing games.",
            "It has been snowing all day.",
            "He has been trying to call."
        ),
        "Past Simple" to listOf(
            "I went to school.",
            "She bought a car.",
            "They played together.",
            "He arrived late.",
            "We enjoyed the party."
        ),
        "Past Continuous" to listOf(
            "I was watching TV.",
            "She was reading a book.",
            "They were playing football.",
            "He was working yesterday.",
            "It was raining at noon."
        ),
        "Past Perfect" to listOf(
            "I had finished my work.",
            "She had left before I arrived.",
            "They had eaten lunch.",
            "He had seen it before.",
            "We had already left."
        ),
        "Past Perfect Continuous" to listOf(
            "I had been working hard.",
            "She had been reading for hours.",
            "They had been waiting.",
            "He had been studying all day.",
            "It had been raining heavily."
        ),
        "Future Simple" to listOf(
            "I will go tomorrow.",
            "She will help you.",
            "They will arrive soon.",
            "He will call you.",
            "We will start Monday."
        ),
        "Conditionals" to listOf(
            "If it rains, I will stay home.",
            "If I were you, I would go.",
            "If she had studied, she would have passed.",
            "If you heat water, it boils.",
            "If they arrive, call me."
        ),
        "Direct/Indirect" to listOf(
            "He said, \"I am happy.\"",
            "He said that he was happy.",
            "She asked, \"Where are you?\"",
            "She asked where I was.",
            "He told me to wait."
        )
    )

    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = EditorialSurface
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Input Card
            EditorialCard {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                            Text("${speechRate}x Speed", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialAccentPurple, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    TextField(
                        value = inputSentence,
                        onValueChange = { viewModel.setAnalyzerInput(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = EditorialContainer,
                            unfocusedContainerColor = EditorialContainer,
                            focusedTextColor = EditorialTextPrimary,
                            unfocusedTextColor = EditorialTextPrimary,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text("e.g. She has finished her homework.", color = EditorialTextMuted, fontSize = 12.sp) }
                    )
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { viewModel.speak(inputSentence, "analyzer_input", speechRate) }, modifier = Modifier.weight(1f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = EditorialSurface, contentColor = EditorialTextSecondary), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, null, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Listen", fontSize = 11.sp)
                        }
                        Button(
                            onClick = { viewModel.runDeepAnalyzer() },
                            enabled = !isAnalyzing,
                            modifier = Modifier.weight(1f).height(40.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isAnalyzing) EditorialTextMuted else EditorialPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isAnalyzing) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                                Spacer(Modifier.width(4.dp))
                                Text("Analyzing...", fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(14.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Analyze", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Analysis Output
            if (!analysisResult.isNullOrBlank()) {
                EditorialCard {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("GRAMMATICAL ANALYSIS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted, letterSpacing = 1.0.sp)
                        TextButton(onClick = { viewModel.setAnalyzerInput("") }, modifier = Modifier.height(30.dp)) { Text("Close", fontSize = 11.sp) }
                    }
                    EditorialDivider()
                    EditorialMixedText(text = analysisResult!!, fontSize = 13.sp)
                }
            }

            // Examples
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("TRY AN EXAMPLE • مثال منتخب کریں", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialTextMuted)
                categorizedExamples.forEach { (category, sentences) ->
                    Text(category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EditorialPrimary, modifier = Modifier.padding(top = 8.dp, bottom = 4.dp))
                    sentences.forEach { sentence ->
                        Surface(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).clickable { viewModel.setAnalyzerInput(sentence); viewModel.runDeepAnalyzer() },
                            color = EditorialContainer,
                            border = BorderStroke(1.dp, EditorialBorderSoft)
                        ) {
                            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text("“$sentence”", fontSize = 12.sp, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, color = EditorialTextPrimary, modifier = Modifier.weight(1f))
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.AutoMirrored.Filled.VolumeUp, null, modifier = Modifier.size(14.dp), tint = EditorialTextMuted)
                                    Icon(Icons.Default.ChevronRight, null, modifier = Modifier.size(14.dp), tint = EditorialTextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }

        ScrollToBottomFAB(
            scrollState = scrollState,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}
}
