package com.livetl.android.ui.screen.home

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.livetl.android.BuildConfig
import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.feed.model.Feed
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

    coroutineScope.launch {
        val newFeed = feedService.getFeed()
        withContext(Dispatchers.Main) {
            feed = newFeed
        }
    }

    LazyColumn {
        if (feed != null) {
            item { Text("Live") }
            items(feed!!.live) { Stream(it) }

            item { Text("Upcoming") }
            items(feed!!.upcoming) { Stream(it) }

            item { Text("Archives") }
            items(feed!!.ended) { Stream(it) }
        }

        if (BuildConfig.DEBUG) {
            item { TestStreams(navigateToStream = { navigateToPlayer(it) }) }
        }
    }
}