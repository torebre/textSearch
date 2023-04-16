package com.kjipo.search

import com.kjipo.Config
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.highlight.*
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TextSearcher(private val config: Config) {
    private val analyzer: Analyzer
    private val searcher: IndexSearcher
    private val indexReader: DirectoryReader

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


    }


    fun search(searchString: String): List<TextSearchResult> {
//        counter = 0
//        for term in BytesRefIterator.cast_(iterator):
//        term_as_string = term.utf8ToString()
//        print('term:', term_as_string)
//        query = QueryParser("contents", analyzer).parse(term_as_string)
//        hits = searcher.search(query, 100000)
//        scorer = QueryScorer(query)
//        fragmenter = SimpleSpanFragmenter(scorer, 10)
//        highlighter = Highlighter(formatter, scorer)
//        highlighter.setTextFragmenter(fragmenter)

//        for hit in hits.scoreDocs:
//        document = searcher.doc(hit.doc)
//
//        stream = TokenSources.getAnyTokenStream(index_reader, hit.doc, 'contents', analyzer)
//        best_fragments = highlighter.getBestFragments(stream, document.get('contents'), 10)
//
//        for fragment in best_fragments:
//        print('fragment: ', fragment)


        val queryParser = QueryParser("contents", analyzer)
        val query = queryParser.parse(searchString)
        val hits = searcher.search(query, 100_000)

        val scorer = QueryScorer(query)
        val fragmenter = SimpleSpanFragmenter(scorer, 40)

        return hits.scoreDocs.map {scoreDoc ->

            val document = searcher.doc(scoreDoc.doc)
            val formatter = SimpleHTMLFormatter()
            val highlighter = Highlighter(formatter, scorer)

            document.fields.forEach {
                println("Field: ${it.name()}. Type: ${it.fieldType()}")
            }

//            println("Document name: ${document.get("doc_name")}")

            highlighter.textFragmenter = fragmenter

            val stream = TokenSources.getAnyTokenStream(indexReader,
                scoreDoc.doc,
                "contents",
                analyzer)
            val bestFragments = highlighter.getBestFragments(stream,
                document.get("contents"),
                10)

            bestFragments.forEach {
                println("Fragment: $it")
            }

            TextSearchResult(scoreDoc.doc,
                LocalDate.parse(document.get("doc_name"), dateFormatter),
                bestFragments.toList())
        }
    }

    fun getDocument(documentId: Int): Document? {
        return indexReader.document(documentId)
    }


    companion object {
        val dateFormatter = DateTimeFormatter.ofPattern("MMddyy")

    }

}


data class TextSearchResult(val documentId: Int,
                       val documentDate: LocalDate,
                       val fragments: List<String>)
