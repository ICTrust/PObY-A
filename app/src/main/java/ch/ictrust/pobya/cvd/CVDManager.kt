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
package ch.ictrust.pobya.cvd

import android.app.Application
import android.content.Context
import android.os.Parcelable
import android.util.Log
import ch.ictrust.pobya.cvd.models.HSB
import ch.ictrust.pobya.cvd.models.NDB
import ch.ictrust.pobya.cvd.repositroy.HSBRepository
import ch.ictrust.pobya.cvd.repositroy.NDBRepository
import ch.ictrust.pobya.utillies.ArchiveHelper
import ch.ictrust.pobya.utillies.FileUtils
import ch.ictrust.pobya.utillies.HashUtils
import ch.ictrust.pobya.utillies.Utilities
import kotlinx.parcelize.Parcelize
import java.io.File
import java.security.MessageDigest


class Signature(
    var malwareName: String,
    var signature: String,
    var type: String
) {
    fun get(): String {
        return this.toString()
    }
}

@Parcelize
enum class ScanType(val value: Int) : Parcelable {
    HSB(0),
    NDB(1),
    HSB_NDB(10),
}

class ClamManager {

    private val TAG = "ClamManager"

    companion object {
        val EXTRACT_FOLDER_NAME = "Extract"
        private var instance: ClamManager? = null
        private var context: Context? = null

        var entriesHSB = ArrayList<HSB>()

        @Synchronized
        fun getInstance(ctx: Context): ClamManager {
            if (instance == null) {
                context = ctx
                entriesHSB = ArrayList()
                instance = ClamManager()
            }
            return instance as ClamManager
        }
    }


    /**
     * Scan a file for malware signatures
     * @param filePath: file path to scan
     * @return: Signature if malware is found, null otherwise
     */
    fun clamScan(filePath: String, scanType: ScanType): Signature? {
        val toScan = File(filePath)
        // If filePath is a directory scan through and scan files
        if (toScan.isDirectory) {
            toScan.walkTopDown().forEach {
                if (it.path == filePath)
                    return null
                return clamScan(it.path, scanType)
            }
            return null
        }

        val fileType = FileUtils().getFileTypeByHeader(File(filePath))

        var targets = arrayOf(0).toList()
        when (fileType) {
            "ZIP" -> { // Support also APK files
                // extract zip file
                var extractPath = context?.getExternalFilesDir(null)?.path +
                        File.separator.toString() + EXTRACT_FOLDER_NAME + File.separator.toString() +
                        filePath.split("/").last() + File.separator.toString()
                var extractDir = File(extractPath)
                var suffix = 0
                while (extractDir.exists()) {
                    suffix++
                    extractPath = context?.getExternalFilesDir(null)?.path +
                            File.separator.toString() + EXTRACT_FOLDER_NAME + File.separator.toString() +
                            filePath.split("/").last() + "-$suffix" + File.separator.toString()
                    extractDir = File(extractPath)
                }

                ArchiveHelper().unpackAPK(extractPath, filePath)
                File(extractPath).walkTopDown().forEach {
                    val malwareFile = clamScan(it.path, scanType)
                    if (malwareFile != null) {
                        // Delete extracted files
                        Utilities.deleteRecursive(File(extractPath))
                        return malwareFile
                    }
                }
                Utilities.deleteRecursive(File(extractPath))
                return null
            }
            // Reference: https://docs.clamav.net/appendix/FileTypes.html#Target-Types
            "PDF" -> {
                targets = arrayOf(0, 10).toList()
            }

            "OLE" -> {
                targets = List(0) { 0 }
            }

            "CLASS" -> {
                targets = arrayOf(0, 12).toList()
            }

            "ELF" -> {
                targets = arrayOf(0, 6).toList()
            }

            "JPG", "GIF", "BNP" -> {
                targets = arrayOf(0, 5).toList()
            }

            "EML" -> {
                targets = arrayOf(0, 4).toList()
            }
        }

        if (scanType == ScanType.HSB || scanType == ScanType.HSB_NDB) {
            val hsbScan = checkHSB(filePath)
            if (hsbScan != null) {
                return Signature(hsbScan.malwareName, hsbScan.hashString, "HSB")
            }
        }

        if (scanType == ScanType.NDB || scanType == ScanType.HSB_NDB) {
            val ndbScan = checkNDB(filePath, targets)
            if (ndbScan != null) {
                return Signature(ndbScan.malwareName, ndbScan.hexSignature, "NDB")
            }
        }

        // No match found
        return null
    }


