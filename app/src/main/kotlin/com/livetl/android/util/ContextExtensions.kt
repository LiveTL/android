package com.livetl.android.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.MultiWindowModeChangedInfo
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.util.Consumer
import com.livetl.android.R
import logcat.LogPriority
import logcat.asLog
import logcat.logcat
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
        val clip = ClipData.newPlainText(text, text)
        clipboard.setPrimaryClip(clip)

        // Android 13 and higher shows a visual confirmation of copied contents
        // https://developer.android.com/about/versions/13/features/copy-paste
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            toast(getString(R.string.copied, text))
        }
    } catch (e: Throwable) {
        toast(getString(R.string.copied_error))
        logcat(LogPriority.ERROR) { e.asLog() }
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

@Throws(IOException::class)
fun Context.readAssetFile(filePath: String): String =
    BufferedReader(InputStreamReader(assets.open(filePath))).use { reader ->
        val total = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            total.appendLine(line)
        }
        total.toString()
    }

fun Context.findActivity(): ComponentActivity {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    throw IllegalStateException("No Activity context found")
}

@Composable
fun rememberIsInPipMode(): Boolean {
    val activity = LocalContext.current.findActivity()
    var pipMode by remember { mutableStateOf(activity.isInPictureInPictureMode) }
    DisposableEffect(activity) {
        val observer = Consumer<PictureInPictureModeChangedInfo> { info ->
            pipMode = info.isInPictureInPictureMode
        }
        activity.addOnPictureInPictureModeChangedListener(
            observer,
        )
        onDispose { activity.removeOnPictureInPictureModeChangedListener(observer) }
    }
    return pipMode
}

@Composable
fun rememberIsInSplitScreenMode(): Boolean {
    val activity = LocalContext.current.findActivity()
    var splitScreenMode by remember { mutableStateOf(activity.isInMultiWindowMode) }
    DisposableEffect(activity) {
        val observer = Consumer<MultiWindowModeChangedInfo> { info ->
            splitScreenMode = info.isInMultiWindowMode
        }
        activity.addOnMultiWindowModeChangedListener(
            observer,
        )
        onDispose { activity.removeOnMultiWindowModeChangedListener(observer) }
    }
    return splitScreenMode
}
