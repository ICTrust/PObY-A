package ch.ictrust.pobya.repository

import android.content.Context
import androidx.lifecycle.LiveData
import ch.ictrust.pobya.dao.PermissionDao
import ch.ictrust.pobya.database.AppDatabase
import ch.ictrust.pobya.models.PermissionModel


class PermissionRepository(context: Context) {

    private val permissionDao: PermissionDao
    private val allPermissions: LiveData<List<PermissionModel>>

    init {
        val database: AppDatabase = AppDatabase.getInstance(context)
        permissionDao = database.permissionDao()
        allPermissions = permissionDao.getAll()
    }

    fun insert(permission: PermissionModel) {
        permissionDao.insert(permission)
    }


    fun update(permission: PermissionModel) {
        permissionDao.update(permission)
    }

    fun delete(permission: PermissionModel) {
        permissionDao.delete(permission)
    }

    fun deleteAllPermissions() {
        permissionDao.deleteAll()
    }
}