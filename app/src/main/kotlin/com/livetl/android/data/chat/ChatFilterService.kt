package com.livetl.android.data.chat

import com.livetl.android.util.PreferencesHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ChatFilterService(
    chatService: ChatService,
    private val prefs: PreferencesHelper,
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>>
        get() = _messages

    init {
        // Clear out previous chat contents, just in case
        stop()

        chatService.messages
            .onEach {
                _messages.value = it.mapNotNull(this::filterMessage)
            }
            .launchIn(scope)
    }

    fun stop() {
        _messages.value = emptyList()
    }

    private fun filterMessage(message: ChatMessage): ChatMessage? {
        if (prefs.allowedUsers().get().contains(message.author.id)) {
            return parseMessage(message)?.second ?: message
        }
        if (prefs.blockedUsers().get().contains(message.author.id)) {
            return null
        }

        if (prefs.showModMessages().get() && message.author.isModerator) {
            return parseMessage(message)?.second ?: message
        }
        if (prefs.showVerifiedMesages().get() && message.author.isVerified) {
            return parseMessage(message)?.second ?: message
        }
        if (prefs.showOwnerMesages().get() && message.author.isOwner) {
            return null
        }

        parseMessage(message)?.let { (lang, parsedMessage) ->
            if (prefs.tlLanguages().get().contains(lang.id)) {
                return parsedMessage
            }
        }

        return null
    }

    private fun parseMessage(message: ChatMessage): Pair<TranslatedLanguage, ChatMessage>? {
        val firstTextContent = message.content.firstOrNull()

        // Language tag should be at the beginning as text
        if (firstTextContent == null || firstTextContent !is ChatMessageContent.Text) {
            return null
        }

        val trimmedText = firstTextContent.text.trim()
        if (trimmedText.isEmpty()) {
            return null
        }

        // We assume anything that roughly starts with something like "[EN]" is a translation
        val leftToken = trimmedText[0]
        val rightToken = LANG_TOKENS[leftToken]
        val isTagged = rightToken != null && trimmedText.indexOf(rightToken) < 5
        if (!isTagged) {
            return null
        }

        val (lang, text) = trimmedText.split(rightToken!!, limit = 2)
        val taggedLang = TranslatedLanguage.fromId(lang.removePrefix(leftToken.toString()).trim())
        val trimmedTextContent = ChatMessageContent.Text(
            text.trim()
                .removePrefix("-")
                .removePrefix(":")
                .trim()
        )

        if (taggedLang == null) {
            return null
        }

        return Pair(
            taggedLang,
            message.withContent(listOf(trimmedTextContent) + message.content.drop(1))
        )
    }
}

private val LANG_TOKENS = mapOf(
    '[' to ']',
    '{' to '}',
    '(' to ')',
    '|' to '|',
    '<' to '>',
    '【' to '】',
    '「' to '」',
    '『' to '』',
    '〚' to '〛',
    '（' to '）',
    '〈' to '〉',
    '⁽' to '₎',
)
