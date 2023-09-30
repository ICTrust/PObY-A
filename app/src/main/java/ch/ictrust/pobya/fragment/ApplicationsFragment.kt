package ch.ictrust.pobya.fragment


import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.activity.AppDetailActivity
import ch.ictrust.pobya.adapter.AppsAdapter
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.repository.ApplicationRepository
import ch.ictrust.pobya.utillies.ApplicationPermissionHelper
import ch.ictrust.pobya.utillies.Utilities
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.launch


class ApplicationsFragment : Fragment() {

    private lateinit var progressScanApps: ProgressBar
    private lateinit var appsAdapter: AppsAdapter
    private lateinit var tabLayout: TabLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_apps, container, false)

        recyclerView = view.findViewById(R.id.appsRecycleView)
        progressScanApps = view.findViewById(R.id.loading_spinner_apps)
        tabLayout = view.findViewById(R.id.tabLayout)
        progressScanApps.visibility = View.VISIBLE
        appsAdapter = AppsAdapter(view.context.applicationContext)
        Utilities.dbScope.launch {
            ApplicationPermissionHelper(view.context, true).getListApps(true)
        }

        appsAdapter.setOnItemClickListener(object : AppsAdapter.OnItemClickListener {
            override fun onItemClick(app: InstalledApplication) {
                Utilities.dbScope.launch {
                    ApplicationRepository.getInstance(view.context.applicationContext as Application)
                        .getAppByPackageName(app.packageName)
                    val intent = Intent(view.context, AppDetailActivity::class.java)
                    intent.putExtra("app", app)
                    view.context.startActivity(intent)
                }
            }
        })


        val categories = ArrayList<String>()
        categories.add(getString(R.string.third_party_apps))
        categories.add(getString(R.string.system_apps))
        categories.add(getString(R.string.uninstalled))

        for (category in categories) {
            tabLayout.addTab(tabLayout.newTab().setText(category))
        }

        // TODO : live refresh when application added or removed (migrate to ViewPager)
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                progressScanApps.visibility = View.VISIBLE

                when (tab.position) {
                    0 -> // Third parties Apps
                        ApplicationRepository.getInstance(view.context.applicationContext as Application)
                            .getThirdPartyApps().observe(requireActivity()) { apps ->
                                appsAdapter.submitList(apps)
                                recyclerView.apply {
                                    layoutManager = LinearLayoutManager(
                                        view.context,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                    adapter = appsAdapter
                                }
                                progressScanApps.visibility = View.GONE
                            }
                    1 -> // System Apps
                        ApplicationRepository.getInstance(view.context.applicationContext as Application)
                            .getSystemApps().observe(viewLifecycleOwner) { apps ->
                                appsAdapter.submitList(apps)
                                appsAdapter.notifyDataSetChanged()
                                progressScanApps.visibility = View.GONE
                            }
                    2 ->// Uninstalled Apps history
                        ApplicationRepository.getInstance(view.context.applicationContext as Application)
                            .getUninstalledApps().observe(
                                viewLifecycleOwner
                            ) { apps ->
                                appsAdapter.submitList(apps.toList())
                                appsAdapter.notifyDataSetChanged()
                                progressScanApps.visibility = View.GONE
                            }
                    else ->
                        ApplicationRepository.getInstance(view.context.applicationContext as Application)
                            .getAllApps().observe(
                                viewLifecycleOwner
                            ) { apps ->
                                appsAdapter.submitList(apps)
                                recyclerView.adapter = appsAdapter
                            }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                appsAdapter.notifyDataSetChanged()
                recyclerView.adapter = appsAdapter

                onTabSelected(tab)
            }
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(
                view.context,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = appsAdapter
        }
        tabLayout.getTabAt(0)?.select()

        return view
    }

}
