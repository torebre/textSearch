package com.kjipo.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.kjipo.search.TextSearcher
import org.slf4j.LoggerFactory
import java.time.LocalDate

class DocumentListModel(private val textSearcher: TextSearcher) {

    var documentListState: DocumentListState by mutableStateOf(DocumentListState(documents = textSearcher.getDocumentsByDate(), ""))
        private set

//    init {
//        logger.info("Number of documents: ${documentListState.documents.size}")
//    }

    fun setCurrentDocument(documentId: Int) {
        textSearcher.getDocument(documentId)?.let { document ->
            document.get("contents").let { documentContents ->
                setState { copy(currentDocument = documentContents) }
                logger.info("Current document: ${documentListState.currentDocument.length}")
            }
        }
    }

    private inline fun setState(updateState: DocumentListState.() -> DocumentListState) {
        documentListState = documentListState.updateState()
    }

    companion object {

        private val logger = LoggerFactory.getLogger(DocumentListModel::class.java)


    }

}


data class DocumentListState(val documents: List<Pair<LocalDate, Int>>, val currentDocument: String)
