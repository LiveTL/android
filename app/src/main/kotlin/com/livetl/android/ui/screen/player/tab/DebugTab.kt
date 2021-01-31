package com.livetl.android.ui.screen.player.tab

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun DebugTab(
    setSource: (String) -> Unit
) {
    Row {
        Button(onClick = { setSource("https://www.youtube.com/watch?v=nJgjiil5lz8") }) {
            Text("Sample 1")
        }
        Button(onClick = { setSource("https://www.youtube.com/watch?v=W8hTq_l7-AQ") }) {
            Text("Sample 2")
        }
        Button(onClick = { setSource("https://www.youtube.com/watch?v=rVFumg4ECVY") }) {
            Text("Sample 3")
        }
    }
}
