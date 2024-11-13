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
package ch.ictrust.pobya.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ch.ictrust.pobya.models.InstalledApplication

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM installedApplication ORDER BY name ASC")
    fun getAll(): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE isSystemApp=(:system)")
    fun loadAllType(system: Boolean): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE packageName IN (:packages) ORDER BY name ASC ")
    fun loadAllByPkgs(packages: ArrayList<String>): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE packageName IS (:packageName)")
    fun loadAllByPkgName(packageName: String): InstalledApplication

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(installedApplication: InstalledApplication): Long

    @Delete
    fun delete(installedApplication: InstalledApplication)

    @Query("DELETE FROM installedApplication WHERE packageName IS (:packageName) ")
    fun delete(packageName: String)

    @Query("SELECT * FROM installedApplication ORDER BY name ASC")
    fun getAllApplications(): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE isSystemApp=0 and uninstalled=0 ORDER BY name ASC")
    fun getThirdPartyApplications(): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE uninstalled=1 ORDER BY name ASC")
    fun getUninstalledApplications(): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE enabled IS FALSE ORDER BY name ASC")
    fun getDisabledApplications(): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE isSystemApp=1 ORDER BY name ASC")
    fun getSystemApplications(): LiveData<List<InstalledApplication>>

    @Update
    fun update(installedApplication: InstalledApplication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllApps(installedApplication: List<InstalledApplication>)

    @Transaction
    fun upsert(installedApplications: List<InstalledApplication>) {
        for (app in installedApplications) {
            val id = insert(app)
            if (id == -1L) {
                update(app)
            }
        }
    }

    @Query("DELETE FROM installedApplication")
    fun deleteAll()

    @Query("SELECT * FROM installedApplication WHERE isSystemApp=0 and uninstalled=0 ORDER BY name ASC")
    fun getPartyApplications(): List<InstalledApplication>

}