package com.livetl.android.ui.screen.home.composable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Divider
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.accompanist.insets.navigationBarsHeight
import com.livetl.android.R
import com.livetl.android.data.feed.Stream
import com.livetl.android.ui.common.SymbolAnnotationType
import com.livetl.android.ui.common.textParser

@Composable
fun StreamSheet(stream: Stream?) {
    if (stream == null) {
        Text(stringResource(R.string.select_a_stream))
        return
    }

    val uriHandler = LocalUriHandler.current

    LazyColumn {
        item {
            Image(
                painter = rememberImagePainter(stream.thumbnail),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .background(Color.Black),
            )
        }

        item {
            Column(
                modifier = Modifier.padding(8.dp),
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

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                StreamActions(stream.id)

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                val styledDescription = textParser(stream.description)
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
                    },
                )

                Spacer(Modifier.navigationBarsHeight())
            }
        }
    }
}
