package ch.ictrust.pobya.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ch.ictrust.pobya.dao.*
import ch.ictrust.pobya.models.*
import ch.ictrust.pobya.repository.MalwareRepository
import ch.ictrust.pobya.utillies.ApplicationPermissionHelper
import ch.ictrust.pobya.utillies.Prefs
import ch.ictrust.pobya.utillies.Utilities
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request

@Database(
    entities = [InstalledApplication::class, PermissionModel::class, ApplicationPermissionCrossRef::class,
        SysSettings::class, MalwareScan::class, Malware::class],
    version = Prefs.DATABASE_VERSION,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun applicationDao(): ApplicationDao
    abstract fun permissionDao(): PermissionDao
    abstract fun applicationPermissionDao(): ApplicationPermissionDao
    abstract fun sysSettingsDao(): SysSettingsDao
    abstract fun malwareScanDao(): MalwareScanDao
    abstract fun malwareDao(): MalwareDao

    companion object {
        private var instance: AppDatabase? = null
        private var context: Context? = null
        @Synchronized
        fun getInstance(c: Context?): AppDatabase {
            if (instance == null) {
                context = c
                instance = databaseBuilder(
                    context!!.applicationContext,
                    AppDatabase::class.java, Prefs.DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build()
            }
            return instance as AppDatabase
        }

        private val roomCallback: Callback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                Utilities.dbScope.launch {
                    val updateDB = instance?.let { context?.let { ctx -> UpdateDb(it, ctx) } }
                    updateDB?.populate()
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                val updateDB = instance?.let { context?.let { ctx -> UpdateDb(it, ctx) } }
                updateDB?.update()
            }
        }
    }


    class UpdateDb(db: AppDatabase, ctx: Context) {
        private val applicationDao: ApplicationDao
        private val permissionDao: PermissionDao
        private val applicationPermissionDao: ApplicationPermissionDao
        private val sysSettingDao: SysSettingsDao
        private val malwareDao: MalwareDao
        private val context: Context

        init {
            context = ctx
            applicationDao = db.applicationDao()
            permissionDao = db.permissionDao()
            applicationPermissionDao = db.applicationPermissionDao()
            sysSettingDao = db.sysSettingsDao()
            malwareDao = db.malwareDao()
        }

         fun populate() {
            val dump = ApplicationPermissionHelper(context.applicationContext, true)
            val perms: List<PermissionModel> = dump.getAllperms()
            for (permission in perms) {
                permissionDao.insert(permission)
            }
            for (app in dump.getListApps(true)) {
                applicationDao.insert(app)
                dump.getAppPermissions(app.packageName).forEach {
                    applicationPermissionDao.insert(
                        ApplicationPermissionCrossRef(
                            app.packageName,
                            it.permission
                        )
                    )
                }
            }

            Utilities.updateMalwareDB(context)
        }

        fun update(){

        }
    }
}
