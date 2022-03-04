package com.livetl.android.ui.screen.home.tab

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
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
    val refreshingFeed = rememberSwipeRefreshState(false)

    fun refreshFeed() {
        coroutineScope.launch {
            refreshingFeed.isRefreshing = true
            viewModel.loadStreams()
            withContext(Dispatchers.Main) {
                refreshingFeed.isRefreshing = false
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshFeed()
    }

    SwipeRefresh(
        modifier = Modifier.fillMaxSize(),
        state = refreshingFeed,
        onRefresh = { refreshFeed() },
    ) {
        LazyColumn {
            items(viewModel.streams) { stream ->
                Stream(
                    modifier = Modifier,
                    stream = stream,
                    timestampFormatStringRes = status.timestampFormatStringRes,
                    timestampSupplier = status.timestampSupplier,
                    onClick = navigateToStream,
                    onLongClick = peekStream,
                )
            }

            item {
                Spacer(Modifier.navigationBarsHeight())
            }
        }
    }
}
