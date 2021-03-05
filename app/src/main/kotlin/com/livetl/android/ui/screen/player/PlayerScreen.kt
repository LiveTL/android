package com.livetl.android.ui.screen.player

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.livetl.android.data.chat.ChatService
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.di.get
import com.livetl.android.ui.screen.player.composable.PlayerTabs
import com.livetl.android.ui.screen.player.composable.TLPanel
import com.livetl.android.ui.screen.player.composable.VideoPlayer
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.collectAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PlayerScreen(
    videoId: String,
    setKeepScreenOn: (Boolean) -> Unit,
    streamService: StreamService = get(),
    chatService: ChatService = get(),
    prefs: PreferencesHelper = get(),
) {
    val coroutineScope = rememberCoroutineScope()

    val showFilteredMessages by prefs.showTlPanel().collectAsState()

    var streamInfo by remember { mutableStateOf<StreamInfo?>(null) }

    fun onCurrentSecond(second: Long) {
        // Live chats don't need to be progressed manually
        if (streamInfo?.isLive == false) {
            chatService.seekTo(videoId, second)
        }
    }

    DisposableEffect(Unit) {
        setKeepScreenOn(true)

        onDispose { setKeepScreenOn(false) }
    }

    DisposableEffect(videoId) {
        if (videoId.isNotEmpty()) {
            coroutineScope.launch {
                val newStream = streamService.getStreamInfo(videoId)
                withContext(Dispatchers.Main) {
                    streamInfo = newStream
                }

                chatService.load(videoId, newStream.isLive)
            }
        }

        onDispose {
            chatService.stop()
        }
    }

    Column {
        VideoPlayer(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9F),
            videoId = videoId,
            isLive = streamInfo?.isLive,
            onCurrentSecond = { onCurrentSecond(it.toLong()) },
        )

        if (showFilteredMessages) {
            TLPanel()
        }

        PlayerTabs(streamInfo)
    }
}
