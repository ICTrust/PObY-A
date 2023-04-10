package ch.ictrust.pobya.utillies

import android.content.Context
import android.content.SharedPreferences

class Prefs private constructor(context: Context) {

    private var mPrefs: SharedPreferences? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val malwareDatabaseURL = "https://codeberg.org/ICTrust/mal-db/raw/branch/main/db-1.json"
    private val malwareDatabaseVersionURL = "https://codeberg.org/ICTrust/mal-db/raw/branch/main/version"

    var isFirstRun: Boolean?
        get() = mPrefs?.getBoolean(IS_FIRST_RUN, true)
        set(isFirstRun) = mPrefs?.edit()?.putBoolean(IS_FIRST_RUN, isFirstRun!!)!!.apply()

    var malwareDbVersion : Int?
        get() = mPrefs?.getInt(MALWARE_DB_VERSION, 0)
        set(malwareDbVersion) = mPrefs?.edit()?.putInt(MALWARE_DB_VERSION, malwareDbVersion!!)!!.apply()

    var malwareDatabaseUrl : String?
        get() = mPrefs?.getString(REMOTE_DATABASE_URL, malwareDatabaseURL)
        set(databaseVersion) = mPrefs?.edit()?.putString(REMOTE_DATABASE_URL, databaseVersion!!)!!.apply()


    var malwareVersionDatabaseUrl : String?
        get() = mPrefs?.getString(DATABASE_VERSION_URL, malwareDatabaseVersionURL)
        set(databaseVersion) = mPrefs?.edit()?.putString(DATABASE_VERSION_URL, databaseVersion!!)!!.apply()



    companion object {
        internal const val DATABASE_VERSION = 100
        internal const val DATABASE_NAME = "Poby-a-dev"

        private const val PREFS_NAME = "Settings"
        private const val IS_FIRST_RUN = "isFirstRun"

        private const val REMOTE_DATABASE_URL = "remoteDatabase"
        private const val DATABASE_VERSION_URL = "malwareDatabaseVersion"
        private const val MALWARE_DB_VERSION = "malwareDbVersion"

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