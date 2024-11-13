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
package ch.ictrust.pobya.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class InstalledApplication(
    var name: String,
    @PrimaryKey
    var packageName: String,
    var versionCode: Long?,
    var icon: ByteArray,
    var isSystemApp: Int,
    var applicationState: ApplicationState,
    var enabled: Boolean,
    var installedDate: Long,
    var updateDate: Long,
    var uninstalled: Boolean = false,
    var uninstallDate: Long = 0,
    var flaggedAsThreat: Boolean = false,
    var flagReason: String? = null,
    var trusted: Boolean = false,
    var lastHash: String? = null,
    var inScan: Boolean = false
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as InstalledApplication

        if (name != other.name) return false
        if (packageName != other.packageName) return false
        if (versionCode != other.versionCode) return false
        if (!icon.contentEquals(other.icon)) return false
        if (isSystemApp != other.isSystemApp) return false
        if (applicationState != other.applicationState) return false
        if (enabled != other.enabled) return false
        if (installedDate != other.installedDate) return false
        if (updateDate != other.updateDate) return false
        if (uninstalled != other.uninstalled) return false
        if (uninstallDate != other.uninstallDate) return false
        if (trusted != other.trusted) return false
        if (lastHash != other.lastHash) return false
        if (inScan != other.inScan) return false

        return true
    }

}