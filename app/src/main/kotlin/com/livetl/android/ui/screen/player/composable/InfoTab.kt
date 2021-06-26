package com.livetl.android.ui.screen.player.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.livetl.android.data.stream.StreamInfo
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.ui.common.SymbolAnnotationType
import com.livetl.android.ui.common.textParser
import com.livetl.android.ui.screen.home.composable.StreamActions

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
            StreamActions(streamInfo.videoId)
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
        LoadingIndicator()
    }
}
