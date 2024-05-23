package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.util.*

/*
 *
 * This sample shows how to search a PDF document using regex pattern matching. The program opens an input PDF, searches for
 * words using the DocTextFinder, and then prints these words to the console.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("RegexTextSearch sample:")
    val lib = Library()

    try {
        val sInput: String =
            if (args.isNotEmpty()) {
                args[0]
            } else {
                Library.getResourceDirectory() + "Sample_Input/RegexTextSearch.pdf"
            }

        val sOutput = "RegexTextSearch-out.pdf"

        // Highlight occurrences of the words that match this regular expression.
        // Phone numbers
        val sRegex = "((1-)?(\\()?\\d{3}(\\))?(\\s)?(-)?\\d{3}-\\d{4})"
        // Email addresses
        //val sRegex = "(\\b[\\w.!#$%&'*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)*\\b)"
        // URLs
        //val sRegex = "((https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,}))"

        println("Reading $sInput")

        val doc = Document(sInput)
        val nPages = doc.numPages
        println("Opened document $sInput")

        val wordConfig = WordFinderConfig()

        // Need to set this to true so phrases will be concatenated properly
        wordConfig.noHyphenDetection = true

        val docTextFinder = DocTextFinder(doc, wordConfig)
        val docMatches = docTextFinder.getMatchList(0, nPages - 1, sRegex)

        for (wInfo in docMatches) {
            // Show the matching phrase
            val s = wInfo.matchString
            println(s)

            // Get the word quads
            val quadList = wInfo.quadInfo

            // Iterate through the quad info and create highlights
            for (qInfo in quadList) {
                val docPage = doc.getPage(qInfo.pageNum)
                val highlight = HighlightAnnotation(docPage, qInfo.quads)
                highlight.normalAppearance = highlight.generateAppearance()
            }
        }

        // Save the document with the highlighted matched strings
        doc.save(EnumSet.of(SaveFlags.FULL), sOutput)
        doc.close()

    } finally {
        lib.delete()
    }
}
