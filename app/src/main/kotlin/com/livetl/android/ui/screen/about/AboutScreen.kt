package com.livetl.android.ui.screen.about

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Savings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.livetl.android.BuildConfig
import com.livetl.android.R
import com.livetl.android.ui.preference.PreferenceGroupHeader
import com.livetl.android.ui.preference.PreferenceRow

@Composable
fun AboutScreen(
    onBackPressed: () -> Unit,
    navigateToLicenses: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.about))
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            /* logo */

            item {
                Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME))
            }

            item {
                Row {
                    LinkIcon(
                        labelRes = R.string.about_website,
                        url = "https://livetl.app/",
                        icon = Icons.Filled.Public,
                    )
                    LinkIcon(
                        labelRes = R.string.about_github,
                        url = "https://github.com/LiveTL/android",
                        icon = Icons.Filled.Code,
                    )
                    LinkIcon(
                        labelRes = R.string.about_donate,
                        url = "https://opencollective.com/livetl",
                        icon = Icons.Filled.Savings,
                    )
                }
            }

            item {
                PreferenceGroupHeader(title = R.string.credits)
            }
            item {
                PreferenceRow(
                    title = R.string.holotools_stream_info,
                    onClick = { uriHandler.openUri("https://hololive.jetri.co/") },
                )
            }
            item {
                PreferenceRow(
                    title = R.string.licenses,
                    onClick = { navigateToLicenses() },
                )
            }
        }
    }
}

@Composable
private fun LinkIcon(
    @StringRes labelRes: Int,
    url: String,
    icon: ImageVector,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .clickable { uriHandler.openUri(url) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null
        )
        Text(stringResource(labelRes))
    }
}

@Preview
@Composable
private fun AboutScreenPreview() {
    AboutScreen(
        onBackPressed = {},
        navigateToLicenses = {}
    )
}
