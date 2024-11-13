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
import androidx.room.PrimaryKey

@Entity
data class CVDVersion(
    val version: Long,
    @PrimaryKey
    val dbType: CVDType,
    val buildTime: Long,
    val nbrSignatures: Long,
    val hashMD5: String,
    val digitalSignature: String,
    val updateDate: Long
)
