package com.livetl.android.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.livetl.android.R
import com.livetl.android.data.feed.Feed
import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.feed.Stream
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

    val refreshingFeed = rememberSwipeRefreshState(false)
    var feed by rememberSaveable { mutableStateOf<Feed?>(null) }

    val navigateToStream = { stream: Stream -> navigateToPlayer(stream.yt_video_key) }

    fun refreshFeed() {
        coroutineScope.launch {
            refreshingFeed.isRefreshing = true
            val newFeed = feedService.getFeed()
            withContext(Dispatchers.Main) {
                feed = newFeed
                refreshingFeed.isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshFeed()
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
        SwipeRefresh(
            modifier = Modifier.fillMaxWidth(),
            state = refreshingFeed,
            onRefresh = { refreshFeed() },
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
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.surface.copy(alpha = 0.9f),
            ) {
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
            Stream(Modifier, stream, timestampFormatStringRes, timestampSupplier, navigateToStream)
        }
    }
}
