package com.livetl.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavHostController
import com.livetl.android.ui.navigation.Route
import com.livetl.android.ui.navigation.mainNavHost
import com.livetl.android.ui.navigation.navigateToPlayer
import com.livetl.android.ui.theme.LiveTLTheme
import com.livetl.android.util.AppPreferences
import com.livetl.android.util.powerManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var prefs: AppPreferences

    private var navController: NavHostController? = null
    private var wakeLock: PowerManager.WakeLock? = null

    @SuppressLint("WakelockTimeout")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val startRoute =
            when (prefs.showWelcomeScreen().get()) {
                true -> Route.Welcome
                false -> Route.Home
            }

        setContent {
            LiveTLTheme {
                navController =
                    mainNavHost(
                        startRoute = startRoute,
                        setKeepScreenOn = this::setKeepScreenOn,
                        setFullscreen = this::setFullscreen,
                    )
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

    @SuppressLint("WakelockTimeout")
    private fun setKeepScreenOn(enabled: Boolean) {
        if (enabled) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            wakeLock =
                powerManager.run {
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
        val controller = WindowInsetsControllerCompat(window!!, window.decorView.rootView)
        if (enabled) {
            controller.apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            controller
                .show(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun handleVideoIntent(data: String?) {
        data?.let { navController?.navigateToPlayer(it) }
    }
}
