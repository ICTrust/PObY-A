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
import androidx.room.Update
import ch.ictrust.pobya.models.PermissionModel

@Dao
interface PermissionDao {
    @Query("SELECT * FROM PermissionModel")
    fun getAll(): LiveData<List<PermissionModel>>

    @Query("SELECT * FROM PermissionModel WHERE permission IS (:permission)")
    fun loadAllByPerm(permission: String): PermissionModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(permission: PermissionModel)

    @Update
    fun update(permission: PermissionModel)

    @Delete
    fun delete(permission: PermissionModel)

    @Query("DELETE FROM PermissionModel")
    fun deleteAll()
}