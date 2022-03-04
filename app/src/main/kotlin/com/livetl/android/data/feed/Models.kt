package com.livetl.android.data.feed

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.livetl.android.R
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
        get() = status == StreamStatus.LIVE.apiValue
}

enum class StreamStatus(
    val apiValue: String,
    val sortField: String,
    val sortAscending: Boolean = true,
    @StringRes val headingRes: Int,
    @StringRes val timestampFormatStringRes: Int? = null,
    val timestampSupplier: (Stream) -> String?,
) {
    LIVE(
        apiValue = "live",
        sortField = "start_actual",
        sortAscending = false,
        headingRes = R.string.live,
        timestampFormatStringRes = R.string.started_streaming,
        timestampSupplier = { it.start_actual },
    ),
    PAST(
        apiValue = "past",
        sortField = "end_actual",
        sortAscending = false,
        headingRes = R.string.archives,
        timestampFormatStringRes = R.string.streamed,
        timestampSupplier = { it.end_actual },
    ),
    UPCOMING(
        apiValue = "upcoming",
        sortField = "start_scheduled",
        headingRes = R.string.upcoming,
        timestampSupplier = { it.start_scheduled },
    ),
}

@Immutable
@Serializable
data class Channel(
    val name: String,
    val photo: String,
    val org: String? = null,
)
