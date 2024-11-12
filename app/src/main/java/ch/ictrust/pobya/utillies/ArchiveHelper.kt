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

import android.util.Log
import org.apache.commons.compress.archivers.tar.TarArchiveEntry
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.security.DigestInputStream
import java.security.MessageDigest
import java.util.zip.ZipInputStream


class ArchiveHelper {

    private val TAG = "ArchiveHelper"
    private val MAX_UNCOMPRESSED_SIZE = 10 * 1024 * 1024

    // Folders to exclude when decompiling APK
    private val excludeDir = arrayOf(
        "^META-INF/", "^res/drawable.*/", "^res/layout.*/", "^res/menu/", "^res/anim.*/",
        "^res/color.*/", "font/", "^res/mipmap.*/", "^kotlin/.*", "^res/interpolator-v[1-9]+/.*",
        "^res/interpolator/.*", "^lib/x86/", "^lib/x86_64/"
    )

    fun unpackAPK(destPath: String, apkPath: String) {
        // TODO: Add support for https://www.virusbulletin.com/uploads/pdf/conference/vb2014/VB2014-Panakkal.pdf
        var destDir = File(destPath)
        var newDestPath = destPath
        var suffix = 0

        // If a destPath already exists, append a suffix to the directory name
        while (destDir.exists()) { //&& !destDir.isDirectory
            suffix++
            newDestPath = "$destPath-$suffix" + File.separator
            destDir = File(newDestPath)
        }

        if (!destDir.exists()) destDir.mkdirs()
        val fileInputStream: FileInputStream
        val buffer = ByteArray(1024)

        try {
            fileInputStream = FileInputStream(apkPath)
            val zipInputStream = ZipInputStream(fileInputStream)
            var zipEntry = zipInputStream.nextEntry

            while (zipEntry != null) {
                // TODO: Add a check of unzipped file size before unzipping => Prevent ZIP bomb DoS
                if (zipEntry.size >= MAX_UNCOMPRESSED_SIZE || zipEntry.name == "" ||
                    excludeDir.any {
                        it.toRegex().containsMatchIn(zipEntry.name)
                    }
                ) {
                    zipEntry = zipInputStream.nextEntry
                    continue
                }
                val fileName = zipEntry.name
                if (zipEntry.isDirectory) {
                    val newDir = File(newDestPath + File.separator.toString() + fileName)
                    newDir.mkdirs()
                    zipEntry = zipInputStream.nextEntry
                    continue
                }
                var newFile = File(newDestPath + File.separator.toString() + fileName)
                if (newFile.exists()) {
                    suffix++
                    if (newFile.isDirectory) {
                        newFile =
                            File(newDestPath + File.separator.toString() + fileName + "-$suffix")
                        newFile.mkdir()
                        zipEntry = zipInputStream.nextEntry
                        continue
                    } else
                        newFile = File(
                            newDestPath + File.separator.toString() + fileName.replace(
                                "/",
                                ""
                            ) + "-$suffix"
                        )
                }
                newFile.parent?.let { File(it).mkdirs() }


                val fileOutputStream = FileOutputStream(newFile)
                var len: Int
                while (zipInputStream.read(buffer).also { len = it } > 0) {
                    fileOutputStream.write(buffer, 0, len)
                }

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
                            val f = File(path + "/" + entry.name)
                            val created = f.mkdir()
                            if (!created) {
                                Log.e(TAG, "Failed to create directory '${f.absolutePath}'")
                            }
                        } else {
                            var count = 0
                            val fos = FileOutputStream(path + "/" + entry.name, false)
                            BufferedOutputStream(fos).use { dest ->
                                try {
                                    val buffer = ByteArray(1024)
                                    while (tarIn.read(buffer, 0, 1024)
                                            .also { count = it } != -1
                                    ) {
                                        dest.write(buffer, 0, count)
                                    }
                                } catch (ex: Exception) {
                                    Log.e(TAG, ex.message.toString())
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
                        Log.e(TAG, ex.message.toString())
                        ex.printStackTrace()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}