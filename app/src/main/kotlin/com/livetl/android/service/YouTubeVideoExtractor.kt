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

        val bestVideoFile = result.files!!.values
            .filter { it.format.height != -1 }
            .maxByOrNull { it.format.height }!!
        Log.d("getYouTubeStream", "Best video: $bestVideoFile")

        val bestAudioFile = result.files!!.values
            .filter { it.format.audioBitrate != -1 }
            .maxByOrNull { it.format.audioBitrate }
        Log.d("getYouTubeStream", "Best audio: $bestAudioFile")

        return Stream(
            videoUrl = bestVideoFile.url,
            audioUrl = bestAudioFile?.url,
            title = result.metadata.title,
            author = result.metadata.author,
            shortDescription = result.metadata.shortDescription,
            isLive = result.metadata.isLive,
        )
    }
}

class NoYouTubeStreamFoundException : Exception()