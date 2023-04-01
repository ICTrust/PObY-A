package ch.ictrust.pobya.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.ictrust.pobya.models.InstalledApplication

@Dao
interface ApplicationDao {
    @Query("SELECT * FROM installedApplication ORDER BY name ASC")
    fun getAll(): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE isSystemApp=(:system)")
    fun loadAllType(system: Boolean): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE packageName IN (:packages) ORDER BY name ASC ")
    fun loadAllByPkgs(packages: ArrayList<String>): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE packageName IS (:packageName)")
    fun loadAllByPkgName(packageName: String): InstalledApplication

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(installedApplication: InstalledApplication)

    @Delete
    fun delete(installedApplication: InstalledApplication)

    @Query("DELETE FROM installedApplication WHERE packageName IS (:packageName) ")
    fun delete(packageName: String)


    @Query("SELECT * FROM installedApplication ORDER BY name ASC")
    fun getAllApplications(): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE isSystemApp=0 and uninstalled=0 ORDER BY name ASC")
    fun getThirdPartyApplications(): LiveData<List<InstalledApplication>>

    @Query("SELECT * FROM installedApplication WHERE uninstalled=1 ORDER BY name ASC")
    fun getUninstalledApplications(): LiveData<List<InstalledApplication>>


    @Query("SELECT * FROM installedApplication WHERE enabled IS FALSE ORDER BY name ASC")
    fun getDisabledApplications(): LiveData<List<InstalledApplication>>


    @Query("SELECT * FROM installedApplication WHERE isSystemApp=1 ORDER BY name ASC")
    fun getSystemApplications(): LiveData<List<InstalledApplication>>

    @Update
    fun update(installedApplication: InstalledApplication)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllApps(installedApplication: List<InstalledApplication>)

    @Query("DELETE FROM installedApplication")
    fun deleteAll()

    @Query("SELECT * FROM installedApplication WHERE isSystemApp=0 and uninstalled=0 ORDER BY name ASC")
    fun getPartyApplications(): List<InstalledApplication>



}