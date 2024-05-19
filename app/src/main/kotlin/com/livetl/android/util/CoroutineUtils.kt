package com.livetl.android.util

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@DelicateCoroutinesApi
fun runOnMainThread(block: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        block()
    }
}

suspend fun waitUntil(
    predicate: () -> Boolean,
    timeout: Duration = 10.seconds,
    delayTime: Duration = 1.seconds,
    block: () -> Unit,
) {
    withTimeout(timeout) {
        while (true) {
            delay(delayTime)
            if (predicate()) {
                block()
                return@withTimeout
            }
        }
    }
}
