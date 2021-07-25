package com.livetl.android.data.feed

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.URLProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FeedService @Inject constructor(private val client: HttpClient, private val json: Json) {

    suspend fun getFeed(): Feed = withContext(Dispatchers.IO) {
        val result = client.get<HttpResponse> {
            url {
                protocol = URLProtocol.HTTPS
                host = "holodex.net"
                path("api", "v2", "videos")
                with(parameters) {
                    append("status", "live,upcoming,past")
                    append("lang", "all")
                    append("type", "stream")
                    append("include", "description,live_info")
                    append("org", "Hololive")
                    append("sort", "start_scheduled")
                    append("order", "desc")
                    append("limit", "50")
                    append("offset", "0")
                    append("paginated", "<empty>")
                    append("max_upcoming_hours", "48")
                }
                headers {
                    append("X-APIKEY", HOLODEX_API_KEY)
                }
            }
        }

        val response: HoloDexResponse = json.decodeFromString(result.readText())

        Feed(
            live = response.items
                .filter { it.status == "live" }
                .sortedByDescending { it.start_actual },
            upcoming = response.items
                .filter { it.status == "upcoming" }
                .sortedBy { it.start_scheduled },
            ended = response.items
                .filter { it.status == "past" }
                .sortedByDescending { it.end_actual },
        )
    }
}

private const val HOLODEX_API_KEY = "a314698f-90b8-452e-9192-2a87ddd471ff"
