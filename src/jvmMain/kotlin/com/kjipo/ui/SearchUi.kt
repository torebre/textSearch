package com.kjipo.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Modifier

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun SearchUi(searchModel: SearchModel) {
    val uiState = searchModel.uiState

    Column {
        TextField(uiState.searchText, onValueChange = {
            searchModel.onSearchTextChanged(it)
        })

        Button(
            onClick = {
                searchModel.search()

            },
            enabled = uiState.searchText.isNotEmpty()
        ) {
            Text(text = "Search")
        }


        Text("Current document")

        Text(uiState.currentDocument)

        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            items(uiState.searchResults) { searchResult ->
                // TODO Create element for showing search result

                Box(modifier= Modifier.background(MaterialTheme.colors.background)
                    .combinedClickable(onClick = {
                    searchModel.setCurrentDocument(searchResult.documentId)
                })) {
                    Text(searchResult.fragments.joinToString { "," })
                }

            }


        }
    }

}
