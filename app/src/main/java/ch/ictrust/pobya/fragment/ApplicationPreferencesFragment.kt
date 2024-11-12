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
import android.widget.Spinner
import androidx.fragment.app.Fragment
import ch.ictrust.pobya.R
import ch.ictrust.pobya.R.layout
import ch.ictrust.pobya.utillies.Prefs
import com.google.android.material.switchmaterial.SwitchMaterial


class ApplicationPreferencesFragment : Fragment() {

    private lateinit var toggleButtonMonitoring: SwitchMaterial
    private lateinit var toggleButtonScanSysApps: SwitchMaterial
    private lateinit var databaseURLSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(layout.fragment_application_settings, container, false)

        toggleButtonScanSysApps = view.findViewById(R.id.toggleButtonScanSysApps)

        // Scan System Apps
        toggleButtonScanSysApps.isChecked =
            Prefs.getInstance(view.context)?.enableSysAppScan == true
        toggleButtonScanSysApps.setOnClickListener {
            Prefs.getInstance(view.context)?.mPrefs?.edit()?.putBoolean(
                Prefs.ENABLE_SYS_APPS_SCAN,
                toggleButtonScanSysApps.isChecked
            )!!.apply()
        }

        // Deep Scan (enable HSB and NDB signatures)
        val toggleButtonDeepScan: SwitchMaterial = view.findViewById(R.id.toggleButtonDeepScan)
        toggleButtonDeepScan.isChecked = Prefs.getInstance(view.context)?.deepScanEnabled == true
        toggleButtonDeepScan.setOnClickListener {
            Prefs.getInstance(view.context)?.mPrefs?.edit()?.putBoolean(
                Prefs.DEEP_SCAN_ENABLED,
                toggleButtonDeepScan.isChecked
            )!!.apply()
        }

        return view
    }

}