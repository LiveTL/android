package com.livetl.android.util

import android.content.Context
import android.util.Log
import me.echeung.youtubeextractor.YouTubeExtractor

suspend fun getYouTubeVideoUrl(context: Context, pageUrl: String): String {
    val result = YouTubeExtractor(context).extract(pageUrl)
    Log.d("getYouTubeVideoUrl", "Video metadata: ${result?.metadata}")
    Log.d("getYouTubeVideoUrl", "Extracted videos: ${result?.videos}")

    if (result?.videos == null) {
        throw NoYouTubeVideoUrlFoundException()
    }

    val highestResFormat = result.videos!!.toList()
        .filter { it.format.ext == "mp4" || it.format.ext == "ts" }
        .maxByOrNull { it.format.height }!!

    Log.d("getYouTubeVideoUrl", "Highest res video: $highestResFormat")
    return highestResFormat.url
}

class NoYouTubeVideoUrlFoundException : Exception()