package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.plusAssign
import org.koin.androidx.compose.get

@Composable
fun MessageActionsDialog(
    message: MutableState<ChatMessage?>,
    prefs: PreferencesHelper = get(),
) {
    val clipboardManager = LocalClipboardManager.current
    var message by message

    if (message != null) {
        fun onDismiss() {
            message = null
        }

        AlertDialog(
            onDismissRequest = { onDismiss() },
            text = {
                message?.let { Message(message = it) }
            },
            buttons = {
                TextButton(
                    onClick = {
                        message?.let {
                            prefs.allowedUsers() += it.author.id
                        }
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.action_allow_author))
                }
                TextButton(
                    onClick = {
                        message?.let {
                            prefs.blockedUsers() += it.author.id
                        }
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.action_block_author))
                }
                TextButton(
                    onClick = {
                        message?.let {
                            clipboardManager.setText(AnnotatedString(it.getTextContent()))
                        }
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.action_copy_text))
                }
            },
        )
    }
}
