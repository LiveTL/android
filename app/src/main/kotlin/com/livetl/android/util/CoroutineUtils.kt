package com.livetl.android.util

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
fun runOnMainThread(block: () -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        block()
    }
}
