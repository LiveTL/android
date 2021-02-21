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
//    val photoUrl: String,
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
) {
    fun toChatMessage(): ChatMessage {
        return ChatMessage.RegularChat(
            author = author.toMessageAuthor(),
            content = messages.joinToString("; ") { it.toChatMessageContent() },
            timestamp = timestamp,
        )
    }
}

@Serializable
data class YTChatAuthor(
    val name: String,
    val id: String,
    val types: List<String>,
) {
    fun toMessageAuthor(): MessageAuthor {
        return MessageAuthor(
            name = name
        )
    }
}

@Serializable
data class YTChatMessageData(
    val type: String,
    val text: String? = null,
    val src: String? = null,
) {
    fun toChatMessageContent(): String {
        return when (type) {
            "text" -> text!!
            "emote" -> src!!
            else -> throw Exception("Unknown YTChatMessageData type: $type")
        }
    }
}