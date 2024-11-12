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
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import ch.ictrust.pobya.R
import ch.ictrust.pobya.activity.AppDetailActivity
import ch.ictrust.pobya.models.InstalledApplication
import java.text.SimpleDateFormat
import java.util.Date


class AppInfosFragment : Fragment() {
    private lateinit var app: InstalledApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appsDetailsActivity: AppDetailActivity = activity as AppDetailActivity
        app = appsDetailsActivity.getCurrentApp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

        val view: View = inflater.inflate(R.layout.fragment_app_infos, container, false)

        val appDetailPackageNameTV: TextView = view.findViewById(R.id.appDetailPackageName)
        appDetailPackageNameTV.text = app.packageName

        val appDetailStatusTV: TextView = view.findViewById(R.id.appDetailAppStatus)
        appDetailStatusTV.text = app.applicationState.toString()

        val appDetailInstallDateTV: TextView = view.findViewById(R.id.appDetailInstallDate)
        val installDate = Date(app.installedDate)

        appDetailInstallDateTV.text = dateFormat.format(installDate)

        val appDetailUpdateDateTV: TextView = view.findViewById(R.id.appDetailUpdateDate)
        val updateDate = Date(app.updateDate)
        appDetailUpdateDateTV.text = dateFormat.format(updateDate)

        val appDetailUninstallDate: TextView = view.findViewById(R.id.appDetailUninstallDate)
        val rlUninstallDate: RelativeLayout = view.findViewById(R.id.rlUninstallDate)
        rlUninstallDate.visibility = View.GONE

        if (app.uninstalled) {
            val uninstallDate = Date(app.uninstallDate)
            appDetailUninstallDate.text = dateFormat.format(uninstallDate)
            rlUninstallDate.visibility = View.VISIBLE
        }

        return view
    }
}