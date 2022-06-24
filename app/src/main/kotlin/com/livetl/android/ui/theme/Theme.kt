package com.livetl.android.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColors(
    primary = Color(0xFF1773BC),
    primaryVariant = Color(0xFF004D81),
    secondary = Color(0xFF1773BC),
    onPrimary = Color.White,
    onSecondary = Color.White,
)

@Composable
fun LiveTLTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = typography,
        content = content,
    )
}
