package com.livetl.android.data.feed

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FeedService @Inject constructor(private val client: HttpClient, private val json: Json) {

    suspend fun getFeed(): Feed = withContext(Dispatchers.IO) {
        val result = client.get<HttpResponse>(SCHEDULE_API)
        json.decodeFromString(result.readText())
    }
}

private const val SCHEDULE_API = "https://jetrico.sfo2.digitaloceanspaces.com/hololive/youtube.json"
