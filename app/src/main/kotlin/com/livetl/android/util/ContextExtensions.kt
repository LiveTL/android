package com.livetl.android.util

import android.app.UiModeManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.PowerManager
import android.widget.Toast
import androidx.core.content.getSystemService
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

        // Android 13 and higher shows a visual confirmation of copied contents
        // https://developer.android.com/about/versions/13/features/copy-paste
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            toast(getString(R.string.copied, text))
        }
    } catch (e: Throwable) {
        toast(getString(R.string.copied_error))
        Timber.e(e)
    }
}

fun Context.share(text: String) {
    val intent =
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, text)
        }
    startActivity(Intent.createChooser(intent, getString(R.string.action_share)))
}

val Context.powerManager: PowerManager
    get() = getSystemService(Context.POWER_SERVICE) as PowerManager

@Throws(IOException::class)
fun Context.readFile(filePath: String): String =
    BufferedReader(InputStreamReader(assets.open(filePath))).use { reader ->
        val total = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            total.appendLine(line)
        }
        total.toString()
    }

fun Context.isTvMode(): Boolean =
    getSystemService<UiModeManager>()?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
