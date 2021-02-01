package com.livetl.android.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.PowerManager

fun Context.copyToClipboard(text: String) {
    val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(/* label */ text, text)
    clipboard.setPrimaryClip(clip)
}

val Context.powerManager: PowerManager
    get() = getSystemService(Context.POWER_SERVICE) as PowerManager