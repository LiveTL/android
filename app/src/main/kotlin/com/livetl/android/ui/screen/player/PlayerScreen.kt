package com.livetl.android.ui.screen.player

import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.util.Rational
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.livetl.android.R
import com.livetl.android.ui.screen.player.composable.PlayerTabs
import com.livetl.android.util.rememberIsInPipMode
import com.livetl.android.util.rememberIsInSplitScreenMode
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(urlOrId: String, viewModel: PlayerViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val isInPipMode = rememberIsInPipMode()
    val isInSplitScreenMode = rememberIsInSplitScreenMode()
    val activity = LocalActivity.current
    val uriHandler = LocalUriHandler.current

    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(urlOrId) {
        if (urlOrId.isNotEmpty()) {
            coroutineScope.launch {
                viewModel.loadStream(urlOrId)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            if (!isInPipMode && !isInSplitScreenMode) {
                ExtendedFloatingActionButton(
                    onClick = {
                        if (activity?.packageManager?.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE) ==
                            true
                        ) {
                            activity.enterPictureInPictureMode(
                                PictureInPictureParams.Builder()
                                    // Must be between 2.39:1 and 1:2.39 (inclusive)
                                    .setAspectRatio(Rational(239, 100))
                                    .build(),
                            )
                        }

                        uriHandler.openUri("https://www.youtube.com/watch?v=${state.streamInfo?.videoId}")
                    },
                    text = { Text(stringResource(R.string.action_open_youtube)) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = null,
                        )
                    },
                )
            }
        },
    ) { contentPadding ->
        PlayerTabs(
            streamInfo = state.streamInfo,
            chatState = state.chatState,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding),
        )
    }
}
