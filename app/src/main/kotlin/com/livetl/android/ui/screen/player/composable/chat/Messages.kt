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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.ChatMessageContent
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.ui.common.SymbolAnnotationType
import com.livetl.android.ui.common.textParser
import com.livetl.android.util.toDebugTimestampString
import com.livetl.android.util.toTimestampString

@Composable
fun MinimalMessage(
    modifier: Modifier = Modifier,
    message: ChatMessage,
    fontScale: Float = 1f,
    emojiCache: EmojiCache,
) {
    val text = buildAnnotatedString {
        CompositionLocalProvider(LocalAuthorNameColor provides LocalContentColor.current) {
            append(getAuthorName(message.author))
        }
        append(textParser(text = message.getTextContent(), parsedContentTypes = parsedContentTypes))
    }

    BasicText(
        modifier = modifier.chatPadding(),
        text = text,
        style = MaterialTheme.typography.body1.copy(
            color = LocalContentColor.current,
            fontSize = MaterialTheme.typography.body1.fontSize * fontScale,
        ),
        inlineContent = message.getEmojiInlineContent(emojiCache)
    )
}

@Composable
fun Message(
    modifier: Modifier = Modifier,
    message: ChatMessage,
    showTimestamp: Boolean = false,
    debugTimestamp: Boolean = false,
    fontScale: Float = 1f,
    emojiCache: EmojiCache,
) {
    val textColor = when (message) {
        is ChatMessage.RegularChat -> LocalContentColor.current
        is ChatMessage.SuperChat -> message.level.textColor
        is ChatMessage.NewMember -> message.textColor
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
        if (message is ChatMessage.NewMember) {
            append(stringResource(R.string.new_member))
        } else {
            append(
                textParser(text = message.getTextContent(), parsedContentTypes = parsedContentTypes)
            )
        }
    }

    BasicText(
        modifier = when (message) {
            is ChatMessage.RegularChat ->
                modifier.chatPadding()
            is ChatMessage.SuperChat ->
                modifier
                    .clip(ChatShape)
                    .background(color = message.level.backgroundColor)
                    .chatPadding()
            is ChatMessage.NewMember ->
                modifier
                    .clip(ChatShape)
                    .background(color = message.backgroundColor)
                    .chatPadding()
        },
        text = text,
        style = MaterialTheme.typography.body1.copy(
            color = LocalContentColor.current,
            fontSize = MaterialTheme.typography.body1.fontSize * fontScale,
        ),
        inlineContent = message.author.getPhotoInlineContent() +
            message.author.getBadgeInlineContent() +
            message.getEmojiInlineContent(emojiCache)
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
            letterSpacing = 0.4.sp
        )
    )
}

private fun ChatMessage.getEmojiInlineContent(emojiCache: EmojiCache) = content
    .filterIsInstance<ChatMessageContent.Emoji>()
    .distinctBy { it.id }
    .associate { it.id to emojiCache.get(it) }

private fun MessageAuthor.getPhotoInlineContent() = mapOf(
    photoUrl to InlineTextContent(
        placeholder = Placeholder(1.5.em, 1.em, PlaceholderVerticalAlign.Center),
        children = {
            Image(
                painter = rememberImagePainter(photoUrl),
                contentDescription = null,
                modifier = Modifier
                    .requiredWidth(16.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
            )
        }
    )
)

private fun MessageAuthor.getBadgeInlineContent() = when {
    membershipBadgeUrl != null -> mapOf(
        membershipBadgeUrl to InlineTextContent(
            placeholder = Placeholder(1.5.em, 1.em, PlaceholderVerticalAlign.Center),
            children = {
                Image(
                    painter = rememberImagePainter(membershipBadgeUrl),
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

val LocalAuthorNameColor = compositionLocalOf { Color.White }

private fun Modifier.chatPadding() = padding(horizontal = 8.dp, vertical = 4.dp)
private val ChatShape = RoundedCornerShape(4.dp)

private val parsedContentTypes = setOf(
    SymbolAnnotationType.LINK.name,
    SymbolAnnotationType.EMOJI.name,
)
