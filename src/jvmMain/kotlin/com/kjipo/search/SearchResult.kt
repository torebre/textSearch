package com.kjipo.search

import org.apache.lucene.search.Query
import org.apache.lucene.search.TopDocs

data class SearchResult(
    val query: Query?,
    val hits: TopDocs)
