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

import android.content.Intent
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import ch.ictrust.pobya.R
import ch.ictrust.pobya.fragment.AppInfosFragment
import ch.ictrust.pobya.fragment.ApplicationPermissionsFragment
import ch.ictrust.pobya.fragment.ApplicationThreatInfoFragment
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.utillies.AppViewPagerAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.tabs.TabLayout


class AppDetailActivity : AppCompatActivity() {

    private lateinit var appIconView: ShapeableImageView
    private lateinit var viewPager: ViewPager
    private lateinit var currentApp: InstalledApplication
    private lateinit var toolbarTitle: TextView
    private lateinit var btnOpenSettings: Button
    private lateinit var btnUninstall: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_detail)

        val actionbar: ActionBar? = supportActionBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar_app_details)
        val appTabLayout = findViewById<TabLayout>(R.id.tab_layout_app_details)
        val appViewPagerAdapter = AppViewPagerAdapter(supportFragmentManager)
        val radius = resources.getDimension(R.dimen.roundedCornerAppDetails)
        //val header = findViewById<FrameLayout>(R.id.flMainContainer)
        val tvMalwareDetection = findViewById<TextView>(R.id.tvMalwareDetection)
        val tvMalwareSignature = findViewById<TextView>(R.id.tvThreatName)

        actionbar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(true)
        }
        actionbar!!.hide()

        appIconView = findViewById(R.id.appDetailIcon)
        toolbarTitle = findViewById(R.id.appInfoToolbarTitle)
        btnUninstall = findViewById(R.id.uninstall)
        btnOpenSettings = findViewById(R.id.open_app_settings)

        currentApp = intent.getParcelableExtra("app")!!

        if (currentApp.flaggedAsThreat) {
            tvMalwareSignature.text = currentApp.flagReason
            tvMalwareSignature.visibility = TextView.VISIBLE
            tvMalwareDetection.visibility = TextView.VISIBLE
        } else {
            tvMalwareSignature.visibility = TextView.GONE
            tvMalwareSignature.height = 0
            tvMalwareDetection.visibility = TextView.GONE
        }

        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            finish()
        }


        if (currentApp.uninstalled) {
            btnOpenSettings.isEnabled = false
            btnUninstall.isEnabled = false
        } else {
            btnOpenSettings.setOnClickListener {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", currentApp.packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            btnUninstall.setOnClickListener {
                val intent = Intent(Intent.ACTION_DELETE)
                intent.data = Uri.parse("package:${currentApp.packageName}")
                startActivity(intent)
            }
        }

        appIconView.setImageIcon(Icon.createWithData(currentApp.icon, 0, currentApp.icon.size))
        appIconView.shapeAppearanceModel = appIconView.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()
        toolbarTitle.text = currentApp.name

        viewPager = findViewById(R.id.appDetailsPager)


        appViewPagerAdapter.addFragment(ApplicationPermissionsFragment(), "Permissions")
        appViewPagerAdapter.addFragment(AppInfosFragment(), "Information")
        if (currentApp.flaggedAsThreat) {
            appViewPagerAdapter.addFragment(ApplicationThreatInfoFragment(), "Threat Info")
        }

        viewPager.adapter = appViewPagerAdapter
        appTabLayout.setupWithViewPager(viewPager)
    }

    fun getCurrentApp(): InstalledApplication {
        return this.currentApp
    }
}
