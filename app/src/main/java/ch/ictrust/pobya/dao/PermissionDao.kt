package ch.ictrust.pobya.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.ictrust.pobya.models.PermissionModel

@Dao
interface PermissionDao {
    @Query("SELECT * FROM PermissionModel")
    fun getAll(): LiveData<List<PermissionModel>>

    @Query("SELECT * FROM PermissionModel WHERE permission IS (:permission)")
    fun loadAllByPerm(permission: String): PermissionModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(permission: PermissionModel)

    @Update
    fun update(permission: PermissionModel)

    @Delete
    fun delete(permission: PermissionModel)

    @Query("DELETE FROM PermissionModel")
    fun deleteAll()
}