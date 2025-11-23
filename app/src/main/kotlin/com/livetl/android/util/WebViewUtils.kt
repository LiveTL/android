package com.livetl.android.util

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.util.Base64
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import com.livetl.android.BuildConfig

fun WebView.injectScript(js: String) {
    val encodedJs = Base64.encodeToString(js.toByteArray(), Base64.NO_WRAP)
    loadUrl(
        """
        javascript:(function() {
            const parent = document.getElementsByTagName('head').item(0);
            const script = document.createElement('script');
            script.type = 'text/javascript';
            script.innerHTML = window.atob('$encodedJs');
            parent.appendChild(script);
        })()
        """.trimToSingleLine(),
    )
}

fun WebView.runJS(js: String) {
    evaluateJavascript("(function() { $js })()", null)
}

@SuppressLint("SetJavaScriptEnabled")
fun WebView.setDefaultSettings() {
    // Debug mode (chrome://inspect/#devices)
    if (BuildConfig.DEBUG && 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
        WebView.setWebContentsDebuggingEnabled(true)
    }

    with(settings) {
        javaScriptEnabled = true
        domStorageEnabled = true
        useWideViewPort = true
        loadWithOverviewMode = true
        scrollBarStyle = WebView.SCROLLBARS_INSIDE_INSET
        isScrollbarFadingEnabled = false
        overScrollMode = View.OVER_SCROLL_NEVER
        cacheMode = WebSettings.LOAD_DEFAULT
        allowContentAccess = true
        allowFileAccess = true

        userAgentString = USER_AGENT
    }
}

// Desktop UA to make the YT player default to the desktop version
const val USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36 Edg/141.0.0.0"
