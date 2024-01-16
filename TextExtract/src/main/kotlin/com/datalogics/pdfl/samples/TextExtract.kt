package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/*
*
* This program pulls text from a PDF file and exports it to a text file (TXT).
* It will open a PDF file called Constitution.PDF and create an output file called
* TextExtract-untagged-out.txt. The export file includes page number references, and
* the text is produced using standard Roman encoding. The program is also written
* to include a provision for working with tagged documents, and determines if the original
* PDF file is tagged or untagged. Tagging is used to make PDF files accessible
* to the blind or to people with vision problems.
*
* Copyright (c) 2007-2024, Datalogics, Inc. All rights reserved.
*
*/

fun main(args: Array<String>) {
    println("TextExtract sample:")
    val lib = Library()

    try {
        // This is an untagged PDF.
        var sInput: String = Library.getResourceDirectory() + "Sample_Input/constitution.pdf"

        // This is a tagged PDF.
        // var sInput: String = Library.getResourceDirectory() + "Sample_Input/pdf_intro.pdf"
        if (args.isNotEmpty()) {
            sInput = args[0]
        }
        println("Reading $sInput")

        val doc = Document(sInput)

        println("Opened document $sInput")

        // Determine if the PDF is tagged.  We'll use a slightly different set of rules
        // for parsing tagged and untagged PDFs.
        //
        // We'll determine if the PDF is tagged by examining the MarkInfo
        // dictionary of the document.  First, check for the existence of the MarkInfo dict.
        var docIsTagged = false
        val markInfoDict: PDFDict? = doc.root.get("MarkInfo") as? PDFDict
        val markedEntry: PDFBoolean? = markInfoDict?.get("Marked") as? PDFBoolean

        if (markInfoDict != null) {
            if (markedEntry != null) {
                if (markedEntry.value) {
                    docIsTagged = true
                }
            }
        }

        val wordConfig = WordFinderConfig()
        wordConfig.ignoreCharGaps = false
        wordConfig.ignoreLineGaps = false
        wordConfig.noAnnots = false
        wordConfig.noEncodingGuess = false

        // Std Roman treatment for custom encoding; overrides the noEncodingGuess option
        wordConfig.unknownToStdEnc = false

        wordConfig.disableTaggedPDF = false   // legacy mode WordFinder creation
        wordConfig.noXYSort = true
        wordConfig.preserveSpaces = false
        wordConfig.noLigatureExp = false
        wordConfig.noHyphenDetection = false
        wordConfig.trustNBSpace = false
        wordConfig.noExtCharOffset = false        // text extraction efficiency
        wordConfig.noStyleInfo = false            // text extraction efficiency

        val wordFinder = WordFinder(doc, WordFinderVersion.LATEST, wordConfig)

        if (docIsTagged)
            extractTextTagged(doc, wordFinder)
        else
            extractTextUntagged(doc, wordFinder)

        doc.close()
    } finally {
        lib.delete()
    }
}

