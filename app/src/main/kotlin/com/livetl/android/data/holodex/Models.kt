package com.livetl.android.data.holodex

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class HolodexVideosResponse(
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

    val isLive: Boolean
        get() = status == "live"
}

@Immutable
@Serializable
data class Channel(
    val name: String,
    val org: String,
    val photo: String,
)
