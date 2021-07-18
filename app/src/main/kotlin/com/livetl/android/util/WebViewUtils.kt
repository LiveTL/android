package com.livetl.android.util

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.util.Base64
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.compose.ui.unit.Density
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.livetl.android.BuildConfig

fun WebView.injectScript(js: String) {
    val encodedJs = Base64.encodeToString(js.toByteArray(), Base64.NO_WRAP)
    loadUrl(
        """
        javascript:(function() {
            var parent = document.getElementsByTagName('head').item(0);
            var script = document.createElement('script');
            script.type = 'text/javascript';
            script.innerHTML = window.atob('$encodedJs');
            parent.appendChild(script);
        })()
        """.trimToSingleLine()
    )
}

fun createScript(js: String): String {
    return """<script type="text/javascript">$js</script>""".trimIndent()
}

fun WebView.runJS(js: String) {
    loadUrl("javascript:(function() { $js })()".trimToSingleLine())
}

@SuppressLint("SetJavaScriptEnabled")
fun WebView.setDefaultSettings(density: Density) {
    // Debug mode (chrome://inspect/#devices)
    if (BuildConfig.DEBUG && 0 != context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) {
        WebView.setWebContentsDebuggingEnabled(true)
    }

//    setInitialScale((density.density * 100).toInt())
//    overScrollMode = View.OVER_SCROLL_NEVER

    with(settings) {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(this, WebSettingsCompat.FORCE_DARK_ON)
        }

//        textZoom = 150
//        defaultFontSize = 32

        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        setAppCacheEnabled(true)
        useWideViewPort = true
        loadWithOverviewMode = true
        cacheMode = WebSettings.LOAD_DEFAULT
        allowContentAccess = true
        allowFileAccess = true
    }
}

private fun String.trimToSingleLine(): String {
    return trimIndent().replace("\n", "")
}
