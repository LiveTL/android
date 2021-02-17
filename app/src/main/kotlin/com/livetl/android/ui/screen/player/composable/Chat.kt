package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
            Text(message.content)
        }
    }
}