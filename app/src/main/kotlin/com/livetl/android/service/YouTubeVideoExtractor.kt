package com.livetl.android.service

import android.content.Context
import com.livetl.android.model.StreamInfo
import me.echeung.youtubeextractor.YouTubeExtractor

object YouTubeVideoExtractor {

    fun getVideoId(context: Context, pageUrl: String): String {
        return YouTubeExtractor(context).getVideoId(pageUrl)
    }

    suspend fun getStreamInfo(context: Context, pageUrl: String): StreamInfo {
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