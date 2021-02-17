package com.livetl.android.ui.screen.home.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.livetl.android.data.feed.Stream
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun Stream(
    stream: Stream,
    navigateToStream: (Stream) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToStream(stream) }
            .padding(8.dp)
    ) {
        CoilImage(
            data = stream.channel.photo,
            contentDescription = null,
            modifier = Modifier
                .width(48.dp)
                .aspectRatio(1F)
                .clip(CircleShape),
            onRequestCompleted = {}
        )
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
        ) {
            Text(stream.title, maxLines = 1)
            Text(stream.channel.name, maxLines = 1)
        }
    }
}