package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*

/*
 *
 * This sample program demonstrates the use of AddDigitalSignature for PAdES
 * (PDF Advanced Electronic Signatures) baseline signature type without a
 * signature policy. PAdES signatures conform to the ETSI standard and use
 * the ETSI.CAdES.detached SubFilter.
 *
 * Copyright (c) 2026, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("AddBasicPAdESElectronicSignature sample:")

    val lib = Library()
    try {
        var sInput = Library.getResourceDirectory() + "Sample_Input/SixPages.pdf"
        var sLogo = Library.getResourceDirectory() + "Sample_Input/ducky_alpha.tif"
        var sOutput = "PAdESBaselineSignature-out.pdf"

        val sPEMCert = Library.getResourceDirectory() + "Sample_Input/Credentials/PEM/ecSecP521r1Cert.pem"
        val sPEMKey = Library.getResourceDirectory() + "Sample_Input/Credentials/PEM/ecSecP521r1Key.pem"

        if (args.isNotEmpty())
            sInput = args[0]

        if (args.size > 1)
            sOutput = args[1]

        if (args.size > 2)
            sLogo = args[2]

        println("Applying a PAdES baseline digital signature to $sInput with a logo $sLogo and saving it as $sOutput")

        val doc = Document(sInput)

        val sigDoc = SignDoc()

        // Setup Sign params
        sigDoc.fieldID = SignatureFieldID.CREATE_FIELD_WITH_QUALIFIED_NAME
        sigDoc.fieldName = "Signature_es_:signatureblock"

        // Set credential related attributes
        sigDoc.digestCategory = DigestCategory.SHA_384
        sigDoc.credentialDataFormat = CredentialDataFmt.NON_PFX
        sigDoc.setNonPfxSignerCert(sPEMCert, 0, CredentialStorageFmt.ON_DISK)
        sigDoc.setNonPfxPrivateKey(sPEMKey, 0, CredentialStorageFmt.ON_DISK)

        // Set the signature type to PAdES (PDF Advanced Electronic Signatures).
        // This produces an ETSI.CAdES.detached signature conforming to the
        // PAdES baseline profile without a signature policy.
        sigDoc.docSignType = SignatureType.PADES

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
        sigDoc.addDigitalSignature(doc)
    } finally {
        lib.delete()
    }
}
