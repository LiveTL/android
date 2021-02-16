package com.livetl.android.data.feed.model

import kotlinx.serialization.Serializable

@Serializable
data class Feed(
    val live: List<Stream>,
    val upcoming: List<Stream>,
    val ended: List<Stream>,
)

@Serializable
data class Stream(
    val yt_video_key: String,
    val title: String,
    val live_schedule: String,
    val live_start: String?,
    val live_end: String?,
    val live_viewers: Int?,
    val channel: Channel,
)

@Serializable
data class Channel(
    val name: String,
    val photo: String,
)