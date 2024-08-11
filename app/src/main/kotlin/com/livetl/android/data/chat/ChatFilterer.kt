package com.livetl.android.data.chat

import androidx.compose.ui.util.fastFirstOrNull
import com.livetl.android.util.AppPreferences
import javax.inject.Inject

class ChatFilterer @Inject constructor(private val prefs: AppPreferences) {
    fun filterMessage(message: ChatMessage): ChatMessage? {
        if (prefs.showAllMessages().get()) {
            return message
        }

        if (
            (prefs.showModMessages().get() && message.author.isModerator) ||
            (prefs.showVerifiedMessages().get() && message.author.isVerified) ||
            (prefs.showOwnerMessages().get() && message.author.isOwner)
        ) {
            return parseMessage(message)?.second ?: message
        }

        parseMessage(message)?.let { (lang, parsedMessage) ->
            if (prefs.tlLanguages().get().contains(lang.id)) {
                return parsedMessage
            }
        }

        return null
    }

    private fun parseMessage(message: ChatMessage): Pair<TranslatedLanguage, ChatMessage>? {
        val firstTextContent = message.content.fastFirstOrNull { it is ChatMessageContent.Text }

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
        val isTagged = rightToken != null &&
            trimmedText.indexOf(rightToken) < MAX_LANG_TAG_LEN &&
            trimmedText.indexOf(rightToken) > 1
        if (!isTagged) {
            return null
        }

        val (lang, text) = trimmedText.split(rightToken!!, limit = 2)
        val taggedLang = TranslatedLanguage.fromId(lang.removePrefix(leftToken.toString()).trim())
        val trimmedTextContent = ChatMessageContent.Text(
            text.trim()
                .removePrefix("-")
                .removePrefix(":")
                .trim(),
        )

        if (taggedLang == null) {
            return null
        }

        return Pair(
            taggedLang,
            message.withContent(listOf(trimmedTextContent) + message.content.drop(1)),
        )
    }
}

private const val MAX_LANG_TAG_LEN = 7

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
