package com.kjipo.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kjipo.search.TextSearcher
import java.time.LocalDate

@Composable
fun SearchUi(searchModel: SearchModel) {
    val uiState = searchModel.uiState

    Column {

        Row() {
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
        }

        val listState = rememberLazyListState()

        Row() {
            LazyColumn(modifier = Modifier.width(360.dp), state = listState) {
                items(uiState.searchResults) { searchResult ->
                    // TODO Create element for showing search result

                    SearchHit(searchResult, searchModel::setCurrentDocument)

                }

            }

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(uiState.currentDocument)
            }

        }

    }

}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchHit(searchResult: SearchResult, setCurrentDocument: (Int) -> Unit) {
    val roundedShape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier.background(MaterialTheme.colors.secondary)
            .clip(roundedShape)
            .combinedClickable(onClick = {
                setCurrentDocument(searchResult.documentId)
            })
    ) {
        val regex = "<B>|</B>".toRegex()
        val splitSummary = regex.split(searchResult.summary())

        Column {
            Text(fontWeight = FontWeight.Bold, text = searchResult.documentDate.format(TextSearcher.dateFormatter))

            Text(
                buildAnnotatedString {
                    splitSummary.forEachIndexed { index, summaryPart ->
                        if (index % 2 != 0) {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(summaryPart)
                            }
                        } else {
                            append(summaryPart)
                        }
                    }
                }
            )
        }
    }
}


@Preview
@Composable
fun SearchHitPreview() {
    SearchHit(SearchResult(1, LocalDate.now(), listOf("Test1", "Test2")),
        {
            // Do nothing
        })

}