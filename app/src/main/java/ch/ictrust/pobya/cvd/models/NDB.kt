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
package ch.ictrust.pobya.cvd.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ndb",
    indices = [Index(
        value = ["offset", "hexSignature"],
        unique = true
    )]
)
data class NDB(
    // MalwareName:TargetType:Offset:HexSignature[:min_flevel:[max_flevel]]
    var malwareName: String,
    var targetType: Int,
    var offset: String,
    var hexSignature: String,
    //var minLevel: Int,
    //var maxLevel: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}

