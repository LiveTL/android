package com.livetl.android.vm

import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.Feed
import com.livetl.android.data.feed.FeedService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedService: FeedService
) : ViewModel() {

    suspend fun getFeed(): Feed {
        return feedService.getFeed()
    }
}
