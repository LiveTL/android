package com.livetl.android.ui.screen.player.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.ui.screen.player.composable.chat.Chat
import com.livetl.android.ui.screen.player.composable.chat.ChatState
import com.livetl.android.ui.screen.player.composable.chat.MessageActionsDialog
import kotlinx.coroutines.launch

enum class Tabs(@StringRes val nameRes: Int, val icon: ImageVector) {
    Info(R.string.info, Icons.Outlined.Info),
    Chat(R.string.chat, Icons.Outlined.Chat),
    Settings(R.string.settings, Icons.Outlined.Settings),
}
val tabs = Tabs.values().toList()

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PlayerTabs(
    streamInfo: StreamInfo?,
    chatState: ChatState,
    playerViewModel: PlayerViewModel = viewModel(),
) {
    val actioningMessage = remember { mutableStateOf<ChatMessage?>(null) }
    val chatMessages by playerViewModel.messages.collectAsState(initial = emptyList())

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = tabs.indexOf(Tabs.Chat),
        pageCount = tabs.size,
    )

    Column(Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                )
            }
        ) {
            tabs.fastForEachIndexed { index, tab ->
                Tab(
                    icon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = stringResource(tab.nameRes)
                        )
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
        ) { page ->
            when (page) {
                Tabs.Info.ordinal -> InfoTab(streamInfo = streamInfo)
                Tabs.Chat.ordinal -> Chat(
                    messages = chatMessages,
                    state = chatState,
                    onClickMessage = { actioningMessage.value = it },
                    showJumpToBottomButton = true,
                )
                Tabs.Settings.ordinal -> SettingsTab()
            }
        }
    }

    MessageActionsDialog(actioningMessage)
}
