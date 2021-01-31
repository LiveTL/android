package com.livetl.android.util

import android.content.Context
import android.util.Log
import com.livetl.android.model.Stream
import me.echeung.youtubeextractor.YouTubeExtractor

suspend fun getYouTubeStream(context: Context, pageUrl: String): Stream {
    val result = YouTubeExtractor(context).extract(pageUrl)
    Log.d("getYouTubeVideoUrl", "Video metadata: ${result?.metadata}")
    Log.d("getYouTubeVideoUrl", "Extracted videos: ${result?.files}")

    if (result?.files == null) {
        throw NoYouTubeVideoUrlFoundException()
    }

    val highestResFormat = result.files!!.values
        .filter { it.format.ext == "mp4" || it.format.ext == "ts" }
        .maxByOrNull { it.format.height }!!

    Log.d("getYouTubeVideoUrl", "Highest res video: $highestResFormat")
    return Stream(
        videoUrl = highestResFormat.url,
        audioUrl = null,
        title = result.metadata.title,
        author = result.metadata.author,
        shortDescription = result.metadata.shortDescription,
        isLive = result.metadata.isLive,
    )
}

class NoYouTubeVideoUrlFoundException : Exception()