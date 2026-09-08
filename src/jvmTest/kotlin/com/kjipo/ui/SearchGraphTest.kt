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
import org.jetbrains.letsPlot.batik.plot.component.DefaultPlotPanelBatik
import org.jetbrains.letsPlot.core.util.MonolithicCommon
import org.jetbrains.letsPlot.intern.toSpec
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.Properties

class SearchGraphTest {

    @Test
    fun testTimeSeriesPlotFromSearchModel(@TempDir tempDir: Path) {
        val indexPath = tempDir.resolve("index")
        val configFile = tempDir.resolve("search_config.properties")

        val properties = Properties()
        properties.setProperty("indexDirectory", indexPath.toAbsolutePath().toString())
        FileOutputStream(configFile.toFile()).use { properties.store(it, null) }

        val directory = FSDirectory.open(indexPath)
        val indexWriterConfig = IndexWriterConfig(StandardAnalyzer())
        IndexWriter(directory, indexWriterConfig).use { writer ->
            val dates = listOf("010120", "010120", "010520", "020120")
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

        val plot = searchModel.getTimeSeries()
        val spec = plot.toSpec()
        assertEquals("plot", spec["kind"])
        val data = spec["data"] as Map<*, *>
        val dateValues = data["Date"] as List<*>
        val countValues = data["Count"] as List<*>
        assertEquals(3, dateValues.size)
        assertEquals(3, countValues.size)

        // Verify DefaultPlotPanelBatik can be created with the processed spec
        val processedSpec = MonolithicCommon.processRawSpecs(spec, frontendOnly = false)
        val panel = DefaultPlotPanelBatik(
            processedSpec = processedSpec,
            preserveAspectRatio = false,
            preferredSizeFromPlot = false,
            repaintDelay = 100,
            computationMessagesHandler = {}
        )
        assertNotNull(panel)
        panel.dispose()
    }

    @Test
    fun testHistogramPlotFromSearchModel(@TempDir tempDir: Path) {
        val indexPath = tempDir.resolve("index")
        val configFile = tempDir.resolve("search_config.properties")

        val properties = Properties()
        properties.setProperty("indexDirectory", indexPath.toAbsolutePath().toString())
        FileOutputStream(configFile.toFile()).use { properties.store(it, null) }

        val directory = FSDirectory.open(indexPath)
        val indexWriterConfig = IndexWriterConfig(StandardAnalyzer())
        IndexWriter(directory, indexWriterConfig).use { writer ->
            val dates = listOf("010120", "010120", "010520", "020120")
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

        val plot = searchModel.getHistogramDataset(bins = 5)
        val spec = plot.toSpec()
        assertEquals("plot", spec["kind"])

        val processedSpec = MonolithicCommon.processRawSpecs(spec, frontendOnly = false)
        val panel = DefaultPlotPanelBatik(
            processedSpec = processedSpec,
            preserveAspectRatio = false,
            preferredSizeFromPlot = false,
            repaintDelay = 100,
            computationMessagesHandler = {}
        )
        assertNotNull(panel)
        panel.dispose()
    }

    @Test
    fun testEmptySearchModelTimeSeries(@TempDir tempDir: Path) {
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

        val plot = searchModel.getTimeSeries()
        val spec = plot.toSpec()
        assertEquals("plot", spec["kind"])
        val data = spec["data"] as Map<*, *>
        val dateValues = data["Date"] as List<*>
        val countValues = data["Count"] as List<*>
        assertEquals(0, dateValues.size)
        assertEquals(0, countValues.size)
    }
}
