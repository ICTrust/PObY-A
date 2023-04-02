package ch.ictrust.pobya.utillies

import android.content.Context
import android.content.SharedPreferences

class Prefs private constructor(context: Context) {

    private var mPrefs: SharedPreferences? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isFirstRun: Boolean?
        get() = mPrefs?.getBoolean(IS_FIRST_RUN, true)
        set(isFirstRun) = mPrefs?.edit()?.putBoolean(IS_FIRST_RUN, isFirstRun!!)!!.apply()


    companion object {
        private const val PREFS_NAME = "Settings"
        private const val IS_FIRST_RUN = "isFirstRun"
        internal const val DATABASE_NAME = "Poby-a-dev"
        internal const val DATABASE_VERSION = 111
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