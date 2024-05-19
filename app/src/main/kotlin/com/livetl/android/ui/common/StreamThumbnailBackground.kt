package com.livetl.android.ui.common

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun BoxScope.StreamThumbnailBackground(thumbnail: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(thumbnail)
            .transformations()
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = modifier
            .matchParentSize()
            .alpha(0.1f)
            .blur(5.dp),
        contentScale = ContentScale.Crop,
    )
}
