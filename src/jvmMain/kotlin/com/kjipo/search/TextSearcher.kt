package com.kjipo.search

import com.kjipo.Config
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.DocIdSetIterator
import org.apache.lucene.search.DocIdSetIterator.NO_MORE_DOCS
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopDocs
import org.apache.lucene.search.highlight.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.slf4j.LoggerFactory.getLogger
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.stream.Stream


class TextSearcher(config: Config) {
    private val analyzer: Analyzer
    private val searcher: IndexSearcher
    private val indexReader: DirectoryReader
    private val queryParser: QueryParser

    init {
        val directory: Directory = FSDirectory.open(config.indexFileDirectory)
        indexReader = DirectoryReader.open(directory)

//        val luceneDictionary = LuceneDictionary(indexReader, "contents")

        searcher = IndexSearcher(indexReader)
        analyzer = StandardAnalyzer()

        // Query parser is not thread-safe, but this application only runs searches on one thread
        queryParser = QueryParser("contents", analyzer)
    }


    fun getQueryAndHits(searchString: String): SearchResult {
        val query = queryParser.parse(searchString)
        val hits = searcher.search(query, 100_000)

        return SearchResult(query, hits)
    }

    fun getDocumentsByDate(): List<Pair<LocalDate, Int>> {
        val docSetIterator = DocIdSetIterator.all(indexReader.maxDoc())

        return Stream.generate { docSetIterator.nextDoc() }
            .takeWhile { it != NO_MORE_DOCS }
            .map { documentId ->
                val document = indexReader.document(documentId)
                val documentDate = LocalDate.parse(document.get("doc_name"), dateFormatter)

                Pair(documentDate, documentId)
            }
            .sorted { document, document2 ->
                document.first.compareTo(document2.first)
            }
            .toList()
    }


    fun getTextResultsForQuery(
        query: Query?,
        hits: TopDocs
    ): List<TextSearchResult> {
        val scorer = QueryScorer(query)
        val fragmenter = SimpleSpanFragmenter(scorer, 50)

        return hits.scoreDocs.map { scoreDoc ->
            val document = searcher.doc(scoreDoc.doc)
            val formatter = SimpleHTMLFormatter()
            val highlighter = Highlighter(formatter, scorer).also {
                it.textFragmenter = fragmenter
            }

            document.fields.forEach {
                logger.info("Field: ${it.name()}. Type: ${it.fieldType()}")
            }

            val stream = TokenSources.getAnyTokenStream(
                indexReader,
                scoreDoc.doc,
                "contents",
                analyzer
            )
            val bestFragments = highlighter.getBestFragments(
                stream,
                document.get("contents"),
                5
            )

            TextSearchResult(
                scoreDoc.doc,
                LocalDate.parse(document.get("doc_name"), dateFormatter),
                bestFragments.toList()
            )
        }
    }

    fun getDocument(documentId: Int): Document? {
        return indexReader.document(documentId)
    }

    fun getTextResultsForQuery(searchResult: SearchResult): List<TextSearchResult> {
        return getTextResultsForQuery(searchResult.query, searchResult.hits)
    }


    fun getDatesForHits(hits: TopDocs): List<LocalDate> {
        return hits.scoreDocs.map { scoreDoc ->
            searcher.doc(scoreDoc.doc).let {
                LocalDate.parse(it.get("doc_name"), dateFormatter)
            }
        }
    }


    companion object {
        val dateFormatter = DateTimeFormatter.ofPattern("MMddyy")

        private val logger = getLogger(TextSearcher::class.java)


    }

}


data class TextSearchResult(
    val documentId: Int,
    val documentDate: LocalDate,
    val fragments: List<String>
)
