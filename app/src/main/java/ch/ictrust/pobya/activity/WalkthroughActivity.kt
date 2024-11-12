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
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import ch.ictrust.pobya.R
import ch.ictrust.pobya.adapter.SliderAdapter
import ch.ictrust.pobya.utillies.Prefs

class WalkthroughActivity : AppCompatActivity() {

    private var mSlideViewPager: ViewPager? = null
    private var mDotsLayout: LinearLayout? = null
    private var mContinueButton: Button? = null

    private var mDots = arrayOfNulls<TextView>(4)
    private var sliderAdapter: SliderAdapter? = null

    private var mCurrentPage: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walkthrough)

        supportActionBar?.hide()

        mSlideViewPager = findViewById(R.id.slide_viewpager)
        mDotsLayout = findViewById(R.id.dots_layout)
        mContinueButton = findViewById(R.id.btnContinue)

        sliderAdapter = SliderAdapter(this)

        mSlideViewPager?.adapter = sliderAdapter

        addDotsIndicator(0)

        mSlideViewPager?.addOnPageChangeListener(viewListener)
        mContinueButton?.setOnClickListener {
            Prefs.getInstance(this)!!.isFirstRun = false
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    fun addDotsIndicator(position: Int) {

        mDots = arrayOfNulls(4)
        mDotsLayout?.removeAllViews()

        for (i in mDots.indices) {
            mDots[i] = TextView(this)
            mDots[i]?.text = Html.fromHtml("&#8226;")
            mDots[i]?.textSize = 35f
            mDots[i]?.setPadding(5, 0, 5, 0)
            mDots[i]?.setTextColor(Color.parseColor("#80ffffff"))

            mDotsLayout?.addView(mDots[i])
        }

        if (mDots.isNotEmpty()) {
            mDots[position]?.setTextColor(Color.parseColor("#ffffff"))
        }
    }

    private var viewListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                addDotsIndicator(position)
                mCurrentPage = position

                if (mCurrentPage == 3) {
                    mContinueButton?.visibility = View.VISIBLE
                } else {
                    mContinueButton?.visibility = View.GONE
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        }
}
