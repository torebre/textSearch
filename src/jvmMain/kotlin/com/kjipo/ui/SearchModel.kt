package com.kjipo.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kjipo.search.TextSearcher
import org.jetbrains.letsPlot.Figure
import org.jetbrains.letsPlot.geom.geomHistogram
import org.jetbrains.letsPlot.geom.geomLine
import org.jetbrains.letsPlot.geom.geomPoint
import org.jetbrains.letsPlot.label.ggtitle
import org.jetbrains.letsPlot.label.labs
import org.jetbrains.letsPlot.letsPlot
import org.jetbrains.letsPlot.scale.scaleXDateTime
import java.sql.Date
import java.time.LocalDate

class SearchModel(private val textSearcher: TextSearcher) {

    var uiState: SearchUiState by mutableStateOf(SearchUiState())
        private set

    var timeSeriesState: MutableState<Figure?> = mutableStateOf(null)
        private set

    var histogramDatasetState: MutableState<Figure?> = mutableStateOf(null)
        private set

    private var currentSearchResult: com.kjipo.search.SearchResult? = null


    fun onSearchTextChanged(searchText: String) {
        setState { copy(searchText = searchText) }
    }

    fun setCurrentDocument(documentId: Int) {
        textSearcher.getDocument(documentId)?.let { document ->
            document.get("contents").let { documentContents ->
                setState {
                    copy(
                        currentDocumentId = documentId,
                        currentDocument = documentContents
                    )
                }
            }
        }
    }

    private inline fun setState(updateState: SearchUiState.() -> SearchUiState) {
        uiState = uiState.updateState()
    }

    fun search() {
        uiState.searchText.let { searchString ->
            if (searchString.isNotEmpty()) {
                currentSearchResult = textSearcher.getQueryAndHits(searchString)

                currentSearchResult?.let {
                    val textSearchResults = textSearcher.getTextResultsForQuery(it)

                    setState {
                        copy(searchResults = textSearchResults.map { textSearchResult ->
                            SearchResult(
                                textSearchResult.documentId,
                                textSearchResult.documentDate,
                                textSearchResult.fragments
                            )
                        })
                    }
                    timeSeriesState.value = getTimeSeries()
                    histogramDatasetState.value = getHistogramDataset()
                }
            }
        }
    }


    fun getTimeSeries(
        title: String = "Hits",
        xAxisLabel: String = "Date",
        yAxisLabel: String = "Count",
        dateFormat: String = "MMM-yyyy"
    ): Figure {
        val dates = currentSearchResult?.let { searchResult ->
            textSearcher.getDatesForHits(searchResult.hits)
        } ?: emptyList()

        val dateCounts = dates.groupingBy { it }.eachCount().toSortedMap()
        val data = mapOf(
            xAxisLabel to dateCounts.keys.map { Date.valueOf(it).time },
            yAxisLabel to dateCounts.values.toList()
        )

        val formattedDatePattern = convertDateFormat(dateFormat)

        return letsPlot(data) +
            geomLine { x = xAxisLabel; y = yAxisLabel } +
            geomPoint { x = xAxisLabel; y = yAxisLabel } +
            ggtitle(title) +
            scaleXDateTime(name = xAxisLabel, format = formattedDatePattern) +
            labs(y = yAxisLabel)
    }

    fun getHistogramDataset(
        bins: Int = 10,
        startDate: LocalDate? = null,
        endDate: LocalDate? = null,
        title: String = "Hits",
        xAxisLabel: String = "Date",
        yAxisLabel: String = "Count",
        dateFormat: String = "MMM-yyyy"
    ): Figure {
        var dates = currentSearchResult?.let { searchResult ->
            textSearcher.getDatesForHits(searchResult.hits)
        } ?: emptyList()

        if (startDate != null) {
            dates = dates.filter { !it.isBefore(startDate) }
        }
        if (endDate != null) {
            dates = dates.filter { !it.isAfter(endDate) }
        }

        val millis = dates.map { Date.valueOf(it).time }
        val data = mapOf(
            xAxisLabel to millis
        )

        val minMillis = startDate?.let { Date.valueOf(it).time }
        val maxMillis = endDate?.let { Date.valueOf(it).time }
        val limits = if (minMillis != null && maxMillis != null && minMillis < maxMillis) {
            Pair(minMillis, maxMillis)
        } else {
            null
        }

        val formattedDatePattern = convertDateFormat(dateFormat)

        return letsPlot(data) +
            geomHistogram(bins = bins) { x = xAxisLabel } +
            ggtitle(title) +
            scaleXDateTime(name = xAxisLabel, format = formattedDatePattern, limits = limits) +
            labs(y = yAxisLabel)
    }

    companion object {
        fun convertDateFormat(dateFormat: String): String {
            if (dateFormat.contains("%")) return dateFormat
            return dateFormat
                .replace("yyyy", "%Y")
                .replace("yy", "%y")
                .replace("MMMM", "%B")
                .replace("MMM", "%b")
                .replace("MM", "%m")
                .replace("dd", "%d")
                .replace("d", "%e")
        }
    }
}


data class SearchResult(val documentId: Int, val documentDate: LocalDate, val fragments: List<String>) {
    fun summary(): String {
        return fragments.joinToString(separator = "...")
    }

}

data class SearchUiState(
    val searchText: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val currentDocumentId: Int? = null,
    val currentDocumentDate: LocalDate? = null,
    val currentDocument: String = ""
)
