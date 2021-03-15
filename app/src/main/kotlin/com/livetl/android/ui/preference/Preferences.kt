package com.livetl.android.ui.preference

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livetl.android.util.collectAsState
import com.livetl.android.util.toggle
import com.tfcporciuncula.flow.Preference

@Composable
fun PreferenceGroup(
    modifier: Modifier = Modifier,
    @StringRes nameRes: Int,
    content: @Composable () -> Unit,
) {
    Column(modifier) {
        PreferenceGroupHeader(nameRes)

        content()
    }
}

@Composable
fun PreferenceGroupHeader(
    @StringRes nameRes: Int,
) {
    Text(
        text = stringResource(nameRes),
        color = MaterialTheme.colors.secondary,
        modifier = Modifier
            .padding(8.dp)
    )
}

@Composable
fun BasicPreference(
    modifier: Modifier = Modifier,
    @StringRes nameRes: Int,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .requiredHeight(PREF_HEIGHT)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(nameRes))
    }
}

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    @StringRes nameRes: Int,
    preference: Preference<Boolean>,
) {
    SwitchPreference(modifier, stringResource(nameRes), preference)
}

@Composable
fun SwitchPreference(
    modifier: Modifier = Modifier,
    name: String,
    preference: Preference<Boolean>,
) {
    val value by preference.collectAsState()

    Row(
        modifier = modifier
            .clickable { preference.toggle() }
            .fillMaxWidth()
            .requiredHeight(PREF_HEIGHT)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            modifier = Modifier.weight(1f),
        )

        Switch(
            checked = value,
            onCheckedChange = { preference.toggle() },
        )
    }
}

private val PREF_HEIGHT = 56.dp
