package com.livetl.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.View
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.navigation.NavHostController
import com.google.accompanist.insets.ProvideWindowInsets
import com.livetl.android.ui.navigation.MainNavHost
import com.livetl.android.ui.navigation.Route
import com.livetl.android.ui.navigation.navigateToPlayer
import com.livetl.android.ui.theme.LiveTLTheme
import com.livetl.android.util.PreferencesHelper
import com.livetl.android.util.powerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefs: PreferencesHelper

    private lateinit var navController: NavHostController
    private var wakeLock: PowerManager.WakeLock? = null

    @SuppressLint("WakelockTimeout")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val startRoute = when (prefs.showWelcomeScreen().get()) {
            true -> Route.Welcome
            false -> Route.Home
        }

        setContent {
            LiveTLTheme {
                ProvideWindowInsets {
                    navController = MainNavHost(
                        startRoute = startRoute,
                        setKeepScreenOn = this::setKeepScreenOn,
                        setFullscreen = this::setFullscreen,
                    )
                }
            }
        }

        // Needs to be delayed so the app contents can load first
        Handler(Looper.getMainLooper()).post {
            onNewIntent(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        setKeepScreenOn(false)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        when (intent.action) {
            Intent.ACTION_VIEW -> handleVideoIntent(intent.dataString)
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    handleVideoIntent(intent.getStringExtra(Intent.EXTRA_TEXT))
                }
            }
        }
    }

    private fun setKeepScreenOn(enabled: Boolean) {
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            wakeLock = powerManager.run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LiveTL::WakelockTag").apply {
                    acquire()
                }
            }
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            wakeLock?.release()
            wakeLock = null
        }
    }

    private fun setFullscreen(enabled: Boolean) {
        if (enabled) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        } else {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    private fun handleVideoIntent(data: String?) {
        data?.let { navController.navigateToPlayer(it) }
    }
}
