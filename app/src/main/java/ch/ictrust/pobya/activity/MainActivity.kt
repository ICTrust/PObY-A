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
package ch.ictrust.pobya.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.StrictMode
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ch.ictrust.pobya.BuildConfig
import ch.ictrust.pobya.R
import ch.ictrust.pobya.fragment.ApplicationPreferencesFragment
import ch.ictrust.pobya.fragment.ApplicationsFragment
import ch.ictrust.pobya.fragment.DashboardFragment
import ch.ictrust.pobya.fragment.DataSafetyPolicyFragment
import ch.ictrust.pobya.fragment.MalwareScanFragment
import ch.ictrust.pobya.fragment.SettingsScanFragment
import ch.ictrust.pobya.service.MalwareScanService
import ch.ictrust.pobya.utillies.PackageInstallUninstallReceiver
import ch.ictrust.pobya.utillies.SettingsHelper
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var menu: Menu
    private val TAG = "MainActivity"
    private lateinit var compName: ComponentName
    private lateinit var devicePolicyManager: DevicePolicyManager
    private var CODE_WRITE_SETTINGS_PERMISSION = 42
    private var READ_EXTERNAL_STORAGE = 1
    private lateinit var toolbarTitle: TextView


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        toolbarTitle = findViewById(R.id.toolbarTitle)

        /*
          // Debuging
          if (BuildConfig.DEBUG) {
          StrictMode.setThreadPolicy(
               StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .penaltyDeath()
                    .build()
            )
        }*/
        //init device policy manager
        compName = ComponentName(this, ch.ictrust.pobya.utillies.AppAdminReceiver::class.java)
        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

        initViews()

        val intentFilter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addDataScheme("package")
        }

        intentFilter.priority = 999

        val rec = PackageInstallUninstallReceiver()
        registerReceiver(rec, intentFilter)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_SETTINGS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_SETTINGS),
                1
            )
        }

        SettingsHelper(applicationContext).scan()


        if (!Settings.System.canWrite(applicationContext)) {
            val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + this.packageName)
            startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION)
        }

    }

    private fun initViews() {

        supportActionBar?.show()
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.flMainContainer, DashboardFragment())
        transaction.commit()

        navView.setNavigationItemSelectedListener(this)
    }


    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_dashboard -> {
                toolbarTitle.text = getString(R.string.menu_dashboard)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, DashboardFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            R.id.nav_malware_scan -> {
                toolbarTitle.text = getString(R.string.menu_malware_scan)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, MalwareScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()

            }

            R.id.nav_settings_scan -> {
                toolbarTitle.text = getString(R.string.menu_privacy_settings)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, SettingsScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            R.id.nav_apps_info -> {
                toolbarTitle.text = getString(R.string.menu_apps_info)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, ApplicationsFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            R.id.nav_preferences -> {
                toolbarTitle.text = getString(R.string.menu_preferences)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, ApplicationPreferencesFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            R.id.nav_data_safety -> {
                toolbarTitle.text = getString(R.string.menu_data_safety)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, DataSafetyPolicyFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(applicationContext, MalwareScanService::class.java)
        stopService(intent)
    }


}
