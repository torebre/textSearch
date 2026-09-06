package com.kjipo.search

import com.kjipo.Config
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.FileOutputStream
import java.nio.file.Path
import java.time.LocalDate
import java.util.Properties

class RunTextSearch {

    @Test
    fun testSearch(@TempDir tempDir: Path) {
        val indexPath = tempDir.resolve("index")
        val configFile = tempDir.resolve("search_config.properties")

        val properties = Properties()
        properties.setProperty("indexDirectory", indexPath.toAbsolutePath().toString())
        FileOutputStream(configFile.toFile()).use { properties.store(it, null) }

        val directory = FSDirectory.open(indexPath)
        val indexWriterConfig = IndexWriterConfig(StandardAnalyzer())
        IndexWriter(directory, indexWriterConfig).use { writer ->
            val doc = Document().apply {
                add(StringField("doc_name", "010220", Field.Store.YES))
                add(TextField("contents", "test content to search", Field.Store.YES))
            }
            writer.addDocument(doc)
        }

        val appConfig = Config.getConfig(configFile.toAbsolutePath().toString())
        val textSearcher = TextSearcher(appConfig)
        val documents = textSearcher.getDocumentsByDate()

        assertEquals(1, documents.size)
        assertEquals(LocalDate.of(2020, 1, 2), documents[0].first)
    }
}