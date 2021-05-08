package com.livetl.android.ui.screen.player.composable.chat

import androidx.collection.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.google.accompanist.coil.rememberCoilPainter
import com.livetl.android.data.chat.ChatMessageContent

class EmojiCache {
    val cache = LruCache<String, InlineTextContent>(50)

    fun get(emote: ChatMessageContent.Emoji): InlineTextContent {
        val cached = cache.get(emote.id)
        return if (cached != null) {
            cached
        } else {
            val newContent = InlineTextContent(
                placeholder = Placeholder(1.5.em, 1.em, PlaceholderVerticalAlign.Center),
                children = {
                    Column {
                        Image(
                            painter = rememberCoilPainter(emote.src),
                            contentDescription = null,
                            modifier = Modifier
                                .requiredWidth(20.dp)
                                .aspectRatio(1f)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            )
            cache.put(emote.id, newContent)
            newContent
        }
    }
}
