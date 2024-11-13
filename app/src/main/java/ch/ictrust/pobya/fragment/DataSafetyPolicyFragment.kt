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
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import ch.ictrust.pobya.R


class DataSafetyPolicyFragment : Fragment() {

    lateinit var wvDataSafetyPolicyFragment: WebView

    companion object
    {
        var flag: Boolean = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = inflater.inflate(R.layout.fragment_data_safety_policy, container, false)
        view.findViewById<WebView>(R.id.wvDataSafetyPolicy).apply {

            wvDataSafetyPolicyFragment = view.findViewById(R.id.wvDataSafetyPolicy)
            try {
                wvDataSafetyPolicyFragment.settings.javaScriptEnabled = true
                wvDataSafetyPolicyFragment.loadUrl("file:///android_res/raw/policy.html")
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        return view
    }
}