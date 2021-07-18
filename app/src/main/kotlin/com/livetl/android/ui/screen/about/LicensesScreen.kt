package com.livetl.android.ui.screen.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsHeight
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.livetl.android.R
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library

@Composable
fun LicensesScreen(
    onBackPressed: () -> Unit,
) {
    val libraries = Libs(LocalContext.current).libraries.sortedBy { it.libraryName.lowercase() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.licenses))
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
    ) { contentPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth().padding(contentPadding),
        ) {
            items(libraries) { library ->
                LicenseItem(library)
            }

            item {
                Spacer(Modifier.navigationBarsHeight())
            }
        }
    }
}

@Composable
private fun LicenseItem(library: Library) {
    val uriHandler = LocalUriHandler.current

    val website = library.libraryWebsite
    val modifier = when {
        website.isEmpty() -> Modifier
        else -> Modifier.clickable { uriHandler.openUri(website) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text("${library.libraryName} ${library.libraryVersion}")
        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.caption,
            LocalContentAlpha provides ContentAlpha.medium,
        ) {
            Text(library.libraryArtifactId)
            library.licenses?.let { licenses ->
                Text(licenses.joinToString { it.licenseName })
            }
        }
    }
}
