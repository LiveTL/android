package com.livetl.android.ui.screen.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.livetl.android.BuildConfig
import com.livetl.android.R
import com.livetl.android.ui.common.LinkIcon
import com.livetl.android.ui.common.ScreenScaffold
import com.livetl.android.ui.common.preference.PreferenceGroupHeader
import com.livetl.android.ui.common.preference.PreferenceRow

@Composable
fun AboutScreen(onBackPressed: () -> Unit, navigateToLicenses: () -> Unit, navigateToWelcome: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    ScreenScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.about))
                },
                navigationIcon = {
                    IconButton(onClick = { onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .consumeWindowInsets(contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = contentPadding,
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
                        icon = Icons.AutoMirrored.Outlined.Chat,
                        url = "https://discord.gg/uJrV3tmthg",
                    )
                    LinkIcon(
                        labelRes = R.string.about_github,
                        icon = Icons.Outlined.Code,
                        url = "https://github.com/LiveTL/android",
                    )
                }
            }

            item {
                PreferenceGroupHeader(title = R.string.credits)
            }
            item {
                PreferenceRow(
                    title = R.string.holodex_stream_info,
                    onClick = { uriHandler.openUri("https://holodex.net/") },
                )
            }
            item {
                PreferenceRow(
                    title = R.string.licenses,
                    onClick = { navigateToLicenses() },
                )
            }
            item {
                PreferenceRow(
                    title = R.string.privacy_policy,
                    onClick = { uriHandler.openUri("https://livetl.app/privacy") },
                )
            }
            item {
                PreferenceRow(
                    title = R.string.show_welcome_screen,
                    onClick = { navigateToWelcome() },
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
        navigateToLicenses = {},
        navigateToWelcome = {},
    )
}
