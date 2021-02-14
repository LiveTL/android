package com.livetl.android.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.livetl.android.navigation.MainNavHost
import com.livetl.android.ui.theme.LiveTLTheme
import com.livetl.android.util.powerManager

class MainActivity : AppCompatActivity() {

    private lateinit var wakeLock: PowerManager.WakeLock

    @SuppressLint("WakelockTimeout")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveTLTheme {
                MainNavHost()
            }
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        wakeLock = powerManager.run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LiveTL::WakelockTag").apply {
                acquire()
            }
        }

        onNewIntent(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        wakeLock?.release()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (Intent.ACTION_VIEW == intent.action) {
            handleVideoIntent(intent.dataString)
        } else if (Intent.ACTION_SEND == intent.action && "text/plain" == intent.type) {
            handleVideoIntent(intent.getStringExtra(Intent.EXTRA_TEXT))
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