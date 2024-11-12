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

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ch.ictrust.pobya.cvd.models.NDB

@Dao
interface NDBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(ndb: NDB)

    // filter unsupported signature RegEx
    @Query(
        "SELECT * FROM ndb WHERE hexSignature NOT LIKE '%{%' AND hexSignature NOT LIKE '%[%' " +
                "ORDER BY targetType ASC"
    )
    fun getAll(): MutableList<NDB>

    @Query("SELECT * FROM ndb WHERE hexSignature=(:hexSignature)")
    fun getByHexSig(hexSignature: String): NDB

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createAll(ndb: List<NDB>)

    @Query("SELECT * FROM ndb WHERE hexSignature LIKE :hexSignature")
    fun searchByHexSig(hexSignature: String): LiveData<List<NDB>>

    @Query("SELECT * FROM ndb WHERE targetType=:targetType")
    fun searchByTargetType(targetType: Int): List<NDB>

    @Query(
        "SELECT * FROM ndb WHERE targetType IN (:targetsTypes) AND hexSignature NOT LIKE '%{%' " +
                "AND hexSignature NOT LIKE '%[%'"
    )
    fun getByTargetsTypes(targetsTypes: List<Int>): MutableList<NDB>

    @Query("SELECT * FROM ndb LIMIT :size OFFSET :offset")
    fun getPage(size: Int, offset: Int): MutableList<NDB>

    // filter unsupported signature RegEx
    @Query(
        "SELECT * FROM ndb WHERE targetType IN (:targets) AND hexSignature NOT LIKE '%{%' AND hexSignature NOT LIKE '%[%'" +
                " LIMIT :size OFFSET :offset"
    )
    fun getPagebyTargets(size: Int, offset: Int, targets: List<Int>): MutableList<NDB>

}