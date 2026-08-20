package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialCard
import com.example.ui.components.EditorialDivider
import com.example.ui.components.EditorialMixedText
import com.example.ui.components.ScrollToBottomFAB
import com.example.ui.components.SleekDropdownSelector
import com.example.ui.theme.*
import com.example.ui.viewmodel.LisanViewModel

// Static immutable dataset allocated once at class load time to eliminate memory churn during navigation
private val ANALYZER_CATEGORIES = listOf(
    "Present Tenses (فعل حال)",
    "Past Tenses (فعل ماضی)",
    "Future Tenses (فعل مستقبل)",
    "Direct & Indirect Speech (روایتِ لفظی و معنوی)",
    "Active & Passive Voice (فعل معروف و مجہول)",
    "Conditionals (جملے شرطیہ)",
    "Articles & Flexible Verbs (حروفِ تنکیر و افعال)"
)

private val ANALYZER_CATEGORIZED_EXAMPLES: Map<String, Map<String, List<String>>> = mapOf(
    "Present Tenses (فعل حال)" to mapOf(
        "Simple Present" to listOf(
            "I drink tea every morning.",
            "She speaks fluent English.",
            "He works hard at the office.",
            "They play football on Fridays.",
            "Water boils at 100 degrees Celsius."
        ),
        "Present Continuous" to listOf(
            "I am learning English grammar right now.",
            "She is reading an interesting book.",
            "They are playing outside in the park.",
            "It is raining heavily today.",
            "He is working on a new project."
        ),
        "Present Perfect" to listOf(
            "I have finished my homework assignment.",
            "She has seen that documentary twice.",
            "They have visited London three times.",
            "He has eaten his lunch already.",
            "We have lived in Cairo for five years."
        ),
        "Present Perfect Continuous" to listOf(
            "I have been studying English for two hours.",
            "She has been working here since morning.",
            "They have been playing games all day.",
            "It has been snowing continuously.",
            "He has been trying to call you all afternoon."
        )
    ),
    "Past Tenses (فعل ماضی)" to mapOf(
        "Simple Past" to listOf(
            "I went to the university library yesterday.",
            "She bought a new car last week.",
            "They played a great match together.",
            "He arrived late for the meeting.",
            "We enjoyed the traditional dinner."
        ),
        "Past Continuous" to listOf(
            "I was watching news when you called.",
            "She was reading a book at 8 PM.",
            "They were playing football in the rain.",
            "He was working on his laptop yesterday.",
            "It was raining when we stepped outside."
        ),
        "Past Perfect" to listOf(
            "I had finished my work before he came.",
            "She had left before I arrived at the venue.",
            "They had eaten lunch when the bell rang.",
            "He had seen that movie before last night.",
            "We had already left the office by then."
        ),
        "Past Perfect Continuous" to listOf(
            "I had been working hard for five years.",
            "She had been reading for three hours straight.",
            "They had been waiting at the airport.",
            "He had been studying all day long.",
            "It had been raining heavily before noon."
        )
    ),
    "Future Tenses (فعل مستقبل)" to mapOf(
        "Simple Future" to listOf(
            "I will travel to Medina tomorrow.",
            "She will help you with the report.",
            "They will arrive at the venue soon.",
            "He will call you after dinner.",
            "We will start the new course on Monday."
        ),
        "Future Continuous" to listOf(
            "I will be studying at this time tomorrow.",
            "She will be working in her office.",
            "They will be flying to Dubai at 9 AM.",
            "He will be waiting at the train station.",
            "We will be attending the conference."
        ),
        "Future Perfect" to listOf(
            "I will have completed the book by night.",
            "She will have graduated by next year.",
            "They will have reached home by 6 PM.",
            "He will have finished his exam by noon.",
            "We will have saved enough money by winter."
        ),
        "Future Perfect Continuous" to listOf(
            "I will have been working here for 10 years.",
            "She will have been teaching for two decades.",
            "They will have been traveling for 24 hours.",
            "He will have been studying all evening.",
            "We will have been living here for a month."
        )
    ),
    "Direct & Indirect Speech (روایتِ لفظی و معنوی)" to mapOf(
        "Assertive Statements" to listOf(
            "He said, \"I am very happy today.\"",
            "He said that he was very happy.",
            "She said, \"I have finished my work.\"",
            "She stated that she had finished her work.",
            "They said, \"We will visit Cairo soon.\""
        ),
        "Interrogative Questions" to listOf(
            "She asked, \"Where are you going?\"",
            "She asked where I was going.",
            "He asked, \"Do you speak English?\"",
            "He asked if I spoke English.",
            "The doctor asked how I felt."
        )
    ),
    "Active & Passive Voice (فعل معروف و مجہول)" to mapOf(
        "Present Passive" to listOf(
            "English is spoken all over the world.",
            "The email is sent by the manager.",
            "The lesson is explained by the teacher.",
            "The cars are washed every Friday.",
            "These books are read by thousands."
        ),
        "Past Passive" to listOf(
            "The book was written by a professor in Riyadh.",
            "The room was cleaned yesterday morning.",
            "The reports were checked by the director.",
            "The song was composed in Cairo.",
            "The decision was announced last night."
        )
    ),
    "Conditionals (جملے شرطیہ)" to mapOf(
        "Zero & First Conditionals" to listOf(
            "If you heat ice, it melts into water.",
            "If it rains tomorrow, I will stay at home.",
            "If she works hard, she will pass the exam.",
            "If you press this button, the machine starts.",
            "If they arrive on time, we will leave together."
        ),
        "Second & Third Conditionals" to listOf(
            "If I were you, I would study harder.",
            "If she had studied, she would have passed.",
            "If I had money, I would buy that car.",
            "If they had called, I would have answered.",
            "If we had taken the train, we would have arrived early."
        )
    ),
    "Articles & Flexible Verbs (حروفِ تنکیر و افعال)" to mapOf(
        "Articles (A, An, The)" to listOf(
            "I bought a book and an apple yesterday.",
            "The sun rises in the east every morning.",
            "She is an honest and diligent student.",
            "He gave me a piece of good advice.",
            "The Amazon is one of the longest rivers."
        ),
        "Flexible Phrasal Verbs" to listOf(
            "Please look over your notes before the test.",
            "She looks after her younger brother kindly.",
            "They decided to give up smoking.",
            "He carried on working despite the fatigue.",
            "We need to figure out the solution quickly."
        )
    )
)

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

    var selectedCategory by remember { mutableStateOf(ANALYZER_CATEGORIES[0]) }
    val lazyListState = rememberLazyListState()

    Scaffold(
        containerColor = EditorialSurface
    ) { padding ->
        SelectionContainer {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Input Card
                    item(key = "input_card") {
                        EditorialCard {
                            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "ENTER A SENTENCE TO LISTEN & ANALYZE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialTextMuted
                                    )

                                    // Speed Toggle
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                val newRate = if (speechRate == 1.0f) 1.2f else if (speechRate == 1.2f) 0.8f else 1.0f
                                                viewModel.setAnalyzerSpeechRate(newRate)
                                            },
                                        color = EditorialContainer,
                                        border = BorderStroke(1.dp, EditorialBorderSoft)
                                    ) {
                                        Text(
                                            "${speechRate}x Speed",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EditorialAccentPurple,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
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
                                    placeholder = {
                                        Text(
                                            "e.g. She has finished her homework.",
                                            color = EditorialTextMuted,
                                            fontSize = 13.sp
                                        )
                                    }
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { viewModel.speak(inputSentence, "analyzer_input", speechRate) },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = EditorialSurface,
                                            contentColor = EditorialTextSecondary
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.AutoMirrored.Filled.VolumeUp, null, modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Listen", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = { viewModel.runDeepAnalyzer() },
                                        enabled = !isAnalyzing && inputSentence.isNotBlank(),
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isAnalyzing) EditorialTextMuted else EditorialPrimary,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        if (isAnalyzing) {
                                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Analyzing...", fontSize = 12.sp)
                                        } else {
                                            Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp))
                                            Spacer(Modifier.width(6.dp))
                                            Text("Analyze", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Category Selector Dropdown Card
                    item(key = "category_dropdown") {
                        SleekDropdownSelector(
                            selectedItem = selectedCategory,
                            items = ANALYZER_CATEGORIES,
                            itemLabel = { it },
                            onItemSelected = { selectedCategory = it },
                            labelPrefix = "Grammar Category",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Analysis Output
                    if (!analysisResult.isNullOrBlank()) {
                        item(key = "analysis_result") {
                            EditorialCard {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        "GRAMMATICAL ANALYSIS",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EditorialTextMuted,
                                        letterSpacing = 1.0.sp
                                    )
                                    TextButton(
                                        onClick = { viewModel.clearAnalyzerOutput() },
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Close", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                EditorialDivider()
                                EditorialMixedText(text = analysisResult!!, fontSize = 13.sp)
                            }
                        }
                    }

                    // Subcategories & Examples
                    val subcategoryMap = ANALYZER_CATEGORIZED_EXAMPLES[selectedCategory] ?: emptyMap()
                    items(
                        items = subcategoryMap.entries.toList(),
                        key = { (subcatTitle, _) -> "$selectedCategory-$subcatTitle" }
                    ) { (subcatTitle, sentenceList) ->
                        EditorialCard {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = subcatTitle.uppercase(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EditorialAccentPurple,
                                    letterSpacing = 1.sp
                                )

                                sentenceList.forEach { sentence ->
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                viewModel.setAnalyzerInput(sentence)
                                                viewModel.runDeepAnalyzer()
                                            },
                                        color = EditorialContainer,
                                        border = BorderStroke(1.dp, EditorialBorderSoft)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "“$sentence”",
                                                fontSize = 13.sp,
                                                fontFamily = FontFamily.Serif,
                                                fontStyle = FontStyle.Italic,
                                                color = EditorialTextPrimary,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Icon(
                                                    Icons.AutoMirrored.Filled.VolumeUp,
                                                    null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = EditorialTextMuted
                                                )
                                                Icon(
                                                    Icons.Default.ChevronRight,
                                                    null,
                                                    modifier = Modifier.size(16.dp),
                                                    tint = EditorialPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                ScrollToBottomFAB(
                    lazyListState = lazyListState,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                )
            }
        }
    }
}


