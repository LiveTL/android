package com.livetl.android.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import com.tfcporciuncula.flow.FlowSharedPreferences
import com.tfcporciuncula.flow.Preference
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

open class PreferencesHelper @Inject constructor(@ApplicationContext context: Context) {
    private val prefs = context.getSharedPreferences("prefs", MODE_PRIVATE)
    private val flowPrefs = FlowSharedPreferences(prefs)

    fun showWelcomeScreen() = flowPrefs.getBoolean("show_welcome_screen", true)
    fun wasPlayerFullscreen() = flowPrefs.getBoolean("player_fullscreen", false)

    fun feedOrganization() = flowPrefs.getString("feed_org", "Hololive")
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
