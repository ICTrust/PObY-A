package ch.ictrust.pobya.activity

import android.Manifest
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import ch.ictrust.pobya.R
import ch.ictrust.pobya.fragment.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.app_bar_main.toolbarTitle
import android.view.animation.AnimationUtils


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

   private lateinit var menu : Menu

    private lateinit  var compName: ComponentName
    private lateinit var devicePolicyManager: DevicePolicyManager
    private val RESULT_ENABLE = 11
    private val CODE_WRITE_SETTINGS_PERMISSION = 42

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        enableAdmin()

        if (!Settings.System.canWrite(this)) {

            var intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
            intent.data = Uri.parse("package:" + this.packageName)
            this.startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION)
        }

        initViews()

    }

    private fun initViews(){

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
        transaction.replace(R.id.container, DashboardFragment())
        transaction.commit()

        navView.setNavigationItemSelectedListener(this)

    }



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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.


        when (item.itemId) {
            R.id.nav_dashboard -> {
                toolbarTitle.text = getString(R.string.menu_dashboard)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, DashboardFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.nav_malware_scan -> {
                toolbarTitle.text = getString(R.string.menu_malware_scan)
                val transaction = supportFragmentManager.beginTransaction()
                // Replace the fragment on container
                transaction.replace(R.id.container, MalwareScanFragment())
                transaction.addToBackStack(null)
                // Finishing the transition
                transaction.commit()
            }
            R.id.nav_settings_scan -> {
                toolbarTitle.text = getString(R.string.menu_privacy_settings)
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container, SettingsScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            R.id.nav_apps_info -> {
                val intent = Intent(this, InstalledAppsActivity::class.java)
                startActivity(intent)
            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }


    private fun enableAdmin(){

        compName = ComponentName(this, ch.ictrust.pobya.Utillies.AppAdminReceiver::class.java)
        devicePolicyManager = getSystemService(DEVICE_POLICY_SERVICE) as DevicePolicyManager

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN)
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName)

        intent.putExtra(
            DevicePolicyManager.EXTRA_ADD_EXPLANATION,
            R.string.admin_perm_force_lock_desc
        )
        startActivityForResult(intent, RESULT_ENABLE)



    }
}
