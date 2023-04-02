package ch.ictrust.pobya.repository

import android.content.Context
import androidx.lifecycle.LiveData
import ch.ictrust.pobya.dao.ApplicationPermissionDao
import ch.ictrust.pobya.database.AppDatabase
import ch.ictrust.pobya.models.ApplicationPermissionCrossRef


class ApplicationPermissionRepository(context: Context) {

    private val applicationPermissionDao: ApplicationPermissionDao
    private val allAppPermission: LiveData<List<ApplicationPermissionCrossRef>>


    init {
        val database: AppDatabase = AppDatabase.getInstance(context)
        applicationPermissionDao = database.applicationPermissionDao()
        allAppPermission = applicationPermissionDao.getAll()
    }

    fun insert(applicationPermissionCrossRef: ApplicationPermissionCrossRef) {
        applicationPermissionDao.insert(applicationPermissionCrossRef)

    }

    fun insertAll(appPermissionsCrossRef: List<ApplicationPermissionCrossRef>) {
        applicationPermissionDao.insertAllAppPerms(appPermissionsCrossRef)

    }

    fun delete(applicationPermissionCrossRef: ApplicationPermissionCrossRef) {

            applicationPermissionDao.delete(applicationPermissionCrossRef)

    }

    fun loadByPermission(permission: String) : LiveData<List<ApplicationPermissionCrossRef>> {
        return applicationPermissionDao.loadByPerm(permission)
    }

    fun loadByPackageName(packageName: String) : LiveData<List<ApplicationPermissionCrossRef>> {
        return applicationPermissionDao.loadByPkg(packageName)
    }


}