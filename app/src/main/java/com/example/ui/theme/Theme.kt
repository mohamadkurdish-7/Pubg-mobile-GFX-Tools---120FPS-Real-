package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCyber,
    onPrimary = OnPrimaryCyber,
    primaryContainer = Color(0xFF004D56),
    onPrimaryContainer = Color(0xFF80F2FF),
    secondary = SecondaryAmber,
    onSecondary = OnSecondaryAmber,
    secondaryContainer = Color(0xFF5A3E00),
    onSecondaryContainer = Color(0xFFFFDF9E),
    tertiary = ToxicGreen,
    onTertiary = Color(0xFF00391A),
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = DarkNavyBorder,
    error = DangerRed,
    onError = Color.White
)

@Composable
fun BattleGfxTheme(
    darkTheme: Boolean = true, // Default to gaming dark theme for authentic experience
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
