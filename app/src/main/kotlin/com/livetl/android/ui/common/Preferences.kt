package com.livetl.android.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.livetl.android.util.collectAsState
import com.livetl.android.util.toggle
import com.tfcporciuncula.flow.Preference

@Composable
fun <Key> MultiChoicePreferenceRow(
    preference: Preference<Set<Key>>,
    choices: Map<Key, String>,
    selected: Set<Key>? = null,
    title: String,
    subtitle: String? = null,
    onSelected: ((Key) -> Unit)? = null,
) {
    val state by preference.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    PreferenceRow(
        title = title,
        subtitle = subtitle
            ?: choices.filter { it.key in state }.map { it.value }.joinToString(),
        onClick = { showDialog = true },
    )

    if (showDialog) {
        MultiChoiceDialog(
            items = choices.toList(),
            selected = selected ?: state,
            title = { Text(title) },
            onDismissRequest = { showDialog = false },
            onSelected = onSelected ?: { preference.toggle(it) }
        )
    }
}

@Composable
private fun <T> MultiChoiceDialog(
    items: List<Pair<T, String>>,
    selected: Set<T>?,
    onDismissRequest: () -> Unit,
    onSelected: (T) -> Unit,
    title: (@Composable () -> Unit)? = null,
    buttons: @Composable () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        buttons = buttons,
        title = title,
        text = {
            LazyColumn {
                items(items) { (value, text) ->
                    Row(
                        modifier = Modifier
                            .requiredHeight(48.dp)
                            .fillMaxWidth()
                            .clickable { onSelected(value) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = selected?.contains(value) ?: false,
                            onCheckedChange = { onSelected(value) },
                        )
                        Text(text = text, modifier = Modifier.padding(start = 24.dp))
                    }
                }
            }
        },
    )
}

@Composable
fun PreferenceGroupHeader(
    modifier: Modifier = Modifier,
    @StringRes title: Int,
) {
    Text(
        text = stringResource(title),
        color = MaterialTheme.colors.secondary,
        fontSize = MaterialTheme.typography.subtitle1.fontSize,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
fun PreferenceRow(
    title: String,
    onClick: () -> Unit = {},
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null,
) {
    val height = if (subtitle != null) 72.dp else 56.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .requiredHeight(height)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .weight(1f),
        ) {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.subtitle1,
            )
            if (subtitle != null) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.subtitle1,
                    LocalContentAlpha provides ContentAlpha.medium,
                ) {
                    Text(
                        text = subtitle,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                    )
                }
            }
        }
        if (action != null) {
            Box(Modifier.widthIn(min = 56.dp)) {
                action()
            }
        }
    }
}

@Composable
fun PreferenceRow(
    @StringRes title: Int,
    onClick: () -> Unit = {},
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null,
) {
    PreferenceRow(stringResource(title), onClick, subtitle, action)
}

@Composable
fun SwitchPreferenceRow(
    preference: Preference<Boolean>,
    title: String,
    subtitle: String? = null,
) {
    val state by preference.collectAsState()

    PreferenceRow(
        title = title,
        subtitle = subtitle,
        action = { Switch(checked = state, onCheckedChange = null) },
        onClick = { preference.toggle() },
    )
}

@Composable
fun SwitchPreferenceRow(
    preference: Preference<Boolean>,
    @StringRes title: Int,
    subtitle: Int? = null,
) {
    SwitchPreferenceRow(preference, stringResource(title), subtitle?.let { stringResource(it) })
}
