package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import com.example.ui.components.VoiceTutorDialog
import com.example.ui.components.RevolvingAppIconCircle
import com.example.ui.components.bidiIsolate
import com.example.ui.components.cleanMarkdownForSpeech
import com.example.ui.components.detectLanguageForSpeech
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale
import androidx.compose.runtime.DisposableEffect
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.EditorialSectionTitle
import com.example.ui.components.EditorialMixedText
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
fun TutorChatScreen(
    viewModel: LisanViewModel,
    modifier: Modifier = Modifier
) {
    val chatHistory by viewModel.chatHistory.collectAsState()
    val isChatLoading by viewModel.isChatLoading.collectAsState()
    val chatError by viewModel.chatError.collectAsState()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    var showVoiceTutor by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }
    var currentlySpeakingText by remember { mutableStateOf<String?>(null) }
    val mainHandler = remember { android.os.Handler(android.os.Looper.getMainLooper()) }
    
    DisposableEffect(Unit) {
        var tts: TextToSpeech? = null
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.15f)
                tts?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {}
                    override fun onDone(utteranceId: String?) {
                        mainHandler.post {
                            currentlySpeakingText = null
                        }
                    }
                    override fun onError(utteranceId: String?) {
                        mainHandler.post {
                            currentlySpeakingText = null
                        }
                    }
                    override fun onStop(utteranceId: String?, interrupted: Boolean) {
                        mainHandler.post {
                            currentlySpeakingText = null
                        }
                    }
                })
            }
        }
        textToSpeech = tts
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val speakText: (String) -> Unit = { text ->
        if (textToSpeech != null) {
            val cleanText = cleanMarkdownForSpeech(text)
            val locale = detectLanguageForSpeech(cleanText)
            try {
                val result = textToSpeech?.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech?.setLanguage(Locale.forLanguageTag("ur"))
                }
            } catch (e: Exception) {
                Log.e("TutorChatScreen", "Error setting language to $locale", e)
            }
            textToSpeech?.setPitch(1.0f)
            textToSpeech?.setSpeechRate(1.15f)
            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "TUTOR_CHAT")
            }
            currentlySpeakingText = text
            textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, params, "TUTOR_CHAT")
        }
    }

    val quickQuestions = listOf(
        "انگریزی میں 'a' اور 'an' میں کیا فرق ہے؟",
        "مجھے شرطیہ جملے (If Conditionals) سمجھائیں",
        "جملہ اسمیہ کا انگریزی میں کیسے ترجمہ کرتے ہیں؟",
        "مجھے فعل حال تمام (Present Perfect) کی کوئی مشق دیں"
    )

    LaunchedEffect(chatHistory.size) {
        if (chatHistory.isNotEmpty()) {
            listState.animateScrollToItem(chatHistory.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Toolbar with Clear History
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EditorialSectionTitle("Interactive AI Teacher • انٹرایکٹو اے آئی استاد")
            Surface(
                color = EditorialContainer,
                shape = CircleShape,
                border = BorderStroke(1.dp, EditorialBorderAccent),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { viewModel.clearChat() }
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear",
                        tint = EditorialAccentPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "محادثہ صاف کریں",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialPrimary
                    )
                }
            }
        }

        // Active Text-To-Speech Stop Header Bar
        AnimatedVisibility(
            visible = currentlySpeakingText != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Surface(
                color = Color(0xFFFFF3E0),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFFB74D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("txt_speaking_stop_banner")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speaking",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "استاد بول رہا ہے... (AI Teacher Speaking)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                    }
                    Button(
                        onClick = {
                            textToSpeech?.stop()
                            currentlySpeakingText = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("stop_txt_speech_button")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop Speech",
                                modifier = Modifier.size(14.dp)
                            )
                            Text("آواز روکیں (Stop)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Quick Starter Questions Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickQuestions.forEach { question ->
                Surface(
                    color = EditorialSurface,
                    shape = CircleShape,
                    border = BorderStroke(1.dp, EditorialBorderAccent),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            viewModel.sendChatMessage(question)
                        }
                ) {
                    Text(
                        text = question,
                        fontSize = 12.sp,
                        color = EditorialPrimary,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Scrollable Chat History
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(chatHistory) { message ->
                val isUser = message.role == "user"
                val textContent = message.parts.firstOrNull()?.text ?: ""

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(0.85f),
                        color = if (isUser) EditorialPrimary else EditorialSurface,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (isUser) 20.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 20.dp
                        ),
                        border = if (isUser) null else BorderStroke(1.dp, EditorialBorder)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isUser) "YOU • آپ" else "TEACHER • استاد",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = if (isUser) Color.White.copy(alpha = 0.7f) else EditorialAccentPurple
                                )
                                if (!isUser) {
                                    val isThisSpeaking = currentlySpeakingText == textContent
                                    IconButton(
                                        onClick = {
                                            if (isThisSpeaking) {
                                                textToSpeech?.stop()
                                                currentlySpeakingText = null
                                            } else {
                                                speakText(textContent)
                                            }
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isThisSpeaking) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = if (isThisSpeaking) "آواز بند کریں" else "آواز سنیں",
                                            tint = if (isThisSpeaking) Color.Red else EditorialAccentPurple,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            EditorialMixedText(
                                text = textContent,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                fontFamily = if (isUser) FontFamily.Default else FontFamily.Serif,
                                color = if (isUser) Color.White else EditorialTextPrimary
                            )
                        }
                    }
                }
            }

            if (isChatLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            color = EditorialContainer,
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, EditorialBorderSoft)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                RevolvingAppIconCircle(
                                    size = 28.dp,
                                    iconSize = 12.dp
                                )
                                Text(
                                    text = "استاد تقابلی جواب تیار کر رہے ہیں...",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    color = EditorialTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            if (chatError != null) {
                item {
                    Surface(
                        color = EditorialSurface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFFB3261E)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = chatError!!,
                            color = Color(0xFFB3261E),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Input Toolbar
        Surface(
            color = EditorialSurface,
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, EditorialBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { showVoiceTutor = true }
                        .testTag("open_voice_tutor_button"),
                    color = EditorialAccentPurple.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Conversation",
                            tint = EditorialAccentPurple,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    textStyle = TextStyle(
                        color = EditorialTextPrimary,
                        fontSize = 14.sp,
                        textDirection = TextDirection.ContentOrRtl
                    ),
                    placeholder = {
                        Text(
                            text = bidiIsolate("اپنے استاد سے پوچھیں (مثلاً: Present Tense سمجھائیں...)"),
                            color = EditorialTextMuted,
                            fontSize = 13.sp,
                            style = TextStyle(textDirection = TextDirection.ContentOrRtl)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedTextColor = EditorialTextPrimary,
                        unfocusedTextColor = EditorialTextPrimary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(inputText)
                                inputText = ""
                            }
                        }
                    )
                )

                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable(enabled = inputText.isNotBlank() && !isChatLoading) {
                            if (inputText.isNotBlank()) {
                                viewModel.sendChatMessage(inputText)
                                inputText = ""
                            }
                        },
                    color = if (inputText.isNotBlank()) EditorialPrimary else EditorialContainer,
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (inputText.isNotBlank()) Color.White else EditorialTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        if (showVoiceTutor) {
            VoiceTutorDialog(
                viewModel = viewModel,
                onDismissRequest = { showVoiceTutor = false }
            )
        }
    }
}
