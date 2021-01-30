package com.livetl.android.util

import android.util.Log
import me.echeung.youtubeextractor.YouTubeExtractor

suspend fun getYouTubeVideoUrl(pageUrl: String): String {
    val result = YouTubeExtractor().extract(pageUrl)
    Log.d("getYouTubeVideoUrl", "Video metadata: ${result?.metadata}")
    Log.d("getYouTubeVideoUrl", "Extracted videos: ${result?.videos}")

    if (result?.videos == null) {
        throw NoYouTubeVideoUrlFoundException()
    }

    val highestResFormat = result.videos!!.values
        .filter { it.format.ext == "mp4" || it.format.ext == "ts" }
        .maxByOrNull { it.format.height }!!

    Log.d("getYouTubeVideoUrl", "Highest res video: $highestResFormat")
    return highestResFormat.url
}

class NoYouTubeVideoUrlFoundException : Exception()