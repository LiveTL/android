package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livetl.android.R
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.common.StreamThumbnailBackground

@Composable
fun StreamInfoPanel(streamInfo: StreamInfo?, modifier: Modifier = Modifier) {
    if (streamInfo == null) {
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = streamInfo.author,
                    style = MaterialTheme.typography.bodyMedium,
                )

                if (streamInfo.isLive) {
                    Text(
                        text = stringResource(R.string.live).uppercase(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(color = Color(0xE6CC0000))
                            .padding(horizontal = 2.dp),
                    )
                }
            }
        }
    }
}
