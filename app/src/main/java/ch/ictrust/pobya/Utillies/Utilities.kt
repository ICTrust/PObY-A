package ch.ictrust.pobya.Utillies

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*


object Utilities {

    fun toDateString(date: Date) : String {
        val format = SimpleDateFormat("MMM dd yyyy")
        return format.format(date)
    }

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && context != null && permissions != null
        ) {

            for (permission in permissions) {

                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {

                    return false
                }
            }
        }

        return true
    }

}