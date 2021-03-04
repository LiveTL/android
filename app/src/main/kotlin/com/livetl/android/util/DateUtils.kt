package com.livetl.android.util

import android.text.format.DateUtils
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.Date

fun String.toDate(): Date {
    val temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(this)
    return Date.from(Instant.from(temporalAccessor))
}

fun Date.toRelativeString(): String {
    return DateUtils.getRelativeTimeSpanString(
        time,
        Instant.now().toEpochMilli(),
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}
