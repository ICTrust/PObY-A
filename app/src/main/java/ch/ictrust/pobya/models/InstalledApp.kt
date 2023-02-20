package ch.ictrust.pobya.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class InstalledApp(
        val name: String,
        val packageName: String,
        val versionCode: Int,
        val icon: ByteArray,
        val isSystemApp: Int,
        val appState: AppState,
        val installedDate: Long,
        val updateDate: Long,
        val permissions: MutableList<PermissionModel>
) : Parcelable {

}