package ch.ictrust.pobya.utillies

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job


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

    fun haveInternetConnection(context: Context): Boolean {
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