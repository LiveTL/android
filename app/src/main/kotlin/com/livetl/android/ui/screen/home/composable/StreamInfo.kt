package com.livetl.android.ui.screen.home.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.livetl.android.R
import com.livetl.android.data.feed.Stream
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.ui.common.SymbolAnnotationType
import com.livetl.android.ui.common.textParser
import kotlinx.coroutines.launch

@Composable
fun StreamInfo(
    urlOrId: String,
    viewModel: StreamInfoViewModel,
) {
    val coroutineScope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(true) }
    var stream by remember { mutableStateOf<Stream?>(null) }

    LaunchedEffect(urlOrId) {
        if (urlOrId.isNotEmpty()) {
            coroutineScope.launch {
                stream = viewModel.getStream(urlOrId)
                loading = false
            }
        }
    }

    if (loading) {
        LoadingIndicator()
        return
    }

    if (stream == null) {
        Text(stringResource(R.string.select_a_stream))
        return
    }

    val uriHandler = LocalUriHandler.current

    Surface {
        LazyColumn(
            modifier = Modifier.safeDrawingPadding(),
        ) {
            item {
                AsyncImage(
                    model = stream!!.thumbnail,
                    contentDescription = null,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .aspectRatio(16 / 9f)
                            .background(Color.Black),
                )
            }

            item {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        text = stream!!.title,
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Spacer(modifier = Modifier.requiredHeight(8.dp))
                    Text(
                        text = stream!!.channel.name,
                        style = MaterialTheme.typography.bodyMedium,
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    StreamActions(stream!!.id)

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                    val styledDescription = textParser(stream!!.description)
                    ClickableText(
                        text = styledDescription,
                        style = MaterialTheme.typography.bodySmall.copy(color = LocalContentColor.current),
                        onClick = {
                            styledDescription
                                .getStringAnnotations(start = it, end = it)
                                .firstOrNull()
                                ?.let { annotation ->
                                    when (annotation.tag) {
                                        SymbolAnnotationType.LINK.name ->
                                            uriHandler.openUri(
                                                annotation.item,
                                            )
                                        SymbolAnnotationType.HASHTAG.name ->
                                            uriHandler.openUri(
                                                "https://www.youtube.com/hashtag/${annotation.item}",
                                            )
                                        else -> Unit
                                    }
                                }
                        },
                    )

                    Spacer(Modifier.navigationBarsPadding())
                }
            }
        }
    }
}
