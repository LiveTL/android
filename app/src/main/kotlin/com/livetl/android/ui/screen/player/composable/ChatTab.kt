package com.livetl.android.ui.screen.player.composable

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.ui.screen.player.composable.chat.Chat
import com.livetl.android.ui.screen.player.composable.chat.ChatState
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ChatTab(
    filteredMessages: ImmutableList<ChatMessage>,
    modifier: Modifier = Modifier,
    state: ChatState = ChatState.LOADED,
    fontScale: Float = 1f,
) {
    Chat(
        modifier = modifier,
        messages = filteredMessages,
        fontScale = fontScale,
        state = state,
    )
}
