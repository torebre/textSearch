package com.kjipo.search

import com.kjipo.Config
import org.junit.jupiter.api.Test

class RunTextSearch {


    @Test
    fun testSearch() {
        val config = Config.getConfig("/home/student/workspace/textSearch/search_config.properties")
        val textSearcher = TextSearcher(config)
        textSearcher.getDocumentsByDate()

    }



}