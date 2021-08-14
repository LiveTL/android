package com.livetl.android.ui.screen.player

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.util.setDefaultSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber

@Composable
fun PlayerScreen(
    videoId: String,
    setKeepScreenOn: (Boolean) -> Unit,
    toggleFullscreen: (Boolean?) -> Unit,
    viewModel: PlayerViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var streamInfo by remember { mutableStateOf<StreamInfo?>(null) }

    val webviews = remember {
        val backgroundWebview = WebView(context).apply {
            setDefaultSettings()
            loadUrl("file:///android_asset/background.html")
        }

        val foregroundWebview = WebView(context).apply {
            setDefaultSettings()

            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest,
                ): WebResourceResponse? {
                    val url = request.url.toString()

                    return runBlocking { viewModel.getInjectedResponse(context, url) }
                        ?: super.shouldInterceptRequest(view, request)
                }
            }
        }

        val jsInterface = NativeJavascriptInterface(
            backgroundWebview,
            foregroundWebview,
            toggleFullscreen,
        )
        backgroundWebview.addJavascriptInterface(jsInterface, JS_INTERFACE_NAME)
        foregroundWebview.addJavascriptInterface(jsInterface, JS_INTERFACE_NAME)

        WebViews(backgroundWebview, foregroundWebview)
    }

    if (streamInfo == null) {
        LoadingIndicator()
    } else {
        AndroidView(
            factory = { webviews.foregroundWebview },
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    rememberInsetsPaddingValues(
                        insets = LocalWindowInsets.current.systemBars,
                        applyTop = true,
                        applyBottom = true,
                    )
                )
        )
    }

    DisposableEffect(Unit) {
        setKeepScreenOn(true)
        toggleFullscreen(viewModel.prefs.wasPlayerFullscreen().get())

        onDispose {
            setKeepScreenOn(false)
            toggleFullscreen(false)

            webviews.backgroundWebview.destroy()
            webviews.foregroundWebview.destroy()
        }
    }

    LaunchedEffect(videoId) {
        if (videoId.isNotEmpty()) {
            coroutineScope.launch {
                try {
                    val newStream = viewModel.getStreamInfo(videoId)
                    withContext(Dispatchers.Main) {
                        streamInfo = newStream

                        val url = "file:///android_asset/watch.html?video=$videoId"
                        if (newStream.isLive) {
                            webviews.foregroundWebview.loadUrl(url)
                        } else {
                            webviews.foregroundWebview.loadUrl(url + "&continuation=${newStream.chatContinuation}&isReplay=true")
                        }
                    }
                } catch (e: Throwable) {
                    Timber.e(e, "Failed to fetch stream info")

                    // Fallback: attempt to load as a live stream
                    val url = "file:///android_asset/watch.html?video=$videoId"
                    webviews.foregroundWebview.loadUrl(url)
                }
            }
        }
    }
}

private const val JS_INTERFACE_NAME = "nativeJavascriptInterface"

private data class WebViews(
    val backgroundWebview: WebView,
    val foregroundWebview: WebView,
)
