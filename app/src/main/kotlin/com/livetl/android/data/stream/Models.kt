package com.livetl.android.data.stream

import androidx.compose.runtime.Immutable

@Immutable
data class StreamInfo(
    val videoId: String,
    val title: String,
    val author: String,
    val shortDescription: String,
    val thumbnail: String?,
    val isLive: Boolean,
    val chatContinuation: String?,
)
