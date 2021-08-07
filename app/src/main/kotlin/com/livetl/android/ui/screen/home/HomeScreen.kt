package com.livetl.android.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.livetl.android.R
import com.livetl.android.data.holodex.Stream
import com.livetl.android.ui.screen.home.composable.Stream
import com.livetl.android.ui.screen.home.composable.StreamSheet
import com.livetl.android.util.collectAsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun HomeScreen(
    showWelcomeScreen: () -> Unit,
    navigateToPlayer: (String) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAbout: () -> Unit,
    homeViewModel: HomeViewModel,
) {
    if (homeViewModel.prefs.showWelcomeScreen().get()) {
//        showWelcomeScreen()
//        return
    }

    val coroutineScope = rememberCoroutineScope()

    val showThumbnailBackground by homeViewModel.prefs.showFeedThumbnailBackgrounds().collectAsState()
    val refreshingFeed = rememberSwipeRefreshState(false)

    val peekStream: (Stream) -> Unit = { stream: Stream ->
        coroutineScope.launch {
            homeViewModel.showSheet(stream)
        }
    }
    val navigateToStream = { stream: Stream -> navigateToPlayer(stream.id) }

    fun refreshFeed() {
        coroutineScope.launch {
            refreshingFeed.isRefreshing = true
            homeViewModel.loadFeed()
            withContext(Dispatchers.Main) {
                refreshingFeed.isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshFeed()
    }

    ModalBottomSheetLayout(
        sheetState = homeViewModel.sheetState,
        sheetContent = { StreamSheet(homeViewModel.sheetStream) },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = { navigateToSettings() }) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = stringResource(R.string.settings)
                            )
                        }
                        IconButton(onClick = { navigateToAbout() }) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.about)
                            )
                        }
                    },
                    contentPadding = rememberInsetsPaddingValues(
                        LocalWindowInsets.current.statusBars,
                        applyBottom = false,
                    ),
                )
            },
        ) { contentPadding ->
            SwipeRefresh(
                modifier = Modifier.fillMaxWidth().padding(contentPadding),
                state = refreshingFeed,
                onRefresh = { refreshFeed() },
            ) {
                if (homeViewModel.feed != null) {
                    LazyColumn {
                        streamItems(
                            headingRes = R.string.live,
                            streams = homeViewModel.feed!!.live,
                            timestampFormatStringRes = R.string.started_streaming,
                            timestampSupplier = { it.start_actual },
                            showThumbnailBackground = showThumbnailBackground,
                            onClick = navigateToStream,
                            onLongClick = peekStream,
                        )
                        streamItems(
                            headingRes = R.string.upcoming,
                            streams = homeViewModel.feed!!.upcoming,
                            timestampSupplier = { it.start_scheduled },
                            showThumbnailBackground = showThumbnailBackground,
                            onClick = navigateToStream,
                            onLongClick = peekStream,
                        )
                        streamItems(
                            headingRes = R.string.archives,
                            streams = homeViewModel.feed!!.ended,
                            timestampFormatStringRes = R.string.streamed,
                            showThumbnailBackground = showThumbnailBackground,
                            timestampSupplier = { it.end_actual },
                            onClick = navigateToStream,
                            onLongClick = peekStream,
                        )

                        item {
                            Spacer(Modifier.navigationBarsHeight())
                        }
                    }
                }
            }
        }
    }
}

private fun LazyListScope.streamItems(
    @StringRes headingRes: Int,
    streams: List<Stream>,
    @StringRes timestampFormatStringRes: Int? = null,
    showThumbnailBackground: Boolean,
    timestampSupplier: (Stream) -> String?,
    onClick: (Stream) -> Unit,
    onLongClick: (Stream) -> Unit,
) {
    if (streams.isNotEmpty()) {
        stickyHeader {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colors.surface.copy(alpha = 0.9f),
            ) {
                Text(
                    text = stringResource(headingRes),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 18.sp,
                )
            }
        }

        items(streams) { stream ->
            Stream(
                Modifier,
                stream,
                timestampFormatStringRes,
                timestampSupplier,
                showThumbnailBackground,
                onClick,
                onLongClick,
            )
        }
    }
}
