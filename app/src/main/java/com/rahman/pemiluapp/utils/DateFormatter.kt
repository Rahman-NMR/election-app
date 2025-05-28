package com.rahman.pemiluapp.utils

import com.rahman.pemiluapp.utils.ImageOperation.FILENAME_FORMAT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    const val DATE_TIME_FORMAT: String = "Asia/Jakarta"
    val currentTime: Long get() = System.currentTimeMillis() // get() for refresh current time when call
    val timeStamp: String get() = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date(currentTime))

    fun formatDate(timestampDate: Long): String {
        val currentDate = Date(timestampDate)
        val sdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(DATE_TIME_FORMAT)
        return sdf.format(currentDate)
    }

    fun tryFormatTimestamp(timestampString: String?) = try {
        when {
            timestampString.isNullOrEmpty() -> ""
            else -> {
                formatDate(timestampString.toLong())
            }
        }
    } catch (_: Exception) {
        ""
    }
}