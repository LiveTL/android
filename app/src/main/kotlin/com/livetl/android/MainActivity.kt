package com.livetl.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.livetl.android.navigation.MainNavHost
import com.livetl.android.ui.theme.LiveTLTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveTLTheme {
                MainNavHost()
            }
        }
        onNewIntent(intent)
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
        const val DEEP_LINK_INTENT = "LiveTL-DeepLink"
        const val DEEP_LINK_INTENT_EXTRA = "data"
    }
}