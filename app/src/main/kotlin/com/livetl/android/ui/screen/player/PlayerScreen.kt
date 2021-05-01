package com.livetl.android.ui.screen.player

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.livetl.android.R
import com.livetl.android.data.chat.ChatService
import com.livetl.android.data.chat.NoChatContinuationFoundException
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.ui.screen.player.composable.PlayerTabs
import com.livetl.android.ui.screen.player.composable.TLPanel
import com.livetl.android.ui.screen.player.composable.VideoPlayer
import com.livetl.android.ui.screen.player.composable.chat.ChatState
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.collectAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.get
import timber.log.Timber

@Composable
fun PlayerScreen(
    videoId: String,
    setKeepScreenOn: (Boolean) -> Unit,
    setFullscreen: (Boolean) -> Unit,
    streamService: StreamService = get(),
    chatService: ChatService = get(),
    prefs: PreferencesHelper = get(),
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var streamInfo by rememberSaveable { mutableStateOf<StreamInfo?>(null) }
    var chatState by remember { mutableStateOf<ChatState>(ChatState.LOADING) }
    val showFullscreen by prefs.showFullscreen().collectAsState()

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

    DisposableEffect(showFullscreen) {
        setFullscreen(showFullscreen)

        onDispose { setFullscreen(false) }
    }

    val errorChatLoadMessage = stringResource(R.string.error_chat_load)
    DisposableEffect(videoId) {
        if (videoId.isNotEmpty()) {
            coroutineScope.launch {
                val newStream = streamService.getStreamInfo(videoId)
                withContext(Dispatchers.Main) {
                    streamInfo = newStream
                }

                try {
                    chatState = ChatState.LOADING
                    chatService.load(videoId, newStream.isLive)
                    chatState = ChatState.LOADED
                } catch (e: NoChatContinuationFoundException) {
                    Timber.e(e)
                    chatState = ChatState.ERROR
                }
            }
        }

        onDispose {
            chatService.stop()
        }
    }

    when (LocalConfiguration.current.orientation) {
        ORIENTATION_LANDSCAPE -> {
            LandscapeLayout(videoId, streamInfo, chatState, { onCurrentSecond(it) })
        }
        else -> {
            PortraitLayout(videoId, streamInfo, chatState, { onCurrentSecond(it) })
        }
    }
}

@Composable
private fun PortraitLayout(
    videoId: String,
    streamInfo: StreamInfo?,
    chatState: ChatState,
    onCurrentSecond: (Long) -> Unit,
    prefs: PreferencesHelper = get(),
) {
    val showFilteredMessages by prefs.showTlPanel().collectAsState()

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
            TLPanel()
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
    prefs: PreferencesHelper = get(),
) {
    val showFilteredMessages by prefs.showTlPanel().collectAsState()

    Row {
        VideoPlayer(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .aspectRatio(16 / 9f)
                .align(Alignment.CenterVertically),
            videoId = videoId,
            isLive = streamInfo?.isLive,
            onCurrentSecond = { onCurrentSecond(it.toLong()) },
        )

        Column {
            if (showFilteredMessages) {
                TLPanel()
            }

            PlayerTabs(streamInfo, chatState)
        }
    }
}
