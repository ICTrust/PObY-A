package ch.ictrust.pobya.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.SysSetting


class SettingsListAdapter (var context: Context,
                           var settings: MutableList<SysSetting>
) : BaseAdapter() {

    private val mInflator: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return settings.size
    }

    override fun getItem(position: Int): SysSetting {
        return settings[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class ViewHolder {
        lateinit var settingsDesc: TextView
        lateinit var settingsCurrent: TextView
        lateinit var settingsKey: TextView
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var convertView = convertView
        val holder: ViewHolder
        val sysSetting: SysSetting = getItem(position)
        try {
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.setting_item, parent, false)

                holder = ViewHolder()
                holder.settingsDesc =
                    convertView!!.findViewById<View>(R.id.setting_desc_tv) as TextView
                holder.settingsCurrent =
                    convertView.findViewById<View>(R.id.setting_value_tv) as TextView
                holder.settingsKey =
                    convertView.findViewById<View>(R.id.setting_key_tv) as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }

            holder.settingsDesc.setText(sysSetting.testDescription)
            holder.settingsCurrent.setText(sysSetting.currentValue.toString() + "")
            holder.settingsKey.setText(sysSetting.expectedValue.toString() + "")
        } catch (e: Exception) {
        }
        return convertView
    }

}
