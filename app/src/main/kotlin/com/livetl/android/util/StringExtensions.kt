package com.livetl.android.util

import android.text.Html
import java.util.Locale

fun String.escapeHtmlEntities(): String = Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY).toString()

fun String.capitalize(): String = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

fun String.trimToSingleLine(): String = trimIndent().replace("\n", "")
