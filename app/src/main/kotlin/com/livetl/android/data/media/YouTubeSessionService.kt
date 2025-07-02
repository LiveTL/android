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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import logcat.asLog
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

// TODO: clean this up
@Singleton
class YouTubeSessionService @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val streamService: StreamService,
) : MediaSessionManager.OnActiveSessionsChangedListener {

    val session = MutableStateFlow<YouTubeSession?>(null)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val component = ComponentName(context, javaClass)

    private var mediaController: MediaController? = null
    private var progressJob: Job? = null
    private val mediaControllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            progressJob = scope.launch {
                val currentSession = updateYouTubeSession()

                // We don't really get progress updates, so we poll for it
                if (state?.state == PlaybackState.STATE_PLAYING && currentSession?.isLive == false) {
                    while (true) {
                        delay(2.seconds)
                        logcat { "Updating playback position" }
                        session.update {
                            it?.copy(
                                positionInMs = mediaController?.playbackState?.position,
                            )
                        }
                    }
                } else {
                    progressJob?.let {
                        logcat { "Stopping playback position update" }
                        it.cancel()
                        progressJob = null
                    }
                }
            }
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            scope.launch {
                updateYouTubeSession()
            }
        }

        override fun onSessionDestroyed() {
            mediaController?.unregisterCallback(this)
            mediaController = null
        }
    }

    fun attach() {
        try {
            if (context.isNotificationAccessGranted()) {
                logcat { "Starting media session listener" }
                val mediaSessionManager = context.getSystemService<MediaSessionManager>()
                mediaSessionManager?.addOnActiveSessionsChangedListener(this, component)
                mediaSessionManager?.getActiveSessions(component)?.let(::listenToYouTubeMediaSession)
            } else {
                logcat { "Can't start media session listener due to missing notification listener permissions" }
            }
        } catch (e: SecurityException) {
            logcat { "Failed to start media session listener: ${e.asLog()}" }
        }
    }

    fun detach() {
        logcat { "Stopping media session listener" }
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
                    logcat { "Found new YouTube media session: ${it.sessionToken}" }

                    if (mediaController != null) {
                        mediaController?.unregisterCallback(mediaControllerCallback)
                        mediaController = null
                    }

                    mediaController = it
                    it.registerCallback(mediaControllerCallback)
                }
            }
    }

    private suspend fun updateYouTubeSession(): YouTubeSession? {
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
        val positionInMs = mediaController?.playbackState?.position

        // The media session doesn't expose the actual ID of the YouTube video, unfortunately
        logcat { "Looking up stream for $title / $channelName" }
        val streamInfo = streamService.findStreamInfo(title, channelName)

        val newSession = YouTubeSession(
            videoId = streamInfo?.videoId,
            videoTitle = title,
            channelName = channelName,
            isLive = positionInMs == 0L || streamInfo?.isLive == true,
            playbackState = state,
            positionInMs = positionInMs,
        )

        return session.updateAndGet { newSession }
    }
}

data class YouTubeSession(
    val videoId: String?,
    val videoTitle: String,
    val channelName: String,
    val isLive: Boolean,
    val playbackState: YouTubeVideoPlaybackState,
    val positionInMs: Long?,
)

enum class YouTubeVideoPlaybackState {
    PLAYING,
    PAUSED,
    UNKNOWN,
}

const val YOUTUBE_PACKAGE_NAME = "com.google.android.youtube"
