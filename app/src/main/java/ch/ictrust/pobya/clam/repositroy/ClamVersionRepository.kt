package ch.ictrust.pobya.clam.repositroy

import android.app.Application
import ch.ictrust.pobya.clam.models.ClamVersion
import ch.ictrust.pobya.clam.dao.ClamVersionDao
import ch.ictrust.pobya.database.AppDatabase

class ClamVersionRepository {

    companion object {

        lateinit var database: AppDatabase
        lateinit var clamVersionDao: ClamVersionDao


        @Volatile
        private var INSTANCE: ClamVersionRepository? = null
        fun getInstance(application: Application): ClamVersionRepository {
            database = AppDatabase.getInstance(application.applicationContext)
            clamVersionDao = database.clamVersionDao()

            return INSTANCE ?: synchronized(this) {
                val appRepo = ClamVersionRepository()
                INSTANCE = appRepo
                appRepo
            }
        }
    }

    fun insert(clamVersion: ClamVersion) {
        clamVersionDao.insert(clamVersion)
    }

    fun getLast(): ClamVersion {
        return clamVersionDao.getLast()
    }

}