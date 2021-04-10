package com.livetl.android.ui.preference

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.AlertDialog
import androidx.compose.material.Checkbox
import androidx.compose.material.ContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
fun PreferencesScrollableColumn(
    modifier: Modifier = Modifier,
    content: @Composable PreferenceScope.() -> Unit
) {
    val dialog = remember { mutableStateOf<(@Composable () -> Unit)?>(null) }

    Column(
        modifier = modifier
            .scrollable(
                state = rememberScrollState(),
                orientation = Orientation.Vertical
            )
    ) {
        val scope = PreferenceScope(dialog)
        scope.content()
    }

    dialog.value?.invoke()
}

class PreferenceScope(dialog: MutableState<(@Composable () -> Unit)?>) {
    var dialog by dialog

    @Composable
    fun <Key> MultiChoicePref(
        preference: Preference<Set<Key>>,
        choices: Map<Key, String>,
        title: String,
        subtitle: String? = null
    ) {
        val state by preference.collectAsState()

        Pref(
            title = title,
            subtitle = subtitle
                ?: choices.filter { it.key in state }.map { it.value }.joinToString(),
            onClick = {
                dialog = {
                    MultiChoiceDialog(
                        items = choices.toList(),
                        selected = state,
                        title = { Text(title) },
                        onDismissRequest = { dialog = null },
                        onSelected = { selected ->
                            preference.toggle(selected)
                        }
                    )
                }
            }
        )
    }

    @Composable
    fun <Key> MultiChoicePref(
        preference: Preference<Set<Key>>,
        choices: Map<Key, Int>,
        @StringRes title: Int,
        subtitle: String? = null
    ) {
        MultiChoicePref(
            preference,
            choices.mapValues { stringResource(it.value) },
            stringResource(title),
            subtitle
        )
    }

    @Composable
    private fun <T> MultiChoiceDialog(
        items: List<Pair<T, String>>,
        selected: Set<T>?,
        onDismissRequest: () -> Unit,
        onSelected: (T) -> Unit,
        title: (@Composable () -> Unit)? = null,
        buttons: @Composable () -> Unit = {}
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
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selected?.contains(value) ?: false,
                                onCheckedChange = { onSelected(value) },
                            )
                            Text(text = text, modifier = Modifier.padding(start = 24.dp))
                        }
                    }
                }
            }
        )
    }
}

@Composable
fun PrefGroupHeader(
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
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun Pref(
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            Text(
                text = title,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                style = MaterialTheme.typography.subtitle1,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    color = LocalContentColor.current.copy(alpha = ContentAlpha.medium),
                    style = MaterialTheme.typography.subtitle1
                )
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
fun Pref(
    @StringRes title: Int,
    onClick: () -> Unit = {},
    subtitle: String? = null,
    action: @Composable (() -> Unit)? = null,
) {
    Pref(stringResource(title), onClick, subtitle, action)
}

@Composable
fun SwitchPref(
    preference: Preference<Boolean>,
    title: String,
    subtitle: String? = null,
) {
    val state by preference.collectAsState()

    Pref(
        title = title,
        subtitle = subtitle,
        action = { Switch(checked = state, onCheckedChange = null) },
        onClick = { preference.toggle() }
    )
}

@Composable
fun SwitchPref(
    preference: Preference<Boolean>,
    @StringRes title: Int,
    subtitle: Int? = null,
) {
    SwitchPref(preference, stringResource(title), subtitle?.let { stringResource(it) })
}
