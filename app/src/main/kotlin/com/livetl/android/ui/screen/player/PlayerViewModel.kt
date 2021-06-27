package com.livetl.android.ui.screen.player

import androidx.lifecycle.ViewModel
import com.livetl.android.data.chat.ChatFilterService
import com.livetl.android.data.chat.ChatService
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.ui.screen.player.composable.chat.EmojiCache
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.minusAssign
import com.livetl.android.util.plusAssign
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val streamService: StreamService,
    private val chatService: ChatService,
    private val chatFilterService: ChatFilterService,
    val emojiCache: EmojiCache,
    val prefs: PreferencesHelper,
) : ViewModel() {

    val messages = chatService.messages
    val filteredMessages = chatFilterService.messages

    var videoAttemptedRetries = 0
    var currentSecond: Float = 0f

    override fun onCleared() {
        chatService.stop()
        chatFilterService.stop()
        emojiCache.cache.evictAll()
    }

    fun getVideoId(urlOrId: String): String {
        return streamService.getVideoId(urlOrId)
    }

    suspend fun getStreamInfo(videoId: String): StreamInfo {
        return streamService.getStreamInfo(videoId)
    }

    suspend fun loadChat(videoId: String, isLive: Boolean) {
        chatFilterService.connect()
        chatService.connect(videoId, isLive)
    }

    fun seekTo(videoId: String, second: Long) {
        chatService.seekTo(videoId, second)
    }

    fun allowUser(author: MessageAuthor) {
        prefs.allowedUsers() += author.toPrefItem()
        prefs.blockedUsers() -= author.toPrefItem()
    }

    fun blockUser(author: MessageAuthor) {
        prefs.blockedUsers() += author.toPrefItem()
        prefs.allowedUsers() -= author.toPrefItem()
    }
}
