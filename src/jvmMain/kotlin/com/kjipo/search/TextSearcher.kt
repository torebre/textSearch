package com.kjipo.search

import com.kjipo.Config
import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.index.DirectoryReader
import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.search.IndexSearcher
import org.apache.lucene.search.highlight.*
import org.apache.lucene.search.spell.LuceneDictionary
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import java.nio.file.Files
import java.nio.file.Path


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

        val luceneDictionary = LuceneDictionary(indexReader, "contents")

        searcher = IndexSearcher(indexReader)

//        iterator = lucene_dictionary.getEntryIterator()
        val iterator = luceneDictionary.entryIterator
        analyzer = StandardAnalyzer()
//        searcher = IndexSearcher(index_reader)
//        formatter = SimpleHTMLFormatter()


    }


    fun search(searchString: String) {
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

        for (scoreDoc in hits.scoreDocs) {
            println("Document : ${scoreDoc.doc}")
            val document = searcher.doc(scoreDoc.doc)

            val fragmenter = SimpleSpanFragmenter(scorer, 10)
            val formatter = SimpleHTMLFormatter()
            val highlighter = Highlighter(formatter, scorer)

            highlighter.setTextFragmenter(fragmenter)

            val stream = TokenSources.getAnyTokenStream(indexReader, scoreDoc.doc, "contents", analyzer)
            val bestFragments = highlighter.getBestFragments(stream, document.get("contents"), 10)

//        for fragment in best_fragments:
//        print('fragment: ', fragment)

            for (bestFragment in bestFragments) {
                println("Fragment: $bestFragment")
            }

        }
    }


}