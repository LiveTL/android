package com.livetl.android.vm

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.Feed
import com.livetl.android.data.feed.FeedService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedService: FeedService
) : ViewModel() {

    var feed by mutableStateOf<Feed?>(null)

    suspend fun loadFeed() {
        feed = feedService.getFeed()
    }
}
