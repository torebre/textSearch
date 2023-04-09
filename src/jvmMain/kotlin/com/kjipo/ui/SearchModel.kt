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

    private inline fun setState(updateState: SearchUiState.() -> SearchUiState) {
        uiState = uiState.updateState()
    }




}


data class SearchUiState(val searchText: String? = null)