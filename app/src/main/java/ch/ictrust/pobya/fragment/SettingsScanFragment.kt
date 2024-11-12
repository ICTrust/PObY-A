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
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.adapter.SettingsAdapter
import ch.ictrust.pobya.models.SysSettings
import ch.ictrust.pobya.utillies.SettingsHelper


class SettingsScanFragment : Fragment() {

    private var settingsList: MutableList<SysSettings> = mutableListOf()
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_settings_scan, container, false)

        settingsList = SettingsHelper(context).scan()
            .sortedWith(compareBy { it.currentValue == it.expectedValue })
            .toMutableList()

        settingsAdapter = SettingsAdapter(settingsList, view.context)
        progressBar = view.findViewById(R.id.loading_spinner_settings)
        progressBar.visibility = View.GONE

        view.findViewById<RecyclerView>(R.id.recyclerViewSettings).apply {
            settingsAdapter = SettingsAdapter(settingsList, view.context)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = settingsAdapter
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        settingsList = SettingsHelper(context).scan()
        view?.findViewById<RecyclerView>(R.id.recyclerViewSettings)?.apply {
            settingsAdapter = SettingsAdapter(settingsList, requireView().context)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = settingsAdapter
        }
    }
}