package com.datalogics.pdfl.samples

import com.datalogics.PDFL.Document
import com.datalogics.PDFL.Library

/*
 *
 * This program converts sample PDF documents to Word, Excel, and PowerPoint files.
 *
 * Please note that the Office conversion APIs convertToWord, convertToExcel, and convertToPowerPoint are available on
 * Windows only.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */
class ConvertToOffice {
    enum class OfficeType {
        WORD,
        EXCEL,
        POWERPOINT
    }

    companion object {
        fun convertPDFToOffice(inputPath: String, outputPath: String, officeType: OfficeType) {
            println("Converting $inputPath, output file is $outputPath")

            val result: Boolean = when (officeType) {
                OfficeType.WORD -> Document.convertToWord(inputPath, outputPath)
                OfficeType.EXCEL -> Document.convertToExcel(inputPath, outputPath)
                OfficeType.POWERPOINT -> Document.convertToPowerPoint(inputPath, outputPath)
            }

            if (result) {
                println("Successfully converted $inputPath to $outputPath")
            } else {
                println("ERROR: Could not convert $inputPath")
            }
        }
    }
}

fun main(args: Array<String>) {
    println("ConvertToOffice Sample:")

    val lib = Library()
    try {
        println("Initialized the library.")

        val inputPathWord = Library.getResourceDirectory() + "Sample_Input/Word.pdf"
        val outputPathWord = "word-out.docx"
        val inputPathExcel = Library.getResourceDirectory() + "Sample_Input/Excel.pdf"
        val outputPathExcel = "excel-out.xlsx"
        val inputPathPowerPoint = Library.getResourceDirectory() + "Sample_Input/PowerPoint.pdf"
        val outputPathPowerPoint = "powerpoint-out.pptx"

        ConvertToOffice.convertPDFToOffice(inputPathWord, outputPathWord, ConvertToOffice.OfficeType.WORD)
        ConvertToOffice.convertPDFToOffice(inputPathExcel, outputPathExcel, ConvertToOffice.OfficeType.EXCEL)
        ConvertToOffice.convertPDFToOffice(
            inputPathPowerPoint,
            outputPathPowerPoint,
            ConvertToOffice.OfficeType.POWERPOINT
        )
    } finally {
        lib.delete()
    }
}
