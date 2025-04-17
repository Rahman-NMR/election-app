package com.rahman.pemiluapp.utils

import com.rahman.pemiluapp.utils.ImageOperation.FILENAME_FORMAT
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateFormatter {
    val timeStamp: String = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(Date())

    fun formatDate(timestampDate: Long, targetTimeZone: String): String {
        val currentDate = Date(timestampDate)
        val sdf = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(targetTimeZone)
        return sdf.format(currentDate)
    }

    fun tryFormatTimestamp(timestampString: String?) = try {
        when {
            timestampString.isNullOrEmpty() -> ""
            else -> {
                formatDate(timestampString.toLong(), "Asia/Jakarta")
            }
        }
    } catch (_: Exception) {
        ""
    }
}