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
            return parseMessage(message).second
        }
        if (prefs.blockedUsers().get().contains(message.author.id)) {
            return null
        }

        if (prefs.showModMessages().get() && message.author.isModerator) {
            return parseMessage(message).second
        }
        if (prefs.showVerifiedMesages().get() && message.author.isVerified) {
            return parseMessage(message).second
        }
        if (prefs.showOwnerMesages().get() && message.author.isOwner) {
            return null
        }

        val (lang, parsedMessage) = parseMessage(message)
        if (lang != null && prefs.tlLanguages().get().contains(lang.id)) {
            return parsedMessage
        }

        return null
    }

    private fun parseMessage(message: ChatMessage): Pair<TranslatedLanguage?, ChatMessage> {
        var isTagged = false
        var taggedLang: TranslatedLanguage? = null
        val parsedContent = message.content
            .map {
                if (isTagged || it !is ChatMessageContent.Text) {
                    return@map it
                }

                val trimmedText = it.text.trim()
                if (trimmedText.isEmpty()) {
                    return@map it
                }

                // We assume anything that roughly starts with something like "[EN]" is a translation
                val leftToken = trimmedText[0]
                val rightToken = LANG_TOKENS[leftToken]
                isTagged = rightToken != null && trimmedText.indexOf(rightToken) < 5
                if (!isTagged) {
                    return@map it
                }

                val (lang, text) = trimmedText.split(rightToken!!)
                taggedLang =
                    TranslatedLanguage.fromId(lang.removePrefix(leftToken.toString()).trim())
                return@map it.copy(
                    text = text.trim()
                        .removePrefix("-")
                        .removePrefix(":")
                        .trim()
                )
            }

        return if (taggedLang == null) {
            Pair(null, message)
        } else {
            val parsedMessage = when (message) {
                is ChatMessage.RegularChat -> {
                    message.copy(content = parsedContent)
                }
                is ChatMessage.SuperChat -> {
                    message.copy(content = parsedContent)
                }
            }

            Pair(taggedLang, parsedMessage)
        }
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
