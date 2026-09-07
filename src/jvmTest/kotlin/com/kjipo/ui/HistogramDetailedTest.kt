package com.kjipo.ui

import org.jfree.chart.axis.DateAxis
import org.jfree.data.statistics.HistogramDataset
import org.jfree.data.statistics.HistogramType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.sql.Date
import java.time.LocalDate

class HistogramDetailedTest {

    @Test
    fun testHistogramBinsWithMultipleDates() {
        val dataset = HistogramDataset()
        dataset.type = HistogramType.FREQUENCY

        val dates = listOf(
            LocalDate.of(2020, 1, 1),
            LocalDate.of(2020, 1, 2),
            LocalDate.of(2020, 1, 3),
            LocalDate.of(2020, 1, 10),
            LocalDate.of(2020, 1, 20)
        )
        val millis = dates.map { Date.valueOf(it).time.toDouble() }.toDoubleArray()
        val bins = 4
        dataset.addSeries("Hits", millis, bins)

        assertEquals(1, dataset.seriesCount)
        assertEquals(bins, dataset.getItemCount(0))

        var totalHits = 0.0
        for (i in 0 until bins) {
            val count = dataset.getY(0, i).toDouble()
            totalHits += count
            val start = dataset.getStartX(0, i).toLong()
            val end = dataset.getEndX(0, i).toLong()
            assertTrue(start <= end)
        }
        assertEquals(dates.size.toDouble(), totalHits)
    }

    @Test
    fun testHistogramWithExplicitDateRange() {
        val dataset = HistogramDataset()
        dataset.type = HistogramType.FREQUENCY

        val dates = listOf(
            LocalDate.of(2020, 1, 5),
            LocalDate.of(2020, 1, 15)
        )
        val millis = dates.map { Date.valueOf(it).time.toDouble() }.toDoubleArray()

        val minDate = LocalDate.of(2020, 1, 1)
        val maxDate = LocalDate.of(2020, 1, 31)
        val minMillis = Date.valueOf(minDate).time.toDouble()
        val maxMillis = Date.valueOf(maxDate).time.toDouble()

        dataset.addSeries("Hits", millis, 10, minMillis, maxMillis)

        assertEquals(10, dataset.getItemCount(0))
        assertEquals(minMillis, dataset.getStartX(0, 0).toDouble())
        assertEquals(maxMillis, dataset.getEndX(0, 9).toDouble())
    }

    @Test
    fun testHistogramWithSingleDate() {
        val dataset = HistogramDataset()
        val date = LocalDate.of(2020, 1, 1)
        val millis = doubleArrayOf(Date.valueOf(date).time.toDouble())

        dataset.addSeries("Hits", millis, 5)
        assertEquals(5, dataset.getItemCount(0))
        var totalHits = 0.0
        for (i in 0 until 5) {
            totalHits += dataset.getY(0, i).toDouble()
        }
        assertEquals(1.0, totalHits)
    }
}
