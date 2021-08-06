package com.livetl.android.data.stream

import com.livetl.android.data.holodex.HoloDexService
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import timber.log.Timber
import javax.inject.Inject

class StreamService @Inject constructor(
    private val holoDexService: HoloDexService,
    private val videoIdParser: VideoIdParser,
    private val client: HttpClient,
) {

    suspend fun getStreamInfo(pageUrl: String): StreamInfo {
        val videoId = videoIdParser.getVideoId(pageUrl)
        Timber.d("Fetching stream: $videoId")
        val stream = holoDexService.getVideoInfo(videoId)

        val chatContinuation: String? = when (stream.isLive) {
            true -> null
            false -> getChatContinuation(stream.id)
        }

        return StreamInfo(
            videoId = stream.id,
            title = stream.title,
            author = stream.channel.name,
            shortDescription = stream.description,
            isLive = stream.isLive,
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
