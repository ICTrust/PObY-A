package ch.ictrust.pobya.utillies

import android.app.Application
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.icu.lang.UCharacter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.app.ActivityCompat
import ch.ictrust.pobya.clam.ParseCVD
import ch.ictrust.pobya.clam.models.ClamDbType
import ch.ictrust.pobya.models.Malware
import ch.ictrust.pobya.models.MalwareCert
import ch.ictrust.pobya.clam.repositroy.ClamVersionRepository
import ch.ictrust.pobya.repository.MalwareCertRepository
import ch.ictrust.pobya.repository.MalwareRepository
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.*
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


object Utilities {

    var dbJob = Job()
    var dbScope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext + dbJob)
    private const val TAG = "Utilities"

    var populateDBJob = Job()
    var populateScope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext + populateDBJob)

    /**
     * Check user permission
     *
     * @param context
     * Application context
     * @param permissions
     * List of permissions
     * @return true if has permission
     */
    fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * Update PObY-A malware DB
     *
     * @param context
     * Application context
     * @param testedURLs
     * Number of tested URLs (codeberg, github)
     */
    fun updateMalwareDB(context: Context, testedURLs: Int = 1) {
        var malwarePackages: List<Malware>
        var malwareCerts: List<MalwareCert>
        var response: Response
        val currentMalwareDbVersion = Prefs.getInstance(context)?.malwareDbVersion
        dbScope.launch {

            val client = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()
            var version = 0
            val baseURL = Prefs.getInstance(context)?.baseURL

            val versionRequest = Request.Builder()
                .url(baseURL + Prefs.getInstance(context)?.malwareVersionDatabaseUrl.toString())
                .build()

            val retrieveNewMalwareDB = Request.Builder()
                .url(baseURL + Prefs.getInstance(context)?.malwareDatabaseUrlPrefs.toString())
                .build()

            try {
                response = client.newCall(versionRequest).execute()
                println(response)
                if (response.isSuccessful) {
                    version = response.body?.string()?.strip()?.toInt()!!
                    val currentDB = MalwareRepository.getInstance(context as Application).getAllMalware()
                    if (version == currentMalwareDbVersion && currentDB != null && currentDB.size > 0) {
                        Log.i("Utilities", "Database up-to-date")
                        response.body?.close()
                        response.close()
                        return@launch
                    }
                    Prefs.getInstance(context)?.malwareDbVersion = version
                } else {
                    throw Exception("Retry")
                }

                response = client.newCall(retrieveNewMalwareDB).execute()
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    malwarePackages =
                        Gson().fromJson(jsonString, Array<Malware>::class.java).asList()

                    MalwareRepository.getInstance(context.applicationContext as Application)
                        .insertList(malwarePackages)
                    Prefs.getInstance(context)?.malwareDbVersion = version
                }
                val retrieveNewMalwareCertDB = Request.Builder()
                    .url(baseURL + Prefs.getInstance(context)?.certsDatabaseURLPrefs.toString())
                    .build()

                response = client.newCall(retrieveNewMalwareCertDB).execute()
                if (response.isSuccessful) {
                    val jsonString = response.body?.string()
                    malwareCerts =
                        Gson().fromJson(jsonString, Array<MalwareCert>::class.java).asList()
                    MalwareCertRepository.getInstance(
                        context.applicationContext as Application
                    ).insertList(malwareCerts)
                }

            } catch (ex: Exception) {
                Log.e("Utilities", ex.message.toString())
                // TODO: Remove hadcoded value (2)
                if (testedURLs > 2) {
                    //TODO: Show error message after trying all URLs
                    return@launch
                } else {
                    val indexURL = Prefs.getMalDbURLs()
                        .filter { it != Prefs.getInstance(context)!!.baseURL }

                    withContext(Dispatchers.Main) {
                        Prefs.getInstance(context)!!.mPrefs?.edit()
                            ?.putString(Prefs.BASE_URL, indexURL[0])!!.apply()
                    }
                    Log.i(
                        "Utilities", "Switching database URL to: " +
                                Prefs.getInstance(context)?.baseURL
                    )
                    updateMalwareDB(context, testedURLs + 1)
                }
            }
        }
    }

    /**
     * Check if device has internet connection
     *
     * @param context
     * Application context
     * @param testedURLs
     * Number of tested URLs (codeberg, github)
     * @return true if device has internet connection
     */
    fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivityManager.activeNetworkInfo != null) {
            val network: Network? = connectivityManager.activeNetwork
            val capabilities = connectivityManager
                .getNetworkCapabilities(network)
            return (capabilities != null
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
        }
        return false
    }


    /**
     * Download CVD (ClamAV Virus Database) file
     *
     * @param context
     * Application context
     * @param type
     * ClamDbType of CVD file: ClamDbType.MAIN, ClamDbType.DAILY or ClamDbType.BYTECODE
     */
    fun downloadCVD(context: Context, type: ClamDbType) {
        val internalFolderPath = context.filesDir.path
        val client = OkHttpClient()

        // TODO: create a mirror
        val mainCVDRequest = Request.Builder()
            .url(Prefs.getInstance(context)?.baseClamURL!! + type.value)
            .header("User-Agent", "CVDUPDATE/0")
            .build()

        val query = client.newCall(mainCVDRequest)

        val lastClamDBVersion = try {
            ClamVersionRepository.getInstance(context as Application).getLast().version
        } catch (ex: Exception) {
            Log.e(TAG, "No version found in db")
            -1L
        }

        val countDownLatch = CountDownLatch(1)

        try {
            client.newCall(mainCVDRequest).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "Failed to connect: " + e.message)
                    countDownLatch.countDown()
                }

                override fun onResponse(call: Call, response: Response) {


                            if (response.code == 200) {
                                val cvdFile = File(internalFolderPath, type.value)
                                val sink: BufferedSink = cvdFile.sink().buffer()
                                val source = response.body?.source()
                                while (!source?.exhausted()!!) {
                                    sink.write(source.buffer.readByteString())
                                    sink.flush()
                                }
                                /*sink.writeAll(response.body!!.source())
                                sink.flush()*/

                                val versionHeader = cvdFile.readLines()[0].split(":")[3]

                                if (!versionHeader.equals(lastClamDBVersion)) {
                                    val pathExtractedCVD = context.filesDir.path + "/" +
                                            type.value.split(".")[0]
                                    val pathFileCVD = context.filesDir.path + File.separator.toString() + type.value
                                    val pathHSB =
                                        "${context.filesDir.path}/${type.value.split('.')[0]}/${
                                            type.value.split('.')[0]
                                        }.hsb"
                                    val pathInfo =
                                        "${context.filesDir.path}/${type.value.split(".")[0]}/${
                                            type.value.split('.')[0]
                                        }.info"

                                    ArchiveHelper().extractCVD(pathExtractedCVD, pathFileCVD)

                                    ParseCVD(context).parseInfoCVD(pathInfo, type)
                                    ParseCVD(context).parseHSB(pathHSB)

                                } else {
                                    Log.i(TAG, "Clam DB is up to date")
                                }
                            }
                            countDownLatch.countDown()
                        }


            })
            countDownLatch.await()

        } catch (ex: Exception) {
            Log.e(TAG, ex.message.toString())
            ex.printStackTrace().toString()
        } finally {
            query.cancel()
        }

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

}