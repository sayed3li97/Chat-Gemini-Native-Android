package com.example.chatwitgemini.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF121212), // Very Dark Gray (Background)
    secondary = Color(0xFF2D2D2D), // Darker Gray (Surfaces)
    tertiary = Color(0xFF00E0FF), // Electric Cyan (Accent - slightly softened)
    background = Color(0xFF121212), // Very Dark Gray Background (repeat for clarity)
    surface = Color(0xFF2D2D2D), // Darker Gray Surface (repeat)
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.Black, // Or very dark gray for text on Cyan
    onBackground = Color.White,
    onSurface = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6200EE), // Example: a shade of blue
    secondary = Color(0xFF03DAC5),
    tertiary = Color(0xFFBB86FC),
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

@Composable
fun ChatwitgeminiTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}