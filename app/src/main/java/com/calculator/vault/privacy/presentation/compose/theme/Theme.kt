package com.calculator.vault.privacy.presentation.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8AB4FF),
    secondary = Color(0xFF7DD3FC),
    tertiary = Color(0xFFC4B5FD),
    background = Color(0xFF0B0B0F),
    surface = Color(0xFF15151C),
    surfaceVariant = Color(0xFF1E1E28),
    onBackground = Color(0xFFF5F5F7),
    onSurface = Color(0xFFF5F5F7),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1A73E8),
    secondary = Color(0xFF0284C7),
    tertiary = Color(0xFF7C3AED),
    background = Color(0xFFF7F7FA),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFF0F1F6),
    onBackground = Color(0xFF111827),
    onSurface = Color(0xFF111827),
)

@Composable
fun PrivacySpaceTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = PrivacyTypography,
        content = content,
    )
}
