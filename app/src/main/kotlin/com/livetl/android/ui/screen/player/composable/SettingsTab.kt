package com.livetl.android.ui.screen.player.composable

import androidx.annotation.StringRes
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.BuildConfig
import com.livetl.android.R
import com.livetl.android.data.chat.MessageAuthor
import com.livetl.android.data.chat.TranslatedLanguage
import com.livetl.android.ui.common.MultiChoicePreferenceRow
import com.livetl.android.ui.common.PreferenceGroupHeader
import com.livetl.android.ui.common.SwitchPreferenceRow
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.util.collectAsState
import com.livetl.android.util.quantityStringResource
import com.tfcporciuncula.flow.Preference
import java.util.Locale

@Composable
fun SettingsTab(
    playerViewModel: PlayerViewModel = viewModel(),
) {
    val showTlPanelSettings by playerViewModel.prefs.showTlPanel().collectAsState()

    LazyColumn {
        item {
            PreferenceGroupHeader(title = R.string.setting_group_filter)
        }

        item {
            SwitchPreferenceRow(title = R.string.setting_show_tl_panel, preference = playerViewModel.prefs.showTlPanel())
        }

        if (showTlPanelSettings) {
            // Filtered languages
            item {
                MultiChoicePreferenceRow(
                    title = stringResource(R.string.setting_tl_languages),
                    preference = playerViewModel.prefs.tlLanguages(),
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
                    preference = playerViewModel.prefs.showModMessages()
                )
            }
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_verified_messages,
                    preference = playerViewModel.prefs.showVerifiedMessages()
                )
            }
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_owner_messages,
                    preference = playerViewModel.prefs.showOwnerMessages()
                )
            }

            // Allow/block list
            item {
                AuthorListDialog(
                    title = R.string.setting_allowed_authors,
                    preference = playerViewModel.prefs.allowedUsers(),
                )
            }
            item {
                AuthorListDialog(
                    title = R.string.setting_blocked_authors,
                    preference = playerViewModel.prefs.blockedUsers(),
                )
            }
        }

        item {
            PreferenceGroupHeader(title = R.string.setting_group_display)
        }

        item {
            SwitchPreferenceRow(title = R.string.setting_fullscreen, preference = playerViewModel.prefs.showFullscreen())
        }

        // TODO: unhide this once message timing is less broken
        // Show timestamps
        if (BuildConfig.DEBUG) {
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_timestamps,
                    preference = playerViewModel.prefs.showTimestamps()
                )
            }
        }
        if (BuildConfig.DEBUG) {
            item {
                SwitchPreferenceRow(
                    title = "Debug mode timestamps",
                    preference = playerViewModel.prefs.debugTimestamps()
                )
            }
        }
    }
}

@Composable
private fun AuthorListDialog(
    @StringRes title: Int,
    preference: Preference<Set<String>>,
) {
    val authors by preference.collectAsState()

    fun getAuthors() = authors.map {
        val author = MessageAuthor.fromPrefItem(it)
        author.id to author.name
    }.toMap()

    MultiChoicePreferenceRow(
        title = stringResource(title),
        subtitle = quantityStringResource(R.plurals.num_items, authors.size),
        preference = preference,
        choices = getAuthors(),
        selected = getAuthors().keys,
        onSelected = { authorId ->
            preference.set(
                preference.get()
                    .filterNot { MessageAuthor.getPrefItemId(it) == authorId }
                    .toSet()
            )
        },
    )
}
