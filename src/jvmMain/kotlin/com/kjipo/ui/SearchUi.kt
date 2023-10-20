package com.kjipo.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.kjipo.search.TextSearcher
import java.time.LocalDate

@Composable
fun SearchUi(searchModel: SearchModel, documentListModel: DocumentListModel) {
    val uiState = searchModel.uiState

    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
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

        var tabIndex by remember { mutableStateOf(0) }

        TabRow(
            selectedTabIndex = tabIndex
        ) {
            Tab(text = { Text("Hits") },
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 })
            Tab(text = { Text("Documents") },
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 })
            Tab(text = { Text("Graph") },
                selected = tabIndex == 2,
                onClick = { tabIndex = 2 })
        }

        when (tabIndex) {
            0 -> SearchList(uiState, searchModel)
            1 -> DocumentList(documentListModel)
            2 -> SearchGraph(searchModel.getTimeSeries())
        }
    }

}

@Composable
private fun SearchList(uiState: SearchUiState, searchModel: SearchModel) {
    val listState = rememberLazyListState()
    val scrollStateVertical = rememberScrollState(0)

    Row {
        Box {
            LazyColumn(
                modifier = Modifier.width(360.dp),
                state = listState
            ) {
                items(uiState.searchResults) { searchResult ->
                    SearchHit(searchResult, searchModel::setCurrentDocument)
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .border(5.dp, color = Color.Green),
                adapter = rememberScrollbarAdapter(listState)
            )
        }

        Box {
            Column {
                Box(modifier = Modifier.padding(5.dp)
                    .verticalScroll(scrollStateVertical)) {
                    Text(
                        text = uiState.currentDocument
                    )
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight().border(5.dp, color = Color.Green),
                adapter = rememberScrollbarAdapter(scrollStateVertical)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun SearchHit(searchResult: SearchResult, setCurrentDocument: (Int) -> Unit) {
    val roundedShape = RoundedCornerShape(12.dp)
    Box(
        modifier = Modifier.padding(2.dp)
            .width(200.dp)
            .clip(roundedShape)
            .background(MaterialTheme.colors.secondary)
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