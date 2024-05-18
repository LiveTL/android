package com.livetl.android.ui.screen.about

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.livetl.android.R
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer

@Composable
fun LicensesScreen(onBackPressed: () -> Unit) {
    Scaffold(
        topBar = {
            Surface {
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
                )
            }
        },
    ) { contentPadding ->
        LibrariesContainer(
            modifier =
            Modifier
                .fillMaxSize()
                .navigationBarsPadding(),
            contentPadding = contentPadding,
        )
    }
}
