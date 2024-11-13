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

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PermissionModel(
    val group: String,
    val group_package: String,
    val group_label: String,
    val group_description: String,
    @PrimaryKey
    val permission: String,
    val package_name: String,
    val label: String,
    val description: String,
    val protectionLevel: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(group)
        parcel.writeString(group_package)
        parcel.writeString(group_label)
        parcel.writeString(group_description)
        parcel.writeString(permission)
        parcel.writeString(package_name)
        parcel.writeString(label)
        parcel.writeString(description)
        parcel.writeInt(protectionLevel)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PermissionModel> {
        override fun createFromParcel(parcel: Parcel): PermissionModel {
            return PermissionModel(parcel)
        }

        override fun newArray(size: Int): Array<PermissionModel?> {
            return arrayOfNulls(size)
        }
    }


}
