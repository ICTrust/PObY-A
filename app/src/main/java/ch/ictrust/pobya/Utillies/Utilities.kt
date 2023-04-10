package ch.ictrust.pobya.utillies

import android.app.Application
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.app.ActivityCompat
import ch.ictrust.pobya.models.Malware
import ch.ictrust.pobya.repository.MalwareRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request


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
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }

        return true
    }

    fun updateMalwareDB(context: Context) {
        var malwarePackages : List<Malware> = ArrayList()
        var currentMalwareDbVersion = Prefs.getInstance(context)?.malwareDbVersion
        dbScope.launch {
            val client = OkHttpClient()
            var version = 0

            val versionRequest = Request.Builder()
                .url(Prefs.getInstance(context)?.malwareVersionDatabaseUrl.toString())
                .build()

            var response = client.newCall(versionRequest).execute()
            if (response.isSuccessful) {
                version = response.body?.string()?.toInt()!!
                if (version == currentMalwareDbVersion)
                    return@launch
            }

            val retrieveNewDB = Request.Builder()
                .url(Prefs.getInstance(context)?.malwareDatabaseUrl.toString())
                .build()

            response = client.newCall(retrieveNewDB).execute()

            if (response.isSuccessful) {
                val jsonString = response.body?.string()
                malwarePackages = Gson().fromJson(jsonString, Array<Malware>::class.java).asList()

                MalwareRepository.getInstance(context.applicationContext as Application).insertList(malwarePackages)
                Prefs.getInstance(context)!!.malwareDbVersion = version

            }
        }
    }

    fun hasInternetConnection(context: Context): Boolean {
        val connectivityManager =
            context.applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

        if(connectivityManager.activeNetworkInfo != null){
            val network: Network? = connectivityManager.activeNetwork
            val capabilities = connectivityManager
                .getNetworkCapabilities(network)
            return (capabilities != null
                    && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED))
        }
        return false
    }

}