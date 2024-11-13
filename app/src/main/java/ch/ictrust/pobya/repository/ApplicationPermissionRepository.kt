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
package ch.ictrust.pobya.repository

import android.content.Context
import androidx.lifecycle.LiveData
import ch.ictrust.pobya.dao.ApplicationPermissionDao
import ch.ictrust.pobya.database.AppDatabase
import ch.ictrust.pobya.models.ApplicationPermissionCrossRef


class ApplicationPermissionRepository(context: Context) {

    private val applicationPermissionDao: ApplicationPermissionDao
    private val allAppPermission: LiveData<List<ApplicationPermissionCrossRef>>


    init {
        val database: AppDatabase = AppDatabase.getInstance(context)
        applicationPermissionDao = database.applicationPermissionDao()
        allAppPermission = applicationPermissionDao.getAll()
    }

    fun insert(applicationPermissionCrossRef: ApplicationPermissionCrossRef) {
        applicationPermissionDao.insert(applicationPermissionCrossRef)

    }

    fun insertAll(appPermissionsCrossRef: List<ApplicationPermissionCrossRef>) {
        applicationPermissionDao.insertAllAppPerms(appPermissionsCrossRef)

    }

    fun delete(applicationPermissionCrossRef: ApplicationPermissionCrossRef) {

        applicationPermissionDao.delete(applicationPermissionCrossRef)

    }

    fun loadByPermission(permission: String): LiveData<List<ApplicationPermissionCrossRef>> {
        return applicationPermissionDao.loadByPerm(permission)
    }

    fun loadByPackageName(packageName: String): LiveData<List<ApplicationPermissionCrossRef>> {
        return applicationPermissionDao.loadByPkg(packageName)
    }


}