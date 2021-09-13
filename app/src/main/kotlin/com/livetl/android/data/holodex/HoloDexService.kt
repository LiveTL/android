package com.livetl.android.data.holodex

import com.livetl.android.util.await
import com.livetl.android.util.get
import com.livetl.android.util.parseAs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class HoloDexService @Inject constructor(
    private val okhttpClient: OkHttpClient,
    private val json: Json,
) {

    suspend fun getFeed(organization: String?): HolodexVideosResponse = withContext(Dispatchers.IO) {
        okhttpClient
            .await(
                get {
                    url(
                        "$BASE_URL/api/v2/videos".toHttpUrl().newBuilder()
                            .addQueryParameter("status", "live,upcoming,past")
                            .addQueryParameter("lang", "all")
                            .addQueryParameter("type", "stream")
                            .addQueryParameter("include", "description,live_info")
                            .addQueryParameter("org", organization ?: "Hololive")
                            .addQueryParameter("sort", "start_scheduled")
                            .addQueryParameter("order", "desc")
                            .addQueryParameter("limit", "50")
                            .addQueryParameter("offset", "0")
                            .addQueryParameter("paginated", "<empty>")
                            .addQueryParameter("max_upcoming_hours", "48")
                            .build()
                    )
                    apiHeader()
                }
            )
            .parseAs(json)
    }

    suspend fun getVideoInfo(videoId: String): Stream = withContext(Dispatchers.IO) {
        okhttpClient
            .await(
                get {
                    url("$BASE_URL/api/v2/videos/$videoId")
                    apiHeader()
                }
            )
            .parseAs(json)
    }

    private fun Request.Builder.apiHeader() {
        header("X-APIKEY", "a314698f-90b8-452e-9192-2a87ddd471ff")
    }
}

private const val BASE_URL = "https://holodex.net"
