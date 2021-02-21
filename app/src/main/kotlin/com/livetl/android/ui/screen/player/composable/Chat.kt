package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livetl.android.data.chat.ChatMessage

val messageModifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)

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

@Composable
private fun RegularMessage(message: ChatMessage.RegularChat) {
    Row(modifier = messageModifier) {
        Text(
            text = message.author.name,
            style = MaterialTheme.typography.caption,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(message.content)
//        Text(message.timestamp.toString())
    }
}

@Composable
private fun SuperMessage(message: ChatMessage.SuperChat) {
    Row(modifier = messageModifier) {
        Text("SC (${message.level})")
        Text(message.author.name)
        Text(message.content)
//        Text(message.timestamp.toString())
    }
}