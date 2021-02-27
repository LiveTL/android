package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.ChatMessageContent
import com.livetl.android.data.chat.MessageAuthor
import dev.chrisbanes.accompanist.coil.CoilImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Chat(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>
) {
    val state = rememberLazyListState()

    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        state = state,
    ) {
        items(messages) { message -> Message(message) }

        // TODO: scroll to bottom button

        // TODO: only do this if already scrolled to bottom
        if (messages.isNotEmpty()) {
            CoroutineScope(Dispatchers.Main).launch {
                state.scrollToItem(messages.lastIndex, 0)
            }
        }
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
        is ChatMessage.RegularChat -> Color.Unspecified
        is ChatMessage.SuperChat -> message.level.textColor
    }

    Row(modifier = modifier) {
//        Text(message.timestamp.toString(), color = textColor)
        CoilImage(
            data = message.author.photoUrl,
            contentDescription = null,
            modifier = Modifier
                .requiredWidth(16.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
        )
        Text(
            text = message.author.name,
            style = MaterialTheme.typography.caption,
            color = textColor,
            modifier = Modifier.padding(top = 1.dp, start = 8.dp, end = 8.dp),
        )
        if (message is ChatMessage.SuperChat) {
            Text(message.amount, color = textColor, modifier = Modifier.padding(end = 8.dp))
        }
        // TODO: wrap text
        message.content.forEach {
            when (it) {
                is ChatMessageContent.Text -> Text(it.text, color = textColor)
                is ChatMessageContent.Emote -> CoilImage(
                    data = it.src,
                    contentDescription = null,
                    modifier = Modifier
                        .requiredWidth(24.dp)
                        .aspectRatio(1f)
                )
            }
        }
    }
}

@Preview
@Composable
private fun MessagePreviews() {
    Column {
        Message(message = ChatMessage.RegularChat(
            author = MessageAuthor(name = "Name", photoUrl = "https://yt3.ggpht.com/ytc/AAUvwng37V0l-NwF3bu7QA4XmOP5EZFwk5zJE-78OHP9=s176-c-k-c0x00ffffff-no-rj"),
            content = listOf(ChatMessageContent.Text("Hello world")),
            timestamp = 1234,
        ))

        Message(message = ChatMessage.SuperChat(
            author = MessageAuthor(name = "Name", photoUrl = "https://yt3.ggpht.com/ytc/AAUvwng37V0l-NwF3bu7QA4XmOP5EZFwk5zJE-78OHP9=s176-c-k-c0x00ffffff-no-rj"),
            content = listOf(ChatMessageContent.Text("Hello world")),
            timestamp = 1234,
            amount = "$100.00",
            level = ChatMessage.SuperChat.Level.RED,
        ))
    }
}