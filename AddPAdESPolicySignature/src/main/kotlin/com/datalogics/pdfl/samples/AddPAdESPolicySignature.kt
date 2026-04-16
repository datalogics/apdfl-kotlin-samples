package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*

/*
 *
 * This sample program demonstrates the use of AddDigitalSignature for PAdES
 * (PDF Advanced Electronic Signatures) with an explicit signature policy.
 * PAdES policy signatures (EPES) conform to the ETSI EN 319 142 standard,
 * use the ETSI.CAdES.detached SubFilter, and embed one or more signature
 * policy identifiers along with optional policy qualifiers.
 *
 * Copyright (c) 2026, Datalogics, Inc. All rights reserved.
 *
 */

fun main(args: Array<String>) {
    println("AddPAdESPolicySignature sample:")

    val lib = Library()
    try {
        var sInput = Library.getResourceDirectory() + "Sample_Input/SixPages.pdf"
        var sLogo = Library.getResourceDirectory() + "Sample_Input/ducky_alpha.tif"
        var sOutput = "PAdESPolicySignature-out.pdf"

        val sPEMCert = Library.getResourceDirectory() + "Sample_Input/Credentials/PEM/ecSecP521r1Cert.pem"
        val sPEMKey = Library.getResourceDirectory() + "Sample_Input/Credentials/PEM/ecSecP521r1Key.pem"

        if (args.isNotEmpty())
            sInput = args[0]

        if (args.size > 1)
            sOutput = args[1]

        if (args.size > 2)
            sLogo = args[2]

        println("Applying a PAdES policy digital signature to $sInput with a logo $sLogo and saving it as $sOutput")

        val doc = Document(sInput)

        val sigDoc = SignDoc()

        // Setup Sign params
        sigDoc.fieldID = SignatureFieldID.CREATE_FIELD_WITH_QUALIFIED_NAME
        sigDoc.fieldName = "Signature_es_:signatureblock"

        // Set credential related attributes
        // PAdES signatures use SHA-384 digest with EC credentials
        sigDoc.digestCategory = DigestCategory.SHA_384
        sigDoc.credentialDataFormat = CredentialDataFmt.NON_PFX
        sigDoc.setNonPfxSignerCert(sPEMCert, 0, CredentialStorageFmt.ON_DISK)
        sigDoc.setNonPfxPrivateKey(sPEMKey, 0, CredentialStorageFmt.ON_DISK)

        // Set the signature type to PAdES (PDF Advanced Electronic Signatures).
        // NOTE: Signature type must be set prior to adding policies.
        sigDoc.docSignType = SignatureType.PADES

        // Define a signature policy (SigPolicyId) using an OID.
        sigDoc.addSigPolicy("2.16.724.1.3.1.1.2.1.9")

        // Add a policy qualifier (SPuri) pointing to the policy specification document.
        sigDoc.addSigPolicyQualifierURI(
            "https://sede.administracion.gob.es/politica_de_firma_anexo_1.pdf")

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
