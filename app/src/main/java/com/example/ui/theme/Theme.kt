package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

data class EditorialThemeColors(
    val background: Color,
    val surface: Color,
    val headerBg: Color,
    val container: Color,
    val border: Color,
    val borderSoft: Color,
    val borderAccent: Color,
    val primary: Color,
    val primaryDark: Color,
    val accentPurple: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val pillBg: Color,
    val darkBoxBg: Color,
    val darkBoxAccent: Color,
    val divider: Color
)

val LightColors = EditorialThemeColors(
    background = LightBg,
    surface = LightSurf,
    headerBg = LightHdrBg,
    container = LightCont,
    border = LightBdr,
    borderSoft = LightBdrSoft,
    borderAccent = LightBdrAcc,
    primary = LightPrim,
    primaryDark = LightPrimDark,
    accentPurple = LightAccPurple,
    textPrimary = LightTxtPrimary,
    textSecondary = LightTxtSecondary,
    textMuted = LightTxtMuted,
    pillBg = LightPill,
    darkBoxBg = LightDarkBox,
    darkBoxAccent = LightDarkBoxAcc,
    divider = LightDiv
)

val DarkColors = EditorialThemeColors(
    background = DarkBg,
    surface = DarkSurf,
    headerBg = DarkHdrBg,
    container = DarkCont,
    border = DarkBdr,
    borderSoft = DarkBdrSoft,
    borderAccent = DarkBdrAcc,
    primary = DarkPrim,
    primaryDark = DarkPrimDark,
    accentPurple = DarkAccPurple,
    textPrimary = DarkTxtPrimary,
    textSecondary = DarkTxtSecondary,
    textMuted = DarkTxtMuted,
    pillBg = DarkPill,
    darkBoxBg = DarkBoxBg,
    darkBoxAccent = DarkBoxAcc,
    divider = DarkDiv
)

val LocalEditorialColors = staticCompositionLocalOf { LightColors }

object EditorialTheme {
    val colors: EditorialThemeColors
        @Composable
        get() = LocalEditorialColors.current
}

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkPrim,
    onPrimary = DarkBg,
    secondary = DarkAccPurple,
    onSecondary = DarkBg,
    tertiary = DarkBoxAcc,
    background = DarkBg,
    onBackground = DarkTxtPrimary,
    surface = DarkSurf,
    onSurface = DarkTxtPrimary,
    surfaceVariant = DarkCont,
    onSurfaceVariant = DarkTxtSecondary,
    outline = DarkBdr
  )

private val LightColorScheme =
  lightColorScheme(
    primary = LightPrim,
    onPrimary = LightSurf,
    secondary = LightAccPurple,
    onSecondary = LightSurf,
    tertiary = LightDarkBoxAcc,
    background = LightBg,
    onBackground = LightTxtPrimary,
    surface = LightSurf,
    onSurface = LightTxtPrimary,
    surfaceVariant = LightCont,
    onSurfaceVariant = LightTxtSecondary,
    outline = LightBdr
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set dynamicColor default to false to showcase the Editorial Aesthetic palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val themeColors = if (darkTheme) DarkColors else LightColors
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  CompositionLocalProvider(LocalEditorialColors provides themeColors) {
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
  }
}
