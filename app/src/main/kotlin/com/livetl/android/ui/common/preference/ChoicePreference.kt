package com.livetl.android.ui.common.preference

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livetl.android.util.collectAsState
import com.tfcporciuncula.flow.Preference

@Composable
fun <T> ChoicePreferenceRow(
    preference: Preference<T>,
    choices: Map<T, String>,
    selected: T? = null,
    title: String,
    subtitle: String? = null,
    text: String? = null,
    onSelected: ((T) -> Unit)? = null,
) {
    val state by preference.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    PreferenceRow(
        title = title,
        subtitle = subtitle ?: choices[state],
        onClick = { showDialog = true },
    )

    if (showDialog) {
        ChoiceDialog(
            items = choices.toList(),
            selected = selected ?: state,
            title = { Text(title) },
            text = text,
            onDismissRequest = { showDialog = false },
            onSelected = onSelected ?: { preference.set(it) },
        )
    }
}

@Composable
private fun <T> ChoiceDialog(
    items: List<Pair<T, String>>,
    selected: T?,
    onDismissRequest: () -> Unit,
    onSelected: (T) -> Unit,
    title: (@Composable () -> Unit)? = null,
    text: String? = null,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = title,
        confirmButton = {},
        text = {
            LazyColumn {
                text?.let { item { Text(text) } }

                items(items) { (value, text) ->
                    Row(
                        modifier = Modifier
                            .requiredHeight(48.dp)
                            .fillMaxWidth()
                            .clickable { onSelected(value) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = selected == value,
                            onClick = { onSelected(value) },
                        )
                        Text(text = text, modifier = Modifier.padding(start = 24.dp))
                    }
                }
            }
        },
    )
}
