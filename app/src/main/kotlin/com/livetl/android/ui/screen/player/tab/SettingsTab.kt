package com.livetl.android.ui.screen.player.tab

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livetl.android.R
import com.livetl.android.di.get
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.toggle
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun SettingsTab(
    prefs: PreferencesHelper = get(),
) {
    val showFilteredMessages by prefs.showTlPanel().asFlow().collectAsState(initial = true)

    Column(modifier = Modifier.padding(8.dp)) {
        Row {
            Text(text = stringResource(R.string.setting_show_tl_panel))

            Switch(
                checked = showFilteredMessages,
                onCheckedChange = { prefs.showTlPanel().toggle() },
            )
        }
    }
}