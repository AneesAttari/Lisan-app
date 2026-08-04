package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = EditorialPrimary,
    onPrimary = EditorialSurface,
    secondary = EditorialAccentPurple,
    onSecondary = EditorialSurface,
    tertiary = EditorialDarkBoxAccent,
    background = EditorialPrimary,
    onBackground = EditorialSurface,
    surface = EditorialPrimary,
    onSurface = EditorialSurface,
    surfaceVariant = EditorialDarkBoxBg,
    onSurfaceVariant = EditorialBorderAccent,
    outline = EditorialBorder
  )

private val LightColorScheme =
  lightColorScheme(
    primary = EditorialPrimary,
    onPrimary = EditorialSurface,
    secondary = EditorialAccentPurple,
    onSecondary = EditorialSurface,
    tertiary = EditorialDarkBoxAccent,
    background = EditorialBackground,
    onBackground = EditorialTextPrimary,
    surface = EditorialSurface,
    onSurface = EditorialTextPrimary,
    surfaceVariant = EditorialContainer,
    onSurfaceVariant = EditorialTextSecondary,
    outline = EditorialBorder
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Set dynamicColor default to false to showcase the Editorial Aesthetic palette
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
