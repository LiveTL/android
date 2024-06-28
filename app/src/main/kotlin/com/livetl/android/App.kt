package com.livetl.android

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import logcat.LogPriority
import logcat.LogcatLogger

@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()

        if (!LogcatLogger.isInstalled) {
            LogcatLogger.install(AndroidLogcatLogger(if (BuildConfig.DEBUG) LogPriority.VERBOSE else LogPriority.ERROR))
        }
    }
}
