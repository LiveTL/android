package com.livetl.android.util

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import ru.gildor.coroutines.okhttp.await

suspend fun OkHttpClient.await(request: Request) = newCall(request).await()

fun get(block: Request.Builder.() -> Unit) = Request.Builder().apply(block).build()

inline fun <reified T> Response.parseAs(json: Json): T {
    val responseBody = body?.string().orEmpty()
    return json.decodeFromString(responseBody)
}
