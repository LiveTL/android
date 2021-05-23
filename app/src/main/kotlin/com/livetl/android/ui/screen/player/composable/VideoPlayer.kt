package com.livetl.android.ui.screen.player.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.viewmodel.compose.viewModel
import com.livetl.android.databinding.VideoPlayerBinding
import com.livetl.android.vm.PlayerViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import timber.log.Timber

@Composable
fun VideoPlayer(
    modifier: Modifier = Modifier,
    videoId: String?,
    isLive: Boolean?,
    onCurrentSecond: (Float) -> Unit,
    playerViewModel: PlayerViewModel = viewModel(),
) {
    var player = remember<YouTubePlayer?> { null }

    fun loadVideo() {
        if (!videoId.isNullOrBlank()) {
            player?.loadVideo(videoId, playerViewModel.currentSecond)
        }
    }

    LaunchedEffect(videoId) {
        loadVideo()
    }

    AndroidViewBinding(factory = VideoPlayerBinding::inflate, modifier = modifier) {
        with(youtubePlayerView.getPlayerUiController()) {
            enableLiveVideoUi(isLive ?: false)
            showVideoTitle(false)
            showYouTubeButton(false)
            showFullscreenButton(false)
        }

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                player = youTubePlayer
                loadVideo()
            }

            override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                super.onError(youTubePlayer, error)
                Timber.w("Error: ${error.name}")
                if (videoId != null && playerViewModel.videoAttemptedRetries < 3) {
                    playerViewModel.videoAttemptedRetries++
                    Timber.d("Retry #${playerViewModel.videoAttemptedRetries} to load $videoId")
                    loadVideo()
                }
            }

            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                playerViewModel.currentSecond = second
                onCurrentSecond(second)
            }
        })
    }
}
