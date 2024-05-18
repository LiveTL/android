package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.common.LoadingIndicator

@Composable
fun StreamInfoPanel(streamInfo: StreamInfo?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(8.dp),
    ) {
        if (streamInfo == null) {
            LoadingIndicator()
            return
        }

        Text(
            text = streamInfo.title,
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.requiredHeight(8.dp))
        Text(
            text = streamInfo.author,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
