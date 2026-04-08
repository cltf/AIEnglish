package com.cltf.aienglish.ui.theme

import androidx.compose.material3.MaterialTheme
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

@Composable
fun AIEnglishTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
