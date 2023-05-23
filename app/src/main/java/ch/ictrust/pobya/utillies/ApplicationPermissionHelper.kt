package ch.ictrust.pobya.utillies

import android.app.Application
import android.content.Context
import android.content.pm.*
import android.content.pm.PackageManager.NameNotFoundException
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
    private var tag : String = "ApplicationPermissionHelper"

    var dbJob = Job()

    var dbScope: CoroutineScope = CoroutineScope(GlobalScope.coroutineContext + dbJob)


    fun getListApps(fresh: Boolean): List<InstalledApplication> {

        //val dbApp = AppDatabase.getDatabase(context, Utilities.dbScope)
        val apps: List<InstalledApplication>
        var appPermissionList : ArrayList<ApplicationPermissionCrossRef> = ArrayList()
        if (!fresh) {
            apps = ApplicationRepository.getInstance(context.applicationContext as Application)
                                                    .getAllApps().value as ArrayList<InstalledApplication>
            if (apps.isNotEmpty())
                return apps
        }

        permissions = getAllperms()
        packageManager.packageInstaller.allSessions
        listApps = ArrayList()
        installedPackages = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val packages = packageManager.getInstalledPackages(
                (PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
                        or PackageManager.GET_SIGNING_CERTIFICATES)
            )
            packages
        } else {
            packageManager.getInstalledPackages(
                (PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
                        or PackageManager.GET_SIGNATURES)
            )
        }

        for (pInfo: PackageInfo in (installedPackages as MutableList<PackageInfo>)) {
            val permissionsList: MutableList<PermissionModel> = ArrayList()
            val reqPermissions = pInfo.requestedPermissions

            if (!dumpSystemApps && isSystemPackage(pInfo) == 1) {
                continue
            }
            if (reqPermissions != null) {
                for (i in pInfo.requestedPermissions.indices) {
                    if ((pInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                        val temp: PermissionModel? =
                            getPermissionByName(pInfo.requestedPermissions[i])
                        if (temp != null)
                            permissionsList.add(temp)
                        else
                            permissionsList.add(
                                PermissionModel(
                                    "",
                                    "",
                                    "",
                                    "",
                                    pInfo.requestedPermissions[i],
                                    "",
                                    "",
                                    context.getString(R.string.no_desc_perm),
                                    0
                                )
                            )
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
                        pInfo.applicationInfo.loadLabel(packageManager).toString(),
                        pInfo.packageName.toString(),
                        pInfo.longVersionCode,
                        bitmapData,
                        isSystemPackage(pInfo),
                        getAppConfidence(pInfo),
                        pInfo.applicationInfo.enabled,
                        pInfo.firstInstallTime,
                        pInfo.lastUpdateTime
                    )
                    if (pInfo.permissions != null)
                        pInfo.permissions.forEach { appPermissionList.add(ApplicationPermissionCrossRef(app.packageName, it.name)) }
                } else {
                    app = InstalledApplication(
                        pInfo.applicationInfo.loadLabel(packageManager).toString(),
                        pInfo.packageName,
                        (pInfo.versionCode as Number).toLong(),
                        bitmapData,
                        isSystemPackage(pInfo),
                        getAppConfidence(pInfo),
                        pInfo.applicationInfo.enabled,
                        pInfo.firstInstallTime,
                        pInfo.lastUpdateTime
                    )
                    if (pInfo.permissions != null)
                        pInfo.permissions.forEach { appPermissionList.add(ApplicationPermissionCrossRef(app.packageName, it.name)) }
                }

                listApps.add(app)
            } catch (e: NameNotFoundException) {
                e.printStackTrace()
            }

        }


        ApplicationRepository.getInstance(context.applicationContext as Application).insertApps(listApps)
        /*for (perm in appPermissionList)
                ApplicationPermissionRepository(context).insert(perm)*/



        return listApps
    }

    private fun isSystemPackage(pkgInfo: PackageInfo): Int {
        return (if (((pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0)) 1 else 0)
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

    private fun getAppConfidence(pInfo: PackageInfo): ApplicationState {
        var state: ApplicationState = ApplicationState.NORMAL
        if (pInfo.requestedPermissions == null || pInfo.requestedPermissions.isEmpty())
            return state

        for (i in pInfo.requestedPermissions.indices) {
            if ((pInfo.requestedPermissionsFlags[i] and PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                try {
                    val pi = packageManager.getPermissionInfo(
                        pInfo.requestedPermissions[i],
                        PackageManager.GET_META_DATA
                    )
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        when (pi.protection) {
                            PermissionInfo.PROTECTION_DANGEROUS -> {
                                if (state.ordinal != ApplicationState.DANGEROUS.ordinal) {
                                    state = ApplicationState.DANGEROUS
                                }
                            }
                            /*
                            PermissionInfo.PROTECTION_DANGEROUS or PermissionInfo.PROTECTION_FLAG_RUNTIME_ONLY -> {
                                if (state.ordinal != AppState.DANGEROUS.ordinal) {
                                    state = AppState.DANGEROUS
                                }
                            }
                            PermissionInfo.PROTECTION_DANGEROUS or PermissionInfo.PROTECTION_FLAG_INSTANT -> {
                                if (state.ordinal != AppState.DANGEROUS.ordinal) {
                                    state = AppState.DANGEROUS
                                }
                            }*/
                        }
                    } else {
                        when (pi.protectionLevel) {
                            PermissionInfo.PROTECTION_DANGEROUS -> {
                                if (state.ordinal != ApplicationState.DANGEROUS.ordinal) {
                                    state = ApplicationState.DANGEROUS
                                }
                            }
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
                    name.toString(),
                    PackageManager.GET_META_DATA
                )) {
                    try {
                        val permissionGroupName =
                            (if (permissionGroup.name == null) "" else permissionGroup.name)
                        val permissionGroupPackageName =
                            (if (permissionGroup.packageName == null) "" else permissionGroup.packageName)
                        val permissionGroupLabel = permissionGroup.loadLabel(packageManager).toString()
                        val permissionGroupDesc =
                            (if (permissionGroup.loadDescription(packageManager) == null) ""
                            else permissionGroup.loadDescription(
                                    packageManager
                            ).toString())
                        val permissionName = (if (pInfo.name == null) "" else pInfo.name)
                        val permissionPackageName =
                            (if (pInfo.packageName == null) "" else pInfo.packageName)
                        val permissionLabel = pInfo.loadLabel(packageManager).toString()
                        val permissionDesc =
                            (if (pInfo.loadDescription(packageManager) == null) ""
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
            val pkgInfo: PackageInfo =
                context.packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA
                )

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
                    pkgInfo.applicationInfo.enabled,
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
                    pkgInfo.applicationInfo.enabled,
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

    fun getAppPermissions(packageName: String) : List<PermissionModel> {
        getAllperms()
        val pkgInfo: PackageInfo =
            packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS  or PackageManager.GET_META_DATA
            )
        val appPerms: ArrayList<PermissionModel> = ArrayList()
        if (pkgInfo.requestedPermissions != null && pkgInfo.requestedPermissions.isNotEmpty()) {
            for (i in pkgInfo.requestedPermissions.indices) {
                val permission = pkgInfo.requestedPermissions[i]
                val sysPerm = getPermissionByName(pkgInfo.requestedPermissions[i])
                if ((permission == null) || (sysPerm == null)) {
                    continue
                }

                if ((pkgInfo.requestedPermissionsFlags[i]
                            and PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) {
                   continue
                }

                val permModel = PermissionModel(
                    sysPerm.group, sysPerm.group_package,
                    sysPerm.group_label, sysPerm.group_description,
                    sysPerm.permission, sysPerm.package_name,
                    sysPerm.label, sysPerm.description,
                    sysPerm.protectionLevel
                )
                appPerms.add(permModel)
            }
        }

        return appPerms
    }

    fun getAppCert(packageName: String): ByteArray {
        val sigs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            ).signingInfo.apkContentsSigners[0].toByteArray()
        } else {
            context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            ).signatures[0].toByteArray()
        }
        return sigs
    }
}