package ch.ictrust.pobya.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.PermissionModel

class AppPermissionsRecyclerViewAdapter(permissions: List<PermissionModel>) :
    RecyclerView.Adapter<AppPermissionsRecyclerViewAdapter.ViewHolder>() {
    private val permissions: List<PermissionModel>

    init {
        this.permissions = permissions
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_app_permissions, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = permissions[position].label
        holder.tvPermissionName.text =
            permissions[position].label.replaceFirstChar { it.uppercase() }
        holder.tvPermissionDesc.text =
            permissions[position].description.replaceFirstChar { it.uppercase() }
    }

    override fun getItemCount(): Int {
        return permissions.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(
        view
    ) {

        val tvPermissionName: TextView
        val tvPermissionDesc: TextView
        var mItem: String? = null

        init {
            tvPermissionName = view.findViewById<View>(R.id.tvPermissionName) as TextView
            tvPermissionDesc = view.findViewById<View>(R.id.tvPermissionDesc) as TextView
        }

        override fun toString(): String {
            return super.toString() + " '" + tvPermissionDesc.text + "'"
        }
    }
}
