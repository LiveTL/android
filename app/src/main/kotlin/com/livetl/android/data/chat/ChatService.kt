package com.livetl.android.data.chat

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.util.fastForEach
import com.livetl.android.util.epochMicro
import com.livetl.android.util.injectScript
import com.livetl.android.util.readAssetFile
import com.livetl.android.util.runJS
import com.livetl.android.util.setDefaultSettings
import com.livetl.android.util.toDebugTimestampString
import com.livetl.android.util.withUIContext
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.util.flattenEntries
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import logcat.logcat
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class ChatService @Inject constructor(
    @ApplicationContext context: Context,
    private val json: Json,
    private val chatUrlFetcher: ChatUrlFetcher,
    private val client: HttpClient,
) {

    private val webview by lazy {
        WebView(context).apply {
            setDefaultSettings()
            addJavascriptInterface(this@ChatService, "livetl")
            webViewClient = object : WebViewClient() {
                // Overwrite "Content-Security-Policy": "require-trusted-types-for 'script'"
                // so that we can inject our interceptor script to get the chat data
                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): WebResourceResponse? {
                    if (request == null || request.url == null) {
                        return null
                    }

                    val url = request.url.toString()

                    if (ChatUrlFetcher.EMBED_SUFFIX !in url) {
                        return super.shouldInterceptRequest(view, request)
                    }

                    return runBlocking {
                        val result = client.get(url) {
                            headers {
                                request.requestHeaders.forEach { (name, value) -> set(name, value) }
                            }
                        }

                        WebResourceResponse(
                            MimeTypeMap.getFileExtensionFromUrl(url),
                            StandardCharsets.UTF_8.name(),
                            result.status.value,
                            result.status.description,
                            result.headers.flattenEntries().toMap() - "content-security-policy",
                            result.body(),
                        )
                    }
                }

                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    injectScript(context.readAssetFile("ChatInjector.js"))
                }
            }
        }
    }

    private var isLive: Boolean = false
    private var currentSecond: Long = 0

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var jobs: List<Job> = mutableListOf()

    val messages = MutableStateFlow<ImmutableList<ChatMessage>>(persistentListOf())

    suspend fun connect(videoId: String, isLive: Boolean) {
        // Clear out previous chat contents, just in case
        stop()

        this.isLive = isLive
        val chatUrl = chatUrlFetcher.getChatUrl(videoId, isLive)
        logcat { "Loading URL: $chatUrl" }
        webview.loadUrl(chatUrl)
    }

    suspend fun seekTo(videoId: String, second: Long) {
        if (second != currentSecond) {
            logcat { "Seeking to $second" }
            withUIContext {
                webview.runJS("window.postMessage({ 'yt-player-video-progress': $second, video: '$videoId'}, '*');")
            }

            // Clear out messages if we seem to be manually seeking
            if (currentSecond - 10 > second || second > currentSecond + 10) {
                logcat { "Manual seek" }
                clearMessages()
            }

            currentSecond = second
        }
    }

    fun stop() {
        webview.loadUrl("")
        clearMessages()
    }

    @Suppress("Unused")
    @JavascriptInterface
    fun receiveMessages(data: String) {
        val job = scope.launch {
            val nowMicro = epochMicro() - CHAT_DELAY_OFFSET_SECS.seconds
                .toDouble(DurationUnit.MICROSECONDS)

            val ytChatMessages = json.decodeFromString<YTChatMessages>(data)

            // TODO: for archive replays, consider jumping around when seeking, pausing, etc.
            ytChatMessages.messages
                .fastForEach {
                    if (isLive) {
                        delay(getMicrosecondDiff(nowMicro.toLong(), it.timestamp))
                        logcat {
                            "Now: ${epochMicro().toDebugTimestampString()}, message timestamp: ${it.timestamp.toDebugTimestampString()}"
                        }
                    } else {
                        delay(it.delay!!.microseconds)
                    }

                    if (!isActive) {
                        return@launch
                    }

                    val message = it.toChatMessage()
                    messages.getAndUpdate { oldMessages ->
                        (oldMessages + message)
                            .distinct()
                            .takeLast(MAX_MESSAGE_QUEUE_SIZE)
                            .toImmutableList()
                    }
                }
        }

        jobs += job
        job.invokeOnCompletion {
            jobs -= job
        }
    }

    /**
     * Calculates number of microseconds from [now] until [microseconds].
     */
    private fun getMicrosecondDiff(now: Long, microseconds: Long): Duration {
        val diff = now - microseconds
        logcat {
            "Now: $now (${now.toDebugTimestampString()}), time: $microseconds (${microseconds.toDebugTimestampString()}), diff: $diff"
        }
        return diff.microseconds
    }

    private fun clearMessages() {
        jobs.forEach { it.cancel() }
        messages.update { persistentListOf() }
    }
}

private const val MAX_MESSAGE_QUEUE_SIZE = 2500
private const val CHAT_DELAY_OFFSET_SECS = 3L
