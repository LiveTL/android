package com.livetl.android.ui.screen.player

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEachIndexed
import com.livetl.android.R
import com.livetl.android.data.chat.ChatService
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.di.get
import com.livetl.android.ui.screen.player.composable.Chat
import com.livetl.android.ui.screen.player.composable.VideoPlayer
import com.livetl.android.ui.screen.player.section.InfoTab
import com.livetl.android.ui.screen.player.section.SettingsTab
import com.livetl.android.ui.screen.player.section.TLPanel
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.collectAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class Tabs(@StringRes val nameRes: Int) {
    Info(R.string.info),
    Chat(R.string.chat),
    Settings(R.string.settings),
}
val tabs = Tabs.values().toList()

@ExperimentalCoroutinesApi
@Composable
fun PlayerScreen(
    urlOrId: String,
    streamService: StreamService = get(),
    chatService: ChatService = get(),
    prefs: PreferencesHelper = get(),
) {
    val coroutineScope = rememberCoroutineScope()

    val chatMessages by chatService.messages.collectAsState()
    val showFilteredMessages by prefs.showTlPanel().collectAsState()

    var videoId by remember { mutableStateOf("") }
    var streamInfo by remember { mutableStateOf<StreamInfo?>(null) }

    var selectedTab by remember { mutableStateOf(Tabs.Info) }

    fun setSource(url: String) {
        videoId = streamService.getVideoId(url)

        coroutineScope.launch {
            val newStream = streamService.getStreamInfo(url)
            withContext(Dispatchers.Main) {
                streamInfo = newStream
            }

            chatService.load(videoId, newStream.isLive)
        }
    }

    fun onCurrentSecond(second: Long) {
        // Live chats don't need to be progressed manually
        if (streamInfo?.isLive == false) {
            chatService.seekTo(videoId, second)
        }
    }

    DisposableEffect(urlOrId) {
        if (urlOrId.isNotEmpty()) {
            setSource(urlOrId)
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

        TabRow(selectedTabIndex = selectedTab.ordinal) {
            tabs.fastForEachIndexed { index, tab ->
                Tab(
                    text = { Text(stringResource(tab.nameRes)) },
                    selected = index == selectedTab.ordinal,
                    onClick = { selectedTab = tab }
                )
            }
        }
        when (selectedTab) {
            Tabs.Info -> InfoTab(streamInfo = streamInfo)
            Tabs.Chat -> Chat(messages = chatMessages)
            Tabs.Settings -> SettingsTab()
        }
    }
}