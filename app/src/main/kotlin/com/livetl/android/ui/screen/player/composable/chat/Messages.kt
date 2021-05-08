package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.google.accompanist.coil.rememberCoilPainter
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.ChatMessageContent
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.ui.common.textParser
import com.livetl.android.util.toDebugTimestampString
import com.livetl.android.util.toTimestampString
import org.koin.androidx.compose.get

@Composable
fun MinimalMessage(
    modifier: Modifier = Modifier,
    message: ChatMessage,
    emojiCache: EmojiCache = get(),
) {
    val text = buildAnnotatedString {
        CompositionLocalProvider(LocalAuthorNameColor provides LocalContentColor.current) {
            append(getAuthorName(message.author))
        }
        append(textParser(message.getTextContent()))
    }

    BasicText(
        modifier = modifier.chatPadding(),
        text = text,
        style = MaterialTheme.typography.body1.copy(color = LocalContentColor.current),
        inlineContent = message.getEmoteInlineContent(emojiCache)
    )
}

@Composable
fun Message(
    modifier: Modifier = Modifier,
    message: ChatMessage,
    showTimestamp: Boolean = false,
    debugTimestamp: Boolean = false,
    emojiCache: EmojiCache = get(),
) {
    val textColor = when (message) {
        is ChatMessage.RegularChat -> LocalContentColor.current
        is ChatMessage.SuperChat -> message.level.textColor
    }

    val authorPicInlineContent = mapOf(
        message.author.photoUrl to InlineTextContent(
            placeholder = Placeholder(1.5.em, 1.em, PlaceholderVerticalAlign.Center),
            children = {
                Image(
                    painter = rememberCoilPainter(message.author.photoUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .requiredWidth(16.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape),
                )
            }
        )
    )

    val authorBadgeInlineContent = when {
        message.author.membershipBadgeUrl != null -> mapOf(
            message.author.membershipBadgeUrl!! to InlineTextContent(
                placeholder = Placeholder(1.5.em, 1.em, PlaceholderVerticalAlign.Center),
                children = {
                    Image(
                        painter = rememberCoilPainter(message.author.membershipBadgeUrl!!),
                        contentDescription = null,
                        modifier = Modifier
                            .requiredWidth(16.dp)
                            .aspectRatio(1f),
                    )
                }
            )
        )
        else -> emptyMap()
    }

    val text = buildAnnotatedString {
        if (showTimestamp) {
            val timestamp = when (debugTimestamp) {
                true -> message.timestamp.toDebugTimestampString()
                false -> message.timestamp.toTimestampString()
            }
            append(
                AnnotatedString(
                    text = " $timestamp ",
                    spanStyle = SpanStyle(
                        color = textColor.copy(alpha = ContentAlpha.medium),
                        fontSize = 12.sp,
                        letterSpacing = 0.4.sp
                    )
                )
            )
        }

        // Profile picture
        appendInlineContent(message.author.photoUrl, message.author.name)

        // Username
        CompositionLocalProvider(LocalAuthorNameColor provides textColor) {
            append(getAuthorName(message.author))
        }

        // Badge icon
        if (message.author.membershipRank != null) {
            appendInlineContent(
                message.author.membershipBadgeUrl!!,
                message.author.membershipRank!!
            )
        }

        // Superchat monetary amount
        if (message is ChatMessage.SuperChat) {
            append(
                AnnotatedString(
                    text = "${message.amount} ",
                    spanStyle = SpanStyle(
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                    )
                )
            )
        }

        // Actual chat message contents
        append(textParser(message.getTextContent()))
    }

    BasicText(
        modifier = when (message) {
            is ChatMessage.RegularChat ->
                modifier.chatPadding()
            is ChatMessage.SuperChat ->
                modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(color = message.level.backgroundColor)
                    .chatPadding()
        },
        text = text,
        style = MaterialTheme.typography.body1.copy(color = textColor),
        inlineContent = authorPicInlineContent + authorBadgeInlineContent + message.getEmoteInlineContent(emojiCache)
    )
}

@Composable
private fun getAuthorName(author: MessageAuthor): AnnotatedString {
    val color = when {
        author.isModerator || author.isVerified -> Color(0xFF5D84F1)
        author.isOwner -> Color(0xFFFED500)
        author.membershipRank != null -> Color(0xFF2BA640)
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
            letterSpacing = 0.4.sp
        )
    )
}

private fun ChatMessage.getEmoteInlineContent(emojiCache: EmojiCache): Map<String, InlineTextContent> {
    return content
        .filterIsInstance<ChatMessageContent.Emoji>()
        .distinctBy { it.id }
        .associate { it.id to emojiCache.get(it) }
}

val LocalAuthorNameColor = compositionLocalOf { Color.White }

private fun Modifier.chatPadding() = padding(horizontal = 8.dp, vertical = 4.dp)
