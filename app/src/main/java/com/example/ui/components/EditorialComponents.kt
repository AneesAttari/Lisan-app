package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EditorialAccentPurple
import com.example.ui.theme.EditorialBorder
import com.example.ui.theme.EditorialBorderAccent
import com.example.ui.theme.EditorialBorderSoft
import com.example.ui.theme.EditorialContainer
import com.example.ui.theme.EditorialDarkBoxAccent
import com.example.ui.theme.EditorialDarkBoxBg
import com.example.ui.theme.EditorialDivider
import com.example.ui.theme.EditorialHeaderBg
import com.example.ui.theme.EditorialPillBg
import com.example.ui.theme.EditorialPrimary
import com.example.ui.theme.EditorialSurface
import com.example.ui.theme.EditorialTextMuted
import com.example.ui.theme.EditorialTextPrimary
import com.example.ui.theme.EditorialTextSecondary

import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.AnnotatedString

private fun countRtlChars(text: String): Int {
    var count = 0
    for (char in text) {
        val code = char.code
        if ((code in 0x0600..0x06FF) || (code in 0x0750..0x077F) || (code in 0x08A0..0x08FF) || (code in 0xFB50..0xFDFF) || (code in 0xFE70..0xFEFF)) {
            count++
        }
    }
    return count
}

private fun countLtrChars(text: String): Int {
    var count = 0
    for (char in text) {
        if (char in 'a'..'z' || char in 'A'..'Z') {
            count++
        }
    }
    return count
}

private fun isLineRtl(text: String): Boolean {
    val rtl = countRtlChars(text)
    val ltr = countLtrChars(text)
    if (rtl > 0 && rtl >= ltr) return true
    if (ltr > rtl) return false
    for (char in text) {
        val code = char.code
        if ((code in 0x0600..0x06FF) || (code in 0x0750..0x077F) || (code in 0x08A0..0x08FF)) return true
        if (char.isLetter()) return false
    }
    return true
}

internal fun bidiIsolate(text: String): String {
    if (text.isEmpty()) return text
    
    if (!text.any { it in 'a'..'z' || it in 'A'..'Z' }) {
        return text
    }

    val result = StringBuilder()
    val nonRtlRun = StringBuilder()

    for (char in text) {
        val code = char.code
        val isRtlChar = (code in 0x0600..0x06FF) || 
                         (code in 0x0750..0x077F) || 
                         (code in 0x08A0..0x08FF) || 
                         (code in 0xFB50..0xFDFF) || 
                         (code in 0xFE70..0xFEFF)

        if (isRtlChar) {
            if (nonRtlRun.isNotEmpty()) {
                val runStr = nonRtlRun.toString()
                if (runStr.any { it in 'a'..'z' || it in 'A'..'Z' }) {
                    val leadingSpaces = runStr.takeWhile { it.isWhitespace() }
                    val trailingSpaces = runStr.takeLastWhile { it.isWhitespace() }
                    val content = runStr.substring(leadingSpaces.length, runStr.length - trailingSpaces.length)
                    
                    result.append(leadingSpaces)
                    result.append("\u2066").append(content).append("\u2069")
                    result.append(trailingSpaces)
                } else {
                    result.append(runStr)
                }
                nonRtlRun.setLength(0)
            }
            result.append(char)
        } else {
            nonRtlRun.append(char)
        }
    }

    if (nonRtlRun.isNotEmpty()) {
        val runStr = nonRtlRun.toString()
        if (runStr.any { it in 'a'..'z' || it in 'A'..'Z' }) {
            val leadingSpaces = runStr.takeWhile { it.isWhitespace() }
            val trailingSpaces = runStr.takeLastWhile { it.isWhitespace() }
            val content = runStr.substring(leadingSpaces.length, runStr.length - trailingSpaces.length)

            result.append(leadingSpaces)
            result.append("\u2066").append(content).append("\u2069")
            result.append(trailingSpaces)
        } else {
            result.append(runStr)
        }
    }

    return result.toString()
}

