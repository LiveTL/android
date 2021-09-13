package com.livetl.android.ui.screen.player

import android.content.Context
import android.webkit.MimeTypeMap
import android.webkit.WebResourceResponse
import androidx.lifecycle.ViewModel
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.data.stream.StreamService
import com.livetl.android.data.stream.USER_AGENT
import com.livetl.android.data.stream.VideoIdParser
import com.livetl.android.util.DownloadUtil
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.await
import com.livetl.android.util.createScriptTag
import com.livetl.android.util.get
import com.livetl.android.util.readFile
import com.livetl.android.util.toggle
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import java.io.ByteArrayInputStream
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val streamService: StreamService,
    private val videoIdParser: VideoIdParser,
    private val downloadUtil: DownloadUtil,
    private val okhttpClient: OkHttpClient,
    val prefs: PreferencesHelper,
) : ViewModel() {

    fun getVideoId(urlOrId: String): String {
        return videoIdParser.getVideoId(urlOrId)
    }

    suspend fun getStreamInfo(videoId: String): StreamInfo {
        return streamService.getStreamInfo(videoId)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getInterceptedResponse(context: Context, url: String): WebResourceResponse? = withContext(Dispatchers.IO) {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)

        // Load local assets from APK
        if (url.startsWith(LOCAL_ASSET_BASEURL)) {
            val assetPath = url.substringAfter(LOCAL_ASSET_BASEURL)
            return@withContext WebResourceResponse(
                mimeType,
                Charsets.UTF_8.toString(),
                context.assets.open(assetPath),
            )
        }

        // TODO: parse manifest.json to do this
        // Mimics script injection defined in extension's manifest.json
        val scriptsToInject = when {
            // "https://www.youtube.com/live_chat*",
            // "https://www.youtube.com/live_chat_replay*"
            url.startsWith("https://www.youtube.com/live_chat") -> listOf(
                "chat-interceptor.bundle.js",
                "injector.bundle.js",
                "chat.bundle.js",
                "translatormode.bundle.js",
            )
            // "https://www.youtube.com/error*?*"
            url.startsWith("https://www.youtube.com/error") -> listOf(
                "video_embedder.bundle.js",
            )
            else -> emptyList()
        }

        val shouldStripHeaders = url.startsWith("https://www.youtube.com/")

        if (scriptsToInject.isEmpty() && !shouldStripHeaders) {
            return@withContext null
        }

        val response = okhttpClient.await(
            get {
                url(url)
                addHeader("User-Agent", USER_AGENT)
            }
        )

        val strippedResponse = when (shouldStripHeaders) {
            true -> response.newBuilder()
                .removeHeader("content-security-policy")
                .removeHeader("x-frame-options")
                .build()
            false -> response
        }

        val scripts = scriptsToInject
            .map { context.readFile(it) }
            .joinToString("\n") { createScriptTag(it) }

        WebResourceResponse(
            mimeType,
            Charsets.UTF_8.toString(),
            ByteArrayInputStream((strippedResponse.body!!.string() + scripts).toByteArray(Charsets.UTF_8)),
        )
    }

    fun toggleFullscreen() {
        prefs.wasPlayerFullscreen().toggle()
    }

    fun downloadText(text: String, fileName: String) {
        downloadUtil.saveTextToStorage(text, fileName)
    }
}

private const val LOCAL_ASSET_BASEURL = "https://__local_android_asset_baseurl__/"
