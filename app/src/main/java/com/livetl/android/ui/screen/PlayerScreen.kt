package com.livetl.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.livetl.android.ui.composable.VideoPlayer
import com.livetl.android.ui.theme.LiveTLTheme

@Composable
fun PlayerScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {
        var source by remember { mutableStateOf("https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }

        Column {
            val mediaPlayback = VideoPlayer(source)

            Button(onClick = {
                // Elephant Dream by Blender Foundation
                source = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            }) {
                Text("Another Video")
            }

            Row {
                IconButton(onClick = {
                    mediaPlayback.rewind(10_000)
                }) {
                    Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Rewind")
                }

                IconButton(onClick = {
                    mediaPlayback.playPause()
                }) {
                    Icon(imageVector = Icons.Filled.PlayArrow, contentDescription = "Play/pause")
                }

                IconButton(onClick = {
                    mediaPlayback.forward(10_000)
                }) {
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Fast forward")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LiveTLTheme {
        PlayerScreen()
    }
}