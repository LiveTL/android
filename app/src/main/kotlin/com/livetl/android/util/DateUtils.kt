package com.livetl.android.util

import android.text.format.DateUtils
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit
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
        DateUtils.MINUTE_IN_MILLIS
    ).toString()
}

/**
 * Converts a microsecond value to a localized string like "12:34 pm".
 */
fun Long.toTimestampString(): String {
    val formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
    val time = Instant.EPOCH.plus(this, ChronoUnit.MICROS)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    return formatter.format(time)
}

/**
 * Converts a microsecond value to a string like "12:34:01.123456".
 */
fun Long.toDebugTimestampString(): String {
    val formatter = DateTimeFormatter.ISO_LOCAL_TIME
    val time = Instant.EPOCH.plus(this, ChronoUnit.MICROS)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
    return formatter.format(time)
}

/**
 * Get number of microseconds from epoch time.
 */
fun epochMicro(): Long {
    return Instant.now().toEpochMilli() * 1000
}
