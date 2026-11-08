package com.still.clock.ui

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

data class ClockDisplay(
    val time: String,
    val period: String,
    val date: String,
    val spokenTime: String,
)

object ClockFormatter {
    fun format(
        now: ZonedDateTime,
        use24Hour: Boolean,
        locale: Locale = Locale.getDefault(),
    ): ClockDisplay {
        val timePattern = if (use24Hour) "HH:mm" else "hh:mm"
        val period = if (use24Hour) {
            ""
        } else {
            DateTimeFormatter.ofPattern("a", locale).format(now)
        }
        val isChinese = locale.language == Locale.CHINESE.language
        val datePattern = if (isChinese) "M月d日 EEEE" else "MMMM d · EEEE"
        val date = DateTimeFormatter.ofPattern(datePattern, locale)
            .format(now)
            .let { if (isChinese) it else it.uppercase(locale) }
        val spokenTime = if (isChinese) {
            val minute = if (now.minute == 0) "整" else "${now.minute}分"
            if (use24Hour) {
                "${now.hour}点$minute"
            } else {
                val hour = now.hour % 12
                "$period${if (hour == 0) 12 else hour}点$minute"
            }
        } else {
            DateTimeFormatter.ofPattern(if (use24Hour) "HH:mm" else "h:mm a", locale)
                .format(now)
        }

        return ClockDisplay(
            time = DateTimeFormatter.ofPattern(timePattern, locale).format(now),
            period = period,
            date = date,
            spokenTime = spokenTime,
        )
    }
}
