package com.livetl.android.data.stream

import android.content.Context
import me.echeung.youtubeextractor.YouTubeExtractor

class StreamService(context: Context) {

    private val extractor = YouTubeExtractor(context)

    fun getVideoId(pageUrl: String): String {
        return extractor.getVideoId(pageUrl)
    }

    suspend fun getStreamInfo(pageUrl: String): StreamInfo {
        val result = extractor.getStreamInfo(pageUrl)

        return StreamInfo(
            videoId = result.videoId,
            title = result.title,
            author = result.author,
            shortDescription = result.shortDescription,
            isLive = result.isLive,
        )
    }
}