fun extractTextUntagged(doc: Document, wordFinder: WordFinder) {
    val nPages = doc.numPages
    var pageWords: List<Word>?

    val logFile = FileOutputStream("TextExtract-untagged-out.txt")
    println("Writing TextExtract-untagged-out.txt")
    val logWriter = OutputStreamWriter(logFile, "UTF-8")

    for (pageIndex in 0 until nPages) {
        pageWords = wordFinder.getWordList(pageIndex)
        var textToExtract = ""

        for (wordNum in pageWords.indices) {
            val wInfo = pageWords[wordNum]
            val s = wInfo.text

            // Check for hyphenated words that break across a line.
            if (wInfo.attributes.contains(WordAttributeFlags.HAS_SOFT_HYPHEN) && wInfo.attributes.contains(
                    WordAttributeFlags.LAST_WORD_ON_LINE
                )
            ) {
                // Remove the hyphen and combine the two parts of the word before adding to the extracted text.
                // Note that we pass in the Unicode character for soft hyphen as well as the regular hyphen.
                //
                // In untagged PDF, it's not uncommon to find a mixture of hard and soft hyphens that may
                // not be used for their intended purposes.
                // (Soft hyphens are intended only for words that break across lines.)
                //
                // For the purposes of this sample, we'll remove all hyphens.  In practice, you may need to check
                // words against a dictionary to determine if the hyphenated word is actually one word or two.
                // Note we remove ascii hyphen, Unicode soft hyphen(\u00ad) and Unicode hyphen(0x2010)
                val splitstrs = s.split("-|\u00ad|0x2010".toRegex())

                for (index in splitstrs.indices) {
                    textToExtract += splitstrs[index]
                }
            } else {
                textToExtract += s
            }

            // Check for space adjacency or last word in region and add a space if necessary.
            // LastWordInRegion is true if the WordFinder determined that this is the last word in a region.
            // This may be set for words that are visually separated when viewing the PDF,
            // but are not separated by a space.  Here, it's used in conjunction with
            // WordAttributes.AdjacentToSpace to determine where to insert spaces when
            // post-processing WordFinder results.
            if (wInfo.attributes.contains(WordAttributeFlags.ADJACENT_TO_SPACE) || wInfo.isLastWordInRegion) {
                textToExtract += " "
            }
            // Check for a line break and add one if necessary
            if (wInfo.attributes.contains(WordAttributeFlags.LAST_WORD_ON_LINE))
                textToExtract += "\n"
        }
        val pageNum = "<page ${pageIndex + 1}>\n"
        logWriter.write(pageNum, 0, pageNum.length)
        logWriter.write(textToExtract, 0, textToExtract.length)
        logWriter.write("\n")

        // Release requested WordList
        for (wordnum in pageWords.indices) {
            pageWords[wordnum].delete()
        }
    }
    println("Extracted $nPages pages.")
    logWriter.close()
}

fun extractTextTagged(doc: Document, wordFinder: WordFinder) {
    val nPages = doc.numPages
    var pageWords: List<Word>

    val logFile = FileOutputStream("TextExtract-tagged-out.txt")
    println("Writing TextExtract-tagged-out.txt")
    val logWriter = OutputStreamWriter(logFile, "UTF-8")

    for (pageIndex in 0 until nPages) {
        pageWords = wordFinder.getWordList(pageIndex)

        var textToExtract = ""

        for (wordNum in pageWords.indices) {
            val wInfo = pageWords[wordNum]
            val s = wInfo.text

            // In most tagged PDFs, soft hyphens are used only to break words across lines, so we'll
            // check for any soft hyphens and remove them from our text output.
            //
            // Note that we're not checking for the LAST_WORD_ON_LINE flag, unlike untagged PDF.  For Tagged PDF,
            // words are not flagged as being the last on the line if they are not at the end of a sentence.
            if (wInfo.attributes.contains(WordAttributeFlags.HAS_SOFT_HYPHEN)) {
                // Remove the hyphen and combine the two parts of the word before adding to the extracted text.
                // Note that we pass in the Unicode character for soft hyphen(\u00ad) and Unicode hyphen(0x2010).
                val splitstrs = s.split("\u00ad|0x2010".toRegex())

                for (index in splitstrs.indices) {
                    textToExtract += splitstrs[index]
                }
            } else {
                textToExtract += s
            }

            // Check for space adjacency or last word in region and add a space if necessary.
            // LastWordInRegion is true if the WordFinder determined that this is the last word in a region.
            // This may be set for words that are visually separated when viewing the PDF,
            // but are not separated by a space.  Here, it's used in conjunction with
            // WordAttributes.AdjacentToSpace to determine where to insert spaces when
            // post-processing WordFinder results.
            if (wInfo.attributes.contains(WordAttributeFlags.ADJACENT_TO_SPACE) || wInfo.isLastWordInRegion) {
                textToExtract += " "
            }

            // Check for a line break and add one if necessary
            if (wInfo.attributes.contains(WordAttributeFlags.LAST_WORD_ON_LINE))
                textToExtract += "\n"
        }

        val pageNum = "<page ${pageIndex + 1}>\n"
        logWriter.write(pageNum, 0, pageNum.length)
        logWriter.write(textToExtract, 0, textToExtract.length)
        logWriter.write("\n")

        // Release requested WordList
        for (wordnum in pageWords.indices) {
            pageWords[wordnum].delete()
        }
    }
    println("Extracted $nPages pages.")
    logWriter.close()
}
