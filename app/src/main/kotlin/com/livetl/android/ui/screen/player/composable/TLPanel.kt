package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.ui.screen.player.composable.chat.Chat
import com.livetl.android.vm.PlayerViewModel

@Composable
fun TLPanel(
    playerViewModel: PlayerViewModel = viewModel(),
) {
    val filteredMessages by playerViewModel.filteredMessages.collectAsState(initial = emptyList())

    DisposableEffect(Unit) {
        onDispose {
            playerViewModel.stopChatFilter()
        }
    }

    Chat(
        modifier = Modifier.requiredHeight(96.dp),
        messages = filteredMessages,
        minimalMode = true,
    )
}
