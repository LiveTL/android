package com.livetl.android.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fredporciuncula.flow.preferences.FlowSharedPreferences
import com.fredporciuncula.flow.preferences.Preference
import com.livetl.android.data.chat.TranslatedLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("prefs", MODE_PRIVATE)
    private val flowPrefs = FlowSharedPreferences(prefs)

    fun showWelcomeScreen() = flowPrefs.getBoolean("show_welcome_screen", true)

    fun feedOrganization() = flowPrefs.getString("feed_org", "Hololive")

    fun showAllMessages() = flowPrefs.getBoolean("all_chat_messages", false)
    fun tlLanguages() = flowPrefs.getStringSet("tl_langs", setOf(TranslatedLanguage.ENGLISH.id))
    fun showModMessages() = flowPrefs.getBoolean("show_mod_messages", true)
    fun showVerifiedMessages() = flowPrefs.getBoolean("show_verified_messages", true)
    fun showOwnerMessages() = flowPrefs.getBoolean("show_owner_messages", true)
    fun tlScale() = flowPrefs.getFloat("tl_display_scale", 1f)
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
fun <T> Preference<T>.collectAsStateWithLifecycle(): State<T> =
    asFlow().collectAsStateWithLifecycle(initialValue = get())
