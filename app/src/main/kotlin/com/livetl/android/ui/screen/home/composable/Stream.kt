package com.livetl.android.ui.screen.home.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
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
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigateToStream(stream) }
            .padding(8.dp)
    ) {
        val (photo, title, channel, timestamp) = createRefs()

        CoilImage(
            data = stream.channel.photo,
            contentDescription = null,
            modifier = Modifier
                .requiredWidth(48.dp)
                .aspectRatio(1F)
                .clip(CircleShape)
                .constrainAs(photo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
        )
        Text(
            stream.title,
            modifier = Modifier
                .constrainAs(title) {
                    val offset = when (stream.title.startsWith('【')) {
                        true -> 0.dp
                        false -> 8.dp
                    }

                    top.linkTo(parent.top)
                    linkTo(
                        start = photo.end,
                        end = parent.end,
                        startMargin = offset,
                        bias = 0f
                    )
                },
            // TODO: why isn't this actually limited to the parent's width?
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            stream.channel.name,
            modifier = Modifier
                .constrainAs(channel) {
                    bottom.linkTo(parent.bottom)
                    linkTo(
                        start = photo.end,
                        end = timestamp.start,
                        startMargin = 8.dp,
                        bias = 0f
                    )
                },
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        timestampSupplier(stream)?.let {
            val relativeDateString = it.toDate().toRelativeString()
            val timestampFormatString = timestampFormatStringRes?.let { res -> stringResource(res) }
            val timestampString = timestampFormatString?.format(relativeDateString) ?: relativeDateString
            Text(
                timestampString,
                modifier = Modifier
                    .constrainAs(timestamp) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                style = MaterialTheme.typography.caption,
            )
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