package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.util.EnumSet

/*
 *
 * This sample demonstrates the OfficeToPDF plugin, which converts a Microsoft
 * Word (.docx) document into a PDF.
 *
 * By default the sample converts DOCXLink.docx from the Sample_Input
 * directory and writes the result to ConvertWordToPDF-out.pdf in the current
 * working directory. The optional first argument is the input .docx path and
 * the optional second argument is the output PDF path.
 *
 * Document.fromOfficeFile takes the input path, an OfficeToPDFConvertParams
 * and an OfficeConvertInfo. It returns the converted Document, and fills the
 * OfficeConvertInfo with the page count and the per-asset diagnostics.
 *
 * Copyright (c) 2026, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("ConvertWordToPDF sample:")
    val lib = Library()
    try {
        var sInput = Library.getResourceDirectory() + "Sample_Input/DOCXLink.docx"
        var sOutput = "ConvertWordToPDF-out.pdf"

        if (args.isNotEmpty()) {
            sInput = args[0]
        }

        if (args.size > 1) {
            sOutput = args[1]
        }
        println("Converting $sInput and saving as $sOutput")

        // Configure the conversion. One OfficeToPDFConvertParams serves any
        // number of conversions.
        val params = OfficeToPDFConvertParams().apply {
            // OfficeCommentRendering.OMIT (default), MARGIN, or ANNOTATIONS.
            comments = OfficeCommentRendering.MARGIN

            // A fixed instant is stamped as /CreationDate and /ModDate and used
            // for DATE and TIME fields, so every conversion carries the same
            // dates. Leave it unset to stamp the system clock instead.
            setConversionTime(2026, 1, 1, 0, 0, 0)
        }

        val info = OfficeConvertInfo()

        val doc = try {
            Document.fromOfficeFile(sInput, params, info)
        } catch (e: RuntimeException) {
            // The message names the reason: the source is missing, not a valid
            // .docx, or password-protected, or the OfficeToPDF plugin is not
            // available.
            println("Conversion failed: ${e.message}")
            return
        }

        println("Successfully converted the document: ${info.pageCount} page(s).")

        // A successful conversion can still report diagnostics: a substituted
        // font, an undecodable image, a hyperlink dropped as unsafe. They
        // describe the rendered assets; they do not make the conversion fail.
        for (diagnostic in info.diagnostics) {
            println("  [${diagnostic.kind}] ${diagnostic.asset}: ${diagnostic.message}")
        }

        doc.save(EnumSet.of(SaveFlags.FULL), sOutput)
        println("Saved to $sOutput")
        doc.delete()
    } finally {
        lib.delete()
    }
}
