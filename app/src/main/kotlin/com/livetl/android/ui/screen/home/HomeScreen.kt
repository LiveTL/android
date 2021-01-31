package com.livetl.android.ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(navigateToPlayer: () -> Unit) {
    Column {
        Text("Hello!")

        Button(onClick = navigateToPlayer) {
            Text("Go to player")
        }
    }
}