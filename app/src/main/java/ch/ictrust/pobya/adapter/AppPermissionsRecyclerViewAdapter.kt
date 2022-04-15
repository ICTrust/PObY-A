package ch.ictrust.pobya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.PermissionModel

class AppPermissionsRecyclerViewAdapter(appPerms: MutableList<PermissionModel>) :
    RecyclerView.Adapter<AppPermissionsRecyclerViewAdapter.ViewHolder> () {
    private var  permissions: MutableList<PermissionModel> = appPerms

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_app_permissions, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = permissions[position].label
        holder.mIdView.setText(permissions[position].permission)
        holder.mContentView.setText(permissions[position].description)
        //holder.mView.setOnClickListener {
            /*if (null != listener) {
                // Notify the active callbacks interface (the activity, if the
                // fragment is attached to one) that an item has been selected.
                // listener.onListFragmentInteraction(holder.mItem);
            }*/
        //}
    }

    override fun getItemCount(): Int {
        return permissions.size
    }

    class ViewHolder(val mView: View) : RecyclerView.ViewHolder(
        mView
    ) {
        val mIdView: TextView
        val mContentView: TextView
        var mItem: String? = null
        override fun toString(): String {
            return super.toString() + " '" + mContentView.text + "'"
        }

        init {
            mIdView = mView.findViewById<View>(R.id.id_text) as TextView
            mContentView = mView.findViewById<View>(R.id.content) as TextView
        }
    }


}