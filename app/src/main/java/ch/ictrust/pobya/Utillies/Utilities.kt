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

import android.app.Application
import android.content.Context
import android.icu.lang.UCharacter
import android.util.Log
import ch.ictrust.pobya.R
import ch.ictrust.pobya.cvd.ParseCVD
import ch.ictrust.pobya.cvd.models.CVDType
import ch.ictrust.pobya.cvd.repositroy.CVDVersionRepository
import ch.ictrust.pobya.models.Malware
import ch.ictrust.pobya.models.MalwareCert
import ch.ictrust.pobya.models.SysSettings
import ch.ictrust.pobya.repository.MalwareCertRepository
import ch.ictrust.pobya.repository.MalwareRepository
import ch.ictrust.pobya.repository.SysSettingsRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

data class MalwareResponse(
    val certs: List<MalwareCert>,
    val malware: List<Malware>,
    val version: List<Version>
)

data class VersionCVDResponse(
    val daily: Int,
    val main: Int
)

data class Version(
    val latest_version: Int
)

object Utilities {


    private const val TAG = "Utilities"

    private val _dbUpdateStatus = MutableStateFlow("Idle")

    var dbJob = Job()
    var dbScope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext + dbJob)

    var scanAppJob = Job()
    var scanAppScope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext + scanAppJob)

    var populateDBJob = Job()
    var populateScope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext + populateDBJob)


    /**
     * Update PObY-A malware Database
     *
     * @param context
     * Application context
     */
    fun updateMalwareDB(context: Context): Boolean {
        _dbUpdateStatus.value = context.getString(R.string.updating)
        updatePObYADB(context)
        updateCVD(context, CVDType.MAIN)
        updateCVD(context, CVDType.DAILY)

        return _dbUpdateStatus.value == context.getString(R.string.success)
    }


    private fun updatePObYADB(context: Context, testedURL: Int = 1) {
        var malwarePackages: List<Malware>
        var malwareCerts: List<MalwareCert>
        val currentMalwareDbVersion = Prefs.getInstance(context)?.pobyDbVersion
        var response: Response? = null
        dbScope.launch {
            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            var version = 0
            val baseURL = Prefs.getURLs().first()

            val param: String

            val malwareListSize = MalwareRepository.getInstance(context.applicationContext as Application).getAllMalware().size

            if (currentMalwareDbVersion == 0 || malwareListSize == 0) {
                param = "?fresh=true"
            } else {
                param = "?version=$currentMalwareDbVersion"
            }

            val retrieveNewMalwareDB = Request.Builder()
                .url(baseURL + param)
                .build()
            try {
                response = client.newCall(retrieveNewMalwareDB).execute()
                if (response!!.isSuccessful) {
                    val jsonString = response!!.body?.string()
                    malwarePackages =
                        Gson().fromJson(jsonString, MalwareResponse::class.java).malware
                    version = Gson().fromJson(
                        jsonString,
                        MalwareResponse::class.java
                    ).version[0].latest_version
                    malwareCerts = Gson().fromJson(jsonString, MalwareResponse::class.java).certs

                    MalwareRepository.getInstance(context.applicationContext as Application)
                        .insertList(malwarePackages)
                    MalwareCertRepository.getInstance(
                        context.applicationContext as Application
                    ).insertList(malwareCerts)

                    Prefs.getInstance(context)?.pobyDbVersion = version

                    MalwareRepository.getInstance(context.applicationContext as Application)
                        .deleteDeprecated()
                    MalwareCertRepository.getInstance(context.applicationContext as Application)
                        .deleteDeprecated()
                }
                _dbUpdateStatus.value = context.getString(R.string.success)
            } catch (ex: Exception) {
                Log.e(TAG, ex.message.toString())
                Prefs.getInstance(context)?.pobyDbVersion = 1

                if (testedURL > Prefs.URLs.size) {
                    _dbUpdateStatus.value = context.getString(R.string.failed)
                    return@launch
                } else {
                    _dbUpdateStatus.value = "Retry"
                    val indexURL = Prefs.getURLs()
                        .filter { it != Prefs.getInstance(context)!!.baseURL }

                    withContext(Dispatchers.Main) {
                        Prefs.getInstance(context)!!.mPrefs?.edit()
                            ?.putString(Prefs.BASE_URL, indexURL[0])!!.apply()
                    }
                    Log.i(TAG, "Switching URL to: " + Prefs.getInstance(context)?.baseURL)
                    updatePObYADB(context, testedURL + 1)
                }
            } finally {
                response?.close()
            }
        }
    }


    /**
     * Update CVD (ClamAV Virus Database) file
     *
     * @param context
     * Application context
     * @param type
     * CVDType of CVD file: CVDType.MAIN, CVDType.DAILY or CVDType.BYTECODE
     *
     */
    fun updateCVD(context: Context, type: CVDType) {
        _dbUpdateStatus.value = context.getString(R.string.updating)
        val internalFolderPath = context.filesDir.path
        val client = OkHttpClient()

        val requestVersion = Request.Builder()
            .url(Prefs.getInstance(context)?.baseClamURL!! + "versions")
            .build()

        val requestDlCVD = Request.Builder()
            .url(Prefs.getInstance(context)?.baseClamURL!! + type.value)
            .header("User-Agent", "CVDUPDATE/0")
            .build()

        val query = client.newCall(requestDlCVD)
        var currentCVDversion = 0L


        val pathExtractedCVD = context.filesDir.path + "/" +
                type.value.split(".")[0]

        val pathFileCVD =
            context.filesDir.path + File.separator.toString() + type.value

        val countDownLatch = CountDownLatch(1)

        try {
            currentCVDversion = CVDVersionRepository.getInstance(context as Application)
                .getLast(type).version
        } catch (ex: Exception) {
            Log.e(TAG, "No version found in db")
        }

        try {
            val responseVersion = client.newCall(requestVersion).execute()
            if (responseVersion.isSuccessful) {
                val jsonString = responseVersion.body?.string()
                val version = Gson().fromJson(jsonString, VersionCVDResponse::class.java)
                    .let {
                        if (type == CVDType.MAIN) {
                            it.main
                        } else {
                            it.daily
                        }
                    }
                if (currentCVDversion.toInt() == version) {
                    Log.i(TAG, " CVD is ${type} up to date")
                    return
                }
            }

            client.newCall(requestDlCVD).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Failed to connect: " + e.message)
                    countDownLatch.countDown()
                    client.connectionPool.evictAll()
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.code == 200) {
                        Log.i(TAG, " CVD downloaded successfully")
                        val cvdFile = File(internalFolderPath, type.value)
                        val sink: BufferedSink = cvdFile.sink().buffer()
                        val source = response.body?.source()
                        while (!source?.exhausted()!!) {
                            sink.write(source.buffer.readByteString())
                            sink.flush()
                        }

                        val versionHeader =
                            cvdFile.bufferedReader().use { it.readLine().split(":")[3] }

                        if (!versionHeader.equals(currentCVDversion.toString())) {
                            val pathHSB =
                                "${context.filesDir.path}/${type.value.split('.')[0]}/${
                                    type.value.split('.')[0]
                                }.hsb"
                            val pathHDB =
                                "${context.filesDir.path}/${type.value.split('.')[0]}/${
                                    type.value.split('.')[0]
                                }.hdb"
                            val pathHDU =
                                "${context.filesDir.path}/${type.value.split('.')[0]}/${
                                    type.value.split('.')[0]
                                }.hdu"
                            val pathHSU =
                                "${context.filesDir.path}/${type.value.split('.')[0]}/${
                                    type.value.split('.')[0]
                                }.hsu"
                            val pathNDB =
                                "${context.filesDir.path}/${type.value.split('.')[0]}/${
                                    type.value.split('.')[0]
                                }.ndb"
                            val pathInfo =
                                "${context.filesDir.path}/${type.value.split(".")[0]}/${
                                    type.value.split('.')[0]
                                }.info"

                            ArchiveHelper().extractCVD(pathExtractedCVD, pathFileCVD)

                            ParseCVD(context).parseInfoCVD(pathInfo, type)
                            try {
                                ParseCVD(context).parseHSB(pathHSB)
                            } catch (ex: Exception) {
                                Log.e(TAG, ex.message.toString())
                            }
                            try {
                                ParseCVD(context).parseNDB(pathNDB)
                            } catch (ex: Exception) {
                                Log.e(TAG, ex.message.toString())
                            }
                            try {
                                ParseCVD(context).parseHSB(pathHDB)
                            } catch (ex: Exception) {
                                Log.e(TAG, ex.message.toString())
                            }
                            try {
                                ParseCVD(context).parseHSB(pathHSU)
                            } catch (ex: Exception) {
                                Log.e(TAG, ex.message.toString())
                            }

                        } else {
                            Log.i(TAG, " CVD is up to date")
                        }
                        sink.close()
                    } else {
                        Log.e(TAG, "Failed to download CVD: " + response.code)
                    }

                    response.close()
                    countDownLatch.countDown()
                }
            })
            countDownLatch.await()

        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            _dbUpdateStatus.value = context.getString(R.string.failed)
            ex.printStackTrace().toString()
        } finally {
            query.cancel()
            client.connectionPool.evictAll()
        }
        // Remove temporary files
        deleteFilesInDir(pathExtractedCVD)
    }

    fun readFile(filePath: String): ArrayList<String> {
        val inputStream: InputStream
        var lines = ArrayList<String>()
        try {
            inputStream = File(filePath).inputStream()
            lines = inputStream.bufferedReader().readLines() as ArrayList<String>
            inputStream.close()
        } catch (ex: FileNotFoundException) {
            Log.e(TAG, "File $filePath not found")
        }
        return lines
    }


    fun ByteArray.toHex(): String =
        UCharacter.toLowerCase(joinToString(separator = "") { eachByte -> "%02x".format(eachByte) })
            .drop(8)


    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) deleteRecursive(
            child
        )
        fileOrDirectory.delete()
    }

    fun insertSettingsScope(context: Context, sysSettings: ArrayList<SysSettings>) {
        dbScope.launch {
            val sysSettingsRepository = SysSettingsRepository(context)
            sysSettingsRepository.insertAll(sysSettings)

        }
    }

    fun deleteFilesInDir(dirPath: String) {
        val dir = File(dirPath)
        if (dir.exists()) {
            val contents = dir.listFiles()
            if (contents != null) {
                for (content in contents) {
                    if (content.isDirectory) {
                        deleteFilesInDir(content.absolutePath)
                    } else {
                        content.delete()
                    }
                }
            }
        }
    }
}