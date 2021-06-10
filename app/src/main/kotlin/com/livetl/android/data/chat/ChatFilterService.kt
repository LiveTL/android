package com.livetl.android.data.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class ChatFilterService @Inject constructor(
    private val chatService: ChatService,
    private val chatFilterer: ChatFilterer,
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>>
        get() = _messages

    fun connect() {
        // Clear out previous chat contents, just in case
        stop()

        chatService.messages
            .onEach {
                _messages.value = it.mapNotNull(chatFilterer::filterMessage)
            }
            .launchIn(scope)
    }

    fun stop() {
        _messages.value = emptyList()
    }
}
