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
package ch.ictrust.pobya.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import ch.ictrust.pobya.R

class SliderAdapter(var context: Context) : PagerAdapter() {

    private var layoutInflater: LayoutInflater = LayoutInflater.from(context)

    private var slideImages = intArrayOf(
        R.drawable.ic_home,
        R.drawable.outline_admin_panel_settings_24,
        R.drawable.outline_app_settings_alt_24,
        R.drawable.outline_radar_24
    )

    private var slideHeadings = arrayOf(
        "PObY-A",
        context.getString(R.string.menu_privacy_settings),
        context.getString(R.string.menu_apps_info),
        context.getString(R.string.menu_malware_scan)
    )

    private var slideDescriptions = arrayOf(
        "Privacy Owned by You - Android",
        "Settings check and privacy guide",
        "List permissions and information of the installed applications",
        "scan for some known malware"
    )

    override fun getCount(): Int {
        return slideHeadings.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layoutInflater = LayoutInflater.from(context) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.slide_layout, container, false)
        val slideImageView: ImageView = view.findViewById(R.id.iv_image_icon)
        val slideHeading: TextView = view.findViewById(R.id.tv_heading)
        val slideDescription: TextView = view.findViewById(R.id.tv_description)

        Log.d("SliderAdapter", "instantiateItem called for position: $position")

        slideImageView.setImageResource(slideImages[position])
        slideImageView.visibility = View.VISIBLE
        slideImageView.contentDescription = slideHeadings[position]
        slideHeading.text = slideHeadings[position]
        slideDescription.text = slideDescriptions[position]
        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}
