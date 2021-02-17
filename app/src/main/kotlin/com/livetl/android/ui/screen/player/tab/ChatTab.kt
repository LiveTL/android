package com.livetl.android.ui.screen.player.tab

import androidx.compose.runtime.Composable
import com.livetl.android.ui.screen.player.composable.ChatState
import com.livetl.android.ui.screen.player.composable.Chat

@Composable
fun ChatTab(chatState: ChatState) {
    Chat(messages = chatState.messages)
}