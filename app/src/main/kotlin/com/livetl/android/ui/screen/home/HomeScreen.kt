package com.livetl.android.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
                            )
                        }
                    ) {
                        // Add tabs for all of our pages
                        viewModel.tabs.forEachIndexed { index, tab ->
                            Tab(
                                text = { Text(stringResource(tab.first.headingRes)) },
                                selected = pagerState.currentPage == index,
                                onClick = { coroutineScope.launch { pagerState.scrollToPage(index) }},
                            )
                        }
                    }
                }
            },
        ) { contentPadding ->
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
