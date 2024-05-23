package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.util.*

/*
 * This sample shows how to redact a PDF document. The program opens an input PDF, searches for
 * specific words using the WordFinder, and then removes these words from the text.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */
fun main(args: Array<String>) {
    println("Redactions sample:")
    val lib = Library()

    try {
        println("Initialized the library.")

        var sInput = Library.getResourceDirectory() + "Sample_Input/sample.pdf"
        var sOutput = "Redactions-out-applied.pdf"

        if (args.isNotEmpty()){
            sInput = args[0]
        }

        if (args.size > 1){
            sOutput = args[1]
        }

        val doc = Document(sInput)
        println("Input file: $sInput, will write to $sOutput")

        val currentPage = doc.getPage(0)

        // Redact occurrences of the word "rain" on the page.
        // Redact occurrences of the word "cloudy" on the page, changing the display details.
        //
        // For a more in-depth example of using the WordFinder, see the TextExtract sample.
        //
        val rainQuads: MutableList<Quad> = ArrayList()
        val cloudyQuads: MutableList<Quad> = ArrayList()

        val wordConfig = WordFinderConfig().apply {
            disableTaggedPDF = true
            ignoreCharGaps = true
        }

        val wordFinder = WordFinder(doc, WordFinderVersion.LATEST, wordConfig)
        val pageWords = wordFinder.getWordList(0)

        for (word in pageWords) {
            val currentWord = word.text.lowercase()
            print(" $currentWord")

            // Store the Quads of all "Cloudy" words in a list for later use in
            // creating the redaction object.
            if (currentWord.contains("rain")) {
                println("\nFound \"${word.text}\" on page 0 ")
                rainQuads.addAll(word.quads)
            } else if (currentWord.contains("cloudy")) {
                println("\nFound \"${word.text}\" on page 0 ")
                cloudyQuads.addAll(word.quads)
            }
        }

        val red = Color(1.0, 0.0, 0.0)
        val green = Color(0.0, 1.0, 0.0)
        val white = Color(1.0)

        println("\nFound ${cloudyQuads.size} \"cloudy\" instances.")
        val notCloudy = Redaction(currentPage, cloudyQuads, red).apply {
            // Fill the "normal" appearance of text with 25% opaque red;
            fillNormal = true
            setFillColor(red, 0.25)
        }

        println("\nFound ${rainQuads.size} \"rain\" instances.")
        val annot = Redaction(currentPage, rainQuads)
        annot.internalColor = green
        annot.textColor = white
        annot.fontFace = "CourierStd"
        annot.fontSize = 8.0

        // Fill the redaction with the word "rain", drawn in white
        annot.overlayText = "rain"
        annot.repeat = true
        annot.scaleToFit = true

        // Update the page's content and save the file with clipping
        currentPage.updateContent()

        doc.save(EnumSet.of(SaveFlags.FULL), "Redactions-out.pdf")

        // Actually redact the instances of the found word
        doc.applyRedactions()

        doc.save(EnumSet.of(SaveFlags.FULL), sOutput)
    } finally {
        lib.delete()
    }
}
