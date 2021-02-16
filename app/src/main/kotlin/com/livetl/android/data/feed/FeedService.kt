package com.livetl.android.data.feed

import com.livetl.android.util.NetworkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.Request
import ru.gildor.coroutines.okhttp.await

class FeedService(private val networkUtil: NetworkUtil, private val json: Json) {

    suspend fun getFeed(): Feed = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(SCHEDULE_API)
            .build()

        val result = networkUtil.client.newCall(request).await()
        val data = result.body?.string() ?: ""
        json.decodeFromString(data)
    }

    companion object {
        private const val SCHEDULE_API = "https://jetrico.sfo2.digitaloceanspaces.com/hololive/youtube.json"
    }
}