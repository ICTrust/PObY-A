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

import android.content.Context
import android.content.SharedPreferences

class Prefs private constructor(context: Context) {

    var mPrefs: SharedPreferences? =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isFirstRun: Boolean?
        get() = mPrefs?.getBoolean(IS_FIRST_RUN, true)
        set(isFirstRun) = mPrefs?.edit()?.putBoolean(IS_FIRST_RUN, isFirstRun!!)!!.apply()

    var baseURL: String?
        get() = mPrefs?.getString(BASE_URL, URLs[0])
        set(baseURL) = mPrefs?.edit()?.putString(BASE_URL, baseURL!!)!!.apply()

    var pobyDbVersion: Int?
        get() = mPrefs?.getInt(POBY_DB_VERSION, 0)
        set(malwareDbVersion) = mPrefs?.edit()?.putInt(POBY_DB_VERSION, malwareDbVersion!!)!!
            .apply()


    var monitoringServiceStatus: Boolean?
        get() = mPrefs?.getBoolean(MONITORING_SERVICE_ENABLED, true)
        set(monitoringServiceStatus) = mPrefs?.edit()
            ?.putBoolean(MONITORING_SERVICE_ENABLED, monitoringServiceStatus!!)!!.apply()


    var enableSysAppScan: Boolean?
        get() = mPrefs?.getBoolean(ENABLE_SYS_APPS_SCAN, false)
        set(enableSysAppScan) = mPrefs?.edit()
            ?.putBoolean(ENABLE_SYS_APPS_SCAN, enableSysAppScan!!)!!.apply()


    var deepScanEnabled: Boolean?
        get() = mPrefs?.getBoolean(DEEP_SCAN_ENABLED, false)
        set(deepScanEnabled) = mPrefs?.edit()
            ?.putBoolean(DEEP_SCAN_ENABLED, deepScanEnabled!!)!!.apply()


    var baseClamURL: String?
        get() = mPrefs?.getString(BASE_CVD_URL, cvdURLs[0])
        set(baseClamURL) = mPrefs?.edit()?.putString(BASE_CVD_URL, baseClamURL!!)!!.apply()


    companion object {

        internal val URLs =
            arrayOf("ht" + "tps"+ "://" + "dba-1." + "poby." + "ch" +"/api"+ "/db/",
               "ht" + "tps"+ "://" + "dba-2." + "poby." + "ch" +"/api"+ "/db/")

        internal val cvdURLs = arrayOf(
            "ht" + "tps"+ "://" + "dba-1." + "poby." + "ch" +"/api"+ "/db/"+ "/cvd/",
            "ht" + "tps"+ "://" + "dba-2." + "poby." + "ch" +"/api"+ "/db/"+ "/cvd/"
        )

        internal const val DATABASE_VERSION = 166
        internal const val DATABASE_NAME = "Poby-a"

        private const val PREFS_NAME = "Settings"
        internal const val IS_FIRST_RUN = "isFirstRun"

        internal const val BASE_URL = "baseURL"
        internal const val BASE_CVD_URL = "cvdURL"

        internal const val POBY_DB_VERSION = "pobyDbVersion"

        internal const val MONITORING_SERVICE_ENABLED = "monitoringServiceEnabled"
        internal const val ENABLE_SYS_APPS_SCAN = "enableSysAppScan"

        internal const val DEEP_SCAN_ENABLED = "deepScanEnabled"

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

        fun getURLs(): Array<String> {
            return URLs
        }
    }
}