package ch.ictrust.pobya.Utillies

import android.content.Context
import android.content.pm.*
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.InstalledApp
import ch.ictrust.pobya.models.PermissionModel
import java.io.ByteArrayOutputStream
import kotlin.collections.ArrayList

class DumpApps(context: Context?, dumpSysApps: Boolean) {

    private var listApps: MutableList<InstalledApp>? = null
    private var installedPackages: List<PackageInfo>? = null
    private var packageManager: PackageManager? =
        context?.getApplicationContext()?.getPackageManager()
    private var permissions: ArrayList<PermissionModel>? = null
    private var dumpSystemApps = dumpSysApps
    private var context = context!!


    fun getListApps(): MutableList<InstalledApp> {
        permissions = getAllperms()
        packageManager!!.packageInstaller.allSessions
        listApps = ArrayList()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            installedPackages = packageManager!!.getInstalledPackages(
                    (PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA or PackageManager.GET_SIGNING_CERTIFICATES)
            )
        } else {
            installedPackages = packageManager!!.getInstalledPackages(
                    (PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA or PackageManager.GET_SIGNATURES)
            )
        }
        installedPackages = packageManager!!.getInstalledPackages(
            (PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA or PackageManager.GET_SIGNING_CERTIFICATES)
        )
        for (pInfo: PackageInfo in (installedPackages as MutableList<PackageInfo>?)!!) {
            val permissionsList: MutableList<PermissionModel> = ArrayList()
            val reqPermissions = pInfo.requestedPermissions

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                println(pInfo.signingInfo.getApkContentsSigners())
            } else {
                println(pInfo.signatures)
            }

            var state: AppState = AppState.NORMAL
            if (reqPermissions != null) state = getAppConfidence(pInfo)
            if (!dumpSystemApps && isSystemPackage(pInfo) == 1) {
                continue
            }
            if (reqPermissions != null) {
                for (i in pInfo.requestedPermissions.indices) {
                    if ((pInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                        val temp: PermissionModel? = getPermissionByName(pInfo.requestedPermissions[i])
                        if (temp != null)
                            permissionsList.add(temp)
                        else
                            permissionsList.add(PermissionModel("", "", "","",pInfo.requestedPermissions[i], "", "", context.getString(R.string.no_desc_perm),
                                0))
                    }
                }
            }
            try {
                val icon = drawableToBitmap(packageManager!!.getApplicationIcon(pInfo.packageName))
                val stream = ByteArrayOutputStream()
                icon.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val bitmapdata = stream.toByteArray()
                listApps!!.add(
                    InstalledApp(
                        pInfo.applicationInfo.loadLabel(packageManager!!).toString(),
                        pInfo.packageName,
                        pInfo.versionCode,
                        bitmapdata,
                        isSystemPackage(pInfo),
                        state,
                        pInfo.firstInstallTime,
                        pInfo.lastUpdateTime,
                        permissionsList
                    )
                )
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        return listApps!!
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Int {
        return (if (((pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)) 1 else 0)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(
                1,
                1,
                Bitmap.Config.ARGB_8888
            )
        } else {
            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun getAppConfidence(pInfo: PackageInfo): AppState {
        var state: AppState = AppState.NORMAL
        for (i in pInfo.requestedPermissions.indices) {
            if ((pInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                try {
                    val pi = packageManager!!.getPermissionInfo(
                        pInfo.requestedPermissions[i],
                        PackageManager.GET_META_DATA
                    )
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        when (pi.protection) {
                            PermissionInfo.PROTECTION_DANGEROUS -> {
                                if (state.ordinal !== AppState.DANGEROUS.ordinal) {
                                    state = AppState.DANGEROUS
                                }
                            }
                            /*
                            PermissionInfo.PROTECTION_DANGEROUS or PermissionInfo.PROTECTION_FLAG_RUNTIME_ONLY -> {
                                if (state.ordinal !== AppState.DANGEROUS.ordinal) {
                                    state = AppState.DANGEROUS
                                }
                            }
                            PermissionInfo.PROTECTION_DANGEROUS or PermissionInfo.PROTECTION_FLAG_INSTANT -> {
                                if (state.ordinal !== AppState.DANGEROUS.ordinal) {
                                    state = AppState.DANGEROUS
                                }
                            }*/
                        }
                    } else {
                        when (pi.protectionLevel) {
                            PermissionInfo.PROTECTION_DANGEROUS -> {
                                if (state.ordinal !== AppState.DANGEROUS.ordinal) {
                                    state = AppState.DANGEROUS
                                }
                            }
                        }
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        return state
    }

    fun getAllperms(): ArrayList<PermissionModel>? {
        permissions = ArrayList()
        val groupList = packageManager!!.getAllPermissionGroups(PackageManager.GET_META_DATA)
        groupList.add(null)
        for (permissionGroup: PermissionGroupInfo? in groupList) {
            val name = permissionGroup?.name
            try {
                for (pInfo: PermissionInfo in packageManager!!.queryPermissionsByGroup(
                    name.toString(),
                    PackageManager.GET_META_DATA
                )) {
                    try {
                        val permissionGroupName =
                            (if (permissionGroup!!.name == null) "" else permissionGroup.name)
                        val permissionGroupPackageName =
                            (if (permissionGroup.packageName == null) "" else permissionGroup.packageName)
                        val permissionGroupLabel =
                            (if (permissionGroup.loadLabel(packageManager!!) == null) "" else permissionGroup.loadLabel(
                                packageManager!!
                            ).toString())
                        val permissionGroupDesc =
                            (if (permissionGroup.loadDescription(packageManager!!) == null) "" else permissionGroup.loadDescription(
                                packageManager!!
                            ).toString())
                        val permissionName = (if (pInfo.name == null) "" else pInfo.name)
                        val permissionPackageName =
                            (if (pInfo.packageName == null) "" else pInfo.packageName)
                        val permissionLabel =
                            (if (pInfo.loadLabel(packageManager!!) == null) "" else pInfo.loadLabel(
                                packageManager!!
                            ).toString())
                        val permissionDesc =
                            (if (pInfo.loadDescription(packageManager!!) == null) "" else pInfo.loadDescription(
                                packageManager!!
                            ).toString())
                        val perm = PermissionModel(
                                        permissionGroupName,
                                        permissionGroupPackageName,
                                        permissionGroupLabel,
                                        permissionGroupDesc,
                                        permissionName,
                                        permissionPackageName,
                                        permissionLabel,
                                        permissionDesc,
                                        pInfo.protectionLevel
                                    )
                        permissions!!.add(perm)
                    } catch (ex: Exception) {
                        Log.e("DumpApps", ex.toString())
                    }
                }
            } catch (ignored: PackageManager.NameNotFoundException) {
            }
        }
        return permissions
    }

    fun getPermissionByName(permissionName: String): PermissionModel? {
        for (perm: PermissionModel in permissions!!) {
            val current: String = perm.permission
            if ((current == permissionName)) {
                return perm
            }
        }
        return null
    }
}