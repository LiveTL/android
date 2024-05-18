package com.livetl.android.ui.screen.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livetl.android.data.feed.StreamStatus
import com.livetl.android.data.media.YouTubeMediaSessionService
import com.livetl.android.data.media.YouTubeSession
import com.livetl.android.data.stream.StreamRepository
import com.livetl.android.ui.screen.home.tab.StreamsTabViewModel
import com.livetl.android.util.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val streamRepository: StreamRepository,
    private val youTubeMediaSessionService: YouTubeMediaSessionService,
    val prefs: AppPreferences,
) : ViewModel() {
    val tabs =
        StreamStatus.entries.map {
            it to
                StreamsTabViewModel(
                    streamRepository = streamRepository,
                    prefs = prefs,
                    status = it,
                )
        }

    var youTubeSession by mutableStateOf<YouTubeSession?>(null)
    var showOpenVideoDialog by mutableStateOf(false)
    var openVideoUrl by mutableStateOf("")

    init {
        viewModelScope.launch {
            youTubeMediaSessionService.session
                .distinctUntilChanged()
                .collectLatest {
                    Timber.i("Current YouTube video: ${it?.videoId} / ${it?.title} / ${it?.position} / ${it?.playbackState}")
                    youTubeSession = it
                }
        }
    }

    fun showOpenVideoDialog() {
        showOpenVideoDialog = true
    }

    fun hideOpenVideoDialog() {
        showOpenVideoDialog = false
    }
}
