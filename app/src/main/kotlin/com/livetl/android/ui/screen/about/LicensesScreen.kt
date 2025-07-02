package com.livetl.android.ui.screen.about

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.livetl.android.R
import com.livetl.android.ui.common.ScreenScaffold
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@Composable
fun LicensesScreen(onBackPressed: () -> Unit) {
    val libs by rememberLibraries()

    ScreenScaffold(
        topBar = { scrollBehavior ->
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.licenses))
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
        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(contentPadding),
            contentPadding = contentPadding,
            libraries = libs,
        )
    }
}
