package com.still.clock

import com.still.clock.ui.ClockFormatter
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Locale

class ClockFormatterTest {
    private val zone: ZoneId = ZoneId.of("UTC")

    @Test
    fun `midnight is formatted correctly in both hour systems`() {
        val midnight = ZonedDateTime.of(2026, 11, 7, 0, 5, 0, 0, zone)

        val twentyFourHour = ClockFormatter.format(midnight, true, Locale.US)
        val twelveHour = ClockFormatter.format(midnight, false, Locale.US)

        assertEquals("00:05", twentyFourHour.time)
        assertEquals("", twentyFourHour.period)
        assertEquals("12:05", twelveHour.time)
        assertEquals("AM", twelveHour.period)
    }

    @Test
    fun `noon stays at twelve in twelve hour mode`() {
        val noon = ZonedDateTime.of(2026, 11, 7, 12, 30, 0, 0, zone)

        val display = ClockFormatter.format(noon, false, Locale.US)

        assertEquals("12:30", display.time)
        assertEquals("PM", display.period)
    }

    @Test
    fun `afternoon uses twelve hour clock and pm`() {
        val afternoon = ZonedDateTime.of(2026, 11, 7, 15, 7, 0, 0, zone)

        val display = ClockFormatter.format(afternoon, false, Locale.US)

        assertEquals("03:07", display.time)
        assertEquals("PM", display.period)
    }

    @Test
    fun `date is localized in Chinese and English`() {
        val date = ZonedDateTime.of(2026, 11, 7, 9, 0, 0, 0, zone)

        val chinese = ClockFormatter.format(date, true, Locale.SIMPLIFIED_CHINESE)
        val english = ClockFormatter.format(date, true, Locale.US)

        assertEquals("11月7日 星期六", chinese.date)
        assertEquals("NOVEMBER 7 · SATURDAY", english.date)
    }

    @Test
    fun `spoken Chinese time handles exact hours naturally`() {
        val time = ZonedDateTime.of(2026, 11, 7, 8, 0, 0, 0, zone)

        val display = ClockFormatter.format(time, true, Locale.SIMPLIFIED_CHINESE)

        assertEquals("8点整", display.spokenTime)
    }
}
