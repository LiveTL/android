package com.livetl.android.util

import android.os.Build
import android.text.Html
import java.util.Locale

fun String.escapeHtmlEntities(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()
} else {
    @Suppress("DEPRECATION")
    Html.fromHtml(this).toString()
}

fun String.capitalize(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

fun String.trimToSingleLine(): String = trimIndent().replace("\n", "")
