package com.livetl.android.data.feed

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Feed(
    val live: List<Stream>,
    val upcoming: List<Stream>,
    val ended: List<Stream>,
)

@Immutable
@Serializable
data class Stream(
    val yt_video_key: String,
    val title: String,
    // e.g. "2021-02-19T13:00:00.000Z"
    val live_schedule: String? = null,
    val live_start: String? = null,
    val live_end: String? = null,
    val live_viewers: Int? = null,
    val channel: Channel,
) {
    fun getThumbnail() = "https://img.youtube.com/vi/$yt_video_key/hqdefault.jpg"
}

@Immutable
@Serializable
data class Channel(
    val name: String,
    val photo: String,
)
