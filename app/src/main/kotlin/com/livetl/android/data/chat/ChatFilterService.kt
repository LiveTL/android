package com.livetl.android.data.chat

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatFilterService @Inject constructor(
    private val chatService: ChatService,
    private val chatFilterer: ChatFilterer,
) {

    private var job: Job? = null

    private val _messages = MutableStateFlow<ImmutableList<ChatMessage>>(persistentListOf())
    val messages: SharedFlow<ImmutableList<ChatMessage>>
        get() = _messages.asSharedFlow()

    suspend fun connect(videoId: String, isLive: Boolean) {
        // Clear out previous chat contents, just in case
        stop()

        chatService.connect(videoId, isLive)

        job = chatService.scope.launch {
            chatService.messages.collect {
                _messages.value = it.mapNotNull(chatFilterer::filterMessage).toImmutableList()
            }
        }
    }

    suspend fun seekTo(videoId: String, second: Long) {
        chatService.seekTo(videoId, second)
    }

    fun stop() {
        chatService.stop()
        job?.cancel()
        _messages.value = persistentListOf()
    }
}
