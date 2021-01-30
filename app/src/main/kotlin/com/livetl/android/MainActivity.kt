package com.livetl.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.livetl.android.ui.screen.PlayerScreen
import com.livetl.android.ui.theme.LiveTLTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveTLTheme {
                PlayerScreen()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val action = intent.action
        val type = intent.type
        if (Intent.ACTION_VIEW == action) {
//            handleVideoIntent(intent.dataString)
        } else if (Intent.ACTION_SEND == action && "text/plain" == type) {
//            handleVideoIntent(intent.getStringExtra(Intent.EXTRA_TEXT))
        }
    }
}