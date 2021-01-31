package com.livetl.android.model

data class Stream(
    val videoUrl: String,
    val audioUrl: String?,
    val title: String,
    val author: String,
    val shortDescription: String,
    val isLive: Boolean,
)