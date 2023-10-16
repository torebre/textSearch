package com.kjipo.search

import com.kjipo.Config
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.Query
import org.apache.lucene.search.TopDocs
import org.apache.lucene.search.highlight.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.jfree.data.time.Day
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TextSearcher(private val config: Config) {
    private val analyzer: Analyzer
    private val searcher: IndexSearcher
    private val indexReader: DirectoryReader
    private val queryParser: QueryParser

    init {
//        paths_dict = util.getPaths()
//        # textSearcher = TextSearcher(paths_dict['fs_directory'])
//        fs_directory = FSDirectory.open(Paths.get(paths_dict['fs_directory']))
//        index_reader = DirectoryReader.open(fs_directory)
//        lucene_dictionary = LuceneDictionary(index_reader, 'contents')
//        iterator = lucene_dictionary.getEntryIterator()
//        analyzer = StandardAnalyzer()
//        searcher = IndexSearcher(index_reader)
//        formatter = SimpleHTMLFormatter()

        val directory: Directory = FSDirectory.open(config.indexFileDirectory)
        indexReader = DirectoryReader.open(directory)

//        val luceneDictionary = LuceneDictionary(indexReader, "contents")

        searcher = IndexSearcher(indexReader)

//        iterator = lucene_dictionary.getEntryIterator()
//        val iterator = luceneDictionary.entryIterator
        analyzer = StandardAnalyzer()
//        searcher = IndexSearcher(index_reader)
//        formatter = SimpleHTMLFormatter()

        // Query parser is not thread-safe, but this application only runs searches on one thread
        queryParser = QueryParser("contents", analyzer)
    }


    fun getQueryAndHits(searchString: String): SearchResult {
        val query = queryParser.parse(searchString)
        val hits = searcher.search(query, 100_000)

        return SearchResult(query, hits)
    }


    fun getTextResultsForQuery(
        query: Query?,
        hits: TopDocs
    ): List<TextSearchResult> {
        val scorer = QueryScorer(query)
        val fragmenter = SimpleSpanFragmenter(scorer, 40)

        return hits.scoreDocs.map { scoreDoc ->
            val document = searcher.doc(scoreDoc.doc)
            val formatter = SimpleHTMLFormatter()
            val highlighter = Highlighter(formatter, scorer)

            document.fields.forEach {
                println("Field: ${it.name()}. Type: ${it.fieldType()}")
            }

            //            println("Document name: ${document.get("doc_name")}")

            highlighter.textFragmenter = fragmenter

            val stream = TokenSources.getAnyTokenStream(
                indexReader,
                scoreDoc.doc,
                "contents",
                analyzer
            )
            val bestFragments = highlighter.getBestFragments(
                stream,
                document.get("contents"),
                10
            )

            bestFragments.forEach {
                println("Fragment: $it")
            }

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

    }

}


data class TextSearchResult(
    val documentId: Int,
    val documentDate: LocalDate,
    val fragments: List<String>
)
