package com.livetl.android.data.chat

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.util.fastForEach
import com.livetl.android.util.injectScript
import com.livetl.android.util.readFile
import com.livetl.android.util.runJS
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime
import kotlin.time.microseconds

@SuppressLint("SetJavaScriptEnabled")
class ChatService(
    context: Context,
    private val json: Json,
    private val client: HttpClient,
) {

    private val webview = WebView(context)
    init {
        with(webview.settings) {
            javaScriptEnabled = true
            domStorageEnabled = true
            userAgentString = USER_AGENT
        }
        webview.addJavascriptInterface(this, "Android")
        webview.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                webview.injectScript(context.readFile("ChatInjector.js"))
            }
        }
    }

    private var currentSecond: Long = 0

    private val scope = CoroutineScope(Dispatchers.IO)
    private var jobs: List<Job> = mutableListOf()
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>>
        get() = _messages

    suspend fun load(videoId: String, isLive: Boolean) {
        // Clear out previous chat contents, just in case
        stop()

        val chatUrl = getChatUrl(videoId, isLive)
        Log.d("ChatService", "Loading URL: $chatUrl")
        webview.loadUrl(chatUrl)
    }

    fun seekTo(videoId: String, second: Long) {
        if (second != currentSecond) {
            Log.d("ChatService", "Seeking to $second for video $videoId")
            webview.runJS("window.postMessage({ 'yt-player-video-progress': $second, video: '$videoId'}, '*');")
            currentSecond = second
        }
    }

    fun stop() {
        webview.loadUrl("")
        jobs.forEach { it.cancel() }
        _messages.value = emptyList()
    }

    @ExperimentalTime
    @Suppress("Unused")
    @JavascriptInterface
    fun receiveMessages(data: String) {
        val job = scope.launch {
            val ytChatMessages = json.decodeFromString<YTChatMessages>(data)

            // TODO: for archive replays, consider jumping around when seeking, pausing, etc.
            ytChatMessages.messages
                .fastForEach {
                    delay(it.delay.microseconds)
                    if (!isActive) {
                        return@launch
                    }

                    val message = it.toChatMessage()
                    _messages.value = (_messages.value + message).takeLast(250)
                }
        }

        jobs += job
        job.invokeOnCompletion {
            jobs -= job
        }
    }

    private suspend fun getChatUrl(videoId: String, isLive: Boolean): String {
        val urlPrefix = "https://www.youtube.com/live_chat"

        if (isLive) {
            return "$urlPrefix?v=$videoId&embed_domain=www.livetl.app&app=desktop"
        }

        val result = client.get<HttpResponse>("https://www.youtube.com/watch?v=$videoId") {
            headers {
                set("User-Agent", USER_AGENT)
            }
        }
        val matches = CHAT_CONTINUATION_PATTERN.matcher(result.readText())
        if (matches.find()) {
            return "${urlPrefix}_replay?v=$videoId&continuation=${matches.group(1)}&embed_domain=www.livetl.app&app=desktop"
        } else {
            throw NoChatContinuationFoundException()
        }
    }
}

class NoChatContinuationFoundException : Exception()

private const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/11.1.2 Safari/605.1.15"
private val CHAT_CONTINUATION_PATTERN by lazy { """continuation":"(\w+)"""".toPattern() }
