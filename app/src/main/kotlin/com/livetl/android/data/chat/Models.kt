package com.livetl.android.data.chat

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastMap
import kotlinx.serialization.Serializable

sealed class ChatMessage {
    abstract val author: MessageAuthor
    abstract val content: List<ChatMessageContent>
    abstract val timestamp: Long

    data class RegularChat(
        override val author: MessageAuthor,
        override val content: List<ChatMessageContent>,
        override val timestamp: Long,
    ) : ChatMessage()

    // TODO: handle super stickers
    data class SuperChat(
        override val author: MessageAuthor,
        override val content: List<ChatMessageContent>,
        override val timestamp: Long,
        val amount: String,
        val level: Level,
    ) : ChatMessage() {
        enum class Level(val backgroundColor: Color, val textColor: Color) {
            BLUE(Color(0xFF1565BF), Color.White),
            LIGHT_BLUE(Color(0xFF00E4FE), Color.Black),
            TURQUOISE(Color(0xFF1CE9B6), Color.Black),
            YELLOW(Color(0xFFFFCA28), Color.Black),
            ORANGE(Color(0xFFF57C00), Color.White),
            PINK(Color(0xFFE91E63), Color.White),
            RED(Color(0xFFE62117), Color.White)
        }
    }

    fun getTextContent(): String {
        return content
            .joinToString("") { it.toString() }
            .trim()
    }
}

sealed class ChatMessageContent {
    data class Text(val text: String) : ChatMessageContent() {
        override fun toString() = text
    }
    data class Emoji(val id: String, val src: String) : ChatMessageContent() {
        override fun toString() = id
    }
}

data class MessageAuthor(
    val id: String,
    val name: String,
    val photoUrl: String,
    val isModerator: Boolean = false,
    val isVerified: Boolean = false,
    val isOwner: Boolean = false,
    val membershipRank: String? = null,
    val membershipBadgeUrl: String? = null,
)

enum class TranslatedLanguage(val id: String) {
    ENGLISH("en"),
    JAPANESE("jp"),
    SPANISH("es"),
    INDONESIAN("id"),
    KOREAN("kr"),
    CHINESE("zh"),
    RUSSIAN("ru"),
    FRENCH("fr"),
    ;

    companion object {
        fun fromId(id: String): TranslatedLanguage? {
            return values().find { it.id == id.toLowerCase() }
        }
    }
}

@Serializable
data class YTChatMessages(
    val messages: List<YTChatMessage>,
    val isReplay: Boolean,
)

@Serializable
data class YTChatMessage(
    val author: YTChatAuthor,
    val messages: List<YTChatMessageData>,
    val timestamp: Long,
    val delay: Long? = null,
    val superchat: YTSuperChat? = null,
) {
    fun toChatMessage(): ChatMessage {
        return if (superchat != null) {
            ChatMessage.SuperChat(
                author = author.toMessageAuthor(),
                content = messages.fastMap { it.toChatMessageContent() },
                timestamp = timestamp,
                amount = superchat.amount,
                level = ChatMessage.SuperChat.Level.valueOf(superchat.color)
            )
        } else {
            ChatMessage.RegularChat(
                author = author.toMessageAuthor(),
                content = messages.fastMap { it.toChatMessageContent() },
                timestamp = timestamp,
            )
        }
    }
}

@Serializable
data class YTChatAuthor(
    val name: String,
    val id: String,
    val photo: String,
    val isModerator: Boolean,
    val isVerified: Boolean,
    val isOwner: Boolean,
    val membershipBadge: YTChatMembershipBadge?,
) {
    fun toMessageAuthor(): MessageAuthor {
        return MessageAuthor(
            id = id,
            name = name,
            photoUrl = photo,
            isModerator = isModerator,
            isVerified = isVerified,
            isOwner = isOwner,
            membershipRank = membershipBadge?.name,
            membershipBadgeUrl = membershipBadge?.thumbnailSrc,
        )
    }
}

@Serializable
data class YTChatMembershipBadge(
    val name: String,
    val thumbnailSrc: String,
)

@Serializable
data class YTChatMessageData(
    val type: String,
    val text: String? = null,
    val emojiId: String? = null,
    val emojiSrc: String? = null,
) {
    fun toChatMessageContent(): ChatMessageContent {
        return when (type) {
            "text" -> ChatMessageContent.Text(text!!)
            "emoji" -> ChatMessageContent.Emoji(emojiId!!, emojiSrc!!)
            else -> throw Exception("Unknown YTChatMessageData type: $type")
        }
    }
}

@Serializable
data class YTSuperChat(
    val amount: String,
    val color: String,
)
