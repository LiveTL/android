package com.livetl.android.util

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

class NetworkUtil(context: Context) {

    private val cacheDir = File(context.cacheDir, "network_cache")
    private val cacheSize = 5L * 1024 * 1024 // 5 MiB

    val client = OkHttpClient.Builder()
        .cache(Cache(cacheDir, cacheSize))
        .build()
}