package com.livetl.android.data.chat

import com.livetl.android.util.USER_AGENT
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import javax.inject.Inject

class ChatUrlFetcher @Inject constructor(private val client: HttpClient) {

    suspend fun getChatUrl(videoId: String, isLive: Boolean): String {
        val urlPrefix = "https://www.youtube.com/live_chat"

        if (isLive) {
            return "$urlPrefix?v=$videoId&$EMBED_SUFFIX"
        }

        val result = client.get("https://www.youtube.com/watch?v=$videoId") {
            headers {
                set("User-Agent", USER_AGENT)
            }
        }
        val matches = CHAT_CONTINUATION_PATTERN.matcher(result.bodyAsText())
        if (!matches.find()) {
            throw NoChatContinuationFoundException(videoId)
        }

        val continuation = matches.group(1)
        return "${urlPrefix}_replay?continuation=$continuation&$EMBED_SUFFIX"
    }

    companion object {
        const val EMBED_SUFFIX = "embed_domain=www.livetl.app"
    }
}

class NoChatContinuationFoundException(videoId: String) : Exception("Continuation not found for $videoId")

private val CHAT_CONTINUATION_PATTERN by lazy { """continuation":"([\w%]+)"""".toPattern() }
