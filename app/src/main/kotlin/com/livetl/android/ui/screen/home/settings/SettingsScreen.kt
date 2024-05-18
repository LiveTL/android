package com.livetl.android.ui.screen.home.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.livetl.android.R
import com.livetl.android.data.feed.ORGANIZATIONS
import com.livetl.android.util.collectAsState

@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val selectedOrganization by viewModel.prefs.feedOrganization().collectAsState()

    Scaffold(
        topBar = {
            Surface(tonalElevation = AppBarDefaults.TopAppBarElevation) {
                TopAppBar(
                    title = {
                        Text(text = stringResource(R.string.setting_feed_org))
                    },
                    modifier = Modifier.statusBarsPadding(),
                    navigationIcon = {
                        IconButton(onClick = { onBackPressed() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
                            )
                        }
                    },
                )
            }
        },
    ) { contentPadding ->
        LazyColumn(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(contentPadding),
        ) {
            items(
                items = ORGANIZATIONS,
                key = { it },
            ) { organization ->
                val selectOrganization = { viewModel.prefs.feedOrganization().set(organization) }

                Row(
                    modifier =
                        Modifier
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
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}
