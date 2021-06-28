package com.livetl.android.data.chat

import android.annotation.SuppressLint
import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.util.fastForEach
import com.livetl.android.util.epochMicro
import com.livetl.android.util.injectScript
import com.livetl.android.util.readFile
import com.livetl.android.util.runJS
import com.livetl.android.util.toDebugTimestampString
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

@SuppressLint("SetJavaScriptEnabled")
class ChatService @Inject constructor(
    @ApplicationContext context: Context,
    private val json: Json,
    private val client: HttpClient,
) {

    private val webview = WebView(context)

    private var isLive: Boolean = false
    private var currentSecond: Long = 0

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var jobs: List<Job> = mutableListOf()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: SharedFlow<List<ChatMessage>>
        get() = _messages.asSharedFlow()

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

    suspend fun connect(videoId: String, isLive: Boolean) {
        // Clear out previous chat contents, just in case
        stop()

        this.isLive = isLive
        val chatUrl = getChatUrl(videoId, isLive)
        Timber.d("Loading URL: $chatUrl")
        webview.loadUrl(chatUrl)
    }

    fun seekTo(videoId: String, second: Long) {
        if (second != currentSecond) {
            Timber.d("$videoId: seeking to $second")
            webview.runJS("window.postMessage({ 'yt-player-video-progress': $second, video: '$videoId'}, '*');")

            // Clear out messages if we seem to be manually seeking
            if (currentSecond - 10 > second || second > currentSecond + 10) {
                Timber.d("$videoId: manual seek")
                clearMessages()
            }

            currentSecond = second
        }
    }

    fun stop() {
        webview.loadUrl("")
        clearMessages()
    }

    @ExperimentalTime
    @Suppress("Unused")
    @JavascriptInterface
    fun receiveMessages(data: String) {
        val job = scope.launch {
            val nowMicro = epochMicro() - Duration.seconds(CHAT_DELAY_OFFSET_SECS)
                .toDouble(DurationUnit.MICROSECONDS)

            val ytChatMessages = json.decodeFromString<YTChatMessages>(data)

            // TODO: for archive replays, consider jumping around when seeking, pausing, etc.
            ytChatMessages.messages
                .fastForEach {
                    if (isLive) {
                        delay(getMicrosecondDiff(nowMicro.toLong(), it.timestamp))
                        Timber.d("Now: ${epochMicro().toDebugTimestampString()}, message timestamp: ${it.timestamp.toDebugTimestampString()}")
                    } else {
                        delay(Duration.microseconds(it.delay!!))
                    }

                    if (!isActive) {
                        return@launch
                    }

                    val message = it.toChatMessage()
                    _messages.value = (_messages.value + message).takeLast(MAX_MESSAGE_QUEUE_SIZE)
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
            throw NoChatContinuationFoundException(videoId)
        }
    }

    /**
     * Calculates number of microseconds from [now] until [microseconds].
     */
    @ExperimentalTime
    private fun getMicrosecondDiff(now: Long, microseconds: Long): Duration {
        val diff = now - microseconds
        Timber.d("Now: $now (${now.toDebugTimestampString()}), time: $microseconds (${microseconds.toDebugTimestampString()}), diff: $diff")
        return Duration.microseconds(diff)
    }

    private fun clearMessages() {
        jobs.forEach { it.cancel() }
        _messages.value = emptyList()
    }
}

class NoChatContinuationFoundException(videoId: String) : Exception("Continuation not found for $videoId")

private const val MAX_MESSAGE_QUEUE_SIZE = 2500
private const val CHAT_DELAY_OFFSET_SECS = 3L

private const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36 Edg/91.0.864.54"
private val CHAT_CONTINUATION_PATTERN by lazy { """continuation":"(\w+)"""".toPattern() }
