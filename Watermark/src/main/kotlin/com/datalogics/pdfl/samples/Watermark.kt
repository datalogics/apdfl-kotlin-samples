package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.util.*

/*
 * This sample program shows how to create a watermark and copy it to a new PDF file.
 * You could use this code to create a message to apply to PDF files you select, like
 * "Confidential" or "Draft Copy." Or you might want to place a copyright statement over
 * a set of photographs shown in a PDF file so that they cannot be easily duplicated without
 * the permission of the owner.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    val lib = Library()

    try {
        var sInput = Library.getResourceDirectory() + "Sample_Input/sample.pdf"
        var sWatermark = Library.getResourceDirectory() + "Sample_Input/ducky.pdf"
        var sOutput = "Watermark-out.pdf"

        if (args.isNotEmpty()){
            sInput = args[0]
        }

        if (args.size > 1){
            sWatermark = args[1]
        }

        if (args.size > 2){
            sOutput = args[2]
        }

        println("Adding watermark from $sWatermark to $sInput and saving to $sOutput")
        val doc = Document(sInput)
        System.setProperty("java.awt.headless", "true")

        val watermarkDoc = Document(sWatermark)

        val watermarkParams = WatermarkParams().apply {
            opacity = 0.8f
            rotation = 45.3f
            scale = 0.5f
            targetRange.pageSpec = PageSpec.EVEN_PAGES_ONLY
        }

        doc.watermark(watermarkDoc.getPage(0), watermarkParams)

        watermarkParams.targetRange.pageSpec = PageSpec.ODD_PAGES_ONLY

        val watermarkTextParams = WatermarkTextParams().apply {
            text = "Multiline\nWatermark"
            val f = Font("Courier", EnumSet.of(FontCreateFlags.EMBEDDED, FontCreateFlags.SUBSET))
            font = f
            textAlign = HorizontalAlignment.CENTER
            val c = Color(109.0 / 255.0, 15.0 / 255.0, 161.0 / 255.0)
            color = c
        }

        doc.watermark(watermarkTextParams, watermarkParams)

        doc.embedFonts()
        doc.save(EnumSet.of(SaveFlags.FULL, SaveFlags.LINEARIZED), sOutput)
    } finally {
        lib.delete()
    }
}
