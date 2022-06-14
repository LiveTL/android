package com.livetl.android.ui.screen.home.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.livetl.android.R
import com.livetl.android.data.feed.ORGANIZATIONS
import com.livetl.android.util.collectAsState

@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel,
) {
    val selectedOrganization by viewModel.prefs.feedOrganization().collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.setting_feed_org))
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
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
            items(ORGANIZATIONS) { organization ->
                val selectOrganization = { viewModel.prefs.feedOrganization().set(organization) }

                Row(
                    modifier = Modifier
                        .requiredHeight(48.dp)
                        .fillMaxWidth()
                        .clickable { selectOrganization() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = selectedOrganization == organization,
                        onClick = { selectOrganization() },
                    )
                    Text(text = organization, modifier = Modifier.padding(start = 24.dp))
                }
            }

            item {
                Spacer(Modifier.navigationBarsHeight())
            }
        }
    }
}
