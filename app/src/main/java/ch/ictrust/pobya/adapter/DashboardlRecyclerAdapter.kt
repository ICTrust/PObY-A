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


import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.DashboardItem

class DashboardRecyclerAdapter(private var items: List<DashboardItem>, val context: Context) :
    RecyclerView.Adapter<DashboardViewHolder>() {

    private var clickListener: ItemClickListener? = null

    override fun onBindViewHolder(holder: DashboardViewHolder, pos: Int) {
        holder.tvName.text = items[pos].name
        holder.tvName.setTextColor(Color.BLACK)
        val unwrappedDrawable = AppCompatResources.getDrawable(
            context, items[pos].image
        )
        val wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable!!)
        DrawableCompat.setTint(wrappedDrawable, context.getColor(R.color.colorPrimary))

        holder.ivMenuIcon.setImageDrawable(wrappedDrawable)
        holder.tvDescription.text = items[pos].description

        holder.itemView.setOnClickListener {
            clickListener?.onItemClick(pos)
        }
    }

    fun setClickListener(clickListener: ItemClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DashboardViewHolder {
        return DashboardViewHolder(
            LayoutInflater.from(context).inflate(R.layout.dashboard_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    val tvName: TextView = view.findViewById(R.id.tvDashboardItemName)
    val ivMenuIcon: ImageView = view.findViewById(R.id.ivMenuIcon)
    val tvDescription: TextView = view.findViewById(R.id.tvDashboardItemDescription)
}