package ch.ictrust.pobya.models

import android.os.Parcel
import android.os.Parcelable


data class PermissionModel (
    val group: String,
    val group_package: String,
    val group_label: String,
    val group_description: String,
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
    ) {
    }

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
