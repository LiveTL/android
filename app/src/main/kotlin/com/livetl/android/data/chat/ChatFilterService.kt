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
                _messages.value = it.filter(this::shouldFilter)
            }
            .launchIn(scope)
    }

    fun stop() {
        _messages.value = emptyList()
    }
    
    private fun shouldFilter(message: ChatMessage): Boolean {
        if (prefs.showModMessages().get() && message.author.isModerator) {
            return true
        }
        if (prefs.showVerifiedMesages().get() && message.author.isVerified) {
            return true
        }
        if (prefs.showOwnerMesages().get() && message.author.isOwner) {
            return true
        }

        if (prefs.allowedUsers().get().contains(message.author.id)) {
            return true
        }
        if (prefs.blockedUsers().get().contains(message.author.id)) {
            return false
        }

        val (lang, parsedMessage) = parseMessage(message)
        if (lang != null && prefs.tlLanguages().get().contains(lang.id)) {
            // TODO: return parsedMessage
            return true
        }

        return false
    }

    private fun parseMessage(message: ChatMessage): Pair<TranslatedLanguage?, ChatMessage> {
        val trimmedMessage = message.getTextContent().trim()

        // We assume anything that roughly starts with something like "[EN]" is a translation
        val leftToken = trimmedMessage[0]
        val rightToken = LANG_TOKENS[leftToken]
        val isTagged = rightToken != null && trimmedMessage.indexOf(rightToken) < 5

        if (isTagged) {
            val (lang, text) = trimmedMessage.split(rightToken!!)

            val trimmedLang = lang.removePrefix(leftToken.toString()).trim()
            val trimmedText = text.trim()
                .removePrefix("-")
                .removePrefix(":")
                .trim()

            // TODO: return trimmedText
            return Pair(TranslatedLanguage.fromId(trimmedLang), message)
        }

        return Pair(null, message)
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
