package ch.ictrust.pobya.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ch.ictrust.pobya.R
import ch.ictrust.pobya.adapter.DashboardRecyclerAdapter
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.Category
import com.google.android.flexbox.AlignItems
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_dashboard.view.*


class DashboardFragment: Fragment(), ItemClickListener {
    private var categoryList: MutableList<Category> = mutableListOf()
    private lateinit var categoryAdapter: DashboardRecyclerAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view : View = inflater.inflate(R.layout.fragment_dashboard, container, false)
        initCategory()
        categoryAdapter = DashboardRecyclerAdapter(categoryList, view.context)
        categoryAdapter.setClickListener(this)
        view.categoryRecyclerView.apply {
            elevation = 10F
            layoutManager = GridLayoutManager(this.context,2)
            layoutManager = FlexboxLayoutManager(context).apply {
                justifyContent = JustifyContent.CENTER
                alignItems = AlignItems.STRETCH
                flexDirection = FlexDirection.ROW
                flexWrap = FlexWrap.WRAP

            }
            adapter = categoryAdapter
        }
        return view
    }

    private fun initCategory(){

        categoryList.clear()

        val appsCategory = Category()
        appsCategory.name = getString(R.string.menu_apps_info)
        appsCategory.image = R.drawable.outline_app_settings_alt_24
        categoryList.add(appsCategory)


        val malwareCategory = Category()
        malwareCategory.name = getString(R.string.menu_malware_scan)
        malwareCategory.image = R.drawable.outline_radar_24
        categoryList.add(malwareCategory)


        val privacyCategory = Category()
        privacyCategory.name = getString(R.string.menu_privacy_settings)
        privacyCategory.image = R.drawable.outline_admin_panel_settings_24
        categoryList.add(privacyCategory)


        val dataSafetyCategory = Category()
        dataSafetyCategory.name = "Data Safety"
        dataSafetyCategory.image = R.drawable.ic_baseline_privacy_tip_24
        categoryList.add(dataSafetyCategory)
    }


    override fun onStart() {
        super.onStart()
        activity?.nav_view?.setCheckedItem(R.id.nav_dashboard)
        activity?.toolbar?.toolbarTitle?.text = getString(R.string.menu_dashboard)
    }


    override fun onItemClick(position: Int) {

        when (position){
            0 -> {

                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, ApplicationsFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            1 -> {
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, MalwareScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()

            }
            2 -> {
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, SettingsScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            3 -> {
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, DataSafetyPolicyFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
        }
    }

}