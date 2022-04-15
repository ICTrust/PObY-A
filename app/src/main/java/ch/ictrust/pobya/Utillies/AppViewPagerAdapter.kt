package ch.ictrust.pobya.Utillies

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class AppViewPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm) {
    private var  fragmentList: MutableList<Fragment> = mutableListOf()
    private var listTitles: MutableList<String> = mutableListOf()

    override fun getItem(position: Int): Fragment {
        return fragmentList[position]
    }

    override fun getCount(): Int {
        return listTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return listTitles[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        fragmentList.add(fragment)
        listTitles.add(title)
    }
}