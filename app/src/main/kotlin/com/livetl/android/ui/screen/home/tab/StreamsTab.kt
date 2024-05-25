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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.livetl.android.R
import com.livetl.android.data.feed.Stream
import com.livetl.android.data.feed.StreamStatus
import com.livetl.android.ui.screen.home.composable.Stream
import kotlinx.coroutines.launch

@Composable
fun StreamsTab(
    navigateToStream: (Stream) -> Unit,
    peekStream: (Stream) -> Unit,
    status: StreamStatus,
    viewModel: StreamsTabViewModel,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()

    val state by viewModel.state.collectAsStateWithLifecycle()

    fun refreshFeed() {
        coroutineScope.launch {
            viewModel.loadStreams()
        }
    }

    LaunchedEffect(Unit) {
        refreshFeed()
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = state.isLoading,
        onRefresh = ::refreshFeed,
    )

    Box(
        modifier = modifier
            .pullRefresh(pullRefreshState),
    ) {
        if (!state.isLoading && state.streams.isEmpty()) {
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
                items = state.streams,
                key = { it.id },
            ) { stream ->
                Stream(
                    modifier = Modifier,
                    stream = stream,
                    timestampFormatStringRes = status.timestampFormatStringRes,
                    timestampSupplier = status.timestampSupplier,
                    onClick = if (status != StreamStatus.UPCOMING) navigateToStream else peekStream,
                    onClickDetails = peekStream,
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

        PullRefreshIndicator(
            refreshing = state.isLoading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}
