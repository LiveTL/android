package com.livetl.android.ui.screen.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
            streamItems("Live", feed!!.live, navigateToStream)
            streamItems("Upcoming", feed!!.upcoming, navigateToStream)
            streamItems("Archives", feed!!.ended, navigateToStream)
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
        item { Text(heading) }
        items(streams) { Stream(it, navigateToStream) }
    }
}