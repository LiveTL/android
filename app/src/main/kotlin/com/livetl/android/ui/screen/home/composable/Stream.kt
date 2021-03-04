package com.livetl.android.ui.screen.home.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livetl.android.R
import com.livetl.android.data.feed.Channel
import com.livetl.android.data.feed.Stream
import com.livetl.android.util.toDate
import com.livetl.android.util.toRelativeString
import dev.chrisbanes.accompanist.coil.CoilImage

@Composable
fun Stream(
    stream: Stream,
    @StringRes timestampFormatStringRes: Int?,
    timestampSupplier: (Stream) -> String?,
    navigateToStream: (Stream) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToStream(stream) }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .requiredHeight(48.dp)
    ) {
        CoilImage(
            data = stream.channel.photo,
            contentDescription = null,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(CircleShape),
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            val titleOffset = when (stream.title.startsWith('【')) {
                true -> 0.dp
                false -> 8.dp
            }
            Text(
                stream.title,
                modifier = Modifier.padding(start = titleOffset),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Row {
                Text(
                    stream.channel.name,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                timestampSupplier(stream)?.let {
                    val relativeDateString = it.toDate().toRelativeString()
                    val timestampFormatString =
                        timestampFormatStringRes?.let { res -> stringResource(res) }
                    val timestampString =
                        timestampFormatString?.format(relativeDateString) ?: relativeDateString
                    Text(
                        timestampString,
                        style = MaterialTheme.typography.caption,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun StreamPreview() {
    Column {
        Stream(
            stream = Stream(
                yt_video_key = "123",
                title = "Some very, extremely, quite long long long title for testing wow",
                live_schedule = "2020-01-01T00:00:00.000Z",
                live_start = "2020-01-01T00:01:12.000Z",
                channel = Channel(
                    name = "Wow Such YouTube Channel",
                    photo = "",
                )
            ),
            timestampFormatStringRes = R.string.started_streaming,
            timestampSupplier = { "2020-01-01T00:01:12.000Z" },
            navigateToStream = {},
        )
        Stream(
            stream = Stream(
                yt_video_key = "123",
                title = "Short title",
                live_schedule = "2030-01-01T00:01:12.000Z",
                channel = Channel(
                    name = "Smol Ch",
                    photo = "",
                )
            ),
            timestampFormatStringRes = null,
            timestampSupplier = { "2030-01-01T00:01:12.000Z" },
            navigateToStream = {},
        )
    }
}