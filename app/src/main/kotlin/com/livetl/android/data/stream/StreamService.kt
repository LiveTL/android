package com.livetl.android.data.stream

import com.livetl.android.util.USER_AGENT
import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
import javax.inject.Inject

class StreamService @Inject constructor(
    private val streamRepository: StreamRepository,
    private val videoIdParser: VideoIdParser,
    private val client: HttpClient,
) {
    suspend fun getStreamInfo(pageUrl: String): StreamInfo {
        val videoId = videoIdParser.getVideoId(pageUrl)

        return try {
            logcat { "Fetching stream: $videoId" }
            val stream = streamRepository.getStream(videoId)

            val chatContinuation: String? =
                when (stream.isLive) {
                    true -> null
                    false -> getChatContinuation(stream.id)
                }

            StreamInfo(
                videoId = stream.id,
                title = stream.title,
                author = stream.channel.name,
                shortDescription = stream.description,
                thumbnail = stream.thumbnail,
                isLive = stream.isLive,
                chatContinuation = chatContinuation,
            )
        } catch (e: ClientRequestException) {
            logcat(LogPriority.ERROR) { "Error getting video info for $videoId from HoloDex: ${e.asLog()}" }

            StreamInfo(
                videoId = videoId,
                title = "",
                author = "",
                shortDescription = "",
                thumbnail = null,
                isLive = false,
                chatContinuation = getChatContinuation(videoId),
            )
        }
    }

    suspend fun findStreamInfo(title: String, channelName: String): StreamInfo? =
        streamRepository.findStream(title, channelName)?.let {
            getStreamInfo(it.id)
        }

    private suspend fun getChatContinuation(videoId: String): String? {
        val result = client.get("https://www.youtube.com/watch?v=$videoId") {
            headers {
                set("User-Agent", USER_AGENT)
            }
        }
        val matches = CHAT_CONTINUATION_PATTERN.matcher(result.bodyAsText())
        return if (matches.find()) {
            matches.group(1)
        } else {
            logcat(LogPriority.WARN) { "Chat continuation not found for $videoId" }
            null
        }
    }
}

private val CHAT_CONTINUATION_PATTERN by lazy { """continuation":"(\w+)"""".toPattern() }
