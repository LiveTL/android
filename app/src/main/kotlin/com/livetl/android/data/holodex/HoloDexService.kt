package com.livetl.android.data.holodex

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.readText
import io.ktor.client.utils.buildHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class HoloDexService @Inject constructor(
    private val client: HttpClient,
    private val json: Json,
) {

    suspend fun getFeed(organization: String?): HolodexVideosResponse = withContext(Dispatchers.IO) {
        val result = client.get<HttpResponse> {
            url {
                baseConfig()
                path("api", "v2", "videos")
                parameter("status", "live,upcoming,past")
                parameter("lang", "all")
                parameter("type", "stream")
                parameter("include", "description,live_info")
                parameter("org", organization ?: "Hololive")
                parameter("sort", "start_scheduled")
                parameter("order", "desc")
                parameter("limit", "50")
                parameter("offset", "0")
                parameter("paginated", "<empty>")
                parameter("max_upcoming_hours", "48")
            }
        }

        json.decodeFromString(result.readText())
    }

    suspend fun getVideoInfo(videoId: String): Stream = withContext(Dispatchers.IO) {
        val result = client.get<HttpResponse> {
            url {
                baseConfig()
                path("api", "v2", "videos", videoId)
            }
        }

        json.decodeFromString(result.readText())
    }

    private fun URLBuilder.baseConfig() {
        protocol = URLProtocol.HTTPS
        host = "holodex.net"
        buildHeaders {
            append("X-APIKEY", "a314698f-90b8-452e-9192-2a87ddd471ff")
        }
    }
}
