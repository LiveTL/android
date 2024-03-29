package com.livetl.android.ui.screen.player

import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
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
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.util.collectAsState
import com.livetl.android.util.isTvMode
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
    toggleFullscreen: (Boolean) -> Unit,
    viewModel: PlayerViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val isFullscreen by viewModel.prefs.wasPlayerFullscreen().collectAsState()
    var streamInfo by remember { mutableStateOf<StreamInfo?>(null) }

    DisposableEffect(isFullscreen) {
        toggleFullscreen(isFullscreen)

        onDispose {
            toggleFullscreen(false)
        }
    }

    val webviews =
        remember {
            val backgroundWebView =
                WebView(context).apply {
                    setDefaultSettings()
                    loadUrl("file:///android_asset/background.html")
                }

            val foregroundWebView =
                WebView(context).apply {
                    setDefaultSettings()

                    webViewClient =
                        object : WebViewClient() {
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

            val jsInterface =
                NativeJavascriptInterface(
                    viewModel.webViewLocalStoragePolyfill,
                    backgroundWebView,
                    foregroundWebView,
                    viewModel::toggleFullscreen,
                    viewModel::saveText,
                )
            backgroundWebView.addJavascriptInterface(jsInterface, JS_INTERFACE_NAME)
            foregroundWebView.addJavascriptInterface(jsInterface, JS_INTERFACE_NAME)

            WebViews(jsInterface, backgroundWebView, foregroundWebView)
        }

    if (streamInfo == null) {
        LoadingIndicator()
    } else {
        val modifier =
            if (isFullscreen) {
                Modifier
                    .fillMaxSize()
            } else {
                Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
            }

        AndroidView(
            modifier = modifier,
            factory = {
                if (context.isTvMode()) {
                    CursorLayout(it).apply {
                        addView(webviews.foregroundWebView)
                        requestFocus()
                    }
                } else {
                    webviews.foregroundWebView
                }
            },
        )
    }

    DisposableEffect(Unit) {
        setKeepScreenOn(true)

        onDispose {
            setKeepScreenOn(false)

            webviews.backgroundWebView.destroy()
            webviews.foregroundWebView.destroy()
            webviews.jsInterface.destroy()
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
                            webviews.foregroundWebView.loadUrl(url)
                        } else {
                            webviews.foregroundWebView.loadUrl(
                                "$url&continuation=${newStream.chatContinuation}&isReplay=true",
                            )
                        }
                    }
                } catch (e: Throwable) {
                    Timber.e(e, "Failed to fetch stream info")

                    // Fallback: attempt to load as a live stream
                    val url = "file:///android_asset/watch.html?video=$videoId"
                    webviews.foregroundWebView.loadUrl(url)
                }
            }
        }
    }
}

private const val JS_INTERFACE_NAME = "nativeJavascriptInterface"

private data class WebViews(
    val jsInterface: NativeJavascriptInterface,
    val backgroundWebView: WebView,
    val foregroundWebView: WebView,
)
