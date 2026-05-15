package com.example.nammasantheledgerapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonGreen,
    secondary = NeonBlue,
    tertiary = NeonPink,
    background = PureBlack,
    surface = DarkContainer,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = NeonGreen, // Background text in Neon Green for high contrast
    onSurface = NeonBlue,      // Surface text in Neon Blue
    surfaceVariant = DarkButton,
    onSurfaceVariant = NeonBlue
)

private val LightColorScheme = lightColorScheme(
    primary = BrightGreen,
    secondary = BrightBlue,
    tertiary = BrightPink,
    background = LightBg,
    surface = LightSurface,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun NammaSantheTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
