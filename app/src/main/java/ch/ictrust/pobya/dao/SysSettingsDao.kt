package ch.ictrust.pobya.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import ch.ictrust.pobya.models.SysSettings

@Dao
interface SysSettingsDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(sysSetting: SysSettings)

    @Update
    fun update(sysSetting: SysSettings)

    @Delete
    fun delete(sysSetting: SysSettings)

    @Query("SELECT * FROM SysSettings WHERE currentValue<>expectedValue")
    fun getSettingsFailed(): SysSettings

    @Query("SELECT * FROM SysSettings ORDER BY info DESC")
    fun getAllSettings(): LiveData<List<SysSettings>>

    @Query("DELETE FROM SysSettings")
    fun deleteAll()
}