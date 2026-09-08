package com.kjipo.ui

import org.jetbrains.letsPlot.geom.geomHistogram
import org.jetbrains.letsPlot.intern.toSpec
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleXDateTime
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import java.sql.Date
import java.time.LocalDate

class HistogramDetailedTest {

    @Test
    fun testHistogramBinsWithMultipleDates() {
        val dates = listOf(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 2),
            LocalDate.of(2020, 1, 3),
            LocalDate.of(2020, 1, 10),
            LocalDate.of(2020, 1, 20)
        )
        val millis = dates.map { Date.valueOf(it).time }
        val bins = 4
        val data = mapOf("Date" to millis)
        val plot = letsPlot(data) +
            geomHistogram(bins = bins) { x = "Date" } +
            ggtitle("Hits") +
            scaleXDateTime(name = "Date", format = "%b-%Y") +
            labs(y = "Count")

        val spec = plot.toSpec()
        assertEquals("plot", spec["kind"])
        val dataMap = spec["data"] as Map<*, *>
        val dateList = dataMap["Date"] as List<*>
        assertEquals(5, dateList.size)
    }

    @Test
    fun testHistogramWithExplicitDateRange() {
        val dates = listOf(
            LocalDate.of(2020, 1, 5),
            LocalDate.of(2020, 1, 15)
        )
        val minDate = LocalDate.of(2020, 1, 1)
        val maxDate = LocalDate.of(2020, 1, 31)
        val minMillis = Date.valueOf(minDate).time
        val maxMillis = Date.valueOf(maxDate).time

        val millis = dates.map { Date.valueOf(it).time }
        val data = mapOf("Date" to millis)
        val plot = letsPlot(data) +
            geomHistogram(bins = 10) { x = "Date" } +
            ggtitle("Hits") +
            scaleXDateTime(name = "Date", format = "%b-%Y", limits = Pair(minMillis, maxMillis)) +
            labs(y = "Count")

        val spec = plot.toSpec()
        assertEquals("plot", spec["kind"])
        val dataMap = spec["data"] as Map<*, *>
        val dateList = dataMap["Date"] as List<*>
        assertEquals(2, dateList.size)
    }

    @Test
    fun testHistogramWithSingleDate() {
        val date = LocalDate.of(2020, 1, 1)
        val millis = listOf(Date.valueOf(date).time)
        val data = mapOf("Date" to millis)
        val plot = letsPlot(data) +
            geomHistogram(bins = 5) { x = "Date" } +
            ggtitle("Hits") +
            scaleXDateTime(name = "Date", format = "%b-%Y") +
            labs(y = "Count")

        val spec = plot.toSpec()
        assertEquals("plot", spec["kind"])
        val dataMap = spec["data"] as Map<*, *>
        val dateList = dataMap["Date"] as List<*>
        assertEquals(1, dateList.size)
    }
}
