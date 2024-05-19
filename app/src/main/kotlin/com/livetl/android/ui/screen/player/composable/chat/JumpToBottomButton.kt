package com.livetl.android.ui.screen.player.composable.chat

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private enum class Visibility {
    VISIBLE,
    GONE,
}

/**
 * Shows a button that lets the user scroll to the bottom.
 */
@Composable
fun JumpToBottomButton(enabled: Boolean, onClicked: () -> Unit, modifier: Modifier = Modifier) {
    val transition = updateTransition(
        targetState = if (enabled) Visibility.VISIBLE else Visibility.GONE,
        label = "visibility",
    )
    val bottomOffset by transition.animateDp(label = "bottomOffset") {
        if (it == Visibility.GONE) {
            (-32).dp
        } else {
            32.dp
        }
    }
    if (bottomOffset > 0.dp) {
        FloatingActionButton(
            content = {
                Icon(
                    imageVector = Icons.Filled.ArrowDownward,
                    modifier = Modifier.height(18.dp),
                    contentDescription = null,
                )
            },
            onClick = onClicked,
            modifier = modifier
                .offset(x = 0.dp, y = -bottomOffset)
                .height(36.dp),
        )
    }
}

@Preview
@Composable
private fun JumpToBottomPreview() {
    JumpToBottomButton(enabled = true, onClicked = {})
}
