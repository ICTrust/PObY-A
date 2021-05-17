package ch.ictrust.pobya.activity

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import ch.ictrust.pobya.R
import ch.ictrust.pobya.Utillies.AppViewPagerAdapter
import ch.ictrust.pobya.fragment.*
import ch.ictrust.pobya.models.InstalledApp
import com.google.android.material.tabs.TabLayout
import de.hdodenhof.circleimageview.CircleImageView


class AppDetailActivity : AppCompatActivity(){

    private lateinit var appIconView : CircleImageView
    private lateinit var viewPager: ViewPager
    private lateinit var currentApp: InstalledApp
    private lateinit var toolbarTitle : TextView
    private lateinit var btnOpenSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)

        val actionbar: ActionBar? = supportActionBar
        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        actionbar!!.hide()

        appIconView = findViewById(R.id.appDetailIcon)
        toolbarTitle = findViewById(R.id.toolbarTitle)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_app_details)
        toolbar.setNavigationIcon(R.drawable.ic_back)


        toolbar.setNavigationOnClickListener {
            finish()
        }

        currentApp = intent.getParcelableExtra("app")

        val bitmap = BitmapFactory.decodeByteArray(currentApp.icon, 0, currentApp.icon.size)

        btnOpenSettings = findViewById(R.id.open_app_settings)

        btnOpenSettings.setOnClickListener(View.OnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", currentApp.packageName, null)
            intent.data = uri
            startActivity(intent)
        })

        appIconView.setImageBitmap(bitmap)
        toolbarTitle.text = currentApp.name

        viewPager = findViewById(R.id.appDetailsPager)
        val appTabLayout = findViewById<TabLayout>(R.id.tab_layout_app_details)

        val appViewPagerAdapter = AppViewPagerAdapter(supportFragmentManager)
        appViewPagerAdapter.addFragment(AppInfosFragment(), "Information")
        appViewPagerAdapter.addFragment(AppPermissionsFragment(), "Permissions")
        viewPager.adapter = appViewPagerAdapter
        appTabLayout.setupWithViewPager(viewPager)
    }

    fun getCurrentApp(): InstalledApp {
        return this.currentApp
    }
}
