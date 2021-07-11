package com.livetl.android.data.stream

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import me.echeung.youtubeextractor.YouTubeExtractor
import timber.log.Timber
import javax.inject.Inject

class StreamService @Inject constructor(
    @ApplicationContext context: Context,
    private val client: HttpClient,
) {

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

        val chatContinuation: String? = when (result.isLive) {
            true -> null
            false -> getChatContinuation(result.videoId)
        }

        return StreamInfo(
            videoId = result.videoId,
            title = result.title,
            author = result.author,
            shortDescription = result.shortDescription,
            isLive = result.isLive,
            chatContinuation = chatContinuation,
        )
    }

    private suspend fun getChatContinuation(videoId: String): String? {
        val result = client.get<HttpResponse>("https://www.youtube.com/watch?v=$videoId") {
            headers {
                set("User-Agent", USER_AGENT)
            }
        }
        val matches = CHAT_CONTINUATION_PATTERN.matcher(result.readText())
        if (matches.find()) {
            return matches.group(1)
        } else {
            // TODO: need to handle this in the UI
            throw NoChatContinuationFoundException(videoId)
        }
    }
}

class NoChatContinuationFoundException(videoId: String) : Exception("Continuation not found for $videoId")

const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36 Edg/91.0.864.54"
private val CHAT_CONTINUATION_PATTERN by lazy { """continuation":"(\w+)"""".toPattern() }
private val LIVETL_URI_REGEX by lazy { "livetl://translate/(.+)".toRegex() }
