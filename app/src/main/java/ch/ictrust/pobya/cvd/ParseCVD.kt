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
import android.util.Log
import ch.ictrust.pobya.cvd.models.CVDType
import ch.ictrust.pobya.cvd.models.CVDVersion
import ch.ictrust.pobya.cvd.models.HSB
import ch.ictrust.pobya.cvd.models.NDB
import ch.ictrust.pobya.cvd.repositroy.CVDVersionRepository
import ch.ictrust.pobya.cvd.repositroy.HSBRepository
import ch.ictrust.pobya.cvd.repositroy.NDBRepository
import ch.ictrust.pobya.utillies.Utilities
import org.apache.commons.io.FileUtils
import org.apache.commons.io.LineIterator
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ParseCVD(var context: Context) {
    private val TAG = "ParseCVD"

    /**
     * Parse information header of CVD file and insert on the database.
     *
     * @param path
     * path of the CVD file
     * @param type
     * CVDType of CVD file: CVDType.MAIN, CVDType.DAILY or CVDType.BYTECODE
     */
    fun parseInfoCVD(path: String, type: CVDType) {
        /**
         * CVD Header (512 bytes long):
         *  ClamAV-VDB:buildTime:version:numberSignatures:MD5:digitalSignature
         *  where:
         *      buildTime is date in format : <dd MMM yyyy hh-mm Z>
         *      digitalSignature is base64 encoded
         *      MD5 is the hash of the extracted tar.gz archive from the CVD file
         */

        var entries = Utilities.readFile(path)
        val clamVersionFields = entries.first().split(":")
        // Clear entries : free-up memory
        entries.clear()
        // "dd MMM yyyy hh-mm Z" : Date format on the CVD file
        val dateFormatter = SimpleDateFormat("dd MMM yyyy hh-mm Z", Locale.ENGLISH)
        val CVDVersion = dateFormatter.parse(clamVersionFields[1])?.let {
            CVDVersion(
                version = clamVersionFields[2].toLong(),
                dbType = type,
                buildTime = it.time,
                nbrSignatures = clamVersionFields[3].toLong(),
                hashMD5 = clamVersionFields[5],
                digitalSignature = clamVersionFields[6],
                updateDate = Date().time
            )
        }
        CVDVersion?.let { CVDVersionRepository.getInstance(context as Application).insert(it) }
    }

    /**
     * Parse HSB file and import entries to DB HSB signatures table
     *
     * @param path
     * path of the HSB file
     * @return: list of HSB signatures read from .hsb file
     */
    fun parseHSB(path: String): List<HSB> {
        // Format: HashString:FileSize:MalwareName:FuncLevelSpec
        // TODO: Improve performance
        // TODO: Handle Exceptions
        var dbHSB = ArrayList<HSB>()
        val it: LineIterator = FileUtils.lineIterator(File(path), "UTF-8")

        Log.d(TAG, "Parsing HSB file ...")

        try {
            while (it.hasNext()) {
                val line: String = it.nextLine()
                val fields = line.split(':')
                if (fields[1] == "*") {
                    dbHSB.add(HSB(fields[0], -1, fields[2], 2))
                } else {
                    dbHSB.add(HSB(fields[0], fields[1].toLong(), fields[2], 2))
                }
                // Avoid using memory to store all data: at 400 entries, insert in DB and free dbHSB
                if (dbHSB.size % 400 == 0) {
                    HSBRepository.getInstance(context as Application).insertList(dbHSB)
                    dbHSB = ArrayList()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            ex.printStackTrace()
        } finally {
            LineIterator.closeQuietly(it)
        }
        // if there are still entries in dbHSB, insert in the DB
        HSBRepository.getInstance(context as Application).insertList(dbHSB)
        Log.d(TAG, "HSB file parsed successfully")
        return dbHSB
    }

    fun parseNDB(path: String): List<NDB> {
        Log.d(TAG, "Parsing NDB file ...")
        // Format: MalwareName:TargetType:Offset:HexSignature[:min_flevel:[max_flevel]]
        var dbNDB = ArrayList<NDB>()
        val it: LineIterator = FileUtils.lineIterator(File(path), "UTF-8")
        try {
            while (it.hasNext()) {
                val line: String = it.nextLine()
                // Skip comments and other lines
                if (line.startsWith("#") || line.startsWith("Win") ||
                    line.startsWith("Osx") || line.startsWith("Php") ||
                    line.startsWith("Py")
                ) {
                    continue
                }

                val fields = line.split(':')
                if (fields.size >= 4) {
                    dbNDB.add(NDB(fields[0], fields[1].toInt(), fields[2], fields[3]))
                }

                // Avoid using memory to store all data: at 400 entries, insert in DB and free dbNDB
                if (dbNDB.size % 400 == 0) {
                    NDBRepository.getInstance(context as Application).insertList(dbNDB)
                    dbNDB = ArrayList()
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            ex.printStackTrace()
        } finally {
            LineIterator.closeQuietly(it)
            it.close()
        }
        // if there are still entries in dbNDB, insert in the DB
        NDBRepository.getInstance(context as Application).insertList(dbNDB)
        Log.d(TAG, "NDB file parsed successfully")
        return dbNDB

    }
}