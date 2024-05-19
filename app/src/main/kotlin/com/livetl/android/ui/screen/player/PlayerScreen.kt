package com.livetl.android.ui.screen.player

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.livetl.android.ui.screen.player.composable.PlayerTabs
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(urlOrId: String, viewModel: PlayerViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(urlOrId) {
        if (urlOrId.isNotEmpty()) {
            coroutineScope.launch {
                viewModel.loadStream(urlOrId)
            }
        }
    }

    Surface {
        PlayerTabs(state.streamInfo, state.chatState)
    }
}
