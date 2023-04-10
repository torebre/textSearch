package com.kjipo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kjipo.search.TextSearcher

class SearchModel(private val textSearcher: TextSearcher) {

    var uiState: SearchUiState by mutableStateOf(SearchUiState())
        private set


    fun onSearchTextChanged(searchText: String) {
        setState { copy(searchText = searchText) }
    }

    fun setCurrentDocument(documentId: Int) {
        textSearcher.getDocument(documentId)?.let { document ->
            document.get("contents").let {documentContents ->
                setState { copy(currentDocumentId = documentId, currentDocument = documentContents) }
            }
        }
    }

    private inline fun setState(updateState: SearchUiState.() -> SearchUiState) {
        uiState = uiState.updateState()
    }

    fun search() {
        uiState.searchText.let {
            if (it.isNotEmpty()) {
                setState {
                    copy(
                        searchResults = textSearcher.search(it).map { textSearchResult ->
                            SearchResult(
                                textSearchResult.documentId,
                                textSearchResult.fragments
                            )
                        })
                }
            }
        }
    }


}


data class SearchResult(val documentId: Int, val fragments: List<String>)

data class SearchUiState(
    val searchText: String = "",
    val searchResults: List<SearchResult> = emptyList(),
    val currentDocumentId: Int? = null,
    val currentDocument: String = ""
)