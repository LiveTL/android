package com.livetl.android.data.holodex

import io.ktor.client.HttpClient
import io.ktor.client.request.get
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

    suspend fun getFeed(organizations: Collection<String>): HolodexVideosResponse = withContext(Dispatchers.IO) {
        val orgs = when {
            organizations.isNotEmpty() -> organizations
            else -> listOf("Hololive")
        }

        val result = client.get<HttpResponse> {
            url {
                baseConfig()
                path("api", "v2", "videos")
                with(parameters) {
                    append("status", "live,upcoming,past")
                    append("lang", "all")
                    append("type", "stream")
                    append("include", "description,live_info")
                    append("org", orgs.joinToString(","))
                    append("sort", "start_scheduled")
                    append("order", "desc")
                    append("limit", "50")
                    append("offset", "0")
                    append("paginated", "<empty>")
                    append("max_upcoming_hours", "48")
                }
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
