package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.components.EditorialMixedText
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialBorder
import com.example.ui.theme.EditorialBorderSoft
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialSurface
import com.example.ui.theme.EditorialTextMuted
import com.example.ui.theme.EditorialTextPrimary
import com.example.ui.theme.EditorialTextSecondary
import com.example.ui.viewmodel.LisanViewModel

@Composable
fun AnalyzerScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val inputSentence by viewModel.analyzerInput.collectAsState()
    val analysisResult by viewModel.analyzerOutput.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    val error by viewModel.analyzerError.collectAsState()

    val sampleSentences = listOf(
        "If I had known about the meeting, I would have arrived early.",
        "The book which you gave me yesterday was fascinating.",
        "He must have been waiting for hours before the train arrived.",
        "No sooner had she left the house than it started raining."
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        EditorialSectionTitle("Deep Sentence Analyzer • جملہ کا گہرائی سے تقابلی تجزیہ")

        // Input Card
        EditorialCard {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "ENTER ENGLISH SENTENCE TO ANALYZE • انگریزی جملہ یہاں درج کریں",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = EditorialTextMuted
                )
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
                    maxLines = 4
                )
            }

            // Quick Samples
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "TRY AN EXAMPLE • مثال منتخب کریں",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = EditorialTextMuted
                )
                sampleSentences.forEach { sentence ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                viewModel.setAnalyzerInput(sentence)
                                viewModel.runDeepAnalyzer()
                            },
                        color = EditorialSurface,
                        border = BorderStroke(1.dp, EditorialBorderSoft),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = "“$sentence”",
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            color = EditorialAccentPurple,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            // Analyze Action Button
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .clickable(enabled = !isAnalyzing) { viewModel.runDeepAnalyzer() },
                color = EditorialPrimary,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "HIGH THINKING ANALYZING • تفصیلی تجزیہ کیا جا رہا ہے...",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Analyze",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "DEEP DISSECT & ARABIC COMPARISON • گہرائی سے تجزیہ اور تقابل",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (error != null) {
            Surface(
                color = EditorialSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFFB3261E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = error!!,
                    color = Color(0xFFB3261E),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        // Analysis Output Card
        if (!analysisResult.isNullOrBlank()) {
            EditorialCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "GEMINI 3.1 PRO • THINKING MODE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EditorialTextMuted,
                            letterSpacing = 1.4.sp
                        )
                        Text(
                            text = "Comparative Grammatical Analysis",
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold,
                            color = EditorialPrimary
                        )
                    }
                }

                EditorialDivider()

                EditorialMixedText(
                    text = analysisResult!!,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    color = EditorialTextPrimary
                )
            }
        }
    }
}
