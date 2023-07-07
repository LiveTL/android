package com.livetl.android.ui.screen.player

import android.content.Context
import android.webkit.MimeTypeMap
import android.webkit.WebResourceResponse
import androidx.lifecycle.ViewModel
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.data.stream.USER_AGENT
import com.livetl.android.data.stream.VideoIdParser
import com.livetl.android.util.AppPreferences
import com.livetl.android.util.WebViewStoragePolyfill
import com.livetl.android.util.createScriptTag
import com.livetl.android.util.readFile
import com.livetl.android.util.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val streamService: StreamService,
    private val videoIdParser: VideoIdParser,
    private val client: HttpClient,
    val webViewLocalStoragePolyfill: WebViewStoragePolyfill,
    val prefs: AppPreferences,
) : ViewModel() {

    fun getVideoId(urlOrId: String): String {
        return videoIdParser.getVideoId(urlOrId)
    }

    suspend fun getStreamInfo(videoId: String): StreamInfo {
        return streamService.getStreamInfo(videoId)
    }

    suspend fun getInjectedResponse(context: Context, url: String): WebResourceResponse? = withContext(Dispatchers.IO) {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)

        // Load local assets from APK
        if (url.startsWith(LOCAL_ASSET_BASEURL)) {
            val assetPath = url.substringAfter(LOCAL_ASSET_BASEURL)
            return@withContext WebResourceResponse(
                mimeType,
                StandardCharsets.UTF_8.toString(),
                context.assets.open(assetPath),
            )
        }

        // Mimics script injection defined in extension's manifest.json
        val scriptsToInject = when {
            // "https://www.youtube.com/live_chat*",
            // "https://www.youtube.com/live_chat_replay*"
            url.startsWith("https://www.youtube.com/live_chat") -> {
                listOf(
                    "chat-interceptor.bundle.js",
                    "chat.bundle.js",
                    "injector.bundle.js",
                    "translatormode.bundle.js",
                )
            }
            url.startsWith("https://www.youtube.com/error") -> {
                listOf(
                    "video_embedder.bundle.js",
                )
            }
            else -> emptyList()
        }

        if (scriptsToInject.isEmpty()) {
            return@withContext null
        }

        val response = client.get(url) {
            headers {
                set("User-Agent", USER_AGENT)
            }
        }

        val scripts = scriptsToInject
            .map { context.readFile(it) }
            .joinToString("\n") { createScriptTag(it) }

        WebResourceResponse(
            mimeType,
            StandardCharsets.UTF_8.toString(),
            ByteArrayInputStream((response.bodyAsText() + scripts).toByteArray(StandardCharsets.UTF_8)),
        )
    }

    fun toggleFullscreen() {
        prefs.wasPlayerFullscreen().toggle()
    }

    fun saveText(text: String, fileName: String) {
        // TODO
    }
}

private const val LOCAL_ASSET_BASEURL = "https://__local_android_asset_baseurl__/"
