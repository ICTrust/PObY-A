package ch.ictrust.pobya.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.adapter.DashboardRecyclerAdapter
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.Category
import com.google.android.flexbox.*
import com.google.android.material.navigation.NavigationView


class DashboardFragment : Fragment(), ItemClickListener {
    private var categoryList: MutableList<Category> = mutableListOf()
    private lateinit var categoryAdapter: DashboardRecyclerAdapter
    private lateinit var toolbarTitle: TextView
    private lateinit var navView: NavigationView


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_dashboard, container, false)

        initCategory()
        toolbarTitle = requireActivity().findViewById(R.id.toolbarTitle)
        navView = requireActivity().findViewById(R.id.nav_view)

        categoryAdapter = DashboardRecyclerAdapter(categoryList, view.context)
        categoryAdapter.setClickListener(this)
        view.findViewById<RecyclerView>(R.id.categoryRecyclerView).apply {
            elevation = 10F
            layoutManager = GridLayoutManager(this.context, 2)
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

    private fun initCategory() {

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

        val appPreferences = Category()
        appPreferences.name = getString(R.string.menu_preferences)
        appPreferences.image = R.drawable.ic_outline_preferences_24
        categoryList.add(appPreferences)


        val dataSafetyCategory = Category()
        dataSafetyCategory.name = getString(R.string.menu_data_safety)
        dataSafetyCategory.image = R.drawable.ic_baseline_privacy_tip_24
        categoryList.add(dataSafetyCategory)
    }


    override fun onItemClick(position: Int) {

        when (position) {
            0 -> {
                toolbarTitle.text = getString(R.string.menu_apps_info)
                navView.menu.getItem(3).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, ApplicationsFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            1 -> {
                toolbarTitle.text = getString(R.string.menu_malware_scan)
                navView.menu.getItem(1).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, MalwareScanFragment())
                transaction.addToBackStack(null)

                transaction.commit()

            }
            2 -> {
                toolbarTitle.text = getString(R.string.menu_privacy_settings)
                navView.menu.getItem(2).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, SettingsScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            3 -> {
                toolbarTitle.text = "Presences"
                navView.menu.getItem(4).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, ApplicationPreferencesFragment())
                transaction.addToBackStack(null)

                transaction.commit()
            }
            4 -> {
                toolbarTitle.text = getString(R.string.menu_data_safety)
                navView.menu.getItem(5).isChecked = true
                val transaction = parentFragmentManager.beginTransaction()
                transaction.replace(R.id.flMainContainer, DataSafetyPolicyFragment())
                transaction.addToBackStack(null)

                transaction.commit()
            }
        }
    }

}