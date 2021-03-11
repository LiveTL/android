package com.livetl.android.ui.screen.player.composable

import androidx.annotation.StringRes
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEachIndexed
import com.livetl.android.R
import com.livetl.android.data.chat.ChatService
import com.livetl.android.data.stream.StreamInfo
import org.koin.androidx.compose.get

enum class Tabs(@StringRes val nameRes: Int) {
    Info(R.string.info),
    Chat(R.string.chat),
    Settings(R.string.settings),
}
val tabs = Tabs.values().toList()

@Composable
fun PlayerTabs(
    streamInfo: StreamInfo?,
    chatService: ChatService = get(),
) {
    val chatMessages by chatService.messages.collectAsState()
    var selectedTab by remember { mutableStateOf(Tabs.Info) }

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
        Tabs.Chat -> Chat(
            messages = chatMessages,
            showJumpToBottomButton = true,
        )
        Tabs.Settings -> SettingsTab()
    }
}
