package ch.ictrust.pobya.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.SysSetting
import kotlinx.android.synthetic.main.setting_item.view.*
import java.lang.reflect.Method


class SettingsAdapter(private var items: MutableList<SysSetting>, var context: Context) : RecyclerView.Adapter<SettingsViewHolder>() {

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: SettingsViewHolder, pos: Int) {

        val sysSetting = items[pos]

        holder.tvContent.text = sysSetting.testDescription

        if (sysSetting.currentValue.toString() == sysSetting.expectedValue.toString()){
            holder.typeView.setBackgroundResource(R.drawable.ic_baseline_check_24)
        } else {
            holder.typeView.setBackgroundResource(R.drawable.ic_baseline_warning_24)
        }
        holder.tvDetail.text = sysSetting.info
        /*if (sysSetting.currentValue == sysSetting.expectedValue ) {
            holder.tvValue.text = context.getString(R.string.true_string)
        } else {
            holder.tvValue.text = context.getString(R.string.warning)
        }*/


        holder.itemView.setOnClickListener(View.OnClickListener {
            AlertDialog.Builder(context)
                .setTitle(sysSetting.testDescription)
                .setMessage(sysSetting.action)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(
                    "Start"
                ) { dialog, whichButton ->
                    var obj = ch.ictrust.pobya.Utillies.SettingsHelper(context)
                    val method: Method = obj.javaClass.getMethod(sysSetting.functionName)

                    method.invoke(obj)

                }
                .setNegativeButton(android.R.string.no, null).show()
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
    val tvDetail = view.setting_key_tv!!
    val tvContent = view.setting_desc_tv!!
    //val tvValue = view.setting_value_tv!!
    val typeView = view.type!!
}

