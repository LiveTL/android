package com.livetl.android.data.feed

import androidx.compose.runtime.Immutable
import com.livetl.android.data.holodex.Stream

@Immutable
data class Feed(
    val live: List<Stream>,
    val upcoming: List<Stream>,
    val ended: List<Stream>,
)
