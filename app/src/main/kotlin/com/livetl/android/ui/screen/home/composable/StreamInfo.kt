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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.livetl.android.R
import com.livetl.android.ui.common.LoadingIndicator
import com.livetl.android.ui.navigation.Route

@Composable
fun StreamInfo(route: Route.StreamInfo) {
    val viewModel = hiltViewModel<StreamInfoViewModel, StreamInfoViewModel.Factory>(
        creationCallback = { factory -> factory.create(route) },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    if (state.isLoading) {
        LoadingIndicator()
        return
    }

    if (state.stream == null) {
        Text(stringResource(R.string.select_a_stream))
        return
    }

    LazyColumn(
        modifier = Modifier.safeDrawingPadding(),
    ) {
        item {
            AsyncImage(
                model = state.stream!!.thumbnail,
                contentDescription = null,
                modifier = Modifier
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
                    text = state.stream!!.title,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.requiredHeight(8.dp))
                Text(
                    text = state.stream!!.channel.name,
                    style = MaterialTheme.typography.bodyMedium,
                )

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                StreamActions(state.stream!!.id)

                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                state.description?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall.copy(color = LocalContentColor.current),
                    )
                }

                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}
