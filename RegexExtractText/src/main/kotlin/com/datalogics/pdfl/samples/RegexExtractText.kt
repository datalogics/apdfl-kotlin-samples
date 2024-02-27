package com.datalogics.pdfl.samples

import com.datalogics.PDFL.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileWriter

/*
 *
 * This sample demonstrates using DocTextFinder to find instances of a phrase
 * that matches a user-supplied regular expression. The output is a JSON file that
 * has the match information.
 *
 * Copyright (c) 2024, Datalogics, Inc. All rights reserved.
 *
 */

// This Datalogics sample uses the org.json (JSON-Java) library to generate JSON output. Below is the JSON license for the org.json software:
/*
Copyright (c) 2002 JSON.org
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
The Software shall be used for Good, not Evil.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
IN THE SOFTWARE.
*/

fun main(args: Array<String>) {
    println("RegexExtractText sample:")

    val lib = Library()

    try {

        val sInput =
            if (args.isNotEmpty()){
                args[0]
            } else {
                Library.getResourceDirectory() + "Sample_Input/RegexExtractText.pdf"
            }

        val sOutput = "RegexExtractText-out.json"

        // Phone numbers
        val sRegex = "((1-)?(\\()?\\d{3}(\\))?(\\s)?(-)?\\d{3}-\\d{4})"
        // Email addresses
        //val sRegex = "(\\b[\\w.!#$%&'*+\\/=?^`{|}~-]+@[\\w-]+(?:\\.[\\w-]+)*\\b)"
        // URLs
        //val sRegex = "((https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,}))"

        println("Reading $sInput")

        val doc = Document(sInput)

        // This array will hold the JSON stream that we will print to the output JSON file.
        val result = JSONArray()

        val nPages = doc.numPages

        println("Opened document $sInput")

        val wordConfig = WordFinderConfig()

        // Need to set this to true so phrases will be concatenated properly.
        wordConfig.noHyphenDetection = true

        val docTextFinder = DocTextFinder(doc, wordConfig)

        val docMatches = docTextFinder.getMatchList(0, nPages - 1, sRegex)

        for (wInfo in docMatches) {
            // This JSON object will store the match phrase and an array of quads for the match.
            val matchObject = JSONObject()

            // This JSON array will store the page number and quad location for each match quad.
            val matchQuadInformation = JSONArray()

            // Set the match phrase in the JSON object.
            matchObject.put("match-phrase", wInfo.matchString)

            // Get the word quads.
            val quadList = wInfo.quadInfo

            // Iterate through the quad info.
            for (qInfo in quadList) {
                for (quad in qInfo.quads) {
                    // Get the coordinates of the quad and set the quad coordinates in JSON objects.
                    val topLeft = JSONObject()
                    topLeft.put("x", quad.topLeft.h)
                    topLeft.put("y", quad.topLeft.v)

                    val bottomLeft = JSONObject()
                    bottomLeft.put("x", quad.bottomLeft.h)
                    bottomLeft.put("y", quad.bottomLeft.v)

                    val topRight = JSONObject()
                    topRight.put("x", quad.topRight.h)
                    topRight.put("y", quad.topRight.v)

                    val bottomRight = JSONObject()
                    bottomRight.put("x", quad.bottomRight.h)
                    bottomRight.put("y", quad.bottomRight.v)

                    // Use the quad coordinate JSON objects to form a single JSON object that holds match quad location information.
                    val quadLocation = JSONObject()
                    quadLocation.put("bottom-left", bottomLeft)
                    quadLocation.put("bottom-right", bottomRight)
                    quadLocation.put("top-left", topLeft)
                    quadLocation.put("top-right", topRight)

                    val quadInformationObject = JSONObject()
                    quadInformationObject.put("page-number", qInfo.pageNum)
                    quadInformationObject.put("quad-location", quadLocation)

                    // Insert the match's page number and quad location(s) in the matchQuadInformation JSON array.
                    matchQuadInformation.put(quadInformationObject)
                }
            }

            // Set the match's quad information in the matchObject.
            matchObject.put("match-quads", matchQuadInformation)

            result.put(matchObject)
        }

        // Write the match information to the output JSON file.
        println("Writing JSON to $sOutput")
        FileWriter(sOutput).use { file -> file.write(result.toString(4)) }

        doc.close()
        
    } finally {
        lib.delete()
    }
}
