package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.ui.screen.player.ChatState
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.util.rememberIsInPipMode
import com.livetl.android.util.rememberIsInSplitScreenMode
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@Composable
fun Chat(
    messages: ImmutableList<ChatMessage>,
    modifier: Modifier = Modifier,
    state: ChatState? = ChatState.LOADED,
    fontScale: Float = 1f,
    playerViewModel: PlayerViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()
    val isInPipMode = rememberIsInPipMode()
    val isInSplitScreenMode = rememberIsInSplitScreenMode()

    val scrollState = rememberLazyListState()
    var isScrolledToBottom by remember { mutableStateOf(true) }
    var _messages by remember { mutableStateOf<ImmutableList<ChatMessage>>(persistentListOf()) }

    fun checkIfAtBottom() {
        isScrolledToBottom = if (_messages.isEmpty()) {
            true
        } else {
            val visibleItems = scrollState.layoutInfo.visibleItemsInfo
            visibleItems.lastOrNull()?.index == _messages.lastIndex
        }
    }

    fun scrollToBottom(force: Boolean) {
        if ((isScrolledToBottom || force) && _messages.isNotEmpty()) {
            scope.launch {
                scrollState.scrollToItem(_messages.lastIndex, 0)
                checkIfAtBottom()
            }
        }
    }

    LaunchedEffect(messages) {
        checkIfAtBottom()
        _messages = messages

        scrollToBottom(force = isInPipMode || isInSplitScreenMode)
    }

    when (state) {
        ChatState.LOADING -> {
            LoadingIndicator()
        }

        ChatState.ERROR -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(stringResource(R.string.error_chat_load))
            }
        }

        ChatState.LOADED -> {
            if (messages.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(stringResource(R.string.chat_no_messages))
                }
                return
            }

            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                state = scrollState,
            ) {
                items(_messages) {
                    Message(
                        message = it,
                        emojiCache = playerViewModel.emojiCache,
                        fontScale = fontScale,
                    )
                }
            }
        }

        null -> {}
    }
}
