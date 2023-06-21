package com.example.notes.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val AppColorPalette = darkColors(
    primary = BlackShade,
    secondary = White
)

@Composable
fun NotesTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = AppColorPalette,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}