package com.livetl.android

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.livetl.android.data.media.YouTubeSessionService
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    DefaultLifecycleObserver {

    @Inject
    lateinit var youTubeSessionService: YouTubeSessionService

    override fun onCreate() {
        super<Application>.onCreate()

        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(AndroidLogcatLogger(if (BuildConfig.DEBUG) LogPriority.VERBOSE else LogPriority.ERROR))
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        // TODO: attach when notification permissions granted?
        youTubeSessionService.attach()
    }

    override fun onStop(owner: LifecycleOwner) {
        // TODO: stop ChatService too?
        youTubeSessionService.detach()
    }
}
