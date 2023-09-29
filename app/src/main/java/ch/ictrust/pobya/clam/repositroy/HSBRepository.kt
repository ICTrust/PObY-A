package ch.ictrust.pobya.clam.repositroy

import android.app.Application
import androidx.lifecycle.LiveData
import ch.ictrust.pobya.clam.models.HSB
import ch.ictrust.pobya.clam.dao.HSBDao
import ch.ictrust.pobya.database.AppDatabase

class HSBRepository {

    companion object {

        lateinit var database: AppDatabase
        lateinit var hsbDao: HSBDao
        lateinit var allHSB: LiveData<List<HSB>>

        @Volatile
        private var INSTANCE: HSBRepository? = null
        fun getInstance(application: Application): HSBRepository {
            database = AppDatabase.getInstance(application.applicationContext)
            hsbDao = database.hsbDao()

            return INSTANCE ?: synchronized(this) {
                val appRepo = HSBRepository()
                INSTANCE = appRepo
                appRepo
            }
        }
    }

    fun insert(hsb: HSB) {
        hsbDao.insert(hsb)
    }

    fun getByHash(hash: String): HSB {
        return hsbDao.getByHash(hash)
    }

    fun getAll(): LiveData<List<HSB>> {
        return hsbDao.getAll()
    }

    fun insertList(hsbs: List<HSB>) {
        hsbDao.createAll(hsbs)
    }



}