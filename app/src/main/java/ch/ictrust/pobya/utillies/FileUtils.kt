/*
 * This file is part of PObY-A.
 *
 * Copyright (C) 2023 ICTrust Sàrl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package ch.ictrust.pobya.utillies

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.Locale

class FileUtils {
    fun getFileTypeByHeader(file: File, headerSize: Int = 10): String? {
        val headers = mapOf(
            "89504E47" to "PNG",
            "47494638" to "GIF",
            "25504446" to "PDF",
            "504B0304" to "ZIP",
            "504B0506" to "ZIP",
            "504B0708" to "ZIP",
            "504B5370" to "ZIP",
            "504B4C49" to "ZIP",
            "52617221" to "RAR",
            "1F8B08" to "GZ",
            "4D5A" to "EXE",
            "6465780A" to "DEX",
            "7F454C46" to "ELF",
            "CAFEBABE" to "CLASS",
            "435753" to "SWF",
            "44656C69766572792D646174653A" to "EML",
            "D0CF11E0" to "OLE",
            "2F2F203C212D2D" to "DBX",
            "2142444E" to "PST",
            "7B5C727466" to "RTF",
            "25504446" to "PDF",
            "FFD8FF" to "JPG",
            "89504E470D0A1A0A" to "JPG",
            "47494638" to "GIF",
            "49492A00" to "TIFF",
            "424D" to "BMP",
        )

        try {
            FileInputStream(file).use { fis ->
                val bytes = ByteArray(headerSize)
                fis.read(bytes)
                val header = bytes.joinToString("") {
                    "%02x".format(it)
                }.uppercase(Locale.ROOT)

                for ((key, value) in headers) {
                    if (header.startsWith(key)) {
                        return value
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    /**
     *
     *  TO-DO:
     *     Important: HTML, ASCII, Javascript are all normalized:
     *
     *         ASCII - All lowercase.
     *         HTML - Whitespace transformed to spaces, tags/tag attributes normalized, all lowercase.
     *         Javascript - All strings are normalized (hex encoding is decoded), numbers are parsed
     *              and normalized, local variables/function names are normalized to n001 format, argument to eval() is parsed as JS again, unescape() is handled, some simple JS packers are handled, output is whitespace normalized.
     */


}