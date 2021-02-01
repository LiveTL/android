package com.livetl.android.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.AmbientContext
import androidx.localbroadcastmanager.content.LocalBroadcastManager

@Composable
fun BroadcastReceiver(
    intentFilter: IntentFilter,
    receiver: (intent: Intent) -> Unit
) {
    val context = AmbientContext.current

    DisposableEffect(context, intentFilter) {
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                intent?.let { receiver(it) }
            }
        }

        LocalBroadcastManager.getInstance(context)
            .registerReceiver(broadcast, intentFilter)

        onDispose {
            LocalBroadcastManager.getInstance(context)
                .unregisterReceiver(broadcast)
        }
    }
}