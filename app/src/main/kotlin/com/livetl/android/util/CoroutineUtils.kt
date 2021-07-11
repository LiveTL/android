package com.livetl.android.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

fun runOnMainThread(block: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        block()
    }
}
