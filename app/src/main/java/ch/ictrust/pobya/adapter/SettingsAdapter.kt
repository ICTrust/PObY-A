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

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.SysSettings
import ch.ictrust.pobya.utillies.SettingsHelper
import java.lang.reflect.Method


class SettingsAdapter(private var items: MutableList<SysSettings>, private var context: Context) :
    RecyclerView.Adapter<SettingsViewHolder>() {

    // sort items where expectedValue != currentValue
    init {
        items = items.sortedWith(
            compareBy({ it.currentValue == it.expectedValue },
                { it.testDescription })
        ).toMutableList()
    }


    override fun onBindViewHolder(holder: SettingsViewHolder, pos: Int) {

        val sysSetting = items[pos]

        holder.tvContent.text = sysSetting.testDescription

        if (sysSetting.currentValue == sysSetting.expectedValue) {
            holder.viewStatusIcon.setBackgroundResource(R.drawable.ic_baseline_check_24)
        } else {
            holder.viewStatusIcon.setBackgroundResource(R.drawable.ic_baseline_warning_24)
        }

        holder.tvDetail.text = sysSetting.info

        holder.itemView.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(sysSetting.testDescription)
                .setMessage(sysSetting.action)
                .setIcon(R.drawable.ic_baseline_tips_and_updates_24)
                .setPositiveButton(
                    holder.currentView.context.getString(R.string.settings_action_start)
                ) { _, _ ->
                    val obj = SettingsHelper(context)
                    try {
                        val method: Method = obj.javaClass.getMethod(sysSetting.functionName)
                        method.invoke(obj)
                    } catch (e: NoSuchMethodException) {
                        e.printStackTrace()
                    }

                    items = SettingsHelper(holder.currentView.context).scan()
                    // sort items by: expectedValue != currentValue
                    items = items.sortedWith(
                        compareBy({ it.currentValue == it.expectedValue },
                            { it.testDescription })
                    ).toMutableList()
                    notifyDataSetChanged()
                    // TODO: Refresh Recycler view if operation succeeded
                }
                .setNegativeButton(
                    holder.currentView.context.getString(R.string.settings_action_cancel),
                    null
                )
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): SettingsViewHolder {
        return SettingsViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.setting_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class SettingsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    val currentView: View = view
    val tvDetail: TextView = view.findViewById(R.id.setting_key_tv)
    val tvContent: TextView = view.findViewById(R.id.setting_desc_tv)
    val viewStatusIcon: View = view.findViewById(R.id.viewStatusIcon)
}

