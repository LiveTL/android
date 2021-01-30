package com.livetl.android.ui.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.livetl.android.databinding.VideoPlayerBinding

@Composable
fun VideoPlayer(sourceUrl: String): MediaPlayback {
    val context = AmbientContext.current

    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build()
    }

    DisposableEffect(sourceUrl) {
        val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
            context,
            Util.getUserAgent(context, context.packageName)
        )

        val mediaItem = MediaItem.Builder().setUri(sourceUrl).build()
        val source = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)

        exoPlayer.setMediaSource(source)
        exoPlayer.prepare()

        onDispose {  }
    }

    AndroidViewBinding(VideoPlayerBinding::inflate) {
        playerView.player = exoPlayer
        exoPlayer.playWhenReady = true
    }

    return object: MediaPlayback {
        override fun playPause() {
            exoPlayer.playWhenReady = !exoPlayer.playWhenReady
        }

        override fun forward(durationInMillis: Long) {
            exoPlayer.seekTo(exoPlayer.currentPosition + durationInMillis)
        }

        override fun rewind(durationInMillis: Long) {
            exoPlayer.seekTo(exoPlayer.currentPosition - durationInMillis)
        }
    }
}

interface MediaPlayback {
    fun playPause()
    fun forward(durationInMillis: Long)
    fun rewind(durationInMillis: Long)
}
