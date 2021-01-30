package com.livetl.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.setContent
import com.livetl.android.screens.MainScreen
import com.livetl.android.ui.theme.LiveTLTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveTLTheme {
                MainScreen()
            }
        }
    }
}