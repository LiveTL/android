package com.livetl.android.util

import android.content.Context
import android.util.Log
import android.util.SparseArray
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

suspend fun getYouTubeVideoUrl(context: Context, pageUrl: String): String {
    return suspendCoroutine { cont ->
        object : YouTubeExtractor(context) {
            override fun onExtractionComplete(ytFiles: SparseArray<YtFile>?, vMeta: VideoMeta?) {
                Log.d("getYouTubeVideoUrl", "Video metadata: $vMeta")
                if (ytFiles != null) {
                    Log.d("getYouTubeVideoUrl", "Extracted files: $ytFiles")

                    val highestResFormat = ytFiles.toList()
                        .filter { it.format.ext == "mp4" }
                        .maxByOrNull { it.format.height }!!

                    Log.d("getYouTubeVideoUrl", "Highest res file: $highestResFormat")
                    cont.resume(highestResFormat.url)
                } else {
                    cont.resumeWithException(NoYouTubeVideoUrlFound())
                }
            }
        }.extract(pageUrl, true, true)
    }
}

class NoYouTubeVideoUrlFound : Exception()