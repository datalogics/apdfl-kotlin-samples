package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*

/*
 *
 * This program lists the text for the words in a PDF document along with text location, style, and attributes.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("ListWords sample:")
    val lib = Library()
    try {
        var filename = Library.getResourceDirectory() + "Sample_Input/sample.pdf"

        if (args.isNotEmpty()) {
            filename = args[0]
        }

        println("Words in file $filename:")

        val doc = Document(filename)
        val nPages = doc.numPages
        println("Pages=$nPages")

        // Set the WordFinder configuration settings
        val wordConfig = WordFinderConfig()
        wordConfig.disableTaggedPDF = true
        wordConfig.ignoreCharGaps = true

        val wordFinder = WordFinder(doc, WordFinderVersion.LATEST, wordConfig)
        var pageWords: List<Word>?

        // Iterate through the pages, retrieve the wordlist, and print out text information
        for (i in 0 until nPages) {
            pageWords = wordFinder.getWordList(i)
            for (w in pageWords) {
                println(w.text)
                println(w.quads.toString())
                println(w.styleTransitions)
                println(w.attributes.toString())
            }
        }

        doc.close()
    } finally {
        lib.delete()
    }
}
