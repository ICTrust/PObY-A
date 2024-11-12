package ch.ictrust.pobya.repository

import android.content.Context
import ch.ictrust.pobya.dao.SysSettingsDao
import ch.ictrust.pobya.database.AppDatabase
import ch.ictrust.pobya.models.SysSettings


class SysSettingsRepository(context: Context) {

    private val sysSettingsDao: SysSettingsDao

    init {
        val database: AppDatabase = AppDatabase.getInstance(context)
        sysSettingsDao = database.sysSettingsDao()
    }

    fun insert(sysSettings: SysSettings) {
        sysSettingsDao.insert(sysSettings)
    }


    fun update(sysSettings: SysSettings) {
        sysSettingsDao.update(sysSettings)
    }

    fun delete(sysSettings: SysSettings) {
        sysSettingsDao.delete(sysSettings)
    }

    fun getSettingsFailed(): SysSettings {
        return sysSettingsDao.getSettingsFailed()
    }

    fun insertAll(sysSettings: List<SysSettings>) {
        sysSettingsDao.insertAll(sysSettings)
    }

}