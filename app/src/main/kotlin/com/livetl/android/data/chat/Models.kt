package com.livetl.android.data.chat

import kotlinx.serialization.Serializable

sealed class ChatMessage {
    abstract val author: MessageAuthor
    abstract val content: String
    abstract val timestamp: Long

    data class RegularChat(
        override val author: MessageAuthor,
        override val content: String,
        override val timestamp: Long,
    ) : ChatMessage()

    data class SuperChat(
        override val author: MessageAuthor,
        override val content: String,
        override val timestamp: Long,
        val level: Level
    ) : ChatMessage() {
        enum class Level {
            BLUE,
            TEAL,
            GREEN,
            YELLOW,
            ORANGE,
            PINK,
            RED
        }
    }
}

data class MessageAuthor(
    val photoUrl: String,
    val name: String,
//    val badge: String,
)

@Serializable
data class YTChatMessages(
    val type: String,
    val messages: List<YTChatMessage>,
    val isReplay: Boolean,
)

@Serializable
data class YTChatMessage(
    val author: YTChatAuthor,
    val index: Int,
    val messages: List<YTChatMessageData>,
    val timestamp: Long,
    val showtime: Int,
)

@Serializable
data class YTChatAuthor(
    val name: String,
    val id: String,
    val types: List<String>,
)

@Serializable
data class YTChatMessageData(
    val type: String, // text: text, emote: src
    val text: String? = null,
    val src: String? = null,
)