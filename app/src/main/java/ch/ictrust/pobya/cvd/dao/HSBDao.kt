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
package ch.ictrust.pobya.cvd.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.ictrust.pobya.cvd.models.HSB

@Dao
interface HSBDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(hsb: HSB)

    @Query("SELECT * FROM hsb ORDER BY hashString ASC")
    fun getAll(): MutableList<HSB>

    @Query("SELECT * FROM hsb WHERE hashString=(:hash)")
    fun getByHash(hash: String): HSB

    @Query("SELECT * FROM hsb WHERE hashString=(:hash) AND fileSize=(:fileSize)")
    fun getByHashAndFileSize(hash: String, fileSize: Long): HSB

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun createAll(hsb: List<HSB>)

}