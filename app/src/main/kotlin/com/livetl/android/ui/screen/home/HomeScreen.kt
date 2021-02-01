package com.livetl.android.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.livetl.android.BuildConfig

@Composable
fun HomeScreen(navigateToPlayer: (String) -> Unit) {
    Column {
        Text("Hello!")

        if (BuildConfig.DEBUG) {
            TestStreams(navigateToStream = { navigateToPlayer(it) })
        }
    }
}