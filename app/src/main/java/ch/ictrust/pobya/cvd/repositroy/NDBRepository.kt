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
import androidx.lifecycle.LiveData
import ch.ictrust.pobya.cvd.dao.NDBDao
import ch.ictrust.pobya.cvd.models.NDB
import ch.ictrust.pobya.database.AppDatabase

class NDBRepository {

    companion object {

        lateinit var database: AppDatabase
        lateinit var ndbDao: NDBDao

        @Volatile
        private var INSTANCE: NDBRepository? = null
        fun getInstance(application: Application): NDBRepository {
            database = AppDatabase.getInstance(application.applicationContext)
            ndbDao = database.ndbDao()

            return INSTANCE ?: synchronized(this) {
                val appRepo = NDBRepository()
                INSTANCE = appRepo
                appRepo
            }
        }
    }

    fun insert(ndb: NDB) {
        ndbDao.insert(ndb)
    }

    fun getByHexSig(hexSig: String): NDB {
        return ndbDao.getByHexSig(hexSig)
    }

    fun getAll(): MutableList<NDB> {
        return ndbDao.getAll()
    }

    fun insertList(ndbList: List<NDB>) {
        ndbDao.createAll(ndbList)
    }

    fun searchByHexSig(hexSig: String): LiveData<List<NDB>> {
        return ndbDao.searchByHexSig(hexSig)
    }

    fun searchByTargetType(targetType: Int): List<NDB> {
        return ndbDao.searchByTargetType(targetType)
    }

    fun getByTargetsTypes(targetsTypes: List<Int>): MutableList<NDB> {
        return ndbDao.getByTargetsTypes(targetsTypes)
    }

    fun getPage(size: Int, offset: Int): MutableList<NDB> {
        val offset = size * offset
        return ndbDao.getPage(size, offset)
    }

    fun getPageByTargets(size: Int, offset: Int, targets: List<Int>): MutableList<NDB> {
        val offset = size * offset
        return ndbDao.getPagebyTargets(size, offset, targets)
    }


}