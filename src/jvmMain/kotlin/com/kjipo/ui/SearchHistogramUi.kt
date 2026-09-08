package com.kjipo.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.intern.toSpec

@Composable
fun SearchHistogram(
    figure: Figure,
    modifier: Modifier = Modifier.fillMaxSize()
) {
    var plotPanel by remember { mutableStateOf<DefaultPlotPanelBatik?>(null) }

    DisposableEffect(plotPanel) {
        onDispose {
            plotPanel?.dispose()
        }
    }

    key(figure) {
        SwingPanel(
            modifier = modifier,
            factory = {
                val processedSpec = MonolithicCommon.processRawSpecs(figure.toSpec(), frontendOnly = false)
                DefaultPlotPanelBatik(
                    processedSpec = processedSpec,
                    preserveAspectRatio = false,
                    preferredSizeFromPlot = false,
                    repaintDelay = 100,
                    computationMessagesHandler = {}
                ).also { plotPanel = it }
            }
        )
    }
}
