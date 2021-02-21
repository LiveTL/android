package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.MessageAuthor

@Composable
fun Chat(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        reverseLayout = true,
    ) {
        items(messages) { message ->
            when (message) {
                is ChatMessage.RegularChat -> RegularMessage(message)
                is ChatMessage.SuperChat -> SuperMessage(message)
            }
        }
    }
}

private fun Modifier.messagePadding() = padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)

@Composable
private fun RegularMessage(message: ChatMessage.RegularChat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .messagePadding()
    ) {
//        Text(message.timestamp.toString())
        Text(
            text = message.author.name,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 1.dp, end = 8.dp),
        )
        Text(message.content)
    }
}

@Composable
private fun SuperMessage(message: ChatMessage.SuperChat) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(color = message.level.backgroundColor)
            .messagePadding()
    ) {
//        Text(message.timestamp.toString(), color = message.level.textColor)
        Text(
            text = message.author.name,
            color = message.level.textColor,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(top = 1.dp, end = 8.dp),
        )
        Text(message.content, color = message.level.textColor)
    }
}

@Preview
@Composable
private fun MessagePreviews() {
    Column {
        RegularMessage(message = ChatMessage.RegularChat(
            author = MessageAuthor(name = "Name", photoUrl = ""),
            content = "Hello world",
            timestamp = 1234,
        ))

        SuperMessage(message = ChatMessage.SuperChat(
            author = MessageAuthor(name = "Name", photoUrl = ""),
            content = "Hello world",
            timestamp = 1234,
            level = ChatMessage.SuperChat.Level.RED,
        ))
    }
}