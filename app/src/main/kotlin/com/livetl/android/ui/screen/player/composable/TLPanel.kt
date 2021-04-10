package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livetl.android.data.chat.ChatFilterService
import com.livetl.android.ui.screen.player.composable.chat.Chat
import org.koin.androidx.compose.get

@Composable
fun TLPanel(
    chatFilterService: ChatFilterService = get(),
) {
    val filteredMessages by chatFilterService.messages.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            chatFilterService.stop()
        }
    }

    Chat(
        modifier = Modifier.requiredHeight(96.dp),
        messages = filteredMessages,
        minimalMode = true,
    )
}
