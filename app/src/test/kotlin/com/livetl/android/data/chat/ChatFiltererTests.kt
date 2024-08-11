package com.livetl.android.data.chat

import android.content.Context
import android.content.SharedPreferences
import com.livetl.android.util.AppPreferences
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.ArgumentMatchers.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ChatFiltererTests {

    private val sharedPrefs = mock<SharedPreferences>()
    private val context = mock<Context>()

    private lateinit var prefs: AppPreferences
    private lateinit var filterer: ChatFilterer

    private val emptyChatMessage = ChatMessage.RegularChat(
        author = MessageAuthor(id = "author", name = "Author"),
        content = emptyList(),
        timestamp = 1L,
        superchatContext = null,
    )
    private val baseChatMessage = emptyChatMessage.copy(
        content = listOf(
            ChatMessageContent.Text("[EN] Hello world"),
            ChatMessageContent.Emoji("id", "src"),
        ),
    )

    @BeforeEach
    fun before() {
        whenever(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPrefs)
        prefs = AppPreferences(context)
        filterer = ChatFilterer(prefs)
    }

    @Test
    fun `ignores empty messages by default`() {
        assertNull(filterer.filterMessage(emptyChatMessage))
    }

    @Test
    fun `always include message if user is moderator and configured`() {
        whenever(sharedPrefs.getBoolean(eq("show_mod_messages"), anyBoolean())).thenReturn(true)

        val mod = MessageAuthor(id = "author", name = "Author", isModerator = true)
        assertNotNull(filterer.filterMessage(baseChatMessage.copy(author = mod)))
    }

    @Test
    fun `always include message if user is verified and configured`() {
        whenever(sharedPrefs.getBoolean(eq("show_verified_messages"), anyBoolean())).thenReturn(true)

        val verified = MessageAuthor(id = "author", name = "Author", isVerified = true)
        assertNotNull(filterer.filterMessage(baseChatMessage.copy(author = verified)))
    }

    @Test
    fun `always include message if user is owner and configured`() {
        whenever(sharedPrefs.getBoolean(eq("show_owner_messages"), anyBoolean())).thenReturn(true)

        val owner = MessageAuthor(id = "author", name = "Author", isOwner = true)
        assertNotNull(filterer.filterMessage(baseChatMessage.copy(author = owner)))
    }

    @Test
    fun `filters basic messages`() {
        whenever(sharedPrefs.getStringSet(eq("tl_langs"), any())).thenReturn(setOf(TranslatedLanguage.ENGLISH.id))

        assertNotNull(filterer.filterMessage(baseChatMessage))
    }

    @Test
    fun `filters message alternate tags`() {
        whenever(sharedPrefs.getStringSet(eq("tl_langs"), any())).thenReturn(setOf(TranslatedLanguage.ENGLISH.id))

        val messages = listOf(
            ChatMessageContent.Text("[英訳/EN] Hello world"),
            ChatMessageContent.Text("[EN TL] Hello world"),
            ChatMessageContent.Text("[EN_TL] Hello world"),
        )

        messages.forEach {
            assertNotNull(filterer.filterMessage(baseChatMessage.copy(content = listOf(it))))
        }
    }

    @Test
    fun `handles tokens that aren't actually language tags`() {
        whenever(sharedPrefs.getStringSet(eq("tl_langs"), any())).thenReturn(setOf(TranslatedLanguage.ENGLISH.id))

        val messages = listOf(
            ChatMessageContent.Text("[] Hello world"),
            ChatMessageContent.Text("Hello world (...)"),
            ChatMessageContent.Text("Hello <> world"),
            ChatMessageContent.Text("| Hello world"),
        )

        messages.forEach {
            assertNull(filterer.filterMessage(baseChatMessage.copy(content = listOf(it))))
        }
    }

    @Test
    fun `does not filter message if language doesn't match`() {
        whenever(sharedPrefs.getStringSet(eq("tl_langs"), any())).thenReturn(setOf(TranslatedLanguage.FRENCH.id))

        assertNull(filterer.filterMessage(baseChatMessage))
    }
}
