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
package ch.ictrust.pobya.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.adapter.DashboardRecyclerAdapter
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.DashboardItem
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.navigation.NavigationView


class DashboardFragment : Fragment(), ItemClickListener {
    private var menuList: MutableList<DashboardItem> = mutableListOf()
    private lateinit var dashboardRecyclerAdapter: DashboardRecyclerAdapter
    private lateinit var toolbarTitle: TextView
    private lateinit var navView: NavigationView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_dashboard, container, false)

        initCategory()
        toolbarTitle = requireActivity().findViewById(R.id.toolbarTitle)
        navView = requireActivity().findViewById(R.id.nav_view)

        dashboardRecyclerAdapter = DashboardRecyclerAdapter(menuList, view.context)
        dashboardRecyclerAdapter.setClickListener(this)
        view.findViewById<RecyclerView>(R.id.dashboardRecyclerView).apply {
            elevation = 10F
            layoutManager = GridLayoutManager(this.context, 2)
            layoutManager = FlexboxLayoutManager(context).apply {
                justifyContent = JustifyContent.CENTER
                alignItems = AlignItems.STRETCH
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP

            }
            adapter = dashboardRecyclerAdapter
        }
        return view
    }

    private fun initCategory() {

        menuList.clear()

        val appsDashboardItem = DashboardItem()
        appsDashboardItem.name = getString(R.string.menu_apps_info)
        appsDashboardItem.description = getString(R.string.applications_settings_description)
        appsDashboardItem.image = R.drawable.outline_app_settings_alt_24
        menuList.add(appsDashboardItem)


        val malware = DashboardItem()
        malware.name = getString(R.string.menu_malware_scan)
        malware.image = R.drawable.outline_radar_24
        malware.description = getString(R.string.malware_scan_description)
        menuList.add(malware)


        val privacyDashboardItem = DashboardItem()
        privacyDashboardItem.name = getString(R.string.menu_privacy_settings)
        privacyDashboardItem.description = getString(R.string.privacy_settings_description)
        privacyDashboardItem.image = R.drawable.outline_admin_panel_settings_24
        menuList.add(privacyDashboardItem)

        val appPreferences = DashboardItem()
        appPreferences.name = getString(R.string.menu_preferences)
        appPreferences.description = getString(R.string.preference_settings_description)
        appPreferences.image = R.drawable.ic_outline_preferences_24
        menuList.add(appPreferences)


        val dataSafetyDashboardItem = DashboardItem()
        dataSafetyDashboardItem.name = getString(R.string.menu_data_safety)
        dataSafetyDashboardItem.description = getString(R.string.data_safety_description)
        dataSafetyDashboardItem.image = R.drawable.ic_baseline_privacy_tip_24
        menuList.add(dataSafetyDashboardItem)
    }


    override fun onItemClick(position: Int) {

        when (position) {
            0 -> {
                toolbarTitle.text = getString(R.string.menu_apps_info)
                navView.menu.getItem(3).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, ApplicationsFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            1 -> {
                toolbarTitle.text = getString(R.string.menu_malware_scan)
                navView.menu.getItem(1).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, MalwareScanFragment())
                transaction.addToBackStack(null)

                transaction.commit()

            }

            2 -> {
                toolbarTitle.text = getString(R.string.menu_privacy_settings)
                navView.menu.getItem(2).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, SettingsScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

            3 -> {
                toolbarTitle.text = getString(R.string.menu_preferences)
                navView.menu.getItem(4).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, ApplicationPreferencesFragment())
                transaction.addToBackStack(null)

                transaction.commit()
            }

            4 -> {
                toolbarTitle.text = getString(R.string.menu_data_safety)
                navView.menu.getItem(5).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, DataSafetyPolicyFragment())
                transaction.addToBackStack(null)

                transaction.commit()
            }
        }
    }

}