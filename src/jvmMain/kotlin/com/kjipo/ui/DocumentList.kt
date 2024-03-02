package com.kjipo.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter

@Composable
internal fun DocumentList(documentListModel: DocumentListModel) {
    DocumentList(documentListModel.documentListState, documentListModel)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun DocumentList(documentListState: DocumentListState, documentListModel: DocumentListModel) {
    val listState = rememberLazyListState()
    val scrollStateVertical = rememberScrollState(0)

    Row {
        Box {
            LazyColumn(
                modifier = Modifier.width(360.dp),
                state = listState
            ) {
                items(documentListState.documents) { document ->
                    Box(modifier = Modifier.combinedClickable(onClick = {
                        documentListModel.setCurrentDocument(document.second)
                    })) {
                        Text(
                            text = document.first.format(DateTimeFormatter.ISO_DATE)
                        )
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .width(20.dp)
                    .border(2.dp, color = MaterialTheme.colors.secondary),
                adapter = rememberScrollbarAdapter(listState)
            )
        }

        Box {
            Column {
                Box(
                    modifier = Modifier.padding(5.dp)
                        .verticalScroll(scrollStateVertical)
                ) {
                    Text(
                        text = documentListState.currentDocument
                    )
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd)
                    .fillMaxHeight()
                    .border(2.dp, color = MaterialTheme.colors.onPrimary),
                adapter = rememberScrollbarAdapter(scrollStateVertical)
            )
        }
    }
}
