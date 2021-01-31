package com.livetl.android.ui.screen.player.tab

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun DebugTab(
    setSource: (String) -> Unit
) {
    var customUrlOrId by remember { mutableStateOf("") }

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

    TextField(
        label = { Text("YouTube URL or ID") },
        value = customUrlOrId,
        onValueChange = {
            customUrlOrId = it
            setSource(it)
        },
    )
}
