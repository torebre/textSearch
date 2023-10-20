package com.kjipo.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kjipo.search.TextSearcher
import org.jfree.data.time.Day
import org.jfree.data.time.TimeSeries
import java.sql.Date
import java.time.LocalDate

class SearchModel(private val textSearcher: TextSearcher) {

    var uiState: SearchUiState by mutableStateOf(SearchUiState())
        private set

    var timeSeriesState: MutableState<TimeSeries?> = mutableStateOf(null)
        private set

    private var currentSearchResult: com.kjipo.search.SearchResult? = null


    fun onSearchTextChanged(searchText: String) {
        setState { copy(searchText = searchText) }
    }

    fun setCurrentDocument(documentId: Int) {
        textSearcher.getDocument(documentId)?.let { document ->
            document.get("contents").let {documentContents ->
                setState { copy(currentDocumentId = documentId,
                    currentDocument = documentContents) }
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
                }
            }
        }
    }


    fun getTimeSeries(): TimeSeries {
       return TimeSeries("Hits").also { timeSeries ->
           currentSearchResult?.let { searchResult ->
               textSearcher.getDatesForHits(searchResult.hits)
                   .forEach {
                       timeSeries.add(Day(Date.valueOf(it)), 1)
                   }
           }
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
