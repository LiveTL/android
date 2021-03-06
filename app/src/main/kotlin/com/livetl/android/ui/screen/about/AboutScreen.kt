package com.livetl.android.ui.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.livetl.android.BuildConfig
import com.livetl.android.R
import com.livetl.android.ui.common.LinkIcon
import com.livetl.android.ui.common.preference.PreferenceGroupHeader
import com.livetl.android.ui.common.preference.PreferenceRow

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
                },
                contentPadding = rememberInsetsPaddingValues(
                    LocalWindowInsets.current.statusBars,
                    applyBottom = false,
                ),
            )
        },
        bottomBar = {
            Spacer(Modifier.navigationBarsHeight().fillMaxWidth())
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(horizontal = 0.dp, vertical = 8.dp),
        ) {
            item {
                Image(
                    painter = painterResource(R.mipmap.ic_launcher_foreground),
                    contentDescription = null,
                )
            }

            item {
                Text(stringResource(R.string.about_version, BuildConfig.VERSION_NAME))
            }

            item {
                Row {
                    LinkIcon(
                        labelRes = R.string.about_website,
                        url = "https://livetl.app/",
                        icon = Icons.Outlined.Public,
                    )
                    LinkIcon(
                        labelRes = R.string.about_discord,
                        icon = Icons.Outlined.Chat,
                        url = "https://discord.gg/uJrV3tmthg",
                    )
                    LinkIcon(
                        labelRes = R.string.about_github,
                        icon = Icons.Outlined.Code,
                        url = "https://github.com/LiveTL/android",
                    )
                    LinkIcon(
                        labelRes = R.string.about_donate,
                        icon = Icons.Outlined.Savings,
                        url = "https://opencollective.com/livetl",
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

@Preview
@Composable
private fun AboutScreenPreview() {
    AboutScreen(
        onBackPressed = {},
        navigateToLicenses = {}
    )
}
