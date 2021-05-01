package com.livetl.android.data.stream

import android.content.Context
import me.echeung.youtubeextractor.YouTubeExtractor
import timber.log.Timber

class StreamService(context: Context) {

    private val extractor = YouTubeExtractor(context)

    fun getVideoId(pageUrl: String): String {
        return when {
            LIVETL_URI_REGEX.matches(pageUrl) -> {
                val videoIdWithQuery = LIVETL_URI_REGEX.find(pageUrl)!!.groupValues[1]
                // We don't do anything with the query parameters right now
                val (videoId, _) = videoIdWithQuery.split('?', limit = 2)
                videoId
            }
            else -> extractor.getVideoId(pageUrl)
        }
    }

    suspend fun getStreamInfo(pageUrl: String): StreamInfo {
        Timber.d("Fetching stream: $pageUrl")

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

private val LIVETL_URI_REGEX by lazy {
    "livetl://translate/(.+)".toRegex()
}
