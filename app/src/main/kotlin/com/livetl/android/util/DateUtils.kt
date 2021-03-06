package com.livetl.android.util

import android.text.format.DateUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
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

fun Long.toTimestampString(): String {
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    val time = Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    return formatter.format(time)
}
