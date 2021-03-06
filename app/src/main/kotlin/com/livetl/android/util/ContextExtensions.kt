package com.livetl.android.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.widget.Toast
import com.livetl.android.R
import timber.log.Timber
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

fun Context.toast(text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}

fun Context.copyToClipboard(text: String) {
    try {
        val clipboard: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(/* label = */ text, text)
        clipboard.setPrimaryClip(clip)
        toast(getString(R.string.copied, text))
    } catch (e: Throwable) {
        toast(getString(R.string.copied_error))
        Timber.e(e)
    }
}

fun Context.share(text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
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
