package com.livetl.android.util

import android.text.format.DateUtils
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * Converts an ISO8601 string to a Date object.
 */
fun String.toDate(): Date {
    val temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(this)
    return Date.from(Instant.from(temporalAccessor))
}

/**
 * Converts a Date to a localized relative string like "In 4 minutes" or "1 hour ago".
 */
fun Date.toRelativeString(): String {
    return DateUtils.getRelativeTimeSpanString(
        time,
        Instant.now().toEpochMilli(),
        DateUtils.MINUTE_IN_MILLIS,
    ).toString()
}
