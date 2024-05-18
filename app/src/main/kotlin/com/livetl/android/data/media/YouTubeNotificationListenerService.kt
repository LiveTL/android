package com.livetl.android.data.media

import android.content.ComponentName
import android.media.MediaMetadata
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.content.getSystemService
import com.livetl.android.data.stream.StreamService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class YouTubeNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var streamService: StreamService

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        if (sbn.packageName != YOUTUBE_PACKAGE_NAME) {
            return
        }

        val mediaSessionManager = getSystemService<MediaSessionManager>() ?: return

        val component = ComponentName(this, YouTubeNotificationListenerService::class.java)
        val sessions = mediaSessionManager.getActiveSessions(component)
        val youtubeSession = sessions.find { it.packageName == YOUTUBE_PACKAGE_NAME } ?: return

        // The notification and media session don't expose the actual ID of the YouTube video, unfortunately
        val videoTitle = youtubeSession.metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: return
        val videoChannelName = youtubeSession.metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: return

        scope.launch {
            Timber.d("Looking up stream for $videoTitle / $videoChannelName")
            val streamInfo = streamService.findStreamInfo(videoTitle, videoChannelName) ?: return@launch

            val position = youtubeSession.playbackState?.position // in ms; 0 is live
            val state = when (youtubeSession.playbackState?.state) {
                PlaybackState.STATE_PAUSED -> "PAUSED"
                PlaybackState.STATE_PLAYING -> "PLAYING"
                else -> null
            }

            Timber.i("Current YouTube video: ${streamInfo.videoId} / ${streamInfo.title} / $position / $state")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)

        if (sbn.packageName != YOUTUBE_PACKAGE_NAME) {
            return
        }

        Timber.i("onNotificationRemoved")
    }
}

private const val YOUTUBE_PACKAGE_NAME = "com.google.android.youtube"
