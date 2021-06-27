package com.livetl.android.ui.screen.home

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.Feed
import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.feed.Stream
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val feedService: FeedService
) : ViewModel() {

    var feed by mutableStateOf<Feed?>(null)

    val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var sheetStream by mutableStateOf<Stream?>(null)

    suspend fun loadFeed() {
        feed = feedService.getFeed()
    }

    suspend fun showSheet(stream: Stream) {
        sheetStream = stream
        sheetState.animateTo(ModalBottomSheetValue.Expanded)
    }
}
