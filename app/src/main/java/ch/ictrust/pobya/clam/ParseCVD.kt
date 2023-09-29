package ch.ictrust.pobya.clam

import android.app.Application
import android.content.Context
import android.util.Log
import ch.ictrust.pobya.clam.models.ClamDbType
import ch.ictrust.pobya.clam.models.ClamVersion
import ch.ictrust.pobya.clam.models.HSB
import ch.ictrust.pobya.clam.repositroy.ClamVersionRepository
import ch.ictrust.pobya.clam.repositroy.HSBRepository
import ch.ictrust.pobya.utillies.Utilities
import org.apache.commons.io.FileUtils
import org.apache.commons.io.LineIterator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ParseCVD(var context: Context) {
    private val TAG = "ParseCVD"

    /**
     * Parse information header of CVD file and insert on the database.
     *
     * @param path
     * path of the CVD file
     * @param type
     * ClamDbType of CVD file: ClamDbType.MAIN, ClamDbType.DAILY or ClamDbType.BYTECODE
     */
    fun parseInfoCVD(path: String, type: ClamDbType) {
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
        val dateFormater = SimpleDateFormat("dd MMM yyyy hh-mm Z", Locale.ENGLISH)
        val clamVersion = ClamVersion(
            version = clamVersionFields[2].toLong(),
            dbType = type,
            buildTime = dateFormater.parse(clamVersionFields[1]).time,
            nbrSignatures = clamVersionFields[3].toLong(),
            hashMD5 = clamVersionFields[5],
            digitalSignature = clamVersionFields[6],
            updateDate = Date().time
        )
        ClamVersionRepository.getInstance(context as Application).insert(clamVersion)
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

        return dbHSB
    }

}