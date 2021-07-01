package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.ui.screen.player.composable.chat.Chat

@Composable
fun TLPanel(
    filteredMessages: List<ChatMessage>,
    fontScale: Float,
) {
    Chat(
        modifier = Modifier.requiredHeight(112.dp),
        messages = filteredMessages,
        fontScale = fontScale,
        minimalMode = true,
    )
}
