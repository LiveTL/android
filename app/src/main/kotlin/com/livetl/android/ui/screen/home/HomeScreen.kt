package com.livetl.android.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livetl.android.R
import com.livetl.android.data.feed.Feed
import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.feed.Stream
import com.livetl.android.ui.core.SwipeToRefreshLayout
import com.livetl.android.ui.screen.home.composable.Stream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get

@Composable
fun HomeScreen(
    navigateToPlayer: (String) -> Unit,
    navigateToAbout: () -> Unit,
    feedService: FeedService = get(),
) {
    val coroutineScope = rememberCoroutineScope()

    var refreshingFeed by rememberSaveable { mutableStateOf(false) }
    var feed by rememberSaveable { mutableStateOf<Feed?>(null) }

    val navigateToStream = { stream: Stream -> navigateToPlayer(stream.yt_video_key) }

    fun refreshFeed() {
        coroutineScope.launch {
            refreshingFeed = true
            val newFeed = feedService.getFeed()
            withContext(Dispatchers.Main) {
                feed = newFeed
                refreshingFeed = false
            }
        }
    }

    DisposableEffect(Unit) {
        refreshFeed()

        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.app_name))
                },
                actions = {
                    IconButton(onClick = { refreshFeed() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                    IconButton(onClick = { navigateToAbout() }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = stringResource(R.string.about)
                        )
                    }
                },
            )
        }
    ) {
        SwipeToRefreshLayout(
            modifier = Modifier.fillMaxWidth(),
            refreshingState = refreshingFeed,
            onRefresh = { refreshFeed() },
            refreshIndicator = {
                Surface(elevation = 10.dp, shape = CircleShape) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(36.dp)
                            .padding(8.dp),
                        strokeWidth = 2.dp,
                    )
                }
            },
        ) {
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
            }
        }
    }
}

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
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 18.sp,
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
