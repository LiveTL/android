package com.livetl.android.util

import android.util.Base64
import android.webkit.WebView

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
        """.trimIndent().replace("\n", "")
    )
}