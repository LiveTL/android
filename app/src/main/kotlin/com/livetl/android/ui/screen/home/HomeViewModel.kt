package com.livetl.android.ui.screen.home

import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.FeedService
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.feed.StreamStatus
import com.livetl.android.ui.screen.home.tab.StreamsTabViewModel
import com.livetl.android.util.PreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val feedService: FeedService,
    val prefs: PreferencesHelper,
) : ViewModel() {

    val tabs = StreamStatus.values().map {
        it to StreamsTabViewModel(
            feedService = feedService,
            prefs = prefs,
            status = it,
        )
    }

    var showOpenVideoDialog by mutableStateOf(false)
    var openVideoUrl by mutableStateOf("")

    val sheetState = ModalBottomSheetState(ModalBottomSheetValue.Hidden)
    var sheetStream by mutableStateOf<Stream?>(null)

    fun showOpenVideoDialog() {
        showOpenVideoDialog = true
    }

    fun hideOpenVideoDialog() {
        showOpenVideoDialog = false
    }

    suspend fun showSheet(stream: Stream) {
        sheetStream = stream
        sheetState.animateTo(ModalBottomSheetValue.Expanded)
    }
}
