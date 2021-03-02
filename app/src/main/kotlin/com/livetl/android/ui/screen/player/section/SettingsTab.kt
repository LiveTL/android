package com.livetl.android.ui.screen.player.section

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.livetl.android.R
import com.livetl.android.di.get
import com.livetl.android.ui.preference.SwitchPreference
import com.livetl.android.util.PreferencesHelper

@Composable
fun SettingsTab(
    prefs: PreferencesHelper = get(),
) {
    Column {
        SwitchPreference(nameRes = R.string.setting_show_tl_panel, preference = prefs.showTlPanel())
    }
}