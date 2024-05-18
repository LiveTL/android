package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.data.chat.ChatMessageContent
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.ui.screen.player.PlayerViewModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

sealed class ChatState {
    data object LOADING : ChatState()
    data object LOADED : ChatState()
    data object ERROR : ChatState()
}

@Composable
fun Chat(
    messages: ImmutableList<ChatMessage>,
    modifier: Modifier = Modifier,
    state: ChatState? = ChatState.LOADED,
    fontScale: Float = 1f,
    playerViewModel: PlayerViewModel = viewModel(),
) {
    val scope = rememberCoroutineScope()

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
            if (messages.isEmpty()) {
                Text(
                    text = stringResource(R.string.chat_no_messages),
                    modifier = Modifier.fillMaxSize().padding(8.dp),
                    textAlign = TextAlign.Center,
                )
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
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isModerator = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isVerified = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
            emojiCache = EmojiCache(),
        )

        Message(
            message = ChatMessage.RegularChat(
                author = author.copy(isOwner = true),
                content = listOf(ChatMessageContent.Text("Hello world")),
                timestamp = 1615001105,
            ),
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
        emojiCache = EmojiCache(),
    )
}
