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
package ch.ictrust.pobya.cvd.repositroy

import android.app.Application
import ch.ictrust.pobya.cvd.dao.CVDVersionDao
import ch.ictrust.pobya.cvd.models.CVDType
import ch.ictrust.pobya.cvd.models.CVDVersion
import ch.ictrust.pobya.database.AppDatabase

class CVDVersionRepository {

    companion object {

        lateinit var database: AppDatabase
        lateinit var CVDVersionDao: CVDVersionDao


        @Volatile
        private var INSTANCE: CVDVersionRepository? = null
        fun getInstance(application: Application): CVDVersionRepository {
            database = AppDatabase.getInstance(application.applicationContext)
            CVDVersionDao = database.clamVersionDao()

            return INSTANCE ?: synchronized(this) {
                val appRepo = CVDVersionRepository()
                INSTANCE = appRepo
                appRepo
            }
        }
    }

    fun insert(CVDVersion: CVDVersion) {
        CVDVersionDao.insert(CVDVersion)
    }

    fun getLast(dbType: CVDType): CVDVersion {
        return CVDVersionDao.getLast(dbType)
    }

}