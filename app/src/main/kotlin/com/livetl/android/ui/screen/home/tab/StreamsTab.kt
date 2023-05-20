package com.livetl.android.ui.screen.home.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livetl.android.R
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.feed.StreamStatus
import com.livetl.android.ui.screen.home.composable.Stream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StreamsTab(
    navigateToStream: (Stream) -> Unit,
    peekStream: (Stream) -> Unit,
    status: StreamStatus,
    viewModel: StreamsTabViewModel,
) {
    val coroutineScope = rememberCoroutineScope()

    fun refreshFeed() {
        coroutineScope.launch {
            viewModel.isRefreshing = true
            viewModel.loadStreams()
            withContext(Dispatchers.Main) {
                viewModel.isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshFeed()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = { refreshFeed() },
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
    ) {
        PullRefreshIndicator(
            refreshing = viewModel.isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )

        if (!viewModel.isRefreshing && viewModel.streams.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(R.string.empty_streams))
            }
            return
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(
                items = viewModel.streams,
                key = { it.id },
            ) { stream ->
                Stream(
                    modifier = Modifier,
                    stream = stream,
                    timestampFormatStringRes = status.timestampFormatStringRes,
                    timestampSupplier = status.timestampSupplier,
                    openPlayer = navigateToStream,
                    openStreamInfo = peekStream,
                )
            }

            item {
                Spacer(
                    Modifier
                        .navigationBarsPadding()
                        .padding(bottom = 88.dp), // Additional padding for FAB
                )
            }
        }
    }
}
