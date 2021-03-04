package com.livetl.android.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFF0094F7),
    primaryVariant = Color(0xFF004D81),
    secondary = Color(0xFF4CAF50),
)

@Composable
fun LiveTLTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
