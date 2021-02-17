package com.livetl.android.ui.screen.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    feedService: FeedService = get()
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

    LazyColumn {
        if (feed != null) {
            streamItems("Live", feed!!.live.sortedBy { it.live_start }, navigateToStream)
            streamItems("Upcoming", feed!!.upcoming.sortedBy { it.live_schedule }, navigateToStream)
            streamItems("Archives", feed!!.ended.sortedByDescending { it.live_end }, navigateToStream)
        }

//        if (BuildConfig.DEBUG) {
//            item { TestStreams(navigateToStream = { navigateToPlayer(it) }) }
//        }
    }
}

private fun LazyListScope.streamItems(
    heading: String,
    streams: List<Stream>,
    navigateToStream: (Stream) -> Unit,
) {
    if (streams.isNotEmpty()) {
        item {
            Text(
                text = heading,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.h6,
            )
        }

        items(streams) { Stream(it, navigateToStream) }
    }
}