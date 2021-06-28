package com.livetl.android.data.chat

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatFilterService @Inject constructor(
    private val chatFilterer: ChatFilterer,
) {

    private var job: Job? = null

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: SharedFlow<List<ChatMessage>>
        get() = _messages.asSharedFlow()

    fun connect(chatService: ChatService) {
        // Clear out previous chat contents, just in case
        stop()

        job = chatService.scope.launch {
            chatService.messages.collect {
                _messages.value = it.mapNotNull(chatFilterer::filterMessage)
            }
        }
    }

    fun stop() {
        job?.cancel()
        _messages.value = emptyList()
    }
}
