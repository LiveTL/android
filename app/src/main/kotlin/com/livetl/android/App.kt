package com.livetl.android

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.livetl.android.data.media.YouTubeSessionService
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import javax.inject.Inject

@HiltAndroidApp
class App :
    Application(),
    DefaultLifecycleObserver {

    @Inject
    lateinit var youTubeSessionService: YouTubeSessionService

    override fun onCreate() {
        super<Application>.onCreate()

        AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)

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
