package com.cltf.aienglish.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AppColors.PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBBDEFB),
    secondary = AppColors.AccentTeal,
    onSecondary = Color.White,
    tertiary = AppColors.AccentOrange,
    onTertiary = Color.White,
    background = AppColors.Background,
    onBackground = AppColors.TextPrimary,
    surface = AppColors.Card,
    onSurface = AppColors.TextPrimary,
    surfaceContainerLow = Color(0xFFF2F2F7),
    surfaceContainerLowest = AppColors.Background,
    surfaceContainerHigh = AppColors.GroupedElevated,
    outline = AppColors.Divider,
    outlineVariant = Color(0xFFEEEEEE)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D47A1),
    primaryContainer = Color(0xFF1565C0),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF004D40),
    tertiary = Color(0xFFFFB74D),
    onTertiary = Color(0xFF4E342E),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    surfaceContainerLow = Color(0xFF1E1E1E),
    surfaceContainerLowest = Color(0xFF121212),
    surfaceContainerHigh = Color(0xFF2C2C2C),
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF383838)
)

@Composable
fun AIEnglishTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        content = content
    )
}
