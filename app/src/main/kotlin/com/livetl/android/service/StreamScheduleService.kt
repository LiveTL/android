package com.livetl.android.service

import com.livetl.android.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Request

class StreamScheduleService(private val networkUtil: NetworkUtil) {

    suspend fun getSchedule() = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(API)
            .build()

//        networkUtil.client
    }

    companion object {
        private const val API = "https://jetrico.sfo2.digitaloceanspaces.com/hololive/youtube.json"
    }
}