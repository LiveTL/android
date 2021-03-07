package com.livetl.android.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.preference.PreferenceManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import com.tfcporciuncula.flow.Preference

class PreferencesHelper(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val flowPrefs = FlowSharedPreferences(prefs)

    fun showTlPanel() = flowPrefs.getBoolean("show_tl_panel", true)
    fun tlLanguages() = flowPrefs.getStringSet("tl_langs", setOf())
    fun showModMessages() = flowPrefs.getBoolean("show_mod_messages", false)
    fun showTimestamps() = flowPrefs.getBoolean("show_timestamps", false)
    fun allowedUsers() = flowPrefs.getStringSet("allowed_users", setOf())
    fun blockedUsers() = flowPrefs.getStringSet("blocked_users", setOf())

    fun debugTimestamps() = flowPrefs.getBoolean("debug_timestamps", false)
}

fun Preference<Boolean>.toggle() {
    set(!get())
}

@Composable
fun <T> Preference<T>.collectAsState(): State<T> {
    return asFlow().collectAsState(initial = get())
}
