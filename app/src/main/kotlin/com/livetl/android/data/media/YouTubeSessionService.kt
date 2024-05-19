package com.livetl.android.data.media

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import androidx.core.content.getSystemService
import com.livetl.android.data.stream.StreamService
import com.livetl.android.util.isNotificationAccessGranted
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

// TODO: clean this up
@Singleton
class YouTubeSessionService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val streamService: StreamService,
) : MediaSessionManager.OnActiveSessionsChangedListener {

    val session = MutableStateFlow<YouTubeSession?>(null)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val component = ComponentName(context, javaClass)

    private var mediaController: MediaController? = null
    private var progressJob: Job? = null
    private val mediaControllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            scope.launch {
                session.value = getYouTubeSession()

                // We don't really get progress updates, so we simulate per-second updates
                // while it's playing
                if (state?.state == PlaybackState.STATE_PLAYING && session.value?.isLive == false) {
                    progressJob = launch {
                        while (true) {
                            delay(2.seconds)
                            session.value = session.value?.copy(
                                positionInMs = (session.value?.positionInMs ?: 0L) + 2000L,
                            )
                        }
                    }
                } else {
                    progressJob?.cancel()
                    progressJob = null
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            scope.launch {
                session.value = getYouTubeSession()
            }
        }

        override fun onSessionDestroyed() {
            mediaController?.unregisterCallback(this)
            mediaController = null
        }
    }

    fun attach() {
        if (context.isNotificationAccessGranted()) {
            Timber.d("Starting media session listener")
            val mediaSessionManager = context.getSystemService<MediaSessionManager>()
            mediaSessionManager?.addOnActiveSessionsChangedListener(this, component)
            mediaSessionManager?.getActiveSessions(component)?.let(::listenToYouTubeMediaSession)
        } else {
            Timber.d("Can't start media session listener due to missing notification listener permissions")
        }
    }

    fun detach() {
        Timber.d("Stopping media session listener")
        context.getSystemService<MediaSessionManager>()?.removeOnActiveSessionsChangedListener(this)
    }

    override fun onActiveSessionsChanged(controllers: List<MediaController>?) {
        listenToYouTubeMediaSession(controllers)
    }

    private fun listenToYouTubeMediaSession(controllers: List<MediaController>?) {
        controllers
            ?.find { it.packageName == YOUTUBE_PACKAGE_NAME }
            ?.let {
                if (mediaController?.sessionToken != it.sessionToken) {
                    Timber.d("Found YouTube media session: ${it.sessionToken}")

                    if (mediaController != null) {
                        mediaController?.unregisterCallback(mediaControllerCallback)
                        mediaController = null
                    }

                    mediaController = it
                    it.registerCallback(mediaControllerCallback)
                }
            }
    }

    private suspend fun getYouTubeSession(): YouTubeSession? {
        if (mediaController == null) return null

        val title = mediaController?.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
        val channelName = mediaController?.metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)

        if (title.isNullOrEmpty() || channelName.isNullOrEmpty()) {
            return null
        }

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
            isLive = position == 0L || streamInfo?.isLive == true,
            positionInMs = position,
            videoId = streamInfo?.videoId,
        )
    }
}

data class YouTubeSession(
    val title: String,
    val channelName: String,
    val playbackState: YouTubeVideoPlaybackState,
    val isLive: Boolean,
    val positionInMs: Long?,
    val videoId: String?,
)

enum class YouTubeVideoPlaybackState {
    PLAYING,
    PAUSED,
    UNKNOWN,
}

const val YOUTUBE_PACKAGE_NAME = "com.google.android.youtube"
