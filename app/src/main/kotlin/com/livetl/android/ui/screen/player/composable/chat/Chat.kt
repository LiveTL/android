package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.ChatMessageContent
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.util.collectAsState
import kotlinx.coroutines.launch

sealed class ChatState {
    object LOADING : ChatState()
    object LOADED : ChatState()
    object ERROR : ChatState()
}

@Composable
fun Chat(
    modifier: Modifier = Modifier,
    messages: List<ChatMessage>,
    state: ChatState? = ChatState.LOADED,
    minimalMode: Boolean = false,
    onClickMessage: (ChatMessage) -> Unit = {},
    showJumpToBottomButton: Boolean = false,
    playerViewModel: PlayerViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()

    val showTimestamp by playerViewModel.prefs.showTimestamps().collectAsState()
    val debugTimestamp by playerViewModel.prefs.debugTimestamps().collectAsState()

    val scrollState = rememberLazyListState()
    var isScrolledToBottom by remember { mutableStateOf(true) }
    var _messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }

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

        scrollToBottom(force = false)
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
            Box {
                LazyColumn(
                    modifier = modifier.fillMaxWidth(),
                    state = scrollState,
                ) {
                    items(_messages) { message ->
                        val baseMessageModifier = Modifier
                            .fillMaxWidth()
                            .clickable { onClickMessage(message) }

                        when (minimalMode) {
                            true -> MinimalMessage(baseMessageModifier, message, playerViewModel.emojiCache)
                            false -> Message(baseMessageModifier, message, showTimestamp, debugTimestamp, playerViewModel.emojiCache)
                        }
                    }
                }

                if (showJumpToBottomButton) {
                    JumpToBottomButton(
                        enabled = !isScrolledToBottom,
                        onClicked = { scrollToBottom(force = true) },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun RegularChatPreviews() {
    val author = MessageAuthor(
        id = "1",
        name = "Name",
    )

    Column {
        Message(
            message = ChatMessage.RegularChat(
                author = author,
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            showTimestamp = true,
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isModerator = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            showTimestamp = true,
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isVerified = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            showTimestamp = true,
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isOwner = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            showTimestamp = true,
            emojiCache = EmojiCache(),
        )
    }
}

@Preview
@Composable
private fun SuperChatPreview() {
    Message(
        message = ChatMessage.SuperChat(
            author = MessageAuthor(
                id = "2",
                name = "Pekora Shachou",
            ),
            content = listOf(ChatMessageContent.Text("HA↑HA↓HA↑HA↓ PE↗KO↘PE↗KO↘ 😂")),
            timestamp = 1615001105,
            amount = "$100.00",
            level = ChatMessage.SuperChat.Level.RED,
        ),
        showTimestamp = true,
        emojiCache = EmojiCache(),
    )
}

@Preview
@Composable
private fun NewMemberPreview() {
    Message(
        message = ChatMessage.NewMember(
            author = MessageAuthor(
                id = "3",
                name = "Ina Ina Ina",
            ),
            timestamp = 1615001105,
        ),
        showTimestamp = true,
        emojiCache = EmojiCache(),
    )
}
