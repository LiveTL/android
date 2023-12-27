package com.livetl.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette =
    darkColorScheme(
        primary = Color(0xFF1773BC),
        primaryContainer = Color(0xFF004D81),
        secondary = Color(0xFF1773BC),
        onPrimary = Color.White,
        onSecondary = Color.White,
    )

@Composable
fun LiveTLTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorPalette,
        typography = typography,
        content = content,
    )
}
