package com.livetl.android.ui.screen.player.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.livetl.android.BuildConfig
import com.livetl.android.R
import com.livetl.android.data.chat.TranslatedLanguage
import com.livetl.android.ui.preference.PrefGroup
import com.livetl.android.ui.preference.PreferencesScrollableColumn
import com.livetl.android.ui.preference.SwitchPref
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.collectAsState
import org.koin.androidx.compose.get
import java.util.Locale

@Composable
fun SettingsTab(
    prefs: PreferencesHelper = get(),
) {
    val showTlPanelSettings by prefs.showTlPanel().collectAsState()

    PreferencesScrollableColumn {
        SwitchPref(title = R.string.setting_fullscreen, preference = prefs.showFullscreen())

        SwitchPref(title = R.string.setting_show_tl_panel, preference = prefs.showTlPanel())

        if (showTlPanelSettings) {
            PrefGroup(title = R.string.setting_group_filter) {
                // Filtered languages
                MultiChoicePref(
                    title = stringResource(R.string.setting_tl_languages),
                    preference = prefs.tlLanguages(),
                    choices = TranslatedLanguage.values().map {
                        val locale = Locale(it.id)
                        it.id to locale.getDisplayName(locale).capitalize()
                    }.toMap()
                )

                // Include author types
                SwitchPref(
                    title = R.string.setting_show_mod_messages,
                    preference = prefs.showModMessages()
                )
                SwitchPref(
                    title = R.string.setting_show_verified_messages,
                    preference = prefs.showVerifiedMesages()
                )
                SwitchPref(
                    title = R.string.setting_show_owner_messages,
                    preference = prefs.showOwnerMesages()
                )
            }

            PrefGroup(title = R.string.setting_group_filter) {
                // Show timestamps
                SwitchPref(
                    title = R.string.setting_show_timestamps,
                    preference = prefs.showTimestamps()
                )
                if (BuildConfig.DEBUG) {
                    SwitchPref(
                        title = "Debug mode timestamps",
                        preference = prefs.debugTimestamps()
                    )
                }
            }

            // TODO
            // Allowlisted users
//            prefs.allowedUsers()
            // Blocklisted users
//            prefs.blockedUsers()
        }
    }
}
