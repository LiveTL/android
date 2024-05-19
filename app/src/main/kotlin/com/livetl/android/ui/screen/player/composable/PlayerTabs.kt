package com.livetl.android.ui.screen.player.composable

import android.app.PictureInPictureParams
import android.os.Build
import android.util.Rational
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.PictureInPictureAlt
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.screen.player.ChatState
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.util.collectAsStateWithLifecycle
import com.livetl.android.util.findActivity
import com.livetl.android.util.rememberIsInPipMode
import com.livetl.android.util.rememberIsInSplitScreenMode
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

    val tlScale by viewModel.prefs.tlScale().collectAsStateWithLifecycle()
    val filteredMessages by viewModel.filteredMessages.collectAsStateWithLifecycle()

    if (isInPipMode || isInSplitScreenMode) {
        ChatTab(
            filteredMessages = filteredMessages,
            fontScale = tlScale,
            state = chatState,
        )
        return
    }

    FullPlayerTab(
        streamInfo,
        filteredMessages,
        tlScale,
        chatState,
        modifier,
    )
}

@Composable
private fun FullPlayerTab(
    streamInfo: StreamInfo?,
    filteredMessages: ImmutableList<ChatMessage>,
    tlScale: Float,
    chatState: ChatState,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Button(
                    onClick = {
                        context.findActivity().enterPictureInPictureMode(
                            PictureInPictureParams.Builder()
                                // Must be between 2.39:1 and 1:2.39 (inclusive)
                                .setAspectRatio(Rational(239, 100))
                                .build(),
                        )
                        uriHandler.openUri("https://www.youtube.com/watch?v=${streamInfo?.videoId}")
                    },
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PictureInPictureAlt,
                        contentDescription = null,
                    )
                }
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
                        fontScale = tlScale,
                        state = chatState,
                    )

                Tabs.Settings.ordinal -> SettingsTab()
            }
        }
    }
}
