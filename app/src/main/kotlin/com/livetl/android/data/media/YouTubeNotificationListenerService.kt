package com.livetl.android.data.media
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.session.MediaSessionManager
import android.os.Build
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
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
        fun getPermissionScreenIntent(context: Context): Intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS).apply {
                val componentName = ComponentName(
                    context.packageName,
                    YouTubeNotificationListenerService::class.java.name,
                )
                putExtra(
                    Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
                    componentName.flattenToString(),
                )
            }
        } else {
            Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        }
    }
}
