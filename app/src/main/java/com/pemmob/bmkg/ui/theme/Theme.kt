package com.pemmob.bmkg.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenDarkPrimary,
    onPrimary = Color.Black,
    surface = DarkCard,
    onSurface = Color(0xFFE0E0E0),
    background = DarkBackground,
    onBackground = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1E2824),
    onSurfaceVariant = Color(0xFFB0B0B0)
)

private val LightColorScheme = lightColorScheme(
    primary = ForestGreenPrimary,
    onPrimary = Color.White,
    surface = CardLight,
    onSurface = Color(0xFF1A1A1A),
    background = SurfaceLight,
    onBackground = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFE8ECE9),
    onSurfaceVariant = Color(0xFF555555)
)

@Composable
fun BMKGTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
