package com.livetl.android.data.media

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import androidx.core.content.getSystemService
import com.livetl.android.data.stream.StreamService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YouTubeMediaSessionService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val streamService: StreamService,
) : MediaSessionManager.OnActiveSessionsChangedListener {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _session = MutableStateFlow<YouTubeSession?>(null)
    val session: SharedFlow<YouTubeSession?>
        get() = _session.asSharedFlow()

    private val component = ComponentName(context, YouTubeMediaSessionService::class.java)

    private var mediaController: MediaController? = null
    private val mediaControllerCallback = object : MediaController.Callback() {
        override fun onSessionDestroyed() {
            mediaController = null
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            scope.launch {
                _session.value = getYouTubeSession()
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            scope.launch {
                _session.value = getYouTubeSession()
            }
        }
    }

    init {
        Timber.i("Listening for YouTube media sessions")
        // TODO: stop listening at some point?
        context.getSystemService<MediaSessionManager>()?.addOnActiveSessionsChangedListener(this, component)
    }

    override fun onActiveSessionsChanged(controllers: MutableList<MediaController>?) {
        controllers
            ?.find { it.packageName == YOUTUBE_PACKAGE_NAME }
            ?.let {
                it.registerCallback(mediaControllerCallback)
                mediaController = it
            }
    }

    private suspend fun getYouTubeSession(): YouTubeSession? {
        if (mediaController == null) return null

        val title = mediaController?.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: return null
        val channelName = mediaController?.metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: return null
        val state = when (mediaController?.playbackState?.state) {
            PlaybackState.STATE_PAUSED -> YouTubeVideoPlaybackState.PAUSED
            PlaybackState.STATE_PLAYING -> YouTubeVideoPlaybackState.PLAYING
            else -> YouTubeVideoPlaybackState.UNKNOWN
        }
        val position = mediaController?.playbackState?.position // in ms; 0 is live

        // The media session doesn't expose the actual ID of the YouTube video, unfortunately
        Timber.d("Looking up stream for $title / $channelName")
        val streamInfo = streamService.findStreamInfo(title, channelName)

        return YouTubeSession(
            title = title,
            channelName = channelName,
            playbackState = state,
            position = position,
            videoId = streamInfo?.videoId,
        )
    }
}

data class YouTubeSession(
    val title: String,
    val channelName: String,
    val playbackState: YouTubeVideoPlaybackState,
    val position: Long?,
    val videoId: String?,
)

enum class YouTubeVideoPlaybackState {
    PLAYING,
    PAUSED,
    UNKNOWN,
}

const val YOUTUBE_PACKAGE_NAME = "com.google.android.youtube"
