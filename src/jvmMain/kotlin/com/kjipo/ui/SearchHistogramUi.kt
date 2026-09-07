package com.kjipo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.axis.DateAxis
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.ui.RectangleInsets
import org.jfree.data.statistics.HistogramDataset
import java.text.SimpleDateFormat

@Composable
fun SearchHistogram(
    histogramDataset: HistogramDataset,
    modifier: Modifier = Modifier.fillMaxSize(),
    title: String = "Hits",
    xAxisLabel: String = "Date",
    yAxisLabel: String = "Count",
    dateFormat: String = "MMM-yyyy"
) {
    SwingPanel(
        background = Color.White,
        modifier = modifier,
        factory = {
            val chart = ChartFactory.createHistogram(
                title,
                xAxisLabel,
                yAxisLabel,
                histogramDataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
            )

            chart.backgroundPaint = java.awt.Color.WHITE
            val plot = chart.plot as XYPlot
            with(plot) {
                backgroundPaint = java.awt.Color.LIGHT_GRAY
                setDomainGridlinePaint(java.awt.Color.WHITE)
                setRangeGridlinePaint(java.awt.Color.WHITE)
                setAxisOffset(RectangleInsets(5.0, 5.0, 5.0, 5.0))
                isDomainCrosshairVisible = true
                isRangeCrosshairVisible = true
            }

            val axis = DateAxis(xAxisLabel)
            axis.setDateFormatOverride(SimpleDateFormat(dateFormat))
            plot.domainAxis = axis

            ChartPanel(chart, false).also {
                it.fillZoomRectangle = true
                it.isMouseWheelEnabled = true
            }
        }
    )
}
