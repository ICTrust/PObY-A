/*
 * This file is part of PObY-A.
 *
 * Copyright (C) 2023 ICTrust Sàrl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
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
