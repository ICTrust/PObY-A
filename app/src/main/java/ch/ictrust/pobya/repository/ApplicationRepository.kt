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

import android.app.Application
import androidx.lifecycle.LiveData
import ch.ictrust.pobya.dao.ApplicationDao
import ch.ictrust.pobya.database.AppDatabase
import ch.ictrust.pobya.models.InstalledApplication

class ApplicationRepository {

    companion object {

        lateinit var database: AppDatabase
        lateinit var applicationDao: ApplicationDao
        lateinit var allApplications: LiveData<List<InstalledApplication>>
        lateinit var userApplications: LiveData<List<InstalledApplication>>
        lateinit var systemApplications: LiveData<List<InstalledApplication>>
        lateinit var disabledApplications: LiveData<List<InstalledApplication>>
        private lateinit var uninstalledApplications: LiveData<List<InstalledApplication>>

        @Volatile
        private var INSTANCE: ApplicationRepository? = null
        fun getInstance(application: Application): ApplicationRepository {
            database = AppDatabase.getInstance(application.applicationContext)
            applicationDao = database.applicationDao()
            allApplications = applicationDao.getAllApplications()
            userApplications = applicationDao.getThirdPartyApplications()
            uninstalledApplications = applicationDao.getUninstalledApplications()
            systemApplications = applicationDao.getSystemApplications()
            disabledApplications = applicationDao.getDisabledApplications()
            return INSTANCE ?: synchronized(this) {
                val appRepo = ApplicationRepository()
                INSTANCE = appRepo
                appRepo
            }
        }
    }

    fun insert(app: InstalledApplication) {
        applicationDao.insert(app)
    }

    suspend fun insertApps(apps: List<InstalledApplication>) {
        applicationDao.upsert(apps)
        //applicationDao.insertAllApps(apps)
    }

    fun update(app: InstalledApplication) {
        applicationDao.update(app)
    }

    fun delete(app: InstalledApplication) {
        applicationDao.delete(app)
    }

    fun deleteAllApps() {
        applicationDao.deleteAll()
    }

    fun getAppByPackageName(packageName: String): InstalledApplication {
        return applicationDao.loadAllByPkgName(packageName)
    }

    fun getThirdPartyApps(): LiveData<List<InstalledApplication>> {
        userApplications = applicationDao.getThirdPartyApplications()
        return userApplications
    }

    fun getAllApps(): LiveData<List<InstalledApplication>> {
        return allApplications
    }

    fun getSystemApps(): LiveData<List<InstalledApplication>> {
        return applicationDao.getSystemApplications()
    }

    fun getDisabledApps(): LiveData<List<InstalledApplication>> {
        return disabledApplications
    }

    fun getUninstalledApps(): LiveData<List<InstalledApplication>> {
        return applicationDao.getUninstalledApplications()
    }
}