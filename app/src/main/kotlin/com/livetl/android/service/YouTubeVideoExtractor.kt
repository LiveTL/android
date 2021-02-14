package com.livetl.android.service

import android.content.Context
import android.util.Log
import com.livetl.android.model.Stream
import me.echeung.youtubeextractor.YouTubeExtractor

object YouTubeVideoExtractor {

    suspend fun getStream(context: Context, pageUrl: String): Stream {
        val result = YouTubeExtractor(context).extract(pageUrl)
        if (result?.files == null) {
            throw NoYouTubeStreamFoundException()
        }

        return Stream(
            videoId = result.metadata.videoId,
            title = result.metadata.title,
            author = result.metadata.author,
            shortDescription = result.metadata.shortDescription,
            isLive = result.metadata.isLive,
        )
    }
}

class NoYouTubeStreamFoundException : Exception()