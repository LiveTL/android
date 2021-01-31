package com.livetl.android.ui.composable

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.livetl.android.databinding.VideoPlayerBinding

@Composable
fun VideoPlayer(
    videoSourceUrl: String? = null,
    audioSourceUrl: String? = null,
    modifier: Modifier = Modifier
): MediaPlayback {
    val context = AmbientContext.current

    // TODO: dispose?
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build()
    }

    DisposableEffect(videoSourceUrl) {
        Log.d("VideoPlayer", "Received sourceUrl: $videoSourceUrl")
        if (videoSourceUrl != null) {
            val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
                context,
                Util.getUserAgent(context, context.packageName)
            )

            val videoSource = if (videoSourceUrl.endsWith(".m3u8")) {
                HlsMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoSourceUrl.toMediaItem())
            } else {
                ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoSourceUrl.toMediaItem())
            }

            if (audioSourceUrl != null) {
                val audioSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(audioSourceUrl.toMediaItem())

                val muxedSource = MergingMediaSource(videoSource, audioSource)
                exoPlayer.setMediaSource(muxedSource)
            } else {
                exoPlayer.setMediaSource(videoSource)
            }

            exoPlayer.prepare()
        } else {
            exoPlayer.stop()
        }

        onDispose {}
    }

    AndroidViewBinding(bindingBlock = VideoPlayerBinding::inflate, modifier = modifier) {
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

private fun String.toMediaItem(): MediaItem {
    return MediaItem.Builder().setUri(this).build()
}
