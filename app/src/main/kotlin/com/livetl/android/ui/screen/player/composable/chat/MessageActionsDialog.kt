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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.R
import com.livetl.android.data.chat.ChatMessage
import com.livetl.android.vm.PlayerViewModel

@Composable
fun MessageActionsDialog(
    message: MutableState<ChatMessage?>,
    playerViewModel: PlayerViewModel = viewModel(),
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
                        message?.let { playerViewModel.allowUser(it.author) }
                        onDismiss()
                    }
                ) {
                    Text(stringResource(R.string.action_allow_author))
                }

                TextButton(
                    onClick = {
                        message?.let { playerViewModel.blockUser(it.author) }
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
