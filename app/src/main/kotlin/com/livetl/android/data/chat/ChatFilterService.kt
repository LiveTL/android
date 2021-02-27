package com.livetl.android.data.chat

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatFilterService(chatService: ChatService) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>>
        get() = _messages

    init {
        chatService.messages
            .onEach {
                _messages.value = it.filter(this::shouldFilter)
            }
            .launchIn(scope)
    }

    // TODO: proper filtering
    private fun shouldFilter(message: ChatMessage): Boolean {
        return message.getTextContent()
            .startsWith("[EN]")
    }
}