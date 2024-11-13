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
package ch.ictrust.pobya.utillies

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.content.pm.PermissionGroupInfo
import android.content.pm.PermissionInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.ApplicationPermissionCrossRef
import ch.ictrust.pobya.models.ApplicationState
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.models.PermissionModel
import ch.ictrust.pobya.repository.ApplicationPermissionRepository
import ch.ictrust.pobya.repository.ApplicationRepository
import ch.ictrust.pobya.repository.PermissionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class ApplicationPermissionHelper(ctx: Context, dumpSysApps: Boolean) {

    private var listApps: MutableList<InstalledApplication> = ArrayList()
    private var installedPackages: List<PackageInfo> = ArrayList()
    private var packageManager: PackageManager = ctx.packageManager
    private var permissions: ArrayList<PermissionModel> = ArrayList()
    private var dumpSystemApps = dumpSysApps
    private var context = ctx
    private var tag: String = "ApplicationPermissionHelper"

    var dbJob = Job()
    var dbScope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext + dbJob)

    fun getListApps(fresh: Boolean): List<InstalledApplication> {

        val apps: List<InstalledApplication>
        val appPermissionList: ArrayList<ApplicationPermissionCrossRef> = ArrayList()
        if (!fresh) {
            apps = ApplicationRepository.getInstance(context.applicationContext as Application)
                .getAllApps().value as ArrayList<InstalledApplication>
            if (apps.isNotEmpty()) return apps
        }

        permissions = getAllperms()
        // packageManager.packageInstaller.allSessions
        listApps = ArrayList()
        installedPackages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val packages = packageManager.getInstalledPackages(
                (PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA or PackageManager.GET_SIGNING_CERTIFICATES)
            )
            packages
        } else {
            packageManager.getInstalledPackages(
                (PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA or PackageManager.GET_SIGNATURES)
            )
        }

        for (pInfo: PackageInfo in (installedPackages as MutableList<PackageInfo>)) {
            val permissionsList: MutableList<PermissionModel> = ArrayList()
            val reqPermissions = pInfo.requestedPermissions?.copyOf()
            val currentApp =
                ApplicationRepository.getInstance(context.applicationContext as Application)
                    .getAppByPackageName(pInfo.packageName)

            if (!dumpSystemApps && isSystemPackage(pInfo) == 1) {
                continue
            }

            if (reqPermissions != null) {
                for (i in pInfo.requestedPermissions!!.indices) {
                    if ((pInfo.requestedPermissionsFlags!![i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                        val temp: PermissionModel? =
                            getPermissionByName(pInfo.requestedPermissions!![i])
                        if (temp != null)
                            permissionsList.add(temp)
                        else {
                            permissionsList.add(
                                PermissionModel(
                                    "",
                                    "",
                                    "",
                                    "",
                                    pInfo.requestedPermissions!![i] ?: continue,
                                    "",
                                    "",
                                    context.getString(R.string.no_desc_perm),
                                    0
                                )
                            )
                        }
                    }
                }
            }
            try {
                val icon = drawableToBitmap(packageManager.getApplicationIcon(pInfo.packageName))
                val stream = ByteArrayOutputStream()
                icon.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                val bitmapData = stream.toByteArray()

                var app: InstalledApplication
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    app = InstalledApplication(
                        pInfo.applicationInfo?.loadLabel(packageManager).toString(),
                        pInfo.packageName.toString(),
                        pInfo.longVersionCode,
                        bitmapData,
                        isSystemPackage(pInfo),
                        getAppConfidence(pInfo),
                        pInfo.applicationInfo?.enabled ?: false,
                        pInfo.firstInstallTime,
                        pInfo.lastUpdateTime
                    )

                    pInfo.permissions?.forEach {
                        appPermissionList.add(
                            ApplicationPermissionCrossRef(app.packageName, it.name)
                        )
                    }
                } else {
                    app = InstalledApplication(
                        pInfo.applicationInfo?.loadLabel(packageManager).toString(),
                        pInfo.packageName,
                        pInfo.versionCode.toLong(),
                        bitmapData,
                        isSystemPackage(pInfo),
                        getAppConfidence(pInfo),
                        pInfo.applicationInfo?.enabled ?: false,
                        pInfo.firstInstallTime,
                        pInfo.lastUpdateTime
                    )
                    pInfo.permissions?.forEach {
                        appPermissionList.add(
                            ApplicationPermissionCrossRef(app.packageName, it.name)
                        )
                    }
                }

                if (currentApp != null) {
                    app.flagReason = currentApp.flagReason
                    app.flaggedAsThreat = currentApp.flaggedAsThreat
                    app.trusted = currentApp.trusted
                    app.applicationState = currentApp.applicationState
                    app.lastHash = currentApp.lastHash
                }
                listApps.add(app)
            } catch (e: NameNotFoundException) {
                e.printStackTrace()
            }
        }
        dbScope.launch {
            ApplicationRepository.getInstance(context.applicationContext as Application)
                .insertApps(listApps)
            ApplicationPermissionRepository(context).insertAll(appPermissionList)

            for (perm in appPermissionList) ApplicationPermissionRepository(context).insert(perm)
        }
        return listApps
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Int {
        return (if ((((pkgInfo.applicationInfo?.flags ?: 0) and ApplicationInfo.FLAG_SYSTEM) != 0)) 1 else 0)
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap {
        val bitmap: Bitmap
        if (drawable is BitmapDrawable) {
            if (drawable.bitmap != null) {
                return drawable.bitmap
            }
        }

        if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            bitmap = Bitmap.createBitmap(
                1, 1, Bitmap.Config.ARGB_8888
            )
        } else {
            bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    internal fun getAppConfidence(pInfo: PackageInfo): ApplicationState {
        var state: ApplicationState = ApplicationState.NORMAL

        if (pInfo.requestedPermissions == null || pInfo.requestedPermissions?.isEmpty() == true)
            return state

        val requesPermission = pInfo.requestedPermissions?.copyOf()
        val requestedPrmissionsFlags = pInfo.requestedPermissionsFlags?.copyOf()
        for (i in requesPermission?.indices!!) {
            if ((requestedPrmissionsFlags!!.get(i)
                    .or(0) and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
            ) {
                try {
                    val pi = packageManager.getPermissionInfo(
                        requesPermission[i], PackageManager.GET_META_DATA
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        if (pi.protection == PermissionInfo.PROTECTION_DANGEROUS) {
                            if (state != ApplicationState.DANGEROUS) {
                                state = ApplicationState.DANGEROUS
                                return ApplicationState.DANGEROUS
                            }
                        }


                    } else {
                        if (pi.protectionLevel == PermissionInfo.PROTECTION_DANGEROUS) {
                            state = ApplicationState.DANGEROUS
                            return ApplicationState.DANGEROUS


                        }
                    }
                } catch (e: NameNotFoundException) {
                    e.printStackTrace()
                }
            }
        }
        return state
    }


    fun getAllperms(): ArrayList<PermissionModel> {
        permissions = ArrayList()
        val groupList = packageManager.getAllPermissionGroups(PackageManager.GET_META_DATA)
        for (permissionGroup: PermissionGroupInfo in groupList) {
            val name = permissionGroup.name
            try {
                for (pInfo: PermissionInfo in packageManager.queryPermissionsByGroup(
                    name.toString(), PackageManager.GET_META_DATA
                )) {
                    try {
                        val permissionGroupName =
                            (if (permissionGroup.name == null) "" else permissionGroup.name)
                        val permissionGroupPackageName =
                            (if (permissionGroup.packageName == null) "" else permissionGroup.packageName)
                        val permissionGroupLabel =
                            permissionGroup.loadLabel(packageManager).toString()
                        val permissionGroupDesc =
                            (if (permissionGroup.loadDescription(packageManager) == null) ""
                            else permissionGroup.loadDescription(
                                packageManager
                            ).toString())
                        val permissionName = (if (pInfo.name == null) "" else pInfo.name)
                        val permissionPackageName =
                            (if (pInfo.packageName == null) "" else pInfo.packageName)
                        val permissionLabel = pInfo.loadLabel(packageManager).toString()
                        val permissionDesc = (if (pInfo.loadDescription(packageManager) == null) ""
                        else pInfo.loadDescription(
                            packageManager
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
                        permissions.add(perm)

                        dbScope.launch {
                            PermissionRepository(context).insert(perm)
                        }


                    } catch (ex: Exception) {
                        Log.e(tag, ex.toString())
                    }
                }
            } catch (ex: NameNotFoundException) {
                Log.e(tag, ex.toString())
            }
        }
        return permissions
    }

    private fun getPermissionByName(permissionName: String): PermissionModel? {
        for (perm: PermissionModel in permissions) {
            val current: String = perm.permission
            if ((current == permissionName)) {
                return perm
            }
        }
        return null
    }

    fun getAppByPackageName(packageName: String): InstalledApplication? {
        try {
            val pkgInfo: PackageInfo = context.packageManager.getPackageInfo(
                packageName, PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
            )

            if (pkgInfo == null) {
                return null
            }

            val icon = this.drawableToBitmap(packageManager.getApplicationIcon(pkgInfo.packageName))
            val stream = ByteArrayOutputStream()
            icon.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            val bitmapByteArray = stream.toByteArray()

            val app: InstalledApplication
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                app = InstalledApplication(
                    pkgInfo.applicationInfo!!.loadLabel(packageManager).toString(),
                    pkgInfo.packageName.toString(),
                    pkgInfo.longVersionCode,
                    bitmapByteArray,
                    isSystemPackage(pkgInfo),
                    getAppConfidence(pkgInfo),
                    pkgInfo.applicationInfo?.enabled ?: false,
                    pkgInfo.firstInstallTime,
                    pkgInfo.lastUpdateTime
                )
            } else {
                app = InstalledApplication(
                    pkgInfo.applicationInfo!!.loadLabel(packageManager).toString(),
                    pkgInfo.packageName,
                    (pkgInfo.versionCode as Number).toLong(),
                    bitmapByteArray,
                    isSystemPackage(pkgInfo),
                    getAppConfidence(pkgInfo),
                    pkgInfo.applicationInfo?.enabled ?: false,
                    pkgInfo.firstInstallTime,
                    pkgInfo.lastUpdateTime
                )
            }

            return app
        } catch (e: NameNotFoundException) {
            Log.e(tag, e.stackTrace.toString())

        }
        return null
    }

    fun getAppPermissions(packageName: String): List<PermissionModel> {
        getAllperms()
        val pkgInfo: PackageInfo
        try {
            pkgInfo = packageManager.getPackageInfo(
                packageName, PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
            )

        } catch (e: NameNotFoundException) {
            Log.e(tag, e.stackTrace.toString())
            return ArrayList()
        }
        if (pkgInfo == null) {
            return ArrayList()
        }
        val appPerms: ArrayList<PermissionModel> = ArrayList()

        if (pkgInfo.requestedPermissions != null) {
            for (i in pkgInfo.requestedPermissions!!.indices) {
                if ((pkgInfo.requestedPermissionsFlags!![i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0
                ) {
                    val temp: PermissionModel? = getPermissionByName(
                        pkgInfo.requestedPermissions!![i])
                    if (packageName == "ch.ictrust.pobya") {
                        Log.d(tag, "Permission: ${pkgInfo.requestedPermissions!![i]}")
                    }

                    if (temp != null) appPerms.add(temp)
                    /*else appPerms.add(
                        PermissionModel(
                            "",
                            "",
                            "",
                            "",
                            pkgInfo.requestedPermissions!![i],
                            "",
                            pkgInfo.requestedPermissions!![i],
                            context.getString(R.string.no_desc_perm),
                            0
                        )
                    )*/
                }
            }
        }
        return appPerms
    }

    fun getAppCert(packageName: String): ByteArray {
        val sigs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(
                packageName, PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo?.apkContentsSigners?.firstOrNull()?.toByteArray() ?: ByteArray(0)
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures?.firstOrNull()?.toByteArray() ?: ByteArray(0)
        }
        return sigs
    }
}