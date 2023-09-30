package ch.ictrust.pobya.utillies

import android.util.Log
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.*
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream


class ArchiveHelper {

    private val TAG = "ArchiveHelper"
    // Folders to exclude when decompiling APK
    private val excludeDir =  arrayOf(
        "^META-INF/", "^res/drawable.*/", "^res/layout.*/", "^res/menu/", "^res/anim/",
        "^res/color.*/", "font/", "^res/mipmap.*/"
    )

    fun unpackAPK(destPath: String, apkPath: String) {
        // TODO: Add support for https://www.virusbulletin.com/uploads/pdf/conference/vb2014/VB2014-Panakkal.pdf
        val destDir = File(destPath)
        if (!destDir.exists()) destDir.mkdirs()
        val fileInputStream: FileInputStream
        val buffer = ByteArray(512)
        try {
            fileInputStream = FileInputStream(apkPath)
            val zipInputStream = ZipInputStream(fileInputStream)
            var zipEntry = zipInputStream.nextEntry
            // TODO: Add a check of unzipped file size before unzipping => Prevent ZIP bomb DoS
            while (zipEntry != null) {
                if (zipEntry.name == "" ||
                    excludeDir.any {
                        it.toRegex().containsMatchIn(zipEntry.name)
                    }
                ) {
                    zipEntry = zipInputStream.nextEntry
                    continue
                }
                val fileName = zipEntry.name
                val newFile = File(destPath + File.separator.toString() + fileName)
                File(newFile.parent).mkdirs()
                val fileOutputStream = FileOutputStream(newFile)
                var len: Int
                while (zipInputStream.read(buffer).also { len = it } > 0) {
                    fileOutputStream.write(buffer, 0, len)
                }
                /** TODO: check type of file
                 *      - If an archive (zip, apk, tar, gz) -> uncompress & scan
                 */
                fileOutputStream.close()
                // close this ZipEntry
                zipInputStream.closeEntry()
                zipEntry = zipInputStream.nextEntry
            }

            zipInputStream.closeEntry()
            zipInputStream.close()
            fileInputStream.close()
        } catch (e: IOException) {
            Log.e(TAG, e.message.toString())
            e.printStackTrace()
        }
    }


    fun extractCVD(path: String, cvdFile: String) {
        val dir = File(path)
        if (!dir.exists()) dir.mkdirs()
        val fis: FileInputStream
        try {
            fis = FileInputStream(cvdFile)
            // skip the CVD header of 512-bytes
            fis.skip(512)
            val gzipIn = GzipCompressorInputStream(fis)

            val mDigestMD5 = MessageDigest.getInstance("md5")

            DigestInputStream(fis, mDigestMD5)

            TarArchiveInputStream(gzipIn).use { tarIn ->
                var entry: TarArchiveEntry? = tarIn.nextTarEntry
                while (entry != null) {
                    try {
                        // If the entry is a directory, create the directory.
                        if (entry.isDirectory) {
                            val f = File(path+"/" + entry.name)
                            val created = f.mkdir()
                            if (!created) {
                                Log.e(TAG,"Failed to create directory '${f.absolutePath}'")
                            }
                        } else {
                            var count = 0
                            val fos = FileOutputStream(path + "/"+ entry.name, false)
                            BufferedOutputStream(fos).use { dest ->
                                try {
                                    val buffer = ByteArray(1024)
                                    while (tarIn.read(buffer, 0, 1024)
                                            .also { count = it } != -1
                                    ) {
                                        dest.write(buffer, 0, count)
                                    }
                                } catch (ex: Exception) {
                                    Log.e(TAG,ex.message.toString())
                                    ex.printStackTrace()
                                } finally {
                                    dest.close()
                                }
                            }
                            fos.flush()
                            fos.close()
                        }
                        entry = tarIn.nextTarEntry
                    } catch (ex: Exception) {
                        entry = null
                        Log.e(TAG,ex.message.toString())
                        ex.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}