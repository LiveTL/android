package com.livetl.android.ui.screen.home.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.coil.rememberCoilPainter
import com.livetl.android.R
import com.livetl.android.data.feed.Stream

@Composable
fun StreamSheet(stream: Stream?) {
    if (stream == null) {
        Text(stringResource(R.string.select_a_stream))
        return
    }

    Column {
        Image(
            painter = rememberCoilPainter(stream.getThumbnail()),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
                .aspectRatio(16 / 9f),
        )

        Column(
            modifier = Modifier
                .padding(8.dp),
        ) {
            Text(
                text = stream.title,
                style = MaterialTheme.typography.h5,
            )
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Text(
                text = stream.channel.name,
                style = MaterialTheme.typography.subtitle1,
            )
        }
    }
}
