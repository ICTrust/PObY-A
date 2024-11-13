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

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.activity.AppDetailActivity
import ch.ictrust.pobya.adapter.AppsAdapter
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.repository.ApplicationRepository
import ch.ictrust.pobya.utillies.ApplicationPermissionHelper
import ch.ictrust.pobya.utillies.Utilities
import kotlinx.coroutines.launch

class ApplicationsFragment : Fragment() {

    private lateinit var progressScanApps: ProgressBar
    private lateinit var appsAdapter: AppsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var appChangeReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Utilities.dbScope.launch {
            ApplicationPermissionHelper(requireContext(), true).getListApps(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.applications_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_apps, container, false)
        Utilities.dbScope.launch {
            ApplicationPermissionHelper(requireContext(), true).getListApps(true)
        }
        recyclerView = view.findViewById(R.id.appsRecycleView)
        progressScanApps = view.findViewById(R.id.loading_spinner_apps)
        progressScanApps.visibility = View.VISIBLE
        appsAdapter = AppsAdapter(view.context.applicationContext)

        appsAdapter.setOnItemClickListener(object : AppsAdapter.OnItemClickListener {
            override fun onItemClick(app: InstalledApplication) {
                Utilities.dbScope.launch {
                    ApplicationRepository.getInstance(view.context.applicationContext as Application)
                        .getAppByPackageName(app.packageName)
                    val intent = Intent(view.context, AppDetailActivity::class.java)
                    intent.putExtra("app", app)
                    view.context.startActivity(intent)
                }
            }
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                view.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = appsAdapter
        }

        observeAppChanges()
        registerAppChangeReceiver()

        return view
    }

    private fun observeAppChanges() {
        ApplicationRepository.getInstance(requireContext().applicationContext as Application)
            .getThirdPartyApps().observe(viewLifecycleOwner, Observer { apps ->
                appsAdapter.submitList(apps)
                appsAdapter.notifyDataSetChanged()
                progressScanApps.visibility = View.GONE
            })
    }

    private fun registerAppChangeReceiver() {
        appChangeReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                observeAppChanges()
            }
        }

        val filter = IntentFilter().apply {
            addAction("ch.ictrust.pobya.APP_ADDED")
            addAction("ch.ictrust.pobya.APP_REMOVED")
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(
                appChangeReceiver,
                filter,
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            requireContext().registerReceiver(appChangeReceiver, filter)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireContext().unregisterReceiver(appChangeReceiver)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.showSystemApps -> {
                if (item.isChecked) {
                    progressScanApps.visibility = View.VISIBLE
                    ApplicationRepository.getInstance(requireContext().applicationContext as Application)
                        .getThirdPartyApps().observe(viewLifecycleOwner, Observer { apps ->
                            appsAdapter.submitList(apps)
                            progressScanApps.visibility = View.GONE
                        })
                    item.isChecked = !item.isChecked
                } else {
                    progressScanApps.visibility = View.VISIBLE
                    ApplicationRepository.getInstance(requireContext().applicationContext as Application)
                        .getSystemApps().observe(viewLifecycleOwner, Observer { apps ->
                            appsAdapter.submitList(apps)
                            progressScanApps.visibility = View.GONE
                        })
                    item.isChecked = !item.isChecked
                }
                appsAdapter.notifyDataSetChanged()

            }

            R.id.refresh -> {
                progressScanApps.visibility = View.VISIBLE
                Utilities.dbScope.launch {
                    appsAdapter.submitList(
                        ApplicationPermissionHelper(requireContext(), true)
                            .getListApps(true)
                    )
                }
                appsAdapter.notifyDataSetChanged()
                progressScanApps.visibility = View.GONE
            }
        }
        return true
    }
}