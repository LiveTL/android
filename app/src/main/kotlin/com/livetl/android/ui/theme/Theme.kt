package com.livetl.android.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
        primary = purple200,
        primaryVariant = purple700,
        secondary = teal200
)

@Composable
fun LiveTLTheme(content: @Composable() () -> Unit) {
    MaterialTheme(
            colors = DarkColorPalette,
            typography = typography,
            shapes = shapes,
            content = content
    )
}