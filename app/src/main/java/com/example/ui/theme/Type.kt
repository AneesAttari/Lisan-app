package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Typography =
  Typography(
    headlineLarge =
      TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
      ),
    headlineMedium =
      TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Italic,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp,
      ),
    titleLarge =
      TextStyle(
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontStyle = FontStyle.Italic,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.1.sp,
      ),
    titleMedium =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp,
      ),
    bodyLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp,
      ),
    bodyMedium =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
      ),
    labelLarge =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 1.0.sp,
      ),
    labelMedium =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.2.sp,
      ),
    labelSmall =
      TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp,
      )
  )

object UrduTypography {
    val body = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.2.sp
    )

    val medium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.2.sp
    )

    val heading = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    )

    val chatMessage = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.2.sp
    )

    val label = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    )
}

fun isUrduOrArabicText(text: String): Boolean {
    var arabicCharCount = 0
    var totalLetters = 0
    for (char in text) {
        val code = char.code
        if (code in 0x0600..0x06FF || code in 0x0750..0x077F || code in 0x08A0..0x08FF || code in 0xFB50..0xFDFF || code in 0xFE70..0xFEFF) {
            arabicCharCount++
        }
        if (char.isLetter()) {
            totalLetters++
        }
    }
    return if (totalLetters > 0) {
        (arabicCharCount.toFloat() / totalLetters) > 0.25f || arabicCharCount >= 3
    } else {
        arabicCharCount > 0
    }
}


