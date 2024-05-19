package com.livetl.android.ui.screen.player.composable

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.media.YouTubeNotificationListenerService
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.screen.player.ChatState
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.util.rememberIsInPipMode
import com.livetl.android.util.rememberIsInSplitScreenMode
import com.livetl.android.util.rememberIsNotificationAccessGranted
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

private enum class Tabs(@StringRes val nameRes: Int, val icon: ImageVector) {
    Chat(R.string.chat, Icons.AutoMirrored.Outlined.Chat),
    Settings(R.string.settings, Icons.Outlined.Settings),
}
private val tabs = Tabs.entries

@Composable
fun PlayerTabs(
    streamInfo: StreamInfo?,
    chatState: ChatState,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel(),
) {
    val isInPipMode = rememberIsInPipMode()
    val isInSplitScreenMode = rememberIsInSplitScreenMode()

    val state by viewModel.state.collectAsStateWithLifecycle()

    if (isInPipMode || isInSplitScreenMode) {
        ChatTab(
            filteredMessages = state.filteredMessages,
            fontScale = state.fontScale,
            state = chatState,
        )
        return
    }

    FullPlayerTab(
        streamInfo = streamInfo,
        filteredMessages = state.filteredMessages,
        fontScale = state.fontScale,
        chatState = chatState,
        modifier = modifier,
    )
}

@Composable
private fun FullPlayerTab(
    streamInfo: StreamInfo?,
    filteredMessages: ImmutableList<ChatMessage>,
    fontScale: Float,
    chatState: ChatState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val isNotificationAccessGranted = rememberIsNotificationAccessGranted()
    val context = LocalContext.current

    val pagerState = rememberPagerState(
        initialPage = tabs.indexOf(Tabs.Chat),
        pageCount = { tabs.size },
    )

    var showStreamInfo by remember { mutableStateOf(true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .safeDrawingPadding(),
    ) {
        AnimatedVisibility(
            visible = showStreamInfo,
        ) {
            StreamInfoPanel(
                streamInfo = streamInfo,
            )
        }

        if (streamInfo?.isLive == false && !isNotificationAccessGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.error_no_notification_access, stringResource(R.string.app_name)),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.requiredHeight(8.dp))

                Button(
                    onClick = {
                        val intent = YouTubeNotificationListenerService.getPermissionScreenIntent(context)
                        context.startActivity(intent)
                    },
                ) {
                    Text(text = stringResource(R.string.action_grant_notification_access))
                }
            }

            return
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                divider = {},
                modifier = Modifier.weight(1f),
            ) {
                tabs.fastForEachIndexed { index, tab ->
                    Tab(
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = stringResource(tab.nameRes),
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

            Button(onClick = { showStreamInfo = !showStreamInfo }) {
                Icon(
                    imageVector = if (showStreamInfo) {
                        Icons.Outlined.KeyboardArrowUp
                    } else {
                        Icons.Outlined.KeyboardArrowDown
                    },
                    contentDescription = null,
                )
            }
        }
        HorizontalDivider()

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.Top,
        ) { page ->
            when (page) {
                Tabs.Chat.ordinal ->
                    ChatTab(
                        modifier = Modifier.fillMaxSize(),
                        filteredMessages = filteredMessages,
                        fontScale = fontScale,
                        state = chatState,
                    )

                Tabs.Settings.ordinal -> SettingsTab()
            }
        }
    }
}
