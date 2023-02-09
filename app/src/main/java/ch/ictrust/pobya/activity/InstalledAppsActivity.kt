package ch.ictrust.pobya.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import ch.ictrust.pobya.R
import ch.ictrust.pobya.Utillies.DumpApps
import ch.ictrust.pobya.adapter.InstalledAppsAdapter
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.InstalledApp
import com.github.clans.fab.FloatingActionButton
import kotlinx.android.synthetic.main.activity_installed_apps.*
import java.lang.ref.WeakReference


@Suppress("OverrideDeprecatedMigration")
class InstalledAppsActivity : AppCompatActivity(), ItemClickListener {


    private var applicationList: MutableList<InstalledApp> = mutableListOf()
    private lateinit var installedAppsAdapter: InstalledAppsAdapter
    private lateinit var progressScanApps : ProgressBar
    private lateinit var floatingActionButton: FloatingActionButton
    private var showSystemApps = false


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_installed_apps)

        progressScanApps = findViewById(R.id.loading_spinner_apps)
        floatingActionButton = findViewById(R.id.menu_item)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        actionbar!!.hide()

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationIcon(R.drawable.ic_back)


        toolbar.setNavigationOnClickListener {
            finish()
        }
        toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.show_sys_apps -> {
                }
            }
            true
        }

        val scanApps = AsyncDumpInstalledApps(this, false)
        scanApps.execute()

        floatingActionButton.labelText = getString(R.string.show_sys_apps)

        floatingActionButton.setOnClickListener {
            showSystemApps = !showSystemApps

                floatingActionButton.labelText = getString(R.string.hide_sys_apps)
                floatingActionButton.colorNormal = R.color.colorPrimaryDark
                val scanApps = AsyncDumpInstalledApps(this, showSystemApps)
                scanApps.execute()
                if(showSystemApps)
                    floatingActionButton.labelText = getString(R.string.hide_sys_apps)
                else
                    floatingActionButton.labelText = getString(R.string.show_sys_apps)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.show_sys_apps -> {
                val scanApps = AsyncDumpInstalledApps(this, true)
                scanApps.execute()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getTasks(listApps: MutableList<InstalledApp>){
        applicationList = listApps
        installedAppsAdapter = InstalledAppsAdapter(listApps, this)
        installedAppsAdapter.setClickListener(this)
        recyclerViewSettings.apply {
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = installedAppsAdapter
        }

    }

    override fun onItemClick(position: Int) {
        startActivity(Intent(this, AppDetailActivity::class.java).apply {
            putExtra("app", applicationList[position])
        })
    }

    class AsyncDumpInstalledApps(context: InstalledAppsActivity, dumpSysApps: Boolean) : AsyncTask<Void, Void, MutableList<InstalledApp>>() {

        private var appContext : Context = context

        private var dumpSysApps : Boolean = dumpSysApps
        private val activityReference: WeakReference<InstalledAppsActivity> = WeakReference(context)
        private var applicationList: MutableList<InstalledApp> = mutableListOf()


        @Deprecated("Deprecated in Java")
        @RequiresApi(Build.VERSION_CODES.M)
        override fun doInBackground(vararg params: Void): MutableList<InstalledApp>? {
            var dumpApps: DumpApps = DumpApps(appContext, dumpSysApps)
            dumpApps.getListApps().also { applicationList = it }
            return  applicationList
        }

        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            super.onPreExecute()
            val activity = activityReference.get()
            if (activity != null) {
                activity.progressScanApps.visibility = View.VISIBLE
            }
        }

        @Deprecated("Deprecated in Java")
        @RequiresApi(Build.VERSION_CODES.M)
        override fun onPostExecute(result: MutableList<InstalledApp>) {
            super.onPostExecute(result)
            val activity = activityReference.get()

            if (activity != null) {
                activity.progressScanApps.visibility = View.GONE
                activity.getTasks(result)
            }
        }
    }
}
