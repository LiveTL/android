package com.livetl.android.data.stream

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StreamInfo(
    val videoId: String,
    val title: String,
    val author: String,
    val shortDescription: String,
    val isLive: Boolean,
) : Parcelable
