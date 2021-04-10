package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.livetl.android.data.chat.MessageAuthor

@Composable
fun AuthorActionDialog(author: MutableState<MessageAuthor?>) {
    var author by author

    if (author != null) {
        AlertDialog(
            onDismissRequest = { author = null },
            buttons = { },
            title = { Text(author?.name ?: "") },
            text = {
                Text("TODO")
            }
        )
    }
}
