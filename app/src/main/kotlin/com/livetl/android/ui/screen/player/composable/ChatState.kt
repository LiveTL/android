package com.livetl.android.ui.screen.player.composable

import androidx.compose.runtime.mutableStateListOf
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.MessageAuthor
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

    private fun addMockMessages() {
        GlobalScope.launch(Dispatchers.IO) {
            for (i in 1..500) {
                delay(500)
                _messages.add(ChatMessage.RegularChat(
                    MessageAuthor("url", "Author #$i"),
                    "Message #$i",
                    0
                ))
            }
        }
    }
}
