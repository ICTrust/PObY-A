package ch.ictrust.pobya.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey

@Parcelize
@Entity
data class InstalledApplication(
    val name: String,
    @PrimaryKey
        val packageName: String,
    val versionCode: Long?,
    val icon: ByteArray,
    val isSystemApp: Int,
    val applicationState: ApplicationState,
    val enabled: Boolean,
    val installedDate: Long,
    val updateDate: Long,
    var uninstalled: Boolean = false,
    var uninstallDate: Long = 0,
    val trusted: Boolean = false,
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

                return true
        }

        override fun hashCode(): Int {
                var result = name.hashCode()
                result = 31 * result + packageName.hashCode()
                result = 31 * result + (versionCode?.hashCode() ?: 0)
                result = 31 * result + icon.contentHashCode()
                result = 31 * result + isSystemApp
                result = 31 * result + applicationState.hashCode()
                result = 31 * result + enabled.hashCode()
                result = 31 * result + installedDate.hashCode()
                result = 31 * result + updateDate.hashCode()
                result = 31 * result + uninstalled.hashCode()
                result = 31 * result + uninstallDate.hashCode()
                result = 31 * result + trusted.hashCode()
                return result
        }

}