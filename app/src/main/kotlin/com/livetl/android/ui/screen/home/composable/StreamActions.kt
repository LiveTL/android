package com.livetl.android.ui.screen.home.composable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material.icons.outlined.Share
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.livetl.android.R
import com.livetl.android.ui.common.LinkIcon
import com.livetl.android.util.copyToClipboard
import com.livetl.android.util.share

@Composable
fun StreamActions(videoId: String) {
    val context = LocalContext.current
    val videoUrl = "https://www.youtube.com/watch?v=$videoId"

    Row(Modifier.fillMaxWidth()) {
        LinkIcon(
            modifier = Modifier.weight(1f),
            labelRes = R.string.action_open_in_youtube,
            icon = Icons.Outlined.OpenInBrowser,
            url = videoUrl,
        )
        LinkIcon(
            modifier = Modifier.weight(1f),
            labelRes = R.string.action_copy_link,
            icon = Icons.Outlined.ContentCopy,
            onClick = { context.copyToClipboard(videoUrl) },
        )
        LinkIcon(
            modifier = Modifier.weight(1f),
            labelRes = R.string.action_share,
            icon = Icons.Outlined.Share,
            onClick = { context.share(videoUrl) },
        )
    }
}
