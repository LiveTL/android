package com.livetl.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.livetl.android.ui.navigation.MainNavHost
import com.livetl.android.ui.theme.LiveTLTheme
import com.livetl.android.util.powerManager

class MainActivity : AppCompatActivity() {

    private var wakeLock: PowerManager.WakeLock? = null

    @SuppressLint("WakelockTimeout")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveTLTheme {
                MainNavHost(
                    setKeepScreenOn = this::setKeepScreenOn,
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

    fun setKeepScreenOn(enabled: Boolean) {
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

    // We broadcast intents that the Compose nav host then handles
    private fun handleVideoIntent(data: String?) {
        data?.let {
            val intent = Intent().apply {
                action = DEEP_LINK_INTENT
                putExtra(DEEP_LINK_INTENT_EXTRA, data)
            }

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
    }

    companion object {
        const val DEEP_LINK_INTENT = "LiveTL::DeepLink"
        const val DEEP_LINK_INTENT_EXTRA = "data"
    }
}
