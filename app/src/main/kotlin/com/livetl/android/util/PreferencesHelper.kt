package com.livetl.android.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.preference.PreferenceManager
import com.livetl.android.data.chat.TranslatedLanguage
import com.tfcporciuncula.flow.FlowSharedPreferences
import com.tfcporciuncula.flow.Preference

class PreferencesHelper(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val flowPrefs = FlowSharedPreferences(prefs)

    fun showFullscreen() = flowPrefs.getBoolean("show_fullscreen", false)
    fun showTlPanel() = flowPrefs.getBoolean("show_tl_panel", true)
    fun tlLanguages() = flowPrefs.getStringSet("tl_langs", setOf(TranslatedLanguage.ENGLISH.id))
    fun showModMessages() = flowPrefs.getBoolean("show_mod_messages", false)
    fun showVerifiedMesages() = flowPrefs.getBoolean("show_verified_messages", false)
    fun showOwnerMesages() = flowPrefs.getBoolean("show_owner_messages", false)
    fun showTimestamps() = flowPrefs.getBoolean("show_timestamps", false)
    fun allowedUsers() = flowPrefs.getStringSet("allowed_users", setOf())
    fun blockedUsers() = flowPrefs.getStringSet("blocked_users", setOf())

    fun debugTimestamps() = flowPrefs.getBoolean("debug_timestamps", false)
}

fun <T> Preference<Set<T>>.toggle(item: T) {
    if (item in get()) {
        minusAssign(item)
    } else {
        plusAssign(item)
    }
}

operator fun <T> Preference<Set<T>>.plusAssign(item: T) {
    set(get() + item)
}

operator fun <T> Preference<Set<T>>.minusAssign(item: T) {
    set(get() - item)
}

fun Preference<Boolean>.toggle() {
    set(!get())
}

@Composable
fun <T> Preference<T>.collectAsState(): State<T> {
    return asFlow().collectAsState(initial = get())
}
