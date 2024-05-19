package com.livetl.android.ui.common.preference

import androidx.annotation.StringRes
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import com.livetl.android.util.collectAsStateWithLifecycle
import com.livetl.android.util.toggle
import com.tfcporciuncula.flow.Preference

@Composable
fun SwitchPreferenceRow(preference: Preference<Boolean>, title: String, subtitle: String? = null) {
    val state by preference.collectAsStateWithLifecycle()

    PreferenceRow(
        title = title,
        subtitle = subtitle,
        action = { Switch(checked = state, onCheckedChange = null) },
        onClick = { preference.toggle() },
    )
}

@Composable
fun SwitchPreferenceRow(preference: Preference<Boolean>, @StringRes title: Int, subtitle: Int? = null) {
    SwitchPreferenceRow(preference, stringResource(title), subtitle?.let { stringResource(it) })
}
