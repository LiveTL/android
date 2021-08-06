package com.livetl.android.ui.screen.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.livetl.android.R
import com.livetl.android.data.feed.ORGANIZATIONS
import com.livetl.android.ui.common.preference.ChoicePreferenceRow
import com.livetl.android.ui.common.preference.PreferenceGroupHeader
import com.livetl.android.ui.common.preference.SwitchPreferenceRow

@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    settingsViewModel: SettingsViewModel,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                contentPadding = rememberInsetsPaddingValues(
                    LocalWindowInsets.current.statusBars,
                    applyBottom = false,
                ),
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
        ) {
            item {
                PreferenceGroupHeader(title = R.string.setting_group_streams_feed)
            }

            item {
                ChoicePreferenceRow(
                    title = stringResource(R.string.setting_feed_org),
                    preference = settingsViewModel.prefs.feedOrganization(),
                    choices = ORGANIZATIONS.associateWith { it },
                )
            }

            item {
                SwitchPreferenceRow(
                    title = R.string.setting_thumbnail_backgrounds,
                    preference = settingsViewModel.prefs.showFeedThumbnailBackgrounds(),
                )
            }

            item {
                PreferenceGroupHeader(title = R.string.setting_group_player)
            }
            item {
                SwitchPreferenceRow(
                    title = R.string.setting_fullscreen,
                    preference = settingsViewModel.prefs.showPlayerFullscreen(),
                )
            }

            item {
                Spacer(Modifier.navigationBarsHeight())
            }
        }
    }
}
