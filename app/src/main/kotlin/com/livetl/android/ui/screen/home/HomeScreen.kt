package com.livetl.android.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddToQueue
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.livetl.android.R
import com.livetl.android.data.feed.Stream
import com.livetl.android.ui.screen.home.composable.StreamSheet
import com.livetl.android.ui.screen.home.tab.StreamsTab
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    navigateToPlayer: (String) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToAbout: () -> Unit,
    viewModel: HomeViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val peekStream: (Stream) -> Unit = { stream: Stream ->
        coroutineScope.launch {
            viewModel.showSheet(stream)
        }
    }
    val navigateToStream = { stream: Stream -> navigateToPlayer(stream.id) }

    ModalBottomSheetLayout(
        sheetState = viewModel.sheetState,
        sheetContent = { StreamSheet(viewModel.sheetStream) },
    ) {
        Scaffold(
            topBar = {
                Column {
                    TopAppBar(
                        title = {
                            Text(text = stringResource(R.string.app_name))
                        },
                        actions = {
                            IconButton(onClick = { navigateToSettings() }) {
                                Icon(
                                    imageVector = Icons.Outlined.Settings,
                                    contentDescription = stringResource(R.string.settings),
                                )
                            }
                            IconButton(onClick = { navigateToAbout() }) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = stringResource(R.string.about),
                                )
                            }
                        },
                        contentPadding = rememberInsetsPaddingValues(
                            LocalWindowInsets.current.statusBars,
                            applyBottom = false,
                        ),
                    )

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
                            )
                        },
                    ) {
                        // Add tabs for all of our pages
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
                FloatingActionButton(
                    modifier = Modifier.navigationBarsPadding(),
                    onClick = { viewModel.showOpenVideoDialog() },
                ) {
                    Icon(
                        imageVector = Icons.Default.AddToQueue,
                        contentDescription = stringResource(R.string.action_open_video),
                    )
                }
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
                    buttons = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp),
                            horizontalAlignment = Alignment.End,
                        ) {
                            TextButton(
                                onClick = { navigateToPlayer(viewModel.openVideoUrl) },
                            ) {
                                Text(stringResource(R.string.action_open))
                            }
                        }
                    },
                )
            }

            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                count = viewModel.tabs.size,
                state = pagerState,
            ) { page ->
                val (status, tabViewModel) = viewModel.tabs[page]
                StreamsTab(
                    navigateToStream = navigateToStream,
                    peekStream = peekStream,
                    status = status,
                    viewModel = tabViewModel,
                )
            }
        }
    }
}
