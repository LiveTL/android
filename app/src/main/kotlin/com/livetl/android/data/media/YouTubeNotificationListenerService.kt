package com.livetl.android.data.media
import android.content.ComponentName
import android.content.Context
import android.media.session.MediaSessionManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class YouTubeNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var youTubeSessionService: YouTubeSessionService

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        if (sbn.packageName != YOUTUBE_PACKAGE_NAME) {
            return
        }

        Timber.d("YouTube notification posted")

        val mediaSessionManager = getSystemService<MediaSessionManager>() ?: return

        val sessions = mediaSessionManager.getActiveSessions(ComponentName(this, javaClass))
        youTubeSessionService.onActiveSessionsChanged(sessions)
    }

    companion object {
        fun isNotificationAccessGranted(context: Context): Boolean =
            NotificationManagerCompat.getEnabledListenerPackages(context)
                .any { it == context.packageName }
    }
}
