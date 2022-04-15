package ch.ictrust.pobya.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ch.ictrust.pobya.R
import ch.ictrust.pobya.activity.*
import ch.ictrust.pobya.adapter.DashboardRecyclerAdapter
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.Category
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class DashboardFragment: Fragment(), ItemClickListener
    {


    private var categoryList: MutableList<Category> = mutableListOf()
    private lateinit var categoryAdapter: DashboardRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val view : View = inflater.inflate(R.layout.fragment_dashboard, container, false)


        initCategory()

        categoryAdapter = DashboardRecyclerAdapter(categoryList,context!!)
        categoryAdapter.setClickListener(this)
        view.categoryRecyclerView.apply {

            layoutManager = GridLayoutManager(this.context,3)
            adapter = categoryAdapter
        }
        return view
    }

    private fun initCategory(){

        categoryList.clear()

        val appsCategory = Category()
        appsCategory.name = "Applications"
        appsCategory.image = R.drawable.outline_app_settings_alt_24
        appsCategory.color = "#FFFFFF"
        appsCategory.bgColor = "#FFFFFF"
        categoryList.add(appsCategory)


        val malwareCategory = Category()
        malwareCategory.name = "Scanner"
        malwareCategory.image = R.drawable.outline_radar_24
        malwareCategory.color = "#FFFFFF"
        malwareCategory.bgColor = "#FFFFFF"
        categoryList.add(malwareCategory)


        val privacyCategory = Category()
        privacyCategory.name = "Privacy"
        privacyCategory.image = R.drawable.outline_admin_panel_settings_24
        privacyCategory.color = "#FFFFFF"
        privacyCategory.bgColor = "#FFFFFF"
        categoryList.add(privacyCategory)
    }


    override fun onStart() {
        super.onStart()
        activity!!.nav_view.setCheckedItem(R.id.nav_dashboard)
        activity!!.toolbar.toolbarTitle.text = "Dashboard"
    }


    override fun onItemClick(position: Int) {
        when (position){
            0 -> {
                startActivity(Intent(context,InstalledAppsActivity::class.java))
            }
            1 -> {
                val transaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container, MalwareScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            2 -> {
                val transaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container, SettingsScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }
}