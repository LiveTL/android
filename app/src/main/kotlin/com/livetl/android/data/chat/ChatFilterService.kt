package com.livetl.android.data.chat

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

class ChatFilterService @Inject constructor(
    private val chatService: ChatService,
    private val chatFilterer: ChatFilterer,
) {

    private var job: Job? = null

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: SharedFlow<List<ChatMessage>>
        get() = _messages.shareIn(
            scope = chatService.scope,
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    fun connect() {
        // Clear out previous chat contents, just in case
        stop()

        job = chatService.messages
            .onEach {
                _messages.value = it.mapNotNull(chatFilterer::filterMessage)
            }
            .launchIn(chatService.scope)
    }

    fun stop() {
        job?.cancel()
        _messages.value = emptyList()
    }
}
