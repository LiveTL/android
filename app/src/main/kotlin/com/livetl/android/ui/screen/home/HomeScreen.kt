package com.livetl.android.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livetl.android.R
import com.livetl.android.data.feed.Feed
import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.feed.Stream
import com.livetl.android.di.get
import com.livetl.android.ui.screen.home.composable.Stream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    navigateToPlayer: (String) -> Unit,
    feedService: FeedService = get(),
) {
    val coroutineScope = rememberCoroutineScope()

    var feed by remember { mutableStateOf<Feed?>(null) }

    val navigateToStream = { stream: Stream -> navigateToPlayer(stream.yt_video_key) }

    coroutineScope.launch {
        val newFeed = feedService.getFeed()
        withContext(Dispatchers.Main) {
            feed = newFeed
        }
    }

    if (feed != null) {
        LazyColumn {
            streamItems(
                headingRes = R.string.live,
                streams = feed!!.live,
                sortByAscending = false,
                timestampFormatStringRes = R.string.started_streaming,
                timestampSupplier = { it.live_start },
                navigateToStream = navigateToStream
            )
            streamItems(
                headingRes = R.string.upcoming,
                streams = feed!!.upcoming,
                timestampSupplier = { it.live_schedule },
                navigateToStream = navigateToStream
            )
            streamItems(
                headingRes = R.string.archives,
                streams = feed!!.ended,
                sortByAscending = false,
                timestampFormatStringRes = R.string.streamed,
                timestampSupplier = { it.live_end },
                navigateToStream = navigateToStream
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.streamItems(
    @StringRes headingRes: Int,
    streams: List<Stream>,
    sortByAscending: Boolean = true,
    @StringRes timestampFormatStringRes: Int? = null,
    timestampSupplier: (Stream) -> String?,
    navigateToStream: (Stream) -> Unit,
) {
    if (streams.isNotEmpty()) {
        stickyHeader {
            Surface(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(headingRes),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.h6,
                )
            }
        }

        val sortedStreams = when (sortByAscending) {
            true -> streams.sortedBy(timestampSupplier)
            false -> streams.sortedByDescending(timestampSupplier)
        }

        items(sortedStreams) { stream ->
            Stream(stream, timestampFormatStringRes, timestampSupplier, navigateToStream)
        }
    }
}