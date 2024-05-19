package com.livetl.android.util

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner

fun Context.isNotificationAccessGranted(): Boolean = NotificationManagerCompat.getEnabledListenerPackages(this)
    .any { it == packageName }

@Composable
fun rememberIsNotificationAccessGranted(initialValue: Boolean = true): Boolean {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var accessGranted by remember { mutableStateOf(initialValue) }

    DisposableEffect(lifecycleOwner.lifecycle) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                accessGranted = context.isNotificationAccessGranted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    return accessGranted
}