    /**
     * Check HSB for malware signatures
     * @param filePath: file path to check
     * @return: HSB signature if found, null otherwise
     */
    private fun checkHSB(filePath: String): HSB? {
        // TODO: Handle exception (filePath not exist, access denied?)
        val mDigestSHA256 = MessageDigest.getInstance("sha256")
        val mDigestMD5 = MessageDigest.getInstance("md5")

        val fileToScan = File(filePath)

        if (fileToScan.isDirectory)
            return null

        val md5 = HashUtils.getCheckSumFromFilePath(mDigestMD5, filePath)
        val sha256 = HashUtils.getCheckSumFromFilePath(mDigestSHA256, filePath)

        val resultMd5 =
            HSBRepository.getInstance(context?.applicationContext as Application)
                .getByHashAndFileSize(md5, fileToScan.length())

        if (resultMd5 != null) {
            // Hash file exist in DB
            Log.d(TAG, "MD5 exist: $resultMd5")
            return resultMd5
        }

        val resultSHA =
            HSBRepository.getInstance(context?.applicationContext as Application)
                .getByHashAndFileSize(sha256, fileToScan.length())

        if (resultSHA != null) {
            // Hash file exist in DB
            Log.d(TAG, "SHA256 exist: $resultSHA")
            return resultSHA
        }

        return null
    }

    /**
     * Check if a Regex pattern matches a list
     * @param pattern: Regex pattern to match
     * @param targetString: list to match
     * @return: true if pattern matches list
     */
    private fun hasMatch(pattern: String, targetString: String): Boolean {
        val regex = Regex(pattern)
        return regex.containsMatchIn(targetString)
    }


    /**
     * Check NDB for malware signatures
     * @param filePath: file path to check
     * @param targets: list of target types to check
     * @return: NDB signature if found, null otherwise
     */
    private fun checkNDB(filePath: String, targets: List<Int>): NDB? {
        if (File(filePath).isDirectory)
            return null

        // TODO: Support for larger files
        // Skip files larger than 20MB: 20000000
        if (File(filePath).length() > 10000000) {
            Log.d(TAG, "File is too large to scan")
            return null
        }

        var ndbSigs: MutableList<NDB>? = null
        val ndbRepository = NDBRepository.getInstance(context?.applicationContext as Application)

        if (targets.isEmpty()) {
            ndbSigs = ndbRepository.getAll()
        } else {
            ndbSigs = ndbRepository.getByTargetsTypes(targets)
        }

        if (ndbSigs.isEmpty()) {
            Log.d(TAG, "No signatures found in NDB")
            // TODO: Update DB if no signatures found and show a message to the user
            return null
        }

        val content = File(filePath).readBytes().joinToString("") { "%02x".format(it) }

        val foundSignature = ndbSigs.parallelStream().filter { sig ->
            // Skip signatures that are not supported yet
            // TODO: Add support for more signatures
            if (sig.hexSignature.contains("{") || sig.hexSignature.contains("[")) {
                return@filter false
            }

            var contentToCheck: String = ""
            var newSig = sig.hexSignature.replace("?", ".").replace("*", ".*")

            // Check if newSig contains more than two wildcards
            var segments = listOf<String>()
            if (sig.hexSignature.count { it == '*' } > 1) {
                // Split the signature into segments to avoid catastrophic backtracking
                segments = newSig.split(".*")
            }

            /**
             * Offset: An asterisk or a decimal number n possibly combined with a special modifier:
             *     * = any
             *     n = absolute offset
             *     EOF-n = end of file minus n bytes
             */
            // TODO: refactor this part
            if (sig.offset == "0") {
                newSig = "^$newSig"
            } else if (sig.offset.contains("EOF-")) {
                val offset = sig.offset.split("-")[1].toInt()
                if (offset > 0 && offset < content.length) {
                    contentToCheck = content.substring(content.length - offset, content.length)
                }
            } else if (sig.offset.toIntOrNull() != null && sig.offset.toInt() > 0) {
                if (sig.offset.toInt() >= content.length) {
                    return@filter false
                }
                contentToCheck = content.substring(sig.offset.toInt(), content.length)
            }

            try {
                if (segments.isNotEmpty()) {
                    if (matchSegments(content, segments)) {
                        Log.d(
                            TAG, "Malware detected on: $filePath with signature:" +
                                    " ${sig.hexSignature} and malware name: ${sig.malwareName}"
                        )
                        return@filter true
                    }
                    return@filter false
                }
                if (contentToCheck != "") {
                    if (hasMatch(newSig, contentToCheck)) {
                        Log.d(
                            TAG, "Malware detected on: $filePath with signature:" +
                                    " ${sig.hexSignature} and malware name: ${sig.malwareName}"
                        )
                        return@filter true
                    }
                    return@filter false
                }
                if (hasMatch(newSig, content)) {
                    Log.d(
                        TAG, "Malware detected on: $filePath with signature:" +
                                " ${sig.hexSignature} and malware name: ${sig.malwareName}"
                    )
                    return@filter true
                }

            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }

            return@filter false

        }.findFirst()

        return foundSignature.orElse(null)
    }

    /**
     * Match segments in a string
     * @param target: target string to match
     * @param segments: list of segments to match
     * @return: true if all segments are found in the target string
     */
    private fun matchSegments(target: String, segments: List<String>): Boolean {
        var targetReduced = target
        for (segment in segments) {
            if (!hasMatch(segment, targetReduced))
                return false
            targetReduced = targetReduced.substring(
                targetReduced.indexOf(segment),
                targetReduced.length
            )
        }
        return true
    }
}