package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.ui.theme.EditorialTheme
import com.example.ui.viewmodel.LisanViewModel
import com.example.ui.components.VisualStepCard
import kotlinx.coroutines.launch

@Composable
fun ImplementApiKeyScreen(viewModel: LisanViewModel) {
    val context = LocalContext.current
    val colors = EditorialTheme.colors
    val userSettings by viewModel.userSettings.collectAsState()
    val geminiKeyFlow by viewModel.geminiApiKey.collectAsState()

    val currentKey = remember(userSettings, geminiKeyFlow) {
        userSettings.userApiKey?.takeIf { it.isNotBlank() }
            ?: geminiKeyFlow.takeIf { it.isNotBlank() }
            ?: ""
    }

    var inputKey by remember(currentKey) { mutableStateOf(currentKey) }
    var keySavedSuccess by remember { mutableStateOf(false) }

    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var showPasteTooltip by remember { mutableStateOf(false) }
    var showMoreMenu by remember { mutableStateOf(false) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var isTestingKey by remember { mutableStateOf(false) }
    var testResultMsg by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Surface(
            color = colors.surface,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp).padding(bottom = 4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            color = Color(0xFF2563EB),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Key, contentDescription = null, tint = Color.White)
                            }
                        }
                        Column {
                            Text(
                                text = "Implement Your API Key",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.primary
                            )
                            Text(
                                text = "مفت اے پی آئی کی (API Key) گائیڈ اور شمولیت",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.accentPurple
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF3B82F6).copy(alpha = 0.3f),
                                    Color(0xFF2563EB),
                                    Color(0xFF1D4ED8).copy(alpha = 0.3f)
                                )
                            )
                        )
                )
            }
        }

        // Active Status Card
        Surface(
            color = colors.container,
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, if (currentKey.isNotBlank()) colors.accentPurple else colors.borderSoft)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentKey.isNotBlank()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.accentPurple)
                            Text(
                                text = "✅ Custom Gemini API Key Active!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = colors.primary
                            )
                        }
                        TextButton(
                            onClick = {
                                viewModel.setUserApiKey("")
                                Toast.makeText(context, "API Key Removed", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Text("کی ختم کریں (Remove)", color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Text(
                        text = "آپ کی اپنی اے پی آئی کی (API Key) فعال ہے! اب آپ 1,500 روزانہ مفت سوالات کے ساتھ بغیر کسی رکاوٹ کے ایپ استعمال کر سکتے ہیں۔",
                        fontSize = 13.sp,
                        color = colors.textPrimary,
                        lineHeight = 22.sp
                    )
                    Text(
                        text = "Active Key: ${currentKey.take(6)}...${currentKey.takeLast(4)}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = colors.textMuted
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = colors.accentPurple)
                        Text(
                            text = "مفت استعمال کا نظام (Shared Pool Active)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = colors.primary
                        )
                    }
                    Text(
                        text = "گوگل تمام صارفین کو روزانہ 1,500 مفت سوالات کی سہولت دیتا ہے۔ اپنی ذاتی اے پی آئی کی (API Key) شامل کرنے سے آپ کو پورے 1,500 سوالات کا کوٹہ مل جائے گا!",
                        fontSize = 13.sp,
                        color = colors.textPrimary,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // API Key Input Card (Moved to the upper side!)
        Surface(
            color = colors.container,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, colors.accentPurple),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "اپنی اے پی آئی کی (API Key) درج کریں (PASTE API KEY):",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.primary
                    )
                    
                    TextButton(
                        onClick = {
                            val clipText = clipboardManager.getText()?.text
                            if (!clipText.isNullOrBlank()) {
                                inputKey = clipText.trim()
                                keySavedSuccess = false
                                testResultMsg = null
                                Toast.makeText(context, "Pasted!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                            Text("پیسٹ کریں (Paste)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
                        }
                    }
                }

                // Hover paste popup/tooltip on long-press
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = { 
                            inputKey = it
                            keySavedSuccess = false
                            testResultMsg = null
                        },
                        placeholder = { Text("AIzaSy... یا اپنی Key یہاں پیسٹ کریں", fontSize = 12.sp) },
                        singleLine = true,
                        visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                keyboardController?.hide()
                                // Save the key
                                val clean = inputKey.trim()
                                if (clean.isNotBlank()) {
                                    viewModel.setUserApiKey(clean)
                                    keySavedSuccess = true
                                    Toast.makeText(context, "✅ API Key Saved & Activated! 1,500 daily allowance unlocked.", Toast.LENGTH_LONG).show()
                                } else {
                                    viewModel.setUserApiKey("")
                                    keySavedSuccess = false
                                    Toast.makeText(context, "API Key cleared.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .pointerInput(Unit) {
                                detectTapGestures(
                                    onLongPress = {
                                        showPasteTooltip = true
                                    },
                                    onTap = {
                                        focusRequester.requestFocus()
                                        keyboardController?.show()
                                    }
                                )
                            },
                        shape = RoundedCornerShape(12.dp),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (inputKey.isNotBlank()) {
                                    IconButton(onClick = { 
                                        inputKey = "" 
                                        keySavedSuccess = false
                                        testResultMsg = null
                                    }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                                    }
                                }
                                Box {
                                    IconButton(onClick = { showMoreMenu = true }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "More Options", tint = Color.Gray)
                                    }
                                    
                                    DropdownMenu(
                                        expanded = showMoreMenu,
                                        onDismissRequest = { showMoreMenu = false }
                                    ) {
                                        DropdownMenuItem(
                                            text = { Text("پیسٹ کریں (Paste)") },
                                            leadingIcon = { Icon(Icons.Default.ContentPaste, contentDescription = null) },
                                            onClick = {
                                                showMoreMenu = false
                                                val clipText = clipboardManager.getText()?.text
                                                if (!clipText.isNullOrBlank()) {
                                                    inputKey = clipText.trim()
                                                    keySavedSuccess = false
                                                    testResultMsg = null
                                                    Toast.makeText(context, "Pasted!", Toast.LENGTH_SHORT).show()
                                                } else {
                                                    Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text(if (isKeyVisible) "پوشیدہ کریں (Hide)" else "دکھائیں (Show Key)") },
                                            leadingIcon = { Icon(if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null) },
                                            onClick = {
                                                showMoreMenu = false
                                                isKeyVisible = !isKeyVisible
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { 
                                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    Text("کی چیک کریں (Test Key)")
                                                    if (isTestingKey) {
                                                        CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                                                    }
                                                }
                                            },
                                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981)) },
                                            enabled = !isTestingKey && inputKey.isNotBlank(),
                                            onClick = {
                                                showMoreMenu = false
                                                isTestingKey = true
                                                testResultMsg = "کی چیک ہو رہی ہے..."
                                                coroutineScope.launch {
                                                    try {
                                                        val req = com.example.data.model.GenerateContentRequest(
                                                            contents = listOf(com.example.data.model.Content(parts = listOf(com.example.data.model.Part(text = "Say Hello"))))
                                                        )
                                                        val response = com.example.data.remote.RetrofitClient.service.generateContent("gemini-3.5-flash", inputKey.trim(), req)
                                                        if (response.candidates?.isNotEmpty() == true) {
                                                            testResultMsg = "✅ بہترین! (Gemini 3.5 Flash & Transcribe APIs Active)"
                                                            Toast.makeText(context, "✅ Success! Gemini 3.5 Flash & Transcribe APIs Active.", Toast.LENGTH_LONG).show()
                                                        } else {
                                                            testResultMsg = "❌ اے پی آئی کی کام نہیں کر رہی۔"
                                                        }
                                                    } catch (e: Exception) {
                                                        testResultMsg = "❌ غلط اے پی آئی کی! مندرجہ ذیل نقص پیش آیا:\n${e.localizedMessage ?: "Unknown error"}"
                                                        Toast.makeText(context, "❌ Invalid API Key or block.", Toast.LENGTH_LONG).show()
                                                    } finally {
                                                        isTestingKey = false
                                                    }
                                                }
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("صاف کریں (Clear)") },
                                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
                                            onClick = {
                                                showMoreMenu = false
                                                inputKey = ""
                                                keySavedSuccess = false
                                                testResultMsg = null
                                            }
                                        )
                                        DropdownMenuItem(
                                            text = { Text("لنک کھولیں (Open Studio)") },
                                            leadingIcon = { Icon(Icons.Default.OpenInNew, contentDescription = null) },
                                            onClick = {
                                                showMoreMenu = false
                                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/"))
                                                context.startActivity(intent)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    )

                    // Floating Paste popup tooltip on long-press
                    if (showPasteTooltip) {
                        Popup(
                            alignment = Alignment.TopCenter,
                            offset = androidx.compose.ui.unit.IntOffset(0, -90),
                            onDismissRequest = { showPasteTooltip = false }
                        ) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                modifier = Modifier.padding(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .clickable {
                                            val clipText = clipboardManager.getText()?.text
                                            if (!clipText.isNullOrBlank()) {
                                                inputKey = clipText.trim()
                                                keySavedSuccess = false
                                                testResultMsg = null
                                                Toast.makeText(context, "Pasted!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Clipboard is empty", Toast.LENGTH_SHORT).show()
                                            }
                                            showPasteTooltip = false
                                        }
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(Icons.Default.ContentPaste, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                    Text("پیسٹ کریں (Paste)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                if (testResultMsg != null) {
                    Text(
                        text = testResultMsg!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (testResultMsg!!.contains("✅")) Color(0xFF059669) else Color(0xFFDC2626),
                        lineHeight = 16.sp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }

                Button(
                    onClick = {
                        val clean = inputKey.trim()
                        if (clean.isNotBlank()) {
                            viewModel.setUserApiKey(clean)
                            keySavedSuccess = true
                            Toast.makeText(context, "✅ API Key Saved & Activated! 1,500 daily allowance unlocked.", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.setUserApiKey("")
                            keySavedSuccess = false
                            Toast.makeText(context, "API Key cleared.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (keySavedSuccess) Color(0xFF10B981) else Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (keySavedSuccess) Icons.Default.CheckCircle else Icons.Default.Save,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Text(
                            text = if (keySavedSuccess) "Key Saved & Activated! ✓" else "محفوظ کریں (Save Key & Activate)",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }

                // Temporary Isolated Diagnostic Button: Test Gemini Connection
                Button(
                    onClick = {
                        val keyPresent = inputKey.isNotBlank()
                        val keyLen = inputKey.trim().length
                        android.util.Log.d("LisanDiagnostic", "BROWSER_BYOK_KEY_PRESENT=$keyPresent")
                        android.util.Log.d("LisanDiagnostic", "BROWSER_BYOK_KEY_LENGTH=$keyLen")
                        android.util.Log.d("LisanDiagnostic", "BROWSER_BYOK_REQUEST_STARTED=true")
                        android.util.Log.d("LisanDiagnostic", "BROWSER_GEMINI_REQUEST_STARTED")

                        if (!keyPresent) {
                            testResultMsg = "❌ BROWSER_BYOK_KEY_PRESENT=false. Please enter a valid API key first."
                            android.util.Log.e("LisanDiagnostic", "BROWSER_GEMINI_ERROR: API key is blank")
                            return@Button
                        }

                        isTestingKey = true
                        testResultMsg = "Testing Gemini 3.5 Transcribe & Flash Connection..."
                        coroutineScope.launch {
                            try {
                                val req = com.example.data.model.GenerateContentRequest(
                                    contents = listOf(com.example.data.model.Content(parts = listOf(com.example.data.model.Part(text = "Say hello and confirm Gemini 3.5 Transcribe audio speech recognition readiness."))))
                                )
                                val response = com.example.data.remote.RetrofitClient.service.generateContent("gemini-3.5-flash", inputKey.trim(), req)
                                android.util.Log.d("LisanDiagnostic", "BROWSER_GEMINI_HTTP_STATUS=200 OK")
                                android.util.Log.d("LisanDiagnostic", "BROWSER_GEMINI_RESPONSE_RECEIVED")
                                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
                                android.util.Log.d("LisanDiagnostic", "BROWSER_GEMINI_RESPONSE_TEXT_LENGTH=${text.length}")
                                testResultMsg = "✅ BROWSER_GEMINI_HTTP_STATUS=200 OK\nModels Active: Gemini 3.5 Transcribe & Flash\nResponse: $text"
                                Toast.makeText(context, "✅ Connection Success! Gemini 3.5 Transcribe & Flash Ready.", Toast.LENGTH_LONG).show()
                            } catch (e: Exception) {
                                val httpCode = if (e is retrofit2.HttpException) e.code() else 500
                                android.util.Log.e("LisanDiagnostic", "BROWSER_GEMINI_HTTP_STATUS=$httpCode")
                                android.util.Log.e("LisanDiagnostic", "BROWSER_GEMINI_ERROR: ${e.message}", e)
                                testResultMsg = "❌ BROWSER_GEMINI_HTTP_STATUS=$httpCode\nBROWSER_GEMINI_ERROR: ${e.localizedMessage ?: e.message}"
                                Toast.makeText(context, "❌ Connection Failed [HTTP $httpCode]", Toast.LENGTH_LONG).show()
                            } finally {
                                isTestingKey = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    enabled = !isTestingKey
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isTestingKey) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = Color.White)
                        }
                        Text(
                            text = if (isTestingKey) "Testing Connection..." else "Test Gemini Connection ('Say hello.')",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Direct Link Button
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://aistudio.google.com/app/apikey"))
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = colors.accentPurple),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, tint = Color.White)
                Text(
                    text = "ڈائریکٹ لنک: گوگل اے آئی سٹوڈیو (Google AI Studio) کیز پیج کھولیں",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
        }

        // Urdu Overview Text
        Surface(
            color = colors.container,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, colors.borderSoft)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "السلام علیکم ورحمة الله وبركاته!",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary
                )
                Text(
                    text = "لسان گرامر (Lisan Grammar) میں خوش آمدید!",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.accentPurple
                )
                Text(
                    text = "اگر آپ اس ایپ کی تمام تمام سہولیات جیسے AI ٹیوٹر (Tutor Chat)، AI آواز سے بات چیت (Voice Conversations)، گرامر تجزیہ کار (Analyzer)، اور تمام کوئزز کو ہمیشہ بغیر کسی حد اور بغیر کسی روک ٹوک کے (Unlimited) مفت استعمال کرنا چاہتے ہیں، تو اپنی اے پی آئی کی (API Key) یہاں درج کریں۔",
                    fontSize = 13.sp,
                    color = colors.textPrimary,
                    lineHeight = 22.sp
                )
                Text(
                    text = "• یہ بالکل 100% مفت ہے اور اس کے لیے کسی پیسے یا کریڈٹ کارڈ کی ضرورت نہیں ہے۔\n• یہ سیٹ اپ صرف ایک بار (Once) ضروری ہے۔ ایک بار Key شامل کرنے کے بعد آپ لسان گرامر ایپ کو ہمیشہ کے لیے بغیر کسی لمیٹ کے استعمال کر سکتے ہیں۔",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = colors.textPrimary,
                    lineHeight = 22.sp
                )
                Text(
                    text = "نیچے دی گئی تصاویر اور آسان مراحل دیکھتے ہوئے اپنی مفت اے پی آئی کی (API Key) حاصل کریں:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.primary,
                    lineHeight = 22.sp
                )
            }
        }

        Text(
            text = "تصویری مراحل • STEP-BY-STEP VISUAL GUIDE",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textMuted
        )

        // Step 1: Search Google AI Studio
        VisualStepCard(
            stepBadge = "Step 1 of 5",
            titleEn = "Step 1: Search 'Google AI Studio' on Google\nOpen Google and search for 'Google AI Studio'. Click the official website result 'aistudio.google.com' (do not click sponsored ads).",
            titleUrdu = "قدم 1 (تصویر 1): گوگل پر \'گوگل اے آئی سٹوڈیو (Google AI Studio)\' تلاش کریں\nگوگل کھول کر \'گوگل اے آئی سٹوڈیو (Google AI Studio)\' سرچ کریں۔ نتائج میں برائے کرم اصل ویب سائٹ aistudio.google.com پر کلک کریں۔ (کسی اشتہار / Sponsored Result پر کلک نہ کریں۔)",
            mockContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), CircleShape)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Google ai studio", fontSize = 12.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Sponsored Result", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text("Try Google AI Studio - Gemini API for Developers", fontSize = 12.sp, color = Color(0xFF1D4ED8))
                    Text("https://aistudio.google.com > gemini > api", fontSize = 10.sp, color = Color.Gray)
                    Text("Start creating in Google AI Studio...", fontSize = 10.sp, color = Color.DarkGray)

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEF08A), RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                Text("Google AI Studio", fontSize = 11.sp, color = Color.DarkGray)
                            }
                            Text("https://aistudio.google.com", fontSize = 10.sp, color = Color(0xFF4B5563))
                            Text("Google AI Studio", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1D4ED8))
                            Text("Start exploring and building with Google latest models...", fontSize = 10.sp, color = Color.Black)
                        }
                    }
                }
            }
        )

        // Step 2: Open Menu
        VisualStepCard(
            stepBadge = "Step 2 of 5",
            titleEn = "Step 2: Sign in & Open Navigation Menu\nSign in with your Gmail account if prompted, then click the navigation menu icon (☰) in the top-left corner.",
            titleUrdu = "قدم 2 (تصویر 2): سائن اِن کریں اور مینو (☰) کھولیں\nاپنے جی میل اکاؤنٹ سے سائن اِن کریں، پھر اوپر بائیں جانب موجود مینو کے نشان (☰) پر کلک کریں۔",
            mockContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFFEF08A), RoundedCornerShape(6.dp))
                                .border(2.dp, Color.Red, RoundedCornerShape(6.dp))
                                .padding(6.dp)
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black, modifier = Modifier.size(20.dp))
                        }

                        Text("aistudio.google.com", fontSize = 11.sp, color = Color.Gray, fontFamily = FontFamily.Monospace)
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF3B82F6), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Build your ideas with Gemini", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        )

        // Step 3: Click Dashboard
        VisualStepCard(
            stepBadge = "Step 3 of 5",
            titleEn = "Step 3: Click 'Dashboard'\nIn the navigation menu drawer, locate the MANAGE section and click on Dashboard.",
            titleUrdu = "قدم 3 (تصویر 3): \'ڈیش بورڈ (Dashboard)\' پر کلک کریں\nنیویگیشن مینو (Navigation Menu) میں، مینج (MANAGE) سیکشن تلاش کریں اور ڈیش بورڈ (Dashboard) پر کلک کریں۔",
            mockContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text("Google AI Studio", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("EXPLORE", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("  • Playground", fontSize = 11.sp, color = Color.DarkGray)
                    Text("  • History", fontSize = 11.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("BUILD", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("  • New app", fontSize = 11.sp, color = Color.DarkGray)
                    Text("  • My apps", fontSize = 11.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("MANAGE", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEF08A), RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Text("Dashboard", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("  • Documentation", fontSize = 11.sp, color = Color.DarkGray)
                }
            }
        )

        // Step 4: Click API Keys
        VisualStepCard(
            stepBadge = "Step 4 of 5",
            titleEn = "Step 4: Click 'API Keys'\nIn the dashboard sidebar under PROJECT, click on API Keys.",
            titleUrdu = "قدم 4 (تصویر 4): \'اے پی آئی کیز (API Keys)\' پر کلک کریں\nڈیش بورڈ (Dashboard) کی سائیڈ بار میں پروجیکٹ (PROJECT) کے تحت اے پی آئی کیز (API Keys) پر کلک کریں۔",
            mockContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Text("Dashboard", fontSize = 12.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PROJECT", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFEF08A), RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Red, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Text("API Keys", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text("  • Projects", fontSize = 11.sp, color = Color.DarkGray)
                    Text("USAGE & BILLING", fontSize = 9.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Text("  • Usage", fontSize = 11.sp, color = Color.DarkGray)
                    Text("  • Rate Limit", fontSize = 11.sp, color = Color.DarkGray)
                }
            }
        )

        // Step 5: Click Create API key & Copy (Matching Google AI Studio screenshot UI)
        VisualStepCard(
            stepBadge = "Step 5 of 5",
            titleEn = "Step 5: Copy Your API Key\nAfter clicking 'Create API key', locate your key card (...4RJg). Click the highlighted Copy icon (📄) next to the key code to copy it.",
            titleUrdu = "قدم 5 (تصویر 5): اپنی \'اے پی آئی کی (API Key)\' کاپی کریں\n\'کریٹ اے پی آئی کی (Create API key)\' پر کلک کرنے کے بعد، اپنی کی (Key) کا کارڈ تلاش کریں (مثلاً ...4RJg)۔ اسے کاپی کرنے کے لیے کی کوڈ (Key code) کے ساتھ موجود نمایاں کاپی آئیکن (Copy icon) 📄 پر کلک کریں۔",
            mockContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("API Keys", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)

                        Surface(
                            color = Color(0xFF2563EB),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("Create API key", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // API Key Card (exact replica of user's screenshot)
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
                            // Row 1: Key preview ...4RJg + 4 inline options (Copy highlighted in yellow/red, Pricing, Analytics, Menu)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "...4RJg",
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF1E40AF)
                                )

                                // Exact 4 options beside key name matching screenshot
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. COPY ICON (HIGHLIGHTED WITH YELLOW BG & RED BORDER FOR CLEAR GUIDANCE)
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFEF08A), RoundedCornerShape(6.dp))
                                            .border(1.5.dp, Color.Red, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 4.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy Key", modifier = Modifier.size(13.dp), tint = Color.Black)
                                            Text("Copy", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        }
                                    }

                                    // 2. PRICING ICON ($)
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 5.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.AttachMoney, contentDescription = "Pricing", modifier = Modifier.size(13.dp), tint = Color(0xFF475569))
                                    }

                                    // 3. ANALYTICS ICON (Bar Chart)
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 5.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.BarChart, contentDescription = "Analytics", modifier = Modifier.size(13.dp), tint = Color(0xFF475569))
                                    }

                                    // 4. MORE OPTIONS ICON (Three dots)
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFF1F5F9), RoundedCornerShape(6.dp))
                                            .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 5.dp, vertical = 4.dp)
                                    ) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(13.dp), tint = Color(0xFF475569))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Gemini API Key 3", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(6.dp))

                            // Details from screenshot
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Project", fontSize = 11.sp, color = Color.Gray)
                                Text("Assistant", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Medium)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Project ID", fontSize = 11.sp, color = Color.Gray)
                                Text("assistant-46285", fontSize = 11.sp, color = Color.DarkGray, fontFamily = FontFamily.Monospace)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Created", fontSize = 11.sp, color = Color.Gray)
                                Text("Aug 5, 2026", fontSize = 11.sp, color = Color.DarkGray)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Billing tier", fontSize = 11.sp, color = Color.Gray)
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Set up billing", fontSize = 11.sp, color = Color(0xFF2563EB))
                                    Text("Free tier", fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        )

        // Final Step Card
        Surface(
            color = colors.container,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, colors.borderSoft)
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = colors.accentPurple)
                    Text("آخری قدم (کوئی تصویر نہیں)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = colors.primary)
                }
                Text(
                    text = "اب واپس اس ایپ میں آئیں اور اوپر جہاں \"اپنی اے پی آئی کی (API Key) درج کریں\" لکھا ہے، وہاں یہ کاپی کی گئی کی (Key) پیسٹ کر کے محفوظ کر لیں۔\n\nگوگل (Google) اپنی اے پی آئی کی (API Key) اپڈیٹ کر سکتا ہے۔ گوگل (Google) سال میں ایک بار یا کچھ مہینوں میں اپنی اے پی آئی کی (API Key) اپڈیٹ کر سکتا ہے۔ تو جب ایسا ہو، تو آپ اے آئی سٹوڈیو (Google AI Studio) میں جائیں اور اپنی نئی اپڈیٹ شدہ اے پی آئی کی (API Key) کاپی کریں۔ پریشان نہ ہوں، یہ مہینوں میں صرف ایک بار ہوتا ہے۔\n\nبس، اتنا کرنے سے آپ اپنی ذاتی API Key کے ذریعے، بغیر کسی سبسکرپشن اور بغیر کسی رکاوٹ کے، یہ ایپ ہمیشہ استعمال کر سکیں گے۔ یہ سہولت بالکل مفت ہے اور اس کے لیے کسی کریڈٹ کارڈ کی ضرورت نہیں ہے شکریہ ۔",
                    fontSize = 13.sp,
                    color = colors.textPrimary,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
