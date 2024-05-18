package com.livetl.android.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Basic polyfill for chrome.storage API in WebView.
 *
 * This allows persisting basic key/value strings and listening to changes.
 */
class WebViewStoragePolyfill @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("webview_storage", Context.MODE_PRIVATE)

    fun get(key: String): String? = prefs.getString(key, null)

    fun set(key: String, value: String) {
        prefs.edit {
            set(key, value)
        }
    }

    fun addListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.registerOnSharedPreferenceChangeListener(listener)
    }

    fun removeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        prefs.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
