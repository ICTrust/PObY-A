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

import okhttp3.internal.and
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.security.MessageDigest

object HashUtils {

    private fun bytesToHex(bytes: ByteArray): String {
        val result = StringBuffer()
        for (b in bytes) {
            result.append(((b and 0xff) + 0x100).toString(16).substring(1))
        }
        return result.toString()
    }

    fun getCheckSumFromFilePath(digest: MessageDigest, filePath: String): String {
        return getCheckSumFromFile(
            digest,
            File(filePath)
        )
    }


    fun getCheckSumFromFile(digest: MessageDigest, file: File): String {
        val fis = FileInputStream(file)
        val byteArray = getDigest(digest, fis).digest()
        fis.close()
        return bytesToHex(byteArray)
    }


    fun getDigest(digest: MessageDigest, data: InputStream): MessageDigest {
        val streamBufferLength = 1024
        val buffer = ByteArray(streamBufferLength)
        var read = data.read(buffer, 0, streamBufferLength)
        while (read > -1) {
            digest.update(buffer, 0, read)
            read = data.read(buffer, 0, streamBufferLength)
        }
        return digest
    }
}