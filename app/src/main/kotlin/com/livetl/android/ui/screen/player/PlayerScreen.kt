package com.livetl.android.ui.screen.player

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.screen.player.composable.PlayerTabs
import com.livetl.android.ui.screen.player.composable.TLPanel
import com.livetl.android.ui.screen.player.composable.VideoPlayer
import com.livetl.android.ui.screen.player.composable.chat.ChatState
import com.livetl.android.util.collectAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun PlayerScreen(
    videoId: String,
    setKeepScreenOn: (Boolean) -> Unit,
    setFullscreen: (Boolean) -> Unit,
    playerViewModel: PlayerViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    var streamInfo by remember { mutableStateOf<StreamInfo?>(null) }
    var chatState by remember { mutableStateOf<ChatState>(ChatState.LOADING) }

    val showFullscreen by playerViewModel.prefs.showFullscreen().collectAsState()
    val showFilteredMessages by playerViewModel.prefs.showTlPanel().collectAsState()
    val filteredMessages by playerViewModel.filteredMessages.collectAsState(initial = emptyList())

    fun onCurrentSecond(second: Long) {
        // Live chats don't need to be progressed manually
        if (streamInfo?.isLive == false) {
            playerViewModel.seekTo(videoId, second)
        }
    }

    DisposableEffect(Unit) {
        setKeepScreenOn(true)

        onDispose {
            setKeepScreenOn(false)
        }
    }

    DisposableEffect(showFullscreen) {
        setFullscreen(showFullscreen)

        onDispose { setFullscreen(false) }
    }

    LaunchedEffect(videoId) {
        if (videoId.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val newStream = playerViewModel.getStreamInfo(videoId)
                    withContext(Dispatchers.Main) {
                        streamInfo = newStream
                    }

                    chatState = ChatState.LOADING
                    playerViewModel.loadChat(videoId, newStream.isLive)
                    chatState = ChatState.LOADED
                } catch (e: Throwable) {
                    Timber.e(e)
                    chatState = ChatState.ERROR
                }
            }
        }
    }

    when (LocalConfiguration.current.orientation) {
        ORIENTATION_LANDSCAPE -> {
            LandscapeLayout(videoId, streamInfo, chatState, { onCurrentSecond(it) }, showFilteredMessages, filteredMessages)
        }
        else -> {
            PortraitLayout(videoId, streamInfo, chatState, { onCurrentSecond(it) }, showFilteredMessages, filteredMessages)
        }
    }
}

@Composable
private fun PortraitLayout(
    videoId: String,
    streamInfo: StreamInfo?,
    chatState: ChatState,
    onCurrentSecond: (Long) -> Unit,
    showFilteredMessages: Boolean,
    filteredMessages: List<ChatMessage>,
) {
    Column {
        VideoPlayer(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f),
            videoId = videoId,
            isLive = streamInfo?.isLive,
            onCurrentSecond = { onCurrentSecond(it.toLong()) },
        )

        if (showFilteredMessages) {
            TLPanel(filteredMessages)
        }

        PlayerTabs(streamInfo, chatState)
    }
}

@Composable
private fun LandscapeLayout(
    videoId: String,
    streamInfo: StreamInfo?,
    chatState: ChatState,
    onCurrentSecond: (Long) -> Unit,
    showFilteredMessages: Boolean,
    filteredMessages: List<ChatMessage>,
) {
    Row {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.6f)
                .background(Color.Black),
            contentAlignment = Alignment.Center,
        ) {
            VideoPlayer(
                modifier = Modifier.aspectRatio(16 / 9f),
                videoId = videoId,
                isLive = streamInfo?.isLive,
                onCurrentSecond = { onCurrentSecond(it.toLong()) },
            )
        }

        Column {
            if (showFilteredMessages) {
                TLPanel(filteredMessages)
            }

            PlayerTabs(streamInfo, chatState)
        }
    }
}
