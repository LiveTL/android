package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.livetl.android.BuildConfig
import com.livetl.android.R
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.data.chat.TranslatedLanguage
import com.livetl.android.ui.preference.MultiChoicePreferenceRow
import com.livetl.android.ui.preference.PreferenceGroupHeader
import com.livetl.android.ui.preference.SwitchPreferenceRow
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

    LazyColumn {
        item {
            SwitchPreferenceRow(title = R.string.setting_fullscreen, preference = prefs.showFullscreen())
        }

        item {
            SwitchPreferenceRow(title = R.string.setting_show_tl_panel, preference = prefs.showTlPanel())
        }

        if (showTlPanelSettings) {
            item {
                PreferenceGroupHeader(title = R.string.setting_group_filter)
            }

            // Filtered languages
            item {
                MultiChoicePreferenceRow(
                    title = stringResource(R.string.setting_tl_languages),
                    preference = prefs.tlLanguages(),
                    choices = TranslatedLanguage.values().map {
                        val locale = Locale(it.id)
                        it.id to locale.getDisplayName(locale).capitalize()
                    }.toMap()
                )
            }

            // Include author types
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_mod_messages,
                    preference = prefs.showModMessages()
                )
            }
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_verified_messages,
                    preference = prefs.showVerifiedMesages()
                )
            }
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_owner_messages,
                    preference = prefs.showOwnerMesages()
                )
            }

            // Allow/block list
            item {
                prefs.allowedUsers().let { pref ->
                    MultiChoicePreferenceRow(
                        title = stringResource(R.string.setting_allowed_authors),
                        subtitle = quantityStringResource(R.plurals.num_items, pref.get().size),
                        preference = pref,
                        choices = pref.get().map {
                            val author = MessageAuthor.fromPrefItem(it)
                            author.id to author.name
                        }.toMap()
                    )
                }
            }
            item {
                prefs.blockedUsers().let { pref ->
                    MultiChoicePreferenceRow(
                        title = stringResource(R.string.setting_blocked_authors),
                        subtitle = quantityStringResource(R.plurals.num_items, pref.get().size),
                        preference = pref,
                        choices = pref.get().map {
                            val author = MessageAuthor.fromPrefItem(it)
                            author.id to author.name
                        }.toMap()
                    )
                }
            }

            item {
                PreferenceGroupHeader(title = R.string.setting_group_display)
            }

            // Show timestamps
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_timestamps,
                    preference = prefs.showTimestamps()
                )
            }
            if (BuildConfig.DEBUG) {
                item {
                    SwitchPreferenceRow(
                        title = "Debug mode timestamps",
                        preference = prefs.debugTimestamps()
                    )
                }
            }
        }
    }
}
