package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.livetl.android.BuildConfig
import com.livetl.android.R
import com.livetl.android.ui.preference.SwitchPreference
import com.livetl.android.util.PreferencesHelper
import org.koin.androidx.compose.get

@Composable
fun SettingsTab(
    prefs: PreferencesHelper = get(),
) {
    Column {
        SwitchPreference(nameRes = R.string.setting_show_tl_panel, preference = prefs.showTlPanel())

        if (prefs.showTlPanel().get()) {
            // TODO
            // Languages
//            prefs.tlLanguages()

            // Show mod messages
            SwitchPreference(
                nameRes = R.string.setting_show_mod_messages,
                preference = prefs.showModMessages()
            )

            // Show timestamps
            SwitchPreference(
                nameRes = R.string.setting_show_timestamps,
                preference = prefs.showTimestamps()
            )
            if (BuildConfig.DEBUG) {
                SwitchPreference(
                    name = "Debug mode timestamps",
                    preference = prefs.debugTimestamps()
                )
            }

            // TODO
            // Allowlisted users
//            prefs.allowedUsers()
            // Blocklisted users
//            prefs.blockedUsers()
        }
    }
}
