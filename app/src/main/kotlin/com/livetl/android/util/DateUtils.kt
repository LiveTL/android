package com.livetl.android.util

import android.text.format.DateUtils
import timber.log.Timber
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
 * Calculates number of microseconds until [time] from now.
 */
fun getMicroDifferenceFromNow(time: Long): Long {
    val nowMicro = System.nanoTime() * 1000
    Timber.d("Now: $nowMicro, time: $time, diff: ${time - nowMicro}")
    return time - nowMicro
}
