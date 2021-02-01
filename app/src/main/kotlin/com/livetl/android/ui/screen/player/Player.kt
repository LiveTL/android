package com.livetl.android.ui.screen.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.livetl.android.model.Stream
import com.livetl.android.ui.composable.VideoPlayer

@Composable
fun Player(stream: Stream?) {
    Column {
        val mediaPlayback = VideoPlayer(
            videoSourceUrl = stream?.videoUrl,
            audioSourceUrl = stream?.audioUrl,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9F)
        )

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