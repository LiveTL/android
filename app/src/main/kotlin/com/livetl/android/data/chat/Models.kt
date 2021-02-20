package com.livetl.android.data.chat

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