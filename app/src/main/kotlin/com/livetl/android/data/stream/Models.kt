package com.livetl.android.data.stream

data class StreamInfo(
    val videoId: String,
    val title: String,
    val author: String,
    val shortDescription: String,
    val isLive: Boolean,
    val chatContinuation: String?,
)
