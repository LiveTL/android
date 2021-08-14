package com.livetl.android.util

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import com.livetl.android.BuildConfig

fun createScriptTag(js: String): String {
    return """<script type="text/javascript">$js</script>""".trimIndent()
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
        databaseEnabled = true
        setAppCacheEnabled(true)
        useWideViewPort = true
        loadWithOverviewMode = true
        scrollBarStyle = WebView.SCROLLBARS_INSIDE_INSET
        isScrollbarFadingEnabled = false
        overScrollMode = View.OVER_SCROLL_NEVER
        cacheMode = WebSettings.LOAD_DEFAULT
        allowContentAccess = true
        allowFileAccess = true
//        desktop UAS to make the YT player default to the desktop version
        userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/94.0.4585.0 Safari/537.36 Edg/94.0.972.2"
    }
}
