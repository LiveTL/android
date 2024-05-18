package com.livetl.android.ui.screen.player

import androidx.lifecycle.ViewModel
import com.livetl.android.data.chat.ChatFilterService
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.data.stream.VideoIdParser
import com.livetl.android.ui.screen.player.composable.chat.EmojiCache
import com.livetl.android.util.AppPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val videoIdParser: VideoIdParser,
    private val streamService: StreamService,
    private val chatFilterService: ChatFilterService,
    val emojiCache: EmojiCache,
    val prefs: AppPreferences,
) : ViewModel() {

    val filteredMessages = chatFilterService.messages

    override fun onCleared() {
        chatFilterService.stop()
        emojiCache.evictAll()
    }

    fun getVideoId(urlOrId: String): String = videoIdParser.getVideoId(urlOrId)

    suspend fun getStreamInfo(videoId: String): StreamInfo = streamService.getStreamInfo(videoId)

    suspend fun loadChat(videoId: String, isLive: Boolean) {
        chatFilterService.connect(videoId, isLive)
    }

    fun seekTo(videoId: String, second: Long) {
        chatFilterService.seekTo(videoId, second)
    }
}
