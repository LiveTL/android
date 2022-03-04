package com.livetl.android.ui.screen.home.tab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.feed.StreamStatus

class StreamsTabViewModel(
    private val feedService: FeedService,
    private val organization: String,
    private val status: StreamStatus,
) : ViewModel() {

    var streams by mutableStateOf<List<Stream>>(emptyList())

    suspend fun loadStreams() {
        streams = feedService.getFeed(organization, status)
    }
}