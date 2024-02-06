package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.util.*

/*
 * This sample shows how to flatten transparencies in a PDF document.
 *
 * PDF files can have objects that are partially or fully transparent, and thus
 * can blend in various ways with objects behind them. Transparent graphics or images
 * can be stacked in a PDF file, with each one contributing to the final result that
 * appears on the page. The process to flatten a set of transparencies merges them
 * into a single image on the page.
 *
 * Copyright (c) 2007-2024, Datalogics, Inc. All rights reserved.
 */

fun main(args: Array<String>) {
    println("FlattenTransparency sample:")

    var sInput1 = Library.getResourceDirectory() + "Sample_Input/trans_1page.pdf"
    var sInput2 = Library.getResourceDirectory() + "Sample_Input/trans_multipage.pdf"
    var sOutput1 = "FlattenTransparency-out1.pdf"
    var sOutput2 = "FlattenTransparency-out2.pdf"

    if (args.isNotEmpty()) {
        sInput1 = args[0]
    }

    if (args.size > 1) {
        sInput2 = args[1]
    }

    if (args.size > 2) {
        sOutput1 = args[2]
    }

    if (args.size > 3) {
        sOutput2 = args[3]
    }

    val lib = Library()

    try {
        // Open a document with a single page.
        val doc1 = Document(sInput1)

        // Verify that the page has transparency.
        val pg1: Page = doc1.getPage(0)
        val isTransparent = pg1.hasTransparency(true)

        // If there is transparency, flatten the document.
        if (isTransparent) {
            // Flattening the document will check each page for transparency.
            // If a page has transparency, it will create a new, flattened
            // version of the page and replace the original page with the
            // new one. Because of this, make sure to dispose of outstanding Page objects
            // that refer to pages in the Document before calling flattenTransparency.
            pg1.delete()

            doc1.flattenTransparency()
            println("Flattened single page document $sInput1 as $sOutput1.")
            doc1.save(EnumSet.of(SaveFlags.FULL), sOutput1)
        }

        // Open a document with multiple pages.
        val doc2 = Document(sInput2)

        // Iterate over the pages of the document and find the first page that has
        // transparency.
        var isTransparent2 = false
        var pageCounter = 0
        while (!isTransparent2 && pageCounter < doc2.numPages) {
            val pg = doc2.getPage(pageCounter)
            if (pg.hasTransparency(true)) {
                isTransparent2 = true
                // Explicitly delete the page here to ensure the reference is gone before we
                // attempt to flatten the document.
                pg.delete()
                break
            }
            pageCounter++
        }

        if (isTransparent2) {
            // Set up some parameters for the flattening.
            val ftParams = FlattenTransparencyParams()

            // The Quality setting indicates the percentage (0%-100%) of vector information
            // that is preserved. Lower values result in higher rasterization of vectors.
            ftParams.quality = 50

            // Flatten transparency in the document, starting from the first page
            // that has transparency.
            doc2.flattenTransparency(ftParams, pageCounter, Document.LAST_PAGE)
            println("Flattened multi-page document $sInput2 as $sOutput2.")
            doc2.save(EnumSet.of(SaveFlags.FULL), sOutput2)
        }
    } finally {
        lib.delete()
    }
}
