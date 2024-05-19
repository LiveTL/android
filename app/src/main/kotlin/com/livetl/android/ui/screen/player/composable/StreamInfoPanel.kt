package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.ui.common.StreamThumbnailBackground

@Composable
fun StreamInfoPanel(streamInfo: StreamInfo?, modifier: Modifier = Modifier) {
    if (streamInfo == null) {
        LoadingIndicator()
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        streamInfo.thumbnail?.let {
            StreamThumbnailBackground(streamInfo.thumbnail)
        }

        Column(
            modifier = modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = streamInfo.title,
                style = MaterialTheme.typography.titleLarge,
            )

            Text(
                text = streamInfo.author,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
