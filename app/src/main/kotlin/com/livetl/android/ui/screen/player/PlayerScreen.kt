package com.livetl.android.ui.screen.player

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.screen.player.composable.PlayerTabs
import com.livetl.android.ui.screen.player.composable.chat.ChatState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun PlayerScreen(videoId: String, viewModel: PlayerViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()

    var streamInfo by remember { mutableStateOf<StreamInfo?>(null) }
    var chatState by remember { mutableStateOf<ChatState>(ChatState.LOADING) }

    // TODO: handle time change for archives
    fun onCurrentSecond(second: Long) {
        // Live chats don't need to be progressed manually
        if (streamInfo?.isLive == false) {
            viewModel.seekTo(videoId, second)
        }
    }

    LaunchedEffect(videoId) {
        if (videoId.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val newStream = viewModel.getStreamInfo(videoId)
                    withContext(Dispatchers.Main) {
                        streamInfo = newStream
                    }

                    chatState = ChatState.LOADING
                    viewModel.loadChat(videoId, newStream.isLive)
                    chatState = ChatState.LOADED
                } catch (e: Throwable) {
                    Timber.e(e)
                    chatState = ChatState.ERROR
                }
            }
        }
    }

    Surface {
        PlayerTabs(streamInfo, chatState)
    }
}
