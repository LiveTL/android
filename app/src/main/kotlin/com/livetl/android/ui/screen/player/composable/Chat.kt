package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.ChatMessageContent
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.di.get
import com.livetl.android.ui.messageFormatter
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.collectAsState
import com.livetl.android.util.toTimestampString
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.launch

@Composable
fun Chat(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    minimalMode: Boolean = false,
    showJumpToBottomButton: Boolean = false,
    prefs: PreferencesHelper = get(),
) {
    val scope = rememberCoroutineScope()

    val showTimestamp by prefs.showTimestamps().collectAsState()

    val scrollState = rememberLazyListState()
    var isScrolledToBottom by remember { mutableStateOf(true) }
    var _messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }

    fun checkIfAtBottom(): Boolean {
        if (_messages.isEmpty()) {
            return true
        }

        val visibleItems = scrollState.layoutInfo.visibleItemsInfo
        return visibleItems.lastOrNull()?.index == _messages.lastIndex
    }

    fun scrollToBottom() {
//        if (isScrolledToBottom && _messages.isNotEmpty()) {
        if (_messages.isNotEmpty()) {
            scope.launch {
                scrollState.scrollToItem(_messages.lastIndex, 0)
            }
        }
    }

    DisposableEffect(messages) {
        isScrolledToBottom = checkIfAtBottom()
        _messages = messages

        scrollToBottom()

        onDispose { }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        state = scrollState,
    ) {
        items(_messages) { message ->
            when (minimalMode) {
                true -> MinimalMessage(message)
                false -> Message(message, showTimestamp)
            }
        }
    }

    if (showJumpToBottomButton) {
        JumpToBottomButton(
            enabled = !isScrolledToBottom,
            onClicked = ::scrollToBottom,
//        modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun MinimalMessage(message: ChatMessage) {
    val text = buildAnnotatedString {
        append(
            AnnotatedString(
                text = "${message.author.name} ",
                spanStyle = SpanStyle(
                    color = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp
                )
            )
        )

        append(messageFormatter(message.getTextContent()))
    }

    BasicText(
        modifier = Modifier
            .fillMaxWidth()
            .chatPadding(),
        text = text,
        style = MaterialTheme.typography.body1,
        inlineContent = message.getEmoteInlineContent()
    )
}

@Composable
private fun Message(message: ChatMessage, showTimestamp: Boolean) {
    val modifier = when (message) {
        is ChatMessage.RegularChat ->
            Modifier
                .fillMaxWidth()
                .chatPadding()
        is ChatMessage.SuperChat ->
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(4.dp))
                .background(color = message.level.backgroundColor)
                .chatPadding()
    }

    val textColor = when (message) {
        is ChatMessage.RegularChat -> LocalContentColor.current
        is ChatMessage.SuperChat -> message.level.textColor
    }

    val authorPicInlineContent = mapOf(
        message.author.photoUrl to InlineTextContent(
            placeholder = Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center),
            children = {
                CoilImage(
                    data = message.author.photoUrl,
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
                    CoilImage(
                        data = message.author.membershipBadgeUrl!!,
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
            append(
                AnnotatedString(
                    text = " ${message.timestamp.toTimestampString()} ",
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
        append(
            AnnotatedString(
                text = " ${message.author.name} ",
                spanStyle = SpanStyle(
                    color = textColor.copy(alpha = ContentAlpha.medium),
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp
                )
            )
        )

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
        append(messageFormatter(message.getTextContent()))
    }

    BasicText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.body1.copy(color = textColor),
        // TODO: should try to cache these
        inlineContent = authorPicInlineContent + authorBadgeInlineContent + message.getEmoteInlineContent()
    )
}

private fun ChatMessage.getEmoteInlineContent(): Map<String, InlineTextContent> {
    return content
        .filterIsInstance<ChatMessageContent.Emoji>()
        .distinct()
        .associate { emote ->
            emote.id to InlineTextContent(
                placeholder = Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center),
                children = {
                    CoilImage(
                        data = emote.src,
                        contentDescription = null,
                        modifier = Modifier
                            .requiredWidth(18.dp)
                            .aspectRatio(1f)
                    )
                }
            )
        }
}

private fun Modifier.chatPadding() = padding(horizontal = 8.dp, vertical = 4.dp)

@Preview
@Composable
private fun RegularChatPreview() {
    Message(
        message = ChatMessage.RegularChat(
            author = MessageAuthor(
                id = "1",
                name = "Name",
                photoUrl = "https://yt3.ggpht.com/ytc/AAUvwng37V0l-NwF3bu7QA4XmOP5EZFwk5zJE-78OHP9=s176-c-k-c0x00ffffff-no-rj",
                isModerator = false,
            ),
            content = listOf(ChatMessageContent.Text("Hello world")),
            timestamp = 1615001105,
        ),
        showTimestamp = true,
    )
}

@Preview
@Composable
private fun SuperChatPreview() {
    Message(
        message = ChatMessage.SuperChat(
            author = MessageAuthor(
                id = "2",
                name = "Pekora Shachou",
                photoUrl = "https://yt3.ggpht.com/ytc/AAUvwng37V0l-NwF3bu7QA4XmOP5EZFwk5zJE-78OHP9=s176-c-k-c0x00ffffff-no-rj",
                isModerator = true,
            ),
            content = listOf(ChatMessageContent.Text("HA↑HA↓HA↑HA↓ PE↗KO↘PE↗KO↘ 😂")),
            timestamp = 1615001105,
            amount = "$100.00",
            level = ChatMessage.SuperChat.Level.RED,
        ),
        showTimestamp = true,
    )
}
