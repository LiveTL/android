package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.ChatMessageContent
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.ui.common.SymbolAnnotationType
import com.livetl.android.ui.common.textParser

@Composable
fun ChatMessage(message: ChatMessage, emojiCache: EmojiCache, modifier: Modifier = Modifier, fontScale: Float = 1f) {
    val text = buildAnnotatedString {
        CompositionLocalProvider(LocalAuthorNameColor provides LocalContentColor.current) {
            append(getAuthorName(message.author))
        }
        append(textParser(text = message.getTextContent(), parsedContentTypes = parsedContentTypes))
    }

    BasicText(
        modifier = modifier
            .fillMaxWidth()
            .chatPadding(),
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = LocalContentColor.current,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontScale,
        ),
        inlineContent = message.getEmojiInlineContent(emojiCache),
    )
}

@Composable
private fun getAuthorName(author: MessageAuthor): AnnotatedString {
    val color = when {
        author.isModerator || author.isVerified -> Color(0xFF5D84F1)
        author.isOwner -> Color(0xFFFED500)
        author.membershipRank != null && !author.isNewMember -> Color(0xFF2BA640)
        else -> LocalAuthorNameColor.current.copy(alpha = ContentAlpha.medium)
    }

    val name = when {
        author.isVerified -> author.name + " ✓"
        else -> author.name
    }

    return AnnotatedString(
        text = "$name ",
        spanStyle = SpanStyle(
            color = color,
            fontSize = 12.sp,
            letterSpacing = 0.4.sp,
        ),
    )
}

private fun ChatMessage.getEmojiInlineContent(emojiCache: EmojiCache) = content
    .filterIsInstance<ChatMessageContent.Emoji>()
    .distinctBy { it.id }
    .associate { it.id to emojiCache[it] }

private val LocalAuthorNameColor = compositionLocalOf { Color.White }

private fun Modifier.chatPadding() = padding(horizontal = 8.dp, vertical = 4.dp)

private val parsedContentTypes = setOf(
    SymbolAnnotationType.LINK.name,
    SymbolAnnotationType.EMOJI.name,
)
