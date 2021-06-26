package com.livetl.android.data.feed

import com.livetl.android.util.toDate
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import javax.inject.Inject

class FeedService @Inject constructor(private val client: HttpClient, private val json: Json) {

    suspend fun getFeed(): Feed = withContext(Dispatchers.IO) {
        val result = client.get<HttpResponse>(SCHEDULE_API)
        val feed: Feed = json.decodeFromString(result.readText())

        // Filter out streams that are scheduled for more than a week from now
        // (usually things like free chats)
        val fallbackDate = Date.from(Instant.now())
        val weekLimit = Date.from(Instant.now().plus(7, ChronoUnit.DAYS))
        feed.copy(
            upcoming = feed.upcoming.filter {
                val scheduledStart = it.live_schedule?.toDate() ?: fallbackDate
                scheduledStart <= weekLimit
            }
        )
    }
}

private const val SCHEDULE_API = "https://jetrico.sfo2.digitaloceanspaces.com/hololive/youtube.json"
