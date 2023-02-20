package ch.ictrust.pobya.Utillies

import android.content.Context
import android.content.SharedPreferences

class Prefs private constructor(context: Context) {


    private var mPrefs: SharedPreferences? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)


    var isFirstRun: Boolean?
        get() = mPrefs?.getBoolean(ISFIRSTRUN, true)
        set(isFirstRun) = mPrefs?.edit()?.putBoolean(ISFIRSTRUN, isFirstRun!!)!!.apply()



    companion object {

        private val PREFS_NAME = "Settings"
        private val ISFIRSTRUN = "isFirstRun"

        private var instance: Prefs? = null

        fun getInstance(context: Context): Prefs? {
            if (instance == null) {
                synchronized(Prefs::class.java) {
                    if (instance == null) {
                        instance = Prefs(context.applicationContext)
                    }
                }
            }
            return instance
        }
    }


}