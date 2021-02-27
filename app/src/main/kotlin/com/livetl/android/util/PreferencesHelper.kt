package com.livetl.android.util

import android.content.Context
import androidx.preference.PreferenceManager
import com.tfcporciuncula.flow.FlowSharedPreferences
import com.tfcporciuncula.flow.Preference
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PreferencesHelper(context: Context) {
    private val prefs = PreferenceManager.getDefaultSharedPreferences(context)
    private val flowPrefs = FlowSharedPreferences(prefs)

    fun showTlPanel() = flowPrefs.getBoolean("show_tl_panel", true)
}

fun Preference<Boolean>.toggle() {
    set(!get())
}