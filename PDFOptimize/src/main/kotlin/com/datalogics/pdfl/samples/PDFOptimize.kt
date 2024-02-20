package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.io.File

/*
 *
 * This program demonstrates the use of PDFOptimizer. This compresses a PDF document
 * to make it smaller, so it's easier to process and download.
 *
 * NOTE: Some documents can't be compressed because they're already well-compressed or contain
 * content that can't be assumed is safe to be removed.  However, you can fine tune the optimization
 * to suit your applications needs and drop such content to achieve better compression if you already
 * know it's unnecessary.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("PDFOptimizer:")
    val lib = Library()
    try {
        var sInput = Library.getResourceDirectory() + "Sample_Input/sample.pdf"
        var sOutput = "PDFOptimizer-out.pdf"

        if (args.isNotEmpty()){
            sInput = args[0]
        }

        if (args.size > 1){
            sOutput = args[1]
        }

        println("Will optimize $sInput and save as $sOutput")
        val doc = Document(sInput)
        val optimizer = PDFOptimizer()
        try {
            val oldFile = File(sInput)
            val beforeLength = oldFile.length()

            optimizer.optimize(doc, sOutput)

            val newFile = File(sOutput)
            val afterLength = newFile.length()

            println("Optimized file ")
            println(afterLength * 100.0 / beforeLength)
            println("% the size of the original.")
        } finally {
            optimizer.delete()
        }
    } finally {
        lib.delete()
    }
}
