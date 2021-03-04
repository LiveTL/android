package com.livetl.android.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.PowerManager
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun Context.copyToClipboard(text: String) {
    val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(/* label */ text, text)
    clipboard.setPrimaryClip(clip)
}

val Context.powerManager: PowerManager
    get() = getSystemService(Context.POWER_SERVICE) as PowerManager

@Throws(IOException::class)
fun Context.readFile(filePath: String): String {
    return BufferedReader(InputStreamReader(assets.open(filePath))).use { reader ->
        val total = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            total.appendLine(line)
        }
        total.toString()
    }
}
