package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.ChatMessageContent
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.ui.common.SymbolAnnotationType
import com.livetl.android.ui.common.textParser

@Composable
fun Message(message: ChatMessage, emojiCache: EmojiCache, modifier: Modifier = Modifier, fontScale: Float = 1f) {
    val textColor = when (message) {
        is ChatMessage.RegularChat -> LocalContentColor.current
        is ChatMessage.SuperChat -> message.level.textColor
        is ChatMessage.NewMember -> message.textColor
        is ChatMessage.MemberMilestone -> message.textColor
    }

    val text = buildAnnotatedString {
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

        if (message is ChatMessage.MemberMilestone) {
            append(
                AnnotatedString(
                    text = "${message.milestone} ",
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
            is ChatMessage.MemberMilestone ->
                modifier
                    .clip(ChatShape)
                    .background(color = message.backgroundColor)
                    .chatPadding()
        },
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            color = textColor,
            fontSize = MaterialTheme.typography.bodyMedium.fontSize * fontScale,
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
            letterSpacing = 0.4.sp,
        ),
    )
}

private fun ChatMessage.getEmojiInlineContent(emojiCache: EmojiCache) = content
    .filterIsInstance<ChatMessageContent.Emoji>()
    .distinctBy { it.id }
    .associate { it.id to emojiCache[it] }

private fun MessageAuthor.getPhotoInlineContent() = mapOf(
    photoUrl to InlineTextContent(
        placeholder = Placeholder(1.5.em, 1.em, PlaceholderVerticalAlign.Center),
        children = {
            Image(
                painter = rememberAsyncImagePainter(photoUrl),
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
                    painter = rememberAsyncImagePainter(membershipBadgeUrl),
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

private val LocalAuthorNameColor = compositionLocalOf { Color.White }

private fun Modifier.chatPadding() = padding(horizontal = 8.dp, vertical = 4.dp)
private val ChatShape = RoundedCornerShape(4.dp)

private val parsedContentTypes = setOf(
    SymbolAnnotationType.LINK.name,
    SymbolAnnotationType.EMOJI.name,
)


@Preview
@Composable
private fun RegularChatPreviews() {
    val author = MessageAuthor(
        id = "1",
        name = "Name",
    )

    Column {
        Message(
            message = ChatMessage.RegularChat(
                author = author,
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isModerator = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isVerified = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isOwner = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            emojiCache = EmojiCache(),
        )
    }
}

@Preview
@Composable
private fun SuperChatPreview() {
    Message(
        message = ChatMessage.SuperChat(
            author = MessageAuthor(
                id = "2",
                name = "Pekora Shachou",
            ),
            content = listOf(ChatMessageContent.Text("HA↑HA↓HA↑HA↓ PE↗KO↘PE↗KO↘ 😂")),
            timestamp = 1615001105,
            amount = "$100.00",
            level = ChatMessage.SuperChat.Level.RED,
        ),
        emojiCache = EmojiCache(),
    )
}

@Preview
@Composable
private fun NewMemberPreview() {
    Message(
        message = ChatMessage.NewMember(
            author = MessageAuthor(
                id = "3",
                name = "Ina Ina Ina",
            ),
            timestamp = 1615001105,
        ),
        emojiCache = EmojiCache(),
    )
}


@Preview
@Composable
private fun MemberMilestonePreview() {
    Message(
        message = ChatMessage.MemberMilestone(
            author = MessageAuthor(
                id = "4",
                name = "Rose",
            ),
            content = listOf(ChatMessageContent.Text("It's been 84 years...")),
            timestamp = 1615001105,
            milestone = "Member for 84 years",
        ),
        emojiCache = EmojiCache(),
    )
}
