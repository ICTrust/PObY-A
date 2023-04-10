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


class SettingsAdapter(private var items: MutableList<SysSettings>, var context: Context) : RecyclerView.Adapter<SettingsViewHolder>() {


    override fun onBindViewHolder(holder: SettingsViewHolder, pos: Int) {

        val sysSetting = items[pos]

        holder.tvContent.text = sysSetting.testDescription

        if (sysSetting.currentValue == sysSetting.expectedValue){
            holder.viewStatusIcon.setBackgroundResource(R.drawable.ic_baseline_check_24)
        } else {
            holder.viewStatusIcon.setBackgroundResource(R.drawable.ic_baseline_warning_24)
        }
        holder.tvDetail.text = sysSetting.info

        holder.itemView.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(context)
                .setTitle(sysSetting.testDescription)
                .setMessage(sysSetting.action)
                .setIcon(R.drawable.ic_baseline_tips_and_updates_24)
                .setPositiveButton(
                    "Start"
                ) { _, _ ->
                    val obj = SettingsHelper(context)
                    val method: Method = obj.javaClass.getMethod(sysSetting.functionName)
                    method.invoke(obj)
                }
                .setNegativeButton("Cancel", null)
                .show()
        })
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
    val tvDetail: TextView = view.findViewById(R.id.setting_key_tv)
    val tvContent: TextView = view.findViewById(R.id.setting_desc_tv)
    val viewStatusIcon: View = view.findViewById(R.id.viewStatusIcon)
}

