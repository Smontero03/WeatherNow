package com.example.weathernow.ui.theme


import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


private val LightColorScheme = lightColorScheme(
    primary = DarkGreen,
    secondary = ButtonBlue,
    background = LightGreen,
    surface = LightGray,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = DarkGreen,
    onSurface = DarkGreen
)

@Composable
fun WeatherNowTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}