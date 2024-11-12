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
import ch.ictrust.pobya.cvd.dao.HSBDao
import ch.ictrust.pobya.cvd.models.HSB
import ch.ictrust.pobya.database.AppDatabase

class HSBRepository {

    companion object {

        lateinit var database: AppDatabase
        lateinit var hsbDao: HSBDao

        @Volatile
        private var INSTANCE: HSBRepository? = null
        fun getInstance(application: Application): HSBRepository {
            database = AppDatabase.getInstance(application.applicationContext)
            hsbDao = database.hsbDao()

            return INSTANCE ?: synchronized(this) {
                val appRepo = HSBRepository()
                INSTANCE = appRepo
                appRepo
            }
        }
    }

    fun insert(hsb: HSB) {
        hsbDao.insert(hsb)
    }

    fun getByHash(hash: String): HSB {
        return hsbDao.getByHash(hash)
    }

    fun getAll(): MutableList<HSB> {
        return hsbDao.getAll()
    }

    fun getByHashAndFileSize(hash: String, fileSize: Long): HSB {
        return hsbDao.getByHashAndFileSize(hash, fileSize)
    }

    fun insertList(hsbs: List<HSB>) {
        hsbDao.createAll(hsbs)
    }


}