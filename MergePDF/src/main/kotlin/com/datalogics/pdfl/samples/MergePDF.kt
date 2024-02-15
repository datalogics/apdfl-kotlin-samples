package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.util.EnumSet

/*
 *
 * This sample demonstrates merging one PDF document into another. The program
 * offers two optional input documents and defines a default output document. The sample
 * takes the content from the second PDF input document and inserts it in the first
 * input document, and saves the result to the output PDF document.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */
fun main(args: Array<String>) {
    println("MergePDF sample:")

    val lib = Library()

    var sInput1 = Library.getResourceDirectory() + "Sample_Input/merge_pdf1.pdf"
    var sInput2 = Library.getResourceDirectory() + "Sample_Input/merge_pdf2.pdf"
    var sOutput = "MergePDF-out.pdf"

    if (args.isNotEmpty())
        sInput1 = args[0]
    if (args.size > 1)
        sInput2 = args[1]
    if (args.size > 2)
        sOutput = args[2]

    println("Adding $sInput1 and $sInput2 and writing to $sOutput")

    val doc1 = Document(sInput1)
    val doc2 = Document(sInput2)

    try {
        doc1.insertPages(
            Document.LAST_PAGE, doc2, 0, Document.ALL_PAGES, EnumSet.of(
                PageInsertFlags.BOOKMARKS, PageInsertFlags.THREADS,
                // For best performance processing large documents, set the following flags.
                PageInsertFlags.DO_NOT_MERGE_FONTS, PageInsertFlags.DO_NOT_RESOLVE_INVALID_STRUCTURE_PARENT_REFERENCES, PageInsertFlags.DO_NOT_REMOVE_PAGE_INHERITANCE
            )
        )
    } catch (ex: LibraryException) {
        if (!ex.message!!.contains("An incorrect structure tree was found in the PDF file but operation continued")) {
            throw ex
        }
        println(ex.message)
    }

    // For best performance processing large documents, set the following flags.
    doc1.save(EnumSet.of(SaveFlags.FULL, SaveFlags.SAVE_LINEARIZED_NO_OPTIMIZE_FONTS, SaveFlags.COMPRESSED), sOutput)

    doc1.close()
    doc2.close()

    lib.delete()
}
