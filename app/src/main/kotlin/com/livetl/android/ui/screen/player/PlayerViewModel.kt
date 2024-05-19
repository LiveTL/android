package com.livetl.android.ui.screen.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livetl.android.data.chat.ChatFilterService
import com.livetl.android.data.media.YouTubeSession
import com.livetl.android.data.media.YouTubeSessionService
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.data.stream.VideoIdParser
import com.livetl.android.ui.screen.player.composable.chat.EmojiCache
import com.livetl.android.util.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val videoIdParser: VideoIdParser,
    private val streamService: StreamService,
    private val chatFilterService: ChatFilterService,
    private val youTubeSessionService: YouTubeSessionService,
    val emojiCache: EmojiCache,
    val prefs: AppPreferences,
) : ViewModel() {

    // TODO: show message if playing video seems to have changed
    var youTubeSession by mutableStateOf<YouTubeSession?>(null)
    val filteredMessages = chatFilterService.messages

    init {
        youTubeSessionService.attach()

        viewModelScope.launch(Dispatchers.IO) {
            youTubeSessionService.session
                .distinctUntilChanged()
                .filterNotNull()
                .collectLatest {
                    Timber.i(
                        "Current YouTube video: ${it.videoId} / ${it.title} / ${it.positionInMs} / ${it.playbackState}",
                    )
                    youTubeSession = it

                    // Update chat progress based on playback state
                    if (!it.isLive && it.videoId != null && it.positionInMs != null) {
                        chatFilterService.seekTo(it.videoId, it.positionInMs / 1000)
                    }
                }
        }
    }

    override fun onCleared() {
        youTubeSessionService.detach()
        chatFilterService.stop()
        emojiCache.evictAll()
    }

    suspend fun loadStream(urlOrId: String): StreamInfo {
        val videoId = videoIdParser.getVideoId(urlOrId)
        return streamService.getStreamInfo(videoId)
    }

    suspend fun loadChat(videoId: String, isLive: Boolean) {
        chatFilterService.connect(videoId, isLive)
    }
}
