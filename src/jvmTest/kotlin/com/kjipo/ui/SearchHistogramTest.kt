package com.kjipo.ui

import com.kjipo.Config
import com.kjipo.search.TextSearcher
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.store.FSDirectory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.FileOutputStream
import java.nio.file.Path
import java.sql.Date
import java.time.LocalDate
import java.util.Properties

class SearchHistogramTest {

    @Test
    fun testHistogramDatasetFromSearchModel(@TempDir tempDir: Path) {
        val indexPath = tempDir.resolve("index")
        val configFile = tempDir.resolve("search_config.properties")

        val properties = Properties()
        properties.setProperty("indexDirectory", indexPath.toAbsolutePath().toString())
        FileOutputStream(configFile.toFile()).use { properties.store(it, null) }

        val directory = FSDirectory.open(indexPath)
        val indexWriterConfig = IndexWriterConfig(StandardAnalyzer())
        IndexWriter(directory, indexWriterConfig).use { writer ->
            val dates = listOf("010120", "010520", "011020", "020120", "030120")
            for (dateStr in dates) {
                val doc = Document().apply {
                    add(StringField("doc_name", dateStr, Field.Store.YES))
                    add(TextField("contents", "target keyword in document", Field.Store.YES))
                }
                writer.addDocument(doc)
            }
        }

        val appConfig = Config.getConfig(configFile.toAbsolutePath().toString())
        val textSearcher = TextSearcher(appConfig)
        val searchModel = SearchModel(textSearcher)

        searchModel.onSearchTextChanged("target")
        searchModel.search()

        val dataset = searchModel.getHistogramDataset(bins = 5)
        assertEquals(1, dataset.seriesCount)
        assertEquals(5, dataset.getItemCount(0))

        var totalHits = 0.0
        for (i in 0 until dataset.getItemCount(0)) {
            totalHits += dataset.getY(0, i).toDouble()
        }
        assertEquals(5.0, totalHits)

        val startDate = LocalDate.of(2020, 1, 1)
        val endDate = LocalDate.of(2020, 4, 1)
        val boundedDataset = searchModel.getHistogramDataset(bins = 3, startDate = startDate, endDate = endDate)
        assertEquals(1, boundedDataset.seriesCount)
        assertEquals(3, boundedDataset.getItemCount(0))
        assertEquals(Date.valueOf(startDate).time.toDouble(), boundedDataset.getStartX(0, 0).toDouble())
        assertEquals(Date.valueOf(endDate).time.toDouble(), boundedDataset.getEndX(0, 2).toDouble())
    }

    @Test
    fun testEmptySearchModelHistogram(@TempDir tempDir: Path) {
        val indexPath = tempDir.resolve("index")
        val configFile = tempDir.resolve("search_config.properties")

        val properties = Properties()
        properties.setProperty("indexDirectory", indexPath.toAbsolutePath().toString())
        FileOutputStream(configFile.toFile()).use { properties.store(it, null) }

        val directory = FSDirectory.open(indexPath)
        val indexWriterConfig = IndexWriterConfig(StandardAnalyzer())
        IndexWriter(directory, indexWriterConfig).close()

        val appConfig = Config.getConfig(configFile.toAbsolutePath().toString())
        val textSearcher = TextSearcher(appConfig)
        val searchModel = SearchModel(textSearcher)

        val dataset = searchModel.getHistogramDataset()
        assertEquals(0, dataset.seriesCount)
    }
}
