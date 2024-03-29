package com.livetl.android.ui.screen.player

import android.content.SharedPreferences
import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.livetl.android.util.WebViewStoragePolyfill
import com.livetl.android.util.runJS
import com.livetl.android.util.runOnMainThread
import kotlinx.coroutines.DelicateCoroutinesApi
import timber.log.Timber

@OptIn(DelicateCoroutinesApi::class)
@Suppress("UNUSED")
class NativeJavascriptInterface(
    private val webViewStoragePolyfill: WebViewStoragePolyfill,
    private val backgroundWebView: WebView,
    private val foregroundWebView: WebView,
    private val toggleAppFullscreen: () -> Unit,
    private val saveText: (String, String) -> Unit,
) : SharedPreferences.OnSharedPreferenceChangeListener {
    init {
        webViewStoragePolyfill.addListener(this)
    }

    fun destroy() {
        webViewStoragePolyfill.removeListener(this)
    }

    @JavascriptInterface
    fun sendToBackground(data: String) {
        Timber.d("Sending to background: $data")
        runOnMainThread {
            backgroundWebView.runJS("window.postMessage($data, '*');")
        }
    }

    @JavascriptInterface
    fun sendToForeground(data: String) {
        Timber.d("Sending to foreground: $data")
        runOnMainThread {
            foregroundWebView.runJS("window.postMessage($data, '*');")
        }
    }

    @JavascriptInterface
    fun toggleFullscreen() {
        Timber.d("Toggling fullscreen")
        toggleAppFullscreen()
    }

    @JavascriptInterface
    fun downloadText(
        text: String,
        fileName: String,
    ) {
        Timber.d("Downloading text to $fileName: $text")
        saveText(text, fileName)
    }

    @JavascriptInterface
    fun getAndroidStorage(key: String): String? {
        return webViewStoragePolyfill.get(key)
    }

    @JavascriptInterface
    fun setAndroidStorage(
        key: String,
        value: String,
    ) {
        webViewStoragePolyfill.set(key, value)
    }

    override fun onSharedPreferenceChanged(
        _prefs: SharedPreferences?,
        key: String?,
    ) {
        Timber.d("Emitting storage key change: $key")
        runOnMainThread {
            backgroundWebView.runJS(
                """
                window.dispatchEvent(
                    new CustomEvent("externalstorage", {
                        "detail": "$key"
                    })
                );
                """.trimIndent(),
            )
        }
    }
}
