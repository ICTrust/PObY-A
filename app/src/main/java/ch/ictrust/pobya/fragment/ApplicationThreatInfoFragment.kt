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
import ch.ictrust.pobya.R
import ch.ictrust.pobya.activity.AppDetailActivity
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.repository.MalwareScanDetailsRepository
import ch.ictrust.pobya.repository.MalwareScanRepository
import ch.ictrust.pobya.utillies.Utilities
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat


class ApplicationThreatInfoFragment : Fragment() {

    private var mColumnCount = 1
    private lateinit var app: InstalledApplication
    private lateinit var appThreatName: TextView
    private lateinit var tvPackageName: TextView
    private lateinit var tvScanDateTime: TextView
    private lateinit var tvAppName: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = requireArguments().getInt(ARG_COLUMN_COUNT)
        }
        val appsDetailsActivity: AppDetailActivity = activity as AppDetailActivity
        app = appsDetailsActivity.getCurrentApp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_app_threat_info, container, false)

        appThreatName = view.findViewById(R.id.tvThreatName)
        tvScanDateTime = view.findViewById(R.id.tvScanDateTime)
        tvPackageName = view.findViewById(R.id.tvPackageName)
        tvAppName = view.findViewById(R.id.tvAppName)

        if (!app.flaggedAsThreat) {
            return view
        }

        appThreatName.text = app.flagReason
        tvPackageName.text = app.packageName
        tvAppName.text = app.name

        Utilities.dbScope.launch {
            val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")
            val scanDetail = MalwareScanDetailsRepository.getInstance(requireActivity().application)
                .loadLastByPackageName(app.packageName)

            val scan = MalwareScanRepository.getInstance(requireActivity().application)
                .getMalwareScanById(scanDetail.idScan)

            withContext(Dispatchers.Main) {
                if (scan != null) {
                    tvScanDateTime.text = dateFormat.format(scan.scanDateTime)
                }
            }
        }

        return view
    }


    companion object {
        private const val ARG_COLUMN_COUNT = "column-count"
    }
}