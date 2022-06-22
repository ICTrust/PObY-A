package ch.ictrust.pobya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.activity.AppDetailActivity
import ch.ictrust.pobya.adapter.AppPermissionsRecyclerViewAdapter
import ch.ictrust.pobya.models.InstalledApp
import ch.ictrust.pobya.models.PermissionModel


class AppPermissionsFragment : Fragment() {

    private var mColumnCount = 1
    private lateinit var app: InstalledApp
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (arguments != null) {
            mColumnCount = arguments!!.getInt(ARG_COLUMN_COUNT)
        }
        val appsDetailsActivity: AppDetailActivity = activity as AppDetailActivity
        app = appsDetailsActivity.getCurrentApp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_app_permissions_list, container, false)
        recyclerView = view as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(view.getContext())
        val appPerms: MutableList<PermissionModel> = app.permissions
        recyclerView.adapter = AppPermissionsRecyclerViewAdapter(appPerms)
        return view
    }


    companion object {
        private const val ARG_COLUMN_COUNT = "column-count"

        fun newInstance(columnCount: Int): AppPermissionsFragment {
            val fragment = AppPermissionsFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}