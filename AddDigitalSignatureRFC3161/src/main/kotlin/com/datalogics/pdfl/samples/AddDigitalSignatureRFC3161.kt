package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/*
 *
 * This sample program demonstrates the use of AddDigitalSignature for RFC3161/TimeStamp signature type.
 *
 * Copyright (c) 2025, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("AddDigitalSignatureRFC3161 sample:")

    val lib = Library()
    try {
        val doc = Document()

        var sInput = Library.getResourceDirectory() + "Sample_Input/CreateAcroForm2h.jpg"
        var sOutput = "DigSigRFC3161-out.pdf"

        if (args.isNotEmpty())
            sInput = args[0]

        if (args.size > 1)
            sOutput = args[1]

        println("Applying an RFC3161/TimeStamp digital signature to $sInput and saving it as $sOutput")

        val sigDoc = SignDoc()

        // Setup Sign params
        sigDoc.fieldID = SignatureFieldID.SEARCH_FOR_FIRST_UNSIGNED_FIELD

        // Set credential related attributes
        sigDoc.digestCategory = DigestCategory.SHA_256

        // Set the signature type to be used, RFC3161/TimeStamp.
        // The available types are defined in the SignatureType enum. Default CMS.
        sigDoc.docSignType = SignatureType.RFC_3161

        // Setup Save params
        sigDoc.outputPath = sOutput

        // Finally, sign and save the document
        sigDoc.addDigitalSignature(doc)
    } finally {
        lib.delete()
    }
}
