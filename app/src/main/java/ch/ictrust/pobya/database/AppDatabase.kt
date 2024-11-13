/*
 * This file is part of PObY-A.
 *
 * Copyright (C) 2023 ICTrust Sàrl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package ch.ictrust.pobya.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import ch.ictrust.pobya.cvd.dao.CVDVersionDao
import ch.ictrust.pobya.cvd.dao.HSBDao
import ch.ictrust.pobya.cvd.dao.NDBDao
import ch.ictrust.pobya.cvd.models.CVDVersion
import ch.ictrust.pobya.cvd.models.HSB
import ch.ictrust.pobya.cvd.models.NDB
import ch.ictrust.pobya.dao.ApplicationDao
import ch.ictrust.pobya.dao.ApplicationPermissionDao
import ch.ictrust.pobya.dao.MalwareCertDao
import ch.ictrust.pobya.dao.MalwareDao
import ch.ictrust.pobya.dao.MalwareScanDao
import ch.ictrust.pobya.dao.MalwareScanDetailsDao
import ch.ictrust.pobya.dao.PermissionDao
import ch.ictrust.pobya.dao.SysSettingsDao
import ch.ictrust.pobya.models.ApplicationPermissionCrossRef
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.models.Malware
import ch.ictrust.pobya.models.MalwareCert
import ch.ictrust.pobya.models.MalwareScan
import ch.ictrust.pobya.models.MalwareScanDetails
import ch.ictrust.pobya.models.PermissionModel
import ch.ictrust.pobya.models.SysSettings
import ch.ictrust.pobya.utillies.ApplicationPermissionHelper
import ch.ictrust.pobya.utillies.Prefs
import ch.ictrust.pobya.utillies.Utilities
import kotlinx.coroutines.launch

@Database(
    entities = [InstalledApplication::class, PermissionModel::class, ApplicationPermissionCrossRef::class,
        SysSettings::class, MalwareScan::class, Malware::class, MalwareCert::class,
        MalwareScanDetails::class,
        //ClamDB Data
        HSB::class, NDB::class, CVDVersion::class],
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
    abstract fun malwareCertDao(): MalwareCertDao
    abstract fun hsbDao(): HSBDao
    abstract fun ndbDao(): NDBDao
    abstract fun clamVersionDao(): CVDVersionDao
    abstract fun malwareScanDetailsDao(): MalwareScanDetailsDao

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
                    //.addMigrations(MIGRATION_149_151)
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
                //updateDB?.update()
            }
        }
    }


    class UpdateDb(db: AppDatabase, ctx: Context) {
        private val applicationDao: ApplicationDao
        private val permissionDao: PermissionDao
        private val applicationPermissionDao: ApplicationPermissionDao
        private val sysSettingDao: SysSettingsDao
        private val malwareDao: MalwareDao
        private val malwareCertDao: MalwareCertDao
        private val hsbDao: HSBDao
        private val ndbDao: NDBDao
        private val CVDVersionDao: CVDVersionDao
        private val malwareScanDao: MalwareScanDao
        private val context: Context

        init {
            context = ctx
            applicationDao = db.applicationDao()
            permissionDao = db.permissionDao()
            applicationPermissionDao = db.applicationPermissionDao()
            sysSettingDao = db.sysSettingsDao()
            malwareDao = db.malwareDao()
            malwareCertDao = db.malwareCertDao()
            hsbDao = db.hsbDao()
            ndbDao = db.ndbDao()
            CVDVersionDao = db.clamVersionDao()
            malwareScanDao = db.malwareScanDao()
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
        }

        fun update() {
            val dump = ApplicationPermissionHelper(context.applicationContext, true)
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
        }
    }
}