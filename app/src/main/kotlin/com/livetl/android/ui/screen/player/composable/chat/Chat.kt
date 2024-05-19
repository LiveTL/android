package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
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
    var _messages by remember { mutableStateOf<ImmutableList<ChatMessage>>(persistentListOf()) }

    fun scrollToBottom(force: Boolean) {
        if ((!scrollState.canScrollForward || force) && _messages.isNotEmpty()) {
            scope.launch {
                scrollState.scrollToItem(_messages.lastIndex, 0)
            }
        }
    }

    LaunchedEffect(messages) {
        _messages = messages

        scrollToBottom(force = isInPipMode || isInSplitScreenMode)
    }

    when (state) {
        ChatState.LOADING -> {
            LoadingIndicator()
        }

        is ChatState.ERROR -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(stringResource(R.string.error_chat_load))

                state.error.message?.let {
                    Spacer(Modifier.requiredHeight(8.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
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

            Box {
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

                JumpToBottomButton(
                    enabled = scrollState.canScrollForward,
                    onClicked = { scrollToBottom(force = true) },
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp),
                )
            }
        }

        null -> {}
    }
}
