package com.livetl.android.ui.screen.home.composable

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.livetl.android.data.feed.model.Stream

@Composable
fun Stream(stream: Stream) {
    Text(stream.title)
}