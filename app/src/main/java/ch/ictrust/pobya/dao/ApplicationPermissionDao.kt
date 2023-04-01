package ch.ictrust.pobya.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.ictrust.pobya.models.ApplicationPermissionCrossRef

@Dao
interface ApplicationPermissionDao {
    @Query("SELECT * FROM ApplicationPermissionCrossRef")
    fun getAll(): LiveData<List<ApplicationPermissionCrossRef>>

    @Query("SELECT * FROM ApplicationPermissionCrossRef WHERE packageName IS (:packageName)")
    fun loadByPkg(packageName: String): LiveData<List<ApplicationPermissionCrossRef>>

    @Query("SELECT * FROM ApplicationPermissionCrossRef WHERE permission IS (:permission)")
    fun loadByPerm(permission: String): LiveData<List<ApplicationPermissionCrossRef>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(permission: ApplicationPermissionCrossRef)

    @Delete
    fun delete(permission: ApplicationPermissionCrossRef)

    @Query("DELETE FROM ApplicationPermissionCrossRef")
    fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAppPerms(appPerms: List<ApplicationPermissionCrossRef>)
}