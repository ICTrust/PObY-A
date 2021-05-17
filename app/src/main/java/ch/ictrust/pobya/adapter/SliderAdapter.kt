package ch.ictrust.pobya.adapter

import android.content.Context
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

    private var slideImages = intArrayOf(R.drawable.ic_home, R.drawable.ic_security, R.drawable.ic_applist, R.drawable.ic_scan)

    private var slideHeadings = arrayOf("PObY-A", "Privacy settings", "List applications", "Malware scan")

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

        val slideImageView = view.findViewById(R.id.iv_image_icon) as ImageView
        val slideHeading = view.findViewById(R.id.tv_heading) as TextView
        val slideDescription = view.findViewById(R.id.tv_description) as TextView

        slideImageView.setImageResource(slideImages[position])
        slideHeading.text = slideHeadings[position]
        slideDescription.text = slideDescriptions[position]

        container.addView(view)
        return view

    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}
