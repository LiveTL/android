package com.livetl.android.ui.screen.home.tab

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.feed.StreamStatus
import com.livetl.android.data.stream.StreamRepository
import com.livetl.android.util.AppPreferences

class StreamsTabViewModel(
    private val streamRepository: StreamRepository,
    private val prefs: AppPreferences,
    private val status: StreamStatus,
) : ViewModel() {
    var isRefreshing by mutableStateOf(false)
    var streams by mutableStateOf<List<Stream>>(emptyList())

    suspend fun loadStreams() {
        streams = streamRepository.getStreams(prefs.feedOrganization().get(), status)
    }
}
