package com.livetl.android.data.chat

import android.util.Log
import android.webkit.JavascriptInterface

class ChatJSInterface {

    @JavascriptInterface
    fun receiveMessages(data: String) {
        Log.d("ChatJSInterface", data)
    }
}