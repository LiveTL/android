package com.livetl.android.model

data class Stream(
    val videoId: String,
    val title: String,
    val author: String,
    val shortDescription: String,
    val isLive: Boolean,
)