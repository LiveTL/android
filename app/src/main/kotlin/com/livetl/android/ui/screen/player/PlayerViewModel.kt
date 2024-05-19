package com.livetl.android.ui.screen.player

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.livetl.android.data.chat.ChatFilterService
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.media.YouTubeSession
import com.livetl.android.data.media.YouTubeSessionService
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.data.stream.VideoIdParser
import com.livetl.android.ui.screen.player.composable.chat.EmojiCache
import com.livetl.android.util.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val videoIdParser: VideoIdParser,
    private val streamService: StreamService,
    private val chatFilterService: ChatFilterService,
    private val youTubeSessionService: YouTubeSessionService,
    val emojiCache: EmojiCache,
    val prefs: AppPreferences,
) : ViewModel() {
    val state = MutableStateFlow(State())

    init {
        viewModelScope.launch {
            chatFilterService.messages
                .collectLatest { messages ->
                    state.update { it.copy(filteredMessages = messages) }
                }
        }

        viewModelScope.launch {
            prefs.tlScale().asFlow()
                .collectLatest { tlScale ->
                    state.update { it.copy(fontScale = tlScale) }
                }
        }

        youTubeSessionService.attach()
        viewModelScope.launch(Dispatchers.IO) {
            youTubeSessionService.session
                .filterNotNull()
                .debounce(2.seconds)
                .collectLatest { session ->
                    Timber.d(
                        "Current YouTube video: ${session.videoId} / ${session.title} / ${session.positionInMs} / ${session.playbackState}",
                    )
                    state.update { it.copy(youTubeSession = session) }

                    // Update chat progress based on playback state
                    if (!session.isLive && session.videoId != null && session.positionInMs != null) {
                        chatFilterService.seekTo(session.videoId, session.positionInMs / 1000)
                    }
                }
        }
    }

    override fun onCleared() {
        youTubeSessionService.detach()
        chatFilterService.stop()
        emojiCache.evictAll()
    }

    suspend fun loadStream(urlOrId: String) {
        try {
            val videoId = videoIdParser.getVideoId(urlOrId)
            val streamInfo = streamService.getStreamInfo(videoId)
            state.update { it.copy(streamInfo = streamInfo) }

            state.update { it.copy(chatState = ChatState.LOADING) }
            chatFilterService.connect(streamInfo.videoId, streamInfo.isLive)
            state.update { it.copy(chatState = ChatState.LOADED) }
        } catch (e: Throwable) {
            Timber.e(e)
            state.update { it.copy(chatState = ChatState.ERROR(e)) }
        }
    }

    @Immutable
    data class State(
        val chatState: ChatState = ChatState.LOADING,
        val filteredMessages: ImmutableList<ChatMessage> = persistentListOf(),
        val fontScale: Float = 1f,
        val streamInfo: StreamInfo? = null,
        // TODO: show message if playing video seems to have changed
        val youTubeSession: YouTubeSession? = null,
    )
}

sealed interface ChatState {
    data object LOADING : ChatState
    data object LOADED : ChatState
    data class ERROR(val error: Throwable) : ChatState
}