private fun parseMarkdownToAnnotatedString(
    text: String,
    textColor: Color,
    boldColor: Color
): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        val len = text.length
        val plainAccumulator = StringBuilder()
        
        while (i < len) {
            if (i + 1 < len && text[i] == '*' && text[i+1] == '*') {
                if (plainAccumulator.isNotEmpty()) {
                    append(bidiIsolate(plainAccumulator.toString()))
                    plainAccumulator.setLength(0)
                }
                i += 2
                val start = i
                while (i < len && !(i + 1 < len && text[i] == '*' && text[i+1] == '*')) {
                    i++
                }
                val content = text.substring(start, i)
                pushStyle(SpanStyle(fontWeight = FontWeight.Bold, color = boldColor))
                append(bidiIsolate(content))
                pop()
                if (i < len) i += 2
            } else if (text[i] == '*') {
                if (plainAccumulator.isNotEmpty()) {
                    append(bidiIsolate(plainAccumulator.toString()))
                    plainAccumulator.setLength(0)
                }
                i += 1
                val start = i
                while (i < len && text[i] != '*') {
                    i++
                }
                val content = text.substring(start, i)
                pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                append(bidiIsolate(content))
                pop()
                if (i < len) i += 1
            } else if (text[i] == '`') {
                if (plainAccumulator.isNotEmpty()) {
                    append(bidiIsolate(plainAccumulator.toString()))
                    plainAccumulator.setLength(0)
                }
                i += 1
                val start = i
                while (i < len && text[i] != '`') {
                    i++
                }
                val content = text.substring(start, i)
                pushStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = EditorialAccentPurple, fontWeight = FontWeight.SemiBold))
                append(bidiIsolate(content))
                pop()
                if (i < len) i += 1
            } else {
                plainAccumulator.append(text[i])
                i++
            }
        }
        
        if (plainAccumulator.isNotEmpty()) {
            append(bidiIsolate(plainAccumulator.toString()))
        }
    }
}

