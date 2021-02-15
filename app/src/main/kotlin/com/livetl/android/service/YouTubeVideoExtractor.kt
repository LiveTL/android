package com.livetl.android.service

import android.content.Context
import com.livetl.android.model.StreamInfo
import me.echeung.youtubeextractor.YouTubeExtractor

class YouTubeVideoExtractor(private val context: Context) {

    fun getVideoId(pageUrl: String): String {
        return YouTubeExtractor(context).getVideoId(pageUrl)
    }

    suspend fun getStreamInfo(pageUrl: String): StreamInfo {
        val result = YouTubeExtractor(context).getStreamInfo(pageUrl)

        return StreamInfo(
            videoId = result.videoId,
            title = result.title,
            author = result.author,
            shortDescription = result.shortDescription,
            isLive = result.isLive,
        )
    }
}