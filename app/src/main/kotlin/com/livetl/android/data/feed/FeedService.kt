package com.livetl.android.data.feed

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class FeedService @Inject constructor(
    private val client: HttpClient,
    private val json: Json,
) {

    suspend fun getFeed(
        organization: String? = "Hololive",
        status: StreamStatus,
    ): List<Stream> = withContext(Dispatchers.IO) {
        val result = client.get<HttpResponse> {
            url {
                baseConfig()
                path("api", "v2", "videos")
                parameter("status", status.apiValue)
                parameter("lang", "all")
                parameter("type", "stream")
                parameter("include", "description,live_info")
                parameter("org", organization)
                parameter("sort", status.sortField)
                parameter("order", if (status.sortAscending) "asc" else "desc")
                parameter("limit", "50")
                parameter("offset", "0")
                parameter("paginated", "<empty>")
                parameter("max_upcoming_hours", "48")
            }
            headers {
                set("X-APIKEY", "278935bd-d91d-4037-a2b8-95b781428af7")
                set("Accept", "application/json")
            }
        }

        val response = json.decodeFromString<HolodexVideosResponse>(result.readText())
        response.items
    }

    suspend fun getVideoInfo(videoId: String): Stream = withContext(SupervisorJob() + Dispatchers.IO) {
        val result = client.get<HttpResponse> {
            url {
                baseConfig()
                path("api", "v2", "videos", videoId)
            }
            headers {
                set("X-APIKEY", "278935bd-d91d-4037-a2b8-95b781428af7")
                set("Accept", "application/json")
            }
        }

        json.decodeFromString(result.readText())
    }

    private fun URLBuilder.baseConfig() {
        protocol = URLProtocol.HTTPS
        host = "holodex.net"
    }
}
