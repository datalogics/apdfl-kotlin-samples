package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*

/*
 *
 * This program demonstrates converting a standard PDF document into a
 * PDF Archive, or PDF/A, compliant version of a PDF file.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("PDFAConverter Sample:")
    val lib = Library()
    try {
        println("Initialized the library.")
        var sInput = Library.getResourceDirectory() + "Sample_Input/ducky.pdf"
        var sOutput = "PDFAConverter-out.pdf"

        if (args.isNotEmpty()) {
            sInput = args[0]
        }

        if (args.size > 1) {
            sOutput = args[1]
        }
        println("Converting $sInput")

        val doc = Document(sInput)

        // Make a conversion parameters object
        val pdfaParams = PDFAConvertParams().apply {
            abortIfXFAIsPresent = true
            ignoreFontErrors = false
            noValidationErrors = false
            validateImplementationLimitsOfDocument = true
        }

        // Create a PDF/A compliant version of the document
        val pdfaResult = doc.cloneAsPDFADocument(PDFAConvertType.RGB_3B, pdfaParams)

        // The conversion may have failed: we must check if the result has a valid Document
        if (pdfaResult.pdfaDocument == null) {
            println("ERROR: Could not convert $sInput to PDF/A.")
        } else {
            println("Successfully converted $sInput to PDF/A.")

            val pdfaDoc = pdfaResult.pdfaDocument

            // Save the result.
            pdfaDoc.save(pdfaResult.pdfaSaveFlags, sOutput)
        }

        doc.delete()
    } finally {
        lib.delete()
    }
}
