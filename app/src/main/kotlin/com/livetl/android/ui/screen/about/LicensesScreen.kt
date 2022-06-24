package com.livetl.android.ui.screen.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.livetl.android.R
import com.mikepenz.aboutlibraries.ui.compose.LibrariesContainer

@Composable
fun LicensesScreen(
    onBackPressed: () -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(
                elevation = AppBarDefaults.TopAppBarElevation,
            ) {
                TopAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    title = {
                        Text(text = stringResource(R.string.licenses))
                    },
                    navigationIcon = {
                        IconButton(onClick = { onBackPressed() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.cd_back),
                            )
                        }
                    },
                    elevation = 0.dp,
                )
            }
        },
    ) { contentPadding ->
        LibrariesContainer(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = contentPadding,
        )
    }
}
