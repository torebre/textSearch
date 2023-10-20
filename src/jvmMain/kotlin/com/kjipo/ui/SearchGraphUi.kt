package com.kjipo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.axis.DateAxis
import org.jfree.chart.plot.XYPlot
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer
import org.jfree.chart.ui.RectangleInsets
import org.jfree.data.time.TimeSeries
import org.jfree.data.time.TimeSeriesCollection
import java.text.SimpleDateFormat

@Composable
fun SearchGraph(timeSeries: TimeSeries) {
    SwingPanel(
        background = Color.White,
        modifier = Modifier.fillMaxSize(),
        factory = {
            val dataset = TimeSeriesCollection()
            dataset.addSeries(timeSeries)

            val chart = ChartFactory.createTimeSeriesChart(
                "Hits",  // title
                "Date",  // x-axis label
                "Count",  // y-axis label
                dataset
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

            val renderer = plot.renderer
            if (renderer is XYLineAndShapeRenderer) {
                with(renderer) {
                    setDefaultShapesVisible(true)
                    setDefaultShapesFilled(true)
                    drawSeriesLineAsPath = true
                }
            }

            val axis = plot.domainAxis as DateAxis
            axis.setDateFormatOverride(SimpleDateFormat("MMM-yyyy"))
            ChartPanel(chart, false).also {
                it.fillZoomRectangle = true
                it.isMouseWheelEnabled = true
            }
        }
    )
}

