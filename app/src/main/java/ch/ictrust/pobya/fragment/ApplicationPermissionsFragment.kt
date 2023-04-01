package ch.ictrust.pobya.fragment


import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.utillies.ApplicationPermissionHelper
import ch.ictrust.pobya.utillies.Utilities
import ch.ictrust.pobya.activity.AppDetailActivity
import ch.ictrust.pobya.adapter.AppPermissionsRecyclerViewAdapter
import ch.ictrust.pobya.models.ApplicationPermissionCrossRef
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.models.PermissionModel
import ch.ictrust.pobya.repository.ApplicationPermissionRepository
import ch.ictrust.pobya.repository.ApplicationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ApplicationPermissionsFragment : Fragment() {

    private var mColumnCount = 1
    private lateinit var app: InstalledApplication
    private lateinit var recyclerView: RecyclerView
    private lateinit var tvNoPermission: TextView


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
        val view: View = inflater.inflate(R.layout.fragment_app_permissions, container, false)

        recyclerView = view.findViewById(R.id.rvPermissions)
        tvNoPermission = view.findViewById(R.id.tvNoPermission)

        val appUninstalled = ApplicationRepository.getInstance(
                                    view.context.applicationContext as Application
                            ).getAppByPackageName(app.packageName)?.uninstalled

        if (appUninstalled == true){

            return view
        }

        val applicationPermissionCrossRefList : ArrayList<ApplicationPermissionCrossRef> = ArrayList()
        var perms: List<PermissionModel>

        Utilities.dbScope.launch {

                val applicationPermissionHelper = ApplicationPermissionHelper(view.context, true)
                perms = applicationPermissionHelper.getAppPermissions(app.packageName)

                perms.forEach {
                    applicationPermissionCrossRefList.add(ApplicationPermissionCrossRef(app.packageName, it.permission))
                }

                ApplicationPermissionRepository(view.context).insertAll(applicationPermissionCrossRefList)
            withContext(Dispatchers.Main){


            if(applicationPermissionCrossRefList.isEmpty())
                tvNoPermission.visibility = View.VISIBLE

            recyclerView.apply {
                layoutManager = LinearLayoutManager(
                    view.context,
                    LinearLayoutManager.VERTICAL,
                    false
                )
                adapter = AppPermissionsRecyclerViewAdapter(perms)
            }
            }
        }


        return view
    }


    companion object {
        private const val ARG_COLUMN_COUNT = "column-count"
        fun newInstance(columnCount: Int): ApplicationPermissionsFragment {
            val fragment = ApplicationPermissionsFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}