package com.livetl.android.data.chat

import android.util.Log
import android.webkit.JavascriptInterface
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatJSInterface(private val json: Json) {

    @Suppress("UNUSED")
    @JavascriptInterface
    fun receiveMessages(data: String) {
        val messages = json.decodeFromString<YTChatMessages>(data)
        Log.d("ChatJSInterface", messages.toString())
    }
}