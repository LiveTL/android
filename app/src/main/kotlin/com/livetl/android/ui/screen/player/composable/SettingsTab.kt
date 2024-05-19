package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.R
import com.livetl.android.data.chat.TranslatedLanguage
import com.livetl.android.ui.common.preference.ChoicePreferenceRow
import com.livetl.android.ui.common.preference.MultiChoicePreferenceRow
import com.livetl.android.ui.common.preference.SwitchPreferenceRow
import com.livetl.android.ui.screen.player.PlayerViewModel
import com.livetl.android.util.capitalize
import com.livetl.android.util.collectAsStateWithLifecycle
import java.util.Locale

@Composable
fun SettingsTab(modifier: Modifier = Modifier, playerViewModel: PlayerViewModel = viewModel()) {
    val showAllMessages by playerViewModel.prefs.showAllMessages().collectAsStateWithLifecycle()

    LazyColumn(modifier = modifier) {
        // Show all messages
        item {
            SwitchPreferenceRow(
                title = R.string.setting_show_all_messages,
                preference = playerViewModel.prefs.showAllMessages(),
            )
        }

        if (!showAllMessages) {
            // Filtered languages
            item {
                MultiChoicePreferenceRow(
                    title = stringResource(R.string.setting_tl_languages),
                    preference = playerViewModel.prefs.tlLanguages(),
                    choices = TranslatedLanguage.entries.associate {
                        val locale = Locale(it.id)
                        it.id to locale.getDisplayName(locale).capitalize()
                    },
                )
            }

            // Include author types
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_mod_messages,
                    preference = playerViewModel.prefs.showModMessages(),
                )
            }
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_verified_messages,
                    preference = playerViewModel.prefs.showVerifiedMessages(),
                )
            }
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_show_owner_messages,
                    preference = playerViewModel.prefs.showOwnerMessages(),
                )
            }
        }

        // Font size
        item {
            ChoicePreferenceRow(
                title = stringResource(R.string.setting_text_size),
                preference = playerViewModel.prefs.tlScale(),
                choices = listOf(0.75f, 1f, 1.25f, 1.5f, 1.75f, 2f)
                    .associateWith { stringResource(R.string.percentage, (it * 100).toInt()) },
            )
        }
    }
}
