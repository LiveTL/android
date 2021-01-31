package com.livetl.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.livetl.android.ui.composable.VideoPlayer
import com.livetl.android.ui.theme.LiveTLTheme
import com.livetl.android.util.getYouTubeVideoUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlayerScreen(urlOrId: String?) {
    val coroutineScope = rememberCoroutineScope()

    var source by remember { mutableStateOf(urlOrId) }

    fun setSource(url: String) {
        coroutineScope.launch {
            val ytUrl = getYouTubeVideoUrl(url)
            withContext(Dispatchers.Main) { source = ytUrl }
        }
    }

    Column {
        val mediaPlayback = VideoPlayer(
            sourceUrl = source,
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
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LiveTLTheme {
        PlayerScreen("")
    }
}