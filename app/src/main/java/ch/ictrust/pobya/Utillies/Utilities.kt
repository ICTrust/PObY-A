package ch.ictrust.pobya.utillies

import android.app.Application
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import androidx.core.app.ActivityCompat
import ch.ictrust.pobya.models.Malware
import ch.ictrust.pobya.models.MalwareCert
import ch.ictrust.pobya.repository.MalwareCertRepository
import ch.ictrust.pobya.repository.MalwareRepository
import com.google.gson.Gson
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit


object Utilities {

    var dbJob = Job()
    var dbScope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext + dbJob)

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

    fun updateMalwareDB(context: Context, testedURL: Int = 1) {
        var malwarePackages: List<Malware> = ArrayList()
        var malwareCerts: List<MalwareCert> = ArrayList()
        var currentMalwareDbVersion = Prefs.getInstance(context)?.malwareDbVersion
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
                var response = client.newCall(versionRequest).execute()
                if (response.isSuccessful) {
                    version = response.body?.string()?.toInt()!!
                    if (version == currentMalwareDbVersion) {
                        Log.i("Utilities", "Database up-to-date")
                        return@launch
                    }
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
            } catch (ex: Exception ) {
                Log.e("Utilities", ex.message.toString())
                // TODO: Remove hadcoded value (2)
                if (testedURL > 2 ) {
                    //TODO: Show error message after trying all URLs
                    return@launch
                } else {
                    println(Prefs.getURLs())
                    val indexURL = Prefs.getURLs()
                        .filter { it != Prefs.getInstance(context)!!.baseURL }

                    withContext(Dispatchers.Main) {
                        Prefs.getInstance(context)!!.mPrefs?.edit()
                            ?.putString(Prefs.BASE_URL, indexURL[0])!!.apply()
                    }
                    Log.i("Utilities", "Switching URL to: " +
                                Prefs.getInstance(context)?.baseURL)
                    updateMalwareDB(context, testedURL+1)
                }
            }
        }
    }
    
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

}