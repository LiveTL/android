package com.livetl.android.ui.common

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun LinkIcon(@StringRes labelRes: Int, icon: ImageVector, url: String, modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    LinkIcon(labelRes, icon, modifier) { uriHandler.openUri(url) }
}

@Composable
fun LinkIcon(@StringRes labelRes: Int, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier =
        modifier
            .clickable(onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
        )
        Text(stringResource(labelRes))
    }
}
