package com.livetl.android.data.chat

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText

class ChatService(private val client: HttpClient) {

    suspend fun getChatUrl(videoId: String, isLive: Boolean): String {
        if (isLive) {
            return "$CHAT_URL_PREFIX?v=$videoId&embed_domain=www.livetl.app&app=desktop"
        }

        val result = client.get<HttpResponse>("https://www.youtube.com/watch?v=$videoId") {
            headers {
                set("User-Agent", USER_AGENT)
            }
        }
        val matches = CHAT_CONTINUATION_PATTERN.matcher(result.readText())
        if (matches.find()) {
            return "${CHAT_URL_PREFIX}_replay?v=$videoId&continuation=${matches.group(1)}&embed_domain=www.livetl.app&app=desktop"
        } else {
            throw NoChatUrlFoundException()
        }
    }

    companion object {
        private const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1.2 Safari/605.1.15"

        private val CHAT_CONTINUATION_PATTERN = """continuation":"(\w+)"""".toPattern()

        private const val CHAT_URL_PREFIX = "https://www.youtube.com/live_chat"
    }
}

class NoChatUrlFoundException : Exception()