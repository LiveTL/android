package com.livetl.android.ui.screen.player

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.livetl.android.util.runJS
import com.livetl.android.util.runOnMainThread
import timber.log.Timber

class NativeJavascriptInterface(
    private val backgroundWebview: WebView,
    private val foregroundWebview: WebView,
    private val toggleFullscreenCallback: () -> Unit,
    private val downloadTextCallback: (String, String) -> Unit,
) {

    @JavascriptInterface
    fun sendToBackground(data: String) {
        Timber.d("Sending to background: $data")
        runOnMainThread {
            backgroundWebview.runJS("window.postMessage($data, '*');")
        }
    }

    @JavascriptInterface
    fun sendToForeground(data: String) {
        Timber.d("Sending to foreground: $data")
        runOnMainThread {
            foregroundWebview.runJS("window.postMessage($data, '*');")
        }
    }

    @Suppress("UNUSED")
    @JavascriptInterface
    fun toggleFullscreen() {
        Timber.d("Toggling fullscreen")
        toggleFullscreenCallback()
    }

    @Suppress("UNUSED")
    @JavascriptInterface
    fun downloadText(text: String, fileName: String) {
        Timber.d("Downloading text to $fileName: $text")
        downloadTextCallback(text, fileName)
    }
}
