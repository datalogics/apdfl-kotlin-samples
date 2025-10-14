package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import java.io.FileOutputStream
import java.io.OutputStreamWriter

/*
 *
 * This sample program demonstrates the use of AddDigitalSignature for CMS signature type.
 *
 * Copyright (c) 2025, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("AddDigitalSignatureCMS sample:")

    val lib = Library()
    try {
        val doc = Document()

        var sInput = Library.getResourceDirectory() + "Sample_Input/SixPages.jpg"
        val sLogo = Library.getResourceDirectory() + "Sample_Input/ducky_alpha.tif"
        var sOutput = "DigSigCMS-out.pdf"

        val sDERCert = Library.getResourceDirectory() + "Sample_Input/Credentials/DER/RSA_certificate.der"
        val sDERKey = Library.getResourceDirectory() + "Sample_Input/Credentials/DER/RSA_privKey.der"

        if (args.isNotEmpty())
            sInput = args[0]

        if (args.size > 1)
            sOutput = args[1]

        if (args.size > 2)
            sOutput = args[2]

        println("Applying a CMS digital signature to $sInput with a logo $sLogo and saving it as $sOutput")

        val sigDoc = SignDoc()

        // Setup Sign params
        sigDoc.fieldID = SignatureFieldID.CREATE_FIELD_WITH_QUALIFIED_NAME
        sigDoc.fieldName = "Signature_es_:signatureblock"

        // Set credential related attributes
        sigDoc.digestCategory = DigestCategory.SHA_256
        sigDoc.credentialDataFormat = CredentialDataFmt.NON_PFX
        sigDoc.setNonPfxSignerCert(sDERCert, 0, CredentialStorageFmt.ON_DISK)
        sigDoc.setNonPfxPrivateKey(sDERKey, 0, CredentialStorageFmt.ON_DISK)

        // Set the signature type to be used.
        // The available types are defined in the SignatureType enum. Default CMS.
        sigDoc.docSignType = SignatureType.CMS

        // Setup the signer information
        // (Logo image is optional)
        sigDoc.setSignerInfo(sLogo, 0.5F, "John Doe", "Chicago, IL", "Approval", "Datalogics, Inc.", DisplayTraits.KDISPLAY_ALL)

        // Set the size and location of the signature box (optional)
        // If not set, invisible signature will be placed on first page
        sigDoc.signatureBoxPageNumber = 0
        sigDoc.signatureBoxRectangle = Rect(100.0, 300.0, 400.0, 400.0)

        // Setup Save params
        sigDoc.outputPath = sOutput

        // Finally, sign and save the document
        sigDoc.addDigitalSignature(sigDoc)
    } finally {
        lib.delete()
    }
}
