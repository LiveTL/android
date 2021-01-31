package com.livetl.android.util

import android.content.Context
import android.util.Log
import com.livetl.android.model.Stream
import me.echeung.youtubeextractor.YouTubeExtractor

suspend fun getYouTubeStream(context: Context, pageUrl: String): Stream {
    val result = YouTubeExtractor(context).extract(pageUrl)
    if (result?.files == null) {
        throw NoYouTubeStreamFoundException()
    }

    val highestResVideo = result.files!!.values
        .filter { it.format.ext == "mp4" || it.format.ext == "ts" }
        .maxByOrNull { it.format.height }!!
    Log.d("getYouTubeStream", "Highest res video: $highestResVideo")

    return Stream(
        videoUrl = highestResVideo.url,
        audioUrl = null,
        title = result.metadata.title,
        author = result.metadata.author,
        shortDescription = result.metadata.shortDescription,
        isLive = result.metadata.isLive,
    )
}

class NoYouTubeStreamFoundException : Exception()