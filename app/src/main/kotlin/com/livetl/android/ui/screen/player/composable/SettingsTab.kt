package com.livetl.android.ui.screen.player.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.livetl.android.BuildConfig
import com.livetl.android.R
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.data.chat.TranslatedLanguage
import com.livetl.android.ui.preference.PrefGroupHeader
import com.livetl.android.ui.preference.PreferencesScrollableColumn
import com.livetl.android.ui.preference.SwitchPref
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.collectAsState
import com.livetl.android.util.quantityStringResource
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
            PrefGroupHeader(title = R.string.setting_group_filter)

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

            // Allow/block list
            with(prefs.allowedUsers()) {
                MultiChoicePref(
                    title = stringResource(R.string.setting_allowed_authors),
                    subtitle = quantityStringResource(R.plurals.num_items, get().size),
                    preference = this,
                    choices = this.get().map {
                        val author = MessageAuthor.fromPrefItem(it)
                        author.id to author.name
                    }.toMap()
                )
            }
            with(prefs.blockedUsers()) {
                MultiChoicePref(
                    title = stringResource(R.string.setting_blocked_authors),
                    subtitle = quantityStringResource(R.plurals.num_items, get().size),
                    preference = this,
                    choices = this.get().map {
                        val author = MessageAuthor.fromPrefItem(it)
                        author.id to author.name
                    }.toMap()
                )
            }

            PrefGroupHeader(title = R.string.setting_group_display)

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
    }
}
