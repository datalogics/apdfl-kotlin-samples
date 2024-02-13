package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.util.*

/*
* This program opens a PDF input document and exports the pages to a set of separate PDF documents.
*
* Copyright (c) 2024, Datalogics, Inc. All rights reserved.
*/

fun main(args: Array<String>) {
    println("SplitPDF sample:")
    val lib = Library()

    try {
        val sInput: String = Library.getResourceDirectory() + "Sample_Input/PDFToBeSplit.pdf"

        val doc = Document(sInput)
        println("Opened document $sInput")

        val numDocPages = doc.numPages

        // Let's go through each page of the document and split it out into its own document.
        for (pageIndex in 0 until numDocPages){
            val outDoc = Document()

            // Insert page into output document.
            outDoc.insertPages(Document.BEFORE_FIRST_PAGE, doc, pageIndex, 1, EnumSet.of(PageInsertFlags.NONE))

            // Generate a name for the output document and save the document.
            val docName ="SplitPDF_out_${pageIndex + 1}.pdf"
            outDoc.save(EnumSet.of(SaveFlags.FULL), docName)

            println("$docName has been created!")
        }
    } finally {
        lib.delete()
    }
}
