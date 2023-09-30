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