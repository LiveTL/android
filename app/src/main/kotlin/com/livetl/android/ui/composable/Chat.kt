package com.livetl.android.ui.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Chat(modifier: Modifier = Modifier) {
    val messages = (1..200).map { "Message #$it" }

    LazyColumn(modifier = modifier) {
        items(messages) { message ->
            Text(message)
        }
    }
}