package com.livetl.android.ui.screen.player.section

import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livetl.android.data.chat.ChatFilterService
import com.livetl.android.di.get
import com.livetl.android.ui.screen.player.composable.Chat

@Composable
fun TLPanel(
    chatFilterService: ChatFilterService = get(),
) {
    val filteredMessages by chatFilterService.messages.collectAsState()

    Chat(
        modifier = Modifier.requiredHeight(96.dp),
        messages = filteredMessages,
        minimalMode = true,
    )
}