@Composable
fun EditorialMixedText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp,
    color: Color = EditorialTextPrimary,
    lineHeight: androidx.compose.ui.unit.TextUnit = 20.sp,
    fontFamily: FontFamily? = null,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        text.split("\n").forEach { line ->
            val trimmed = line.trim()
            if (trimmed.isEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                return@forEach
            }

            // 1. Check for Headers
            if (trimmed.startsWith("###")) {
                val headerText = trimmed.removePrefix("###").trim()
                val annotated = parseMarkdownToAnnotatedString(headerText, textColor = EditorialPrimary, boldColor = EditorialPrimary)
                val hasRtl = isLineRtl(headerText)
                
                Text(
                    text = annotated,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = EditorialPrimary,
                    textAlign = if (hasRtl) TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 2.dp)
                )
            } else if (trimmed.startsWith("##")) {
                val headerText = trimmed.removePrefix("##").trim()
                val annotated = parseMarkdownToAnnotatedString(headerText, textColor = EditorialPrimary, boldColor = EditorialPrimary)
                val hasRtl = isLineRtl(headerText)
                
                Text(
                    text = annotated,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = EditorialPrimary,
                    textAlign = if (hasRtl) TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 4.dp)
                )
            } else if (trimmed.startsWith("#")) {
                val headerText = trimmed.removePrefix("#").trim()
                val annotated = parseMarkdownToAnnotatedString(headerText, textColor = EditorialPrimary, boldColor = EditorialPrimary)
                val hasRtl = isLineRtl(headerText)
                
                Text(
                    text = annotated,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif,
                    color = EditorialPrimary,
                    textAlign = if (hasRtl) TextAlign.Right else TextAlign.Left,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp, bottom = 6.dp)
                )
            }
            // 2. Check for Bullet Points
            else if (trimmed.startsWith("* ") || trimmed.startsWith("- ") || trimmed.startsWith("• ")) {
                val bulletText = trimmed.substring(2).trim()
                val annotated = parseMarkdownToAnnotatedString(bulletText, textColor = color, boldColor = EditorialAccentPurple)
                val hasRtl = isLineRtl(bulletText)
                
                if (hasRtl) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = annotated,
                            fontSize = fontSize,
                            lineHeight = lineHeight,
                            color = color,
                            fontFamily = fontFamily,
                            fontStyle = fontStyle,
                            fontWeight = fontWeight,
                            textAlign = TextAlign.Right,
                            style = TextStyle(textDirection = TextDirection.Rtl),
                            modifier = Modifier.weight(1f).padding(end = 6.dp)
                        )
                        Text(
                            text = "•",
                            fontSize = fontSize,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = "•",
                            fontSize = fontSize,
                            fontWeight = FontWeight.Bold,
                            color = EditorialAccentPurple,
                            modifier = Modifier.padding(top = 2.dp, end = 6.dp)
                        )
                        Text(
                            text = annotated,
                            fontSize = fontSize,
                            lineHeight = lineHeight,
                            color = color,
                            fontFamily = fontFamily,
                            fontStyle = fontStyle,
                            fontWeight = fontWeight,
                            textAlign = TextAlign.Left,
                            style = TextStyle(textDirection = TextDirection.Ltr),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            // 3. Regular Paragraph
            else {
                val annotated = parseMarkdownToAnnotatedString(trimmed, textColor = color, boldColor = EditorialAccentPurple)
                val hasRtl = isLineRtl(trimmed)
                val textDir = if (hasRtl) TextDirection.Rtl else TextDirection.Ltr
                
                Text(
                    text = annotated,
                    fontSize = fontSize,
                    lineHeight = lineHeight,
                    color = color,
                    fontFamily = fontFamily,
                    fontStyle = fontStyle,
                    fontWeight = fontWeight,
                    textAlign = if (hasRtl) TextAlign.Right else TextAlign.Left,
                    style = TextStyle(textDirection = textDir),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun EditorialHeader(
    subtitle: String,
    title: String,
    levelBadge: String,
    onLevelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = EditorialHeaderBg,
        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp),
        border = BorderStroke(1.dp, EditorialBorderSoft)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = subtitle.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.0.sp,
                    color = EditorialTextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = title,
                    fontSize = 28.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    color = EditorialPrimary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Surface(
                color = EditorialPillBg,
                shape = CircleShape,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onLevelClick() }
            ) {
                Text(
                    text = levelBadge,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = EditorialPrimary,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun EditorialCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(24.dp)),
        color = EditorialSurface,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, EditorialBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}

@Composable
fun EditorialSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.8.sp,
        color = EditorialAccentPurple,
        modifier = modifier
    )
}

@Composable
fun EditorialDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        thickness = 1.dp,
        color = EditorialDivider,
        modifier = modifier
    )
}

@Composable
fun EditorialComparisonGrid(
    similarities: List<String>,
    differences: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Similarities Box
            Surface(
                modifier = Modifier.weight(1f),
                color = EditorialContainer,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, EditorialBorderAccent)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "SIMILARITIES • مماثلتیں / یکسانیت",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    similarities.forEach { sim ->
                        EditorialMixedText(
                            text = "• $sim",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // Differences Box
            Surface(
                modifier = Modifier.weight(1f),
                color = EditorialContainer,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, EditorialBorderAccent)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DIFFERENCES • اختلافات",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EditorialTextMuted,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    differences.forEach { diff ->
                        EditorialMixedText(
                            text = "• $diff",
                            fontSize = 12.sp,
                            color = EditorialTextSecondary,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EditorialDarkPracticeBox(
    question: String,
    userText: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    isChecking: Boolean,
    feedback: String?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        color = EditorialDarkBoxBg,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "PRACTICE • مشق",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.0.sp,
                color = EditorialDarkBoxAccent
            )
            Spacer(modifier = Modifier.height(6.dp))
            EditorialMixedText(
                text = question,
                fontSize = 14.sp,
                color = Color.White,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = userText,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(
                            text = "Type your answer...",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = { onSubmit() }
                    )
                )

                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = !isChecking) { onSubmit() },
                    color = EditorialDarkBoxAccent,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isChecking) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = EditorialPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Submit",
                                tint = EditorialPrimary
                            )
                        }
                    }
                }
            }

            if (!feedback.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color.White.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    EditorialMixedText(
                        text = feedback,
                        color = Color.White,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}
