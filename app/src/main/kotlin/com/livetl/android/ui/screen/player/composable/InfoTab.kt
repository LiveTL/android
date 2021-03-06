package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.SymbolAnnotationType
import com.livetl.android.ui.textParser

@Composable
fun InfoTab(
    streamInfo: StreamInfo?
) {
    val uriHandler = LocalUriHandler.current

    if (streamInfo != null) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
        ) {
            Text(
                text = streamInfo.title,
                style = MaterialTheme.typography.h5,
            )
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Text(
                text = streamInfo.author,
                style = MaterialTheme.typography.subtitle1,
            )
            Spacer(modifier = Modifier.requiredHeight(8.dp))
            Divider()
            Spacer(modifier = Modifier.requiredHeight(8.dp))

            val styledDescription = textParser(streamInfo.shortDescription)
            ClickableText(
                text = styledDescription,
                style = MaterialTheme.typography.body1.copy(color = LocalContentColor.current),
                onClick = {
                    styledDescription
                        .getStringAnnotations(start = it, end = it)
                        .firstOrNull()
                        ?.let { annotation ->
                            when (annotation.tag) {
                                SymbolAnnotationType.LINK.name -> uriHandler.openUri(annotation.item)
                                SymbolAnnotationType.HASHTAG.name -> uriHandler.openUri("https://www.youtube.com/hashtag/${annotation.item}")
                                else -> Unit
                            }
                        }
                }
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}
