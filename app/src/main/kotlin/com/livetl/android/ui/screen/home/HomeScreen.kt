package com.livetl.android.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToQueue
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.livetl.android.R
import com.livetl.android.data.feed.Stream
import com.livetl.android.ui.screen.home.tab.StreamsTab
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigateToStreamInfo: (String) -> Unit,
    navigateToPlayer: (String) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAbout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState { viewModel.tabs.size }

    val openStreamInfo = { stream: Stream -> navigateToStreamInfo(stream.id) }
    val openStream = { stream: Stream -> navigateToPlayer(stream.id) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.app_name))
                    },
                    actions = {
                        IconButton(onClick = navigateToSettings) {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = stringResource(R.string.settings),
                            )
                        }
                        IconButton(onClick = navigateToAbout) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = stringResource(R.string.about),
                            )
                        }
                    },
                )

                TabRow(
                    selectedTabIndex = pagerState.currentPage,
                ) {
                    viewModel.tabs.forEachIndexed { index, tab ->
                        Tab(
                            text = { Text(stringResource(tab.first.headingRes)) },
                            selected = pagerState.currentPage == index,
                            onClick = { coroutineScope.launch { pagerState.scrollToPage(index) } },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.showOpenVideoDialog() },
                text = { Text(stringResource(R.string.action_open_video)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AddToQueue,
                        contentDescription = stringResource(R.string.action_open_video),
                    )
                },
            )
        },
    ) { contentPadding ->
        if (viewModel.showOpenVideoDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.hideOpenVideoDialog() },
                title = {
                    Text(stringResource(R.string.action_open_video))
                },
                text = {
                    Column {
//                        if (viewModel.youTubeSession != null) {
//                            Text(text = viewModel.youTubeSession!!.title)
//                        }

                        Text(
                            modifier = Modifier.padding(bottom = 16.dp),
                            text = stringResource(R.string.open_video_hint, stringResource(R.string.app_name)),
                        )

                        TextField(
                            value = viewModel.openVideoUrl,
                            onValueChange = { viewModel.openVideoUrl = it },
                            placeholder = { Text(stringResource(R.string.youtube_url_hint)) },
                            singleLine = true,
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = viewModel.openVideoUrl.isNotEmpty(),
                        onClick = { navigateToPlayer(viewModel.openVideoUrl) },
                    ) {
                        Text(stringResource(R.string.action_open))
                    }
                },
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding),
            contentPadding = contentPadding,
        ) { page ->
            val (status, tabViewModel) = viewModel.tabs[page]
            StreamsTab(
                navigateToStream = openStream,
                peekStream = openStreamInfo,
                status = status,
                viewModel = tabViewModel,
            )
        }
    }
}
