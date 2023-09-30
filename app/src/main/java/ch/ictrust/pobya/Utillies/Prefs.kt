package ch.ictrust.pobya.utillies

import android.content.Context
import android.content.SharedPreferences

class Prefs private constructor(context: Context) {

    var mPrefs: SharedPreferences? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val malwareDatabaseURL =
        "ICTrust/mal-db/raw/branch/main/malware.json"
    private val certsDatabaseURL =
        "ICTrust/mal-db/raw/branch/main/malware_cert.json"
    private val malwareDatabaseVersionURL =
        "ICTrust/mal-db/raw/branch/main/version"

    var isFirstRun: Boolean?
        get() = mPrefs?.getBoolean(IS_FIRST_RUN, true)
        set(isFirstRun) = mPrefs?.edit()?.putBoolean(IS_FIRST_RUN, isFirstRun!!)!!.apply()

    var baseURL : String?
        get() = mPrefs?.getString(BASE_URL, malDbURLs[0])
        set(baseURL) = mPrefs?.edit()?.putString(BASE_URL, baseURL!!)!!.apply()

    var malwareDbVersion: Int?
        get() = mPrefs?.getInt(MALWARE_DB_VERSION, 0)
        set(malwareDbVersion) = mPrefs?.edit()?.putInt(MALWARE_DB_VERSION, malwareDbVersion!!)!!
            .apply()

    var malwareDatabaseUrlPrefs: String?
        get() = mPrefs?.getString(REMOTE_DATABASE_URL, malwareDatabaseURL)
        set(malwareDatabaseUrlPrefs) = mPrefs?.edit()
            ?.putString(REMOTE_DATABASE_URL, malwareDatabaseUrlPrefs!!)!!.apply()


    var certsDatabaseURLPrefs: String?
        get() = mPrefs?.getString(REMOTE_CERTS_DATABASE_URL, certsDatabaseURL)
        set(certsDatabaseURLPrefs) = mPrefs?.edit()
            ?.putString(REMOTE_CERTS_DATABASE_URL, certsDatabaseURLPrefs!!)!!.apply()


    var malwareVersionDatabaseUrl: String?
        get() = mPrefs?.getString(DATABASE_VERSION_URL, malwareDatabaseVersionURL)
        set(malwareDatabaseVersionURL) = mPrefs?.edit()
            ?.putString(DATABASE_VERSION_URL, malwareDatabaseVersionURL!!)!!.apply()


    var monitoringServiceStatus: Boolean?
        get() = mPrefs?.getBoolean(MONITORING_SERVICE_ENABLED, true)
        set(monitoringServiceStatus) = mPrefs?.edit()
            ?.putBoolean(MONITORING_SERVICE_ENABLED, monitoringServiceStatus!!)!!.apply()

    // TODO: enable at startup
    var autoStartEnabled: Boolean?
        get() = mPrefs?.getBoolean(AUTO_START_ENABLED, true)
        set(autoStartEnabled) = mPrefs?.edit()
            ?.putBoolean(AUTO_START_ENABLED, autoStartEnabled!!)!!.apply()


    var enableSysAppScan: Boolean?
        get() = mPrefs?.getBoolean(ENABLE_SYS_APPS_SCAN, false)
        set(enableSysAppScan) = mPrefs?.edit()
            ?.putBoolean(ENABLE_SYS_APPS_SCAN, enableSysAppScan!!)!!.apply()


    var baseClamURL : String?
        get() = mPrefs?.getString(BASE_CLAM_DB_URL, clamDbURLs[0])
        set(baseClamURL) = mPrefs?.edit()?.putString(BASE_CLAM_DB_URL, baseClamURL!!)!!.apply()

    companion object {

        internal val malDbURLs = arrayOf("https://codeberg.org/", "https://github.com/")
        // A mirror is created but not yet ready for production
        // https://database.clamav.net/ is for testing purpose
        internal val clamDbURLs = arrayOf("https://database.clamav.net/")


        internal const val DATABASE_VERSION = 149
        internal const val DATABASE_NAME = "Poby-a"

        private const val PREFS_NAME = "Settings"
        internal const val IS_FIRST_RUN = "isFirstRun"

        internal const val BASE_URL = "baseURL"
        internal const val BASE_CLAM_DB_URL = "clamURL"

        internal const val REMOTE_DATABASE_URL = "remoteDatabase"
        internal const val REMOTE_CERTS_DATABASE_URL = "remoteCertsDatabase"

        internal const val DATABASE_VERSION_URL = "malwareDatabaseVersion"
        internal const val MALWARE_DB_VERSION = "malwareDbVersion"

        internal const val MONITORING_SERVICE_ENABLED = "monitoringServiceEnabled"
        internal const val AUTO_START_ENABLED = "autoStart"
        internal const val ENABLE_SYS_APPS_SCAN = "enableSysAppScan"

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

        fun getMalDbURLs(): Array<String> {
            return malDbURLs
        }

        fun getClamDbURLs(): Array<String> {
            return clamDbURLs
        }
    }
}