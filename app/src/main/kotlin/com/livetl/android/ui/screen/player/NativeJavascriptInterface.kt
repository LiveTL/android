package com.livetl.android.ui.screen.player

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.livetl.android.util.runJS
import com.livetl.android.util.runOnMainThread
import timber.log.Timber

class NativeJavascriptInterface(
    private val backgroundWebview: WebView,
    private val foregroundWebview: WebView,
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
    fun toggleFullscreen(isFullscreen: Boolean) {
        Timber.d("Toggling fullscreen: $isFullscreen")
        // TODO
    }

    @Suppress("UNUSED")
    @JavascriptInterface
    fun downloadText(text: String) {
        Timber.d("Downloading text: $text")
        // TODO
    }
}
