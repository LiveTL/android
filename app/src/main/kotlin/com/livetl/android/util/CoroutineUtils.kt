package com.livetl.android.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

suspend fun <T> withUIContext(block: suspend CoroutineScope.() -> T): T = withContext(Dispatchers.Main, block)

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
