package com.livetl.android.data.feed

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
data class Feed(
    val live: List<Stream>,
    val upcoming: List<Stream>,
    val ended: List<Stream>,
)

@Immutable
@Serializable
data class HolodexResponse(
    val total: Int,
    val items: List<Stream>,
)

@Immutable
@Serializable
data class Stream(
    val id: String,
    val title: String,
    val status: String,
    // e.g. "2021-07-19T14:00:00.000Z"
    val start_scheduled: String,
    val start_actual: String? = null,
    val end_actual: String? = null,
    val description: String,
    val channel: Channel,
) {
    val thumbnail: String
        get() = "https://i.ytimg.com/vi/$id/maxresdefault.jpg"
}

@Immutable
@Serializable
data class Channel(
    val name: String,
    val org: String,
    val photo: String,
)
