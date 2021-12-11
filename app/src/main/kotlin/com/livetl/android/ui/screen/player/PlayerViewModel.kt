package com.livetl.android.ui.screen.player

import android.content.Context
import android.webkit.MimeTypeMap
import android.webkit.WebResourceResponse
import androidx.lifecycle.ViewModel
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.data.stream.USER_AGENT
import com.livetl.android.data.stream.VideoIdParser
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.createScriptTag
import com.livetl.android.util.readFile
import com.livetl.android.util.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.features.expectSuccess
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
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
    val prefs: PreferencesHelper,
) : ViewModel() {

    fun getVideoId(urlOrId: String): String {
        return videoIdParser.getVideoId(urlOrId)
    }

    suspend fun getStreamInfo(videoId: String): StreamInfo {
        return streamService.getStreamInfo(videoId)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
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
        if (url.startsWith(FILE_ASSET)) {
            val assetPath = url.substringAfter(FILE_ASSET)
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
                    "polyfill.bundle.js",
                    "chat-interceptor.bundle.js",
                    "chat.bundle.js",
                    "injector.bundle.js",
                    "translatormode.bundle.js",
                )
            }
            // "https://www.youtube.com/error*?*"
            url.startsWith("https://www.youtube.com/error") -> {
                listOf(
                    "polyfill.bundle.js",
                    "workaround-injector.bundle.js",
                )
            }
            // "https://www.twitch.tv/*"
            url.startsWith("https://www.twitch.tv/") -> {
                listOf(
                    "polyfill.bundle.js",
                    "twitch-injector.bundle.js",
                )
            }
            else -> emptyList()
        }

        if (scriptsToInject.isEmpty()) {
            return@withContext null
        }

        val response = client.get<HttpResponse>(url) {
            // Don't throw an exception on the error 404 page that's
            // used for the player
            expectSuccess = false

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
            ByteArrayInputStream((response.readText() + scripts).toByteArray(StandardCharsets.UTF_8)),
        )
    }

    fun toggleFullscreen() {
        prefs.wasPlayerFullscreen().toggle()
    }
}

private const val LOCAL_ASSET_BASEURL = "https://__local_android_asset_baseurl__/"
private const val FILE_ASSET = "file:///"
