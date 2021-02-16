package com.livetl.android.ui.screen.player.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livetl.android.data.stream.StreamInfo

@Composable
fun InfoTab(
    streamInfo: StreamInfo?
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
    ) {
        if (streamInfo != null) {
            Text(
                text = streamInfo.title,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = streamInfo.author,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(
                text = "Live: ${streamInfo.isLive}",
                modifier = Modifier.padding(bottom = 8.dp),
            )
            Text(text = streamInfo.shortDescription)
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
