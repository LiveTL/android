package com.livetl.android.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livetl.android.model.Stream
import com.livetl.android.ui.composable.VideoPlayer
import com.livetl.android.ui.theme.LiveTLTheme
import com.livetl.android.util.getYouTubeStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlayerScreen(urlOrId: String?) {
    val context = AmbientContext.current
    val coroutineScope = rememberCoroutineScope()

    var source by remember { mutableStateOf(urlOrId) }
    var stream by remember { mutableStateOf<Stream?>(null) }

    fun setSource(url: String) {
        coroutineScope.launch {
            val newStream = getYouTubeStream(context, url)
            withContext(Dispatchers.Main) {
                source = newStream.videoUrl
                stream = newStream
            }
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

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        ) {
            if (stream != null) {
                Text(
                    text = stream!!.title,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    text = stream!!.author,
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(
                    text = "Live: ${stream!!.isLive}",
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                Text(text = stream!!.shortDescription)
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