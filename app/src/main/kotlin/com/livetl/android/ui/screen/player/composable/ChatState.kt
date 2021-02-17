package com.livetl.android.ui.screen.player.composable

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChatState {
    private val _messages: MutableList<ChatMessage> = mutableStateListOf()
    val messages: List<ChatMessage> = _messages

    suspend fun connect(videoId: String, currentSecond: Long) {
        // TODO
        addMockMessages()
    }

    suspend fun seekTo(currentSecond: Long) {
        // TODO
        addMockMessages()
    }

    suspend fun setState(state: PlayerState) {
        // TODO
        addMockMessages()
    }

    private fun addMockMessages() {
        GlobalScope.launch(Dispatchers.IO) {
            for (i in 1..500) {
                delay(500)
                _messages.add(ChatMessage("Message #$i"))
            }
        }
    }
}

@Immutable
data class ChatMessage(
//    val author: String,
    val content: String,
//    val timestamp: String,
)
