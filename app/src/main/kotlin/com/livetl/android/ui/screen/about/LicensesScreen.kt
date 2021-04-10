package com.livetl.android.ui.screen.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livetl.android.R
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.entity.Library

@Composable
fun LicensesScreen(
    onBackPressed: () -> Unit,
) {
    val libraries = Libs(LocalContext.current).libraries.sortedBy { it.libraryName }

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
                }
            )
        }
    ) {
        LazyColumn {
            items(libraries) { library ->
                LicenseItem(library)
            }
        }
    }
}

@Composable
private fun LicenseItem(library: Library) {
    val uriHandler = LocalUriHandler.current

    val website = library.libraryWebsite
    val modifier = when {
        website.isNullOrEmpty() -> Modifier
        else -> Modifier.clickable { uriHandler.openUri(website) }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text("${library.libraryName} ${library.libraryVersion}")
        Text(library.libraryArtifactId)
        library.licenses?.let { licenses ->
            Text(licenses.joinToString { it.licenseName })
        }
    }
}