package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
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
import com.livetl.android.ui.flow.FlowRow
import com.livetl.android.ui.messageFormatter
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.launch

@Composable
fun Chat(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    showJumpToBottomButton: Boolean = false,
) {
    val scope = rememberCoroutineScope()

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
        items(_messages) { message -> Message(message) }
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
private fun Message(message: ChatMessage) {
    val modifier = when (message) {
        is ChatMessage.RegularChat -> Modifier
            .fillMaxWidth()
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
        is ChatMessage.SuperChat -> Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(color = message.level.backgroundColor)
            .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
    }

    val textColor = when (message) {
        is ChatMessage.RegularChat -> LocalContentColor.current
        is ChatMessage.SuperChat -> message.level.textColor
    }

    FlowRow(modifier = modifier) {
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

        val textPrefix = buildAnnotatedString {
            appendInlineContent(message.author.photoUrl, message.author.name)

            // message.timestamp.toString()

            append(AnnotatedString(
                text = " ${message.author.name} ",
                spanStyle = SpanStyle(
                    color = textColor.copy(alpha = ContentAlpha.medium),
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp
                )
            ))

            if (message is ChatMessage.SuperChat) {
                append(AnnotatedString(
                    text = "${message.amount} ",
                    spanStyle = SpanStyle(
                        color = textColor,
                        fontWeight = FontWeight.Bold,
                    )
                ))
            }
        }

        val styledText = textPrefix + messageFormatter(message.getTextContent())

        BasicText(
            text = styledText,
            style = MaterialTheme.typography.body1.copy(color = textColor),
            inlineContent = authorPicInlineContent + message.getEmoteInlineContent()
        )
    }
}

private fun ChatMessage.getEmoteInlineContent(): Map<String, InlineTextContent> {
    return content
        .filterIsInstance<ChatMessageContent.Emoji>()
        .associate { emote ->
            emote.id to InlineTextContent(
                placeholder = Placeholder(1.em, 1.em, PlaceholderVerticalAlign.Center),
                children = {
                    CoilImage(
                        data = emote.src,
                        contentDescription = null,
                        modifier = Modifier
                            .requiredWidth(24.dp)
                            .aspectRatio(1f)
                    )
                }
            )
        }
}

@Preview
@Composable
private fun MessagePreviews() {
    Column {
        Message(message = ChatMessage.RegularChat(
            author = MessageAuthor(name = "Name",
                photoUrl = "https://yt3.ggpht.com/ytc/AAUvwng37V0l-NwF3bu7QA4XmOP5EZFwk5zJE-78OHP9=s176-c-k-c0x00ffffff-no-rj"),
            content = listOf(ChatMessageContent.Text("Hello world")),
            timestamp = 1234,
        ))

        Message(message = ChatMessage.SuperChat(
            author = MessageAuthor(name = "Name",
                photoUrl = "https://yt3.ggpht.com/ytc/AAUvwng37V0l-NwF3bu7QA4XmOP5EZFwk5zJE-78OHP9=s176-c-k-c0x00ffffff-no-rj"),
            content = listOf(ChatMessageContent.Text("Hello world")),
            timestamp = 1234,
            amount = "$100.00",
            level = ChatMessage.SuperChat.Level.RED,
        ))
    }
}