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


/**
 * A fragment representing a list of Items.
 *
 *
 * Activities containing this fragment MUST implement the [OnListFragmentInteractionListener]
 * interface.
 */
class AppPermissionsFragment
/**
 * Mandatory empty constructor for the fragment manager to instantiate the
 * fragment (e.g. upon screen orientation changes).
 */
    : Fragment() {
    // TODO: Customize parameters
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



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        //fun onListFragmentInteraction(item: ?)
    }

    companion object {
        // TODO: Customize parameter argument names
        private const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        fun newInstance(columnCount: Int): AppPermissionsFragment {
            val fragment = AppPermissionsFragment()
            val args = Bundle()
            args.putInt(ARG_COLUMN_COUNT, columnCount)
            fragment.arguments = args
            return fragment
        }
    }
}