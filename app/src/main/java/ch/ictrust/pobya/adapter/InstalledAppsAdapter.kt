package ch.ictrust.pobya.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.AppState
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.*
import kotlinx.android.synthetic.main.app_item.view.*


class InstalledAppsAdapter (private var items : List<InstalledApp>, val context: Context) : RecyclerView.Adapter<InstalledAppViewHolder>() {

    private var clickListener: ItemClickListener? = null


    @SuppressLint("Range")
    override fun onBindViewHolder(holder: InstalledAppViewHolder, pos: Int) {


        val installedApp = items[pos]

        holder.tvTitle.text = installedApp.name

        when (installedApp.appState){
            AppState.DANGEROUS -> {

                holder.ivTask.setImageBitmap(BitmapFactory.decodeByteArray(installedApp.icon, 0, installedApp.icon.size));
                holder.tvStatus.text = context.getString(R.string.warning)
                holder.tvStatus.setTextColor(context.resources.getColor(R.color.warningColor))
            }
            AppState.NORMAL -> {
                holder.ivTask.setImageBitmap(BitmapFactory.decodeByteArray(installedApp.icon, 0, installedApp.icon.size));
                holder.tvStatus.text = context.getString(R.string.normal)
                holder.tvStatus.setTextColor(context.resources.getColor(R.color.doneColor))
            }
            AppState.MEDIUM -> {
                holder.ivTask.setImageBitmap(BitmapFactory.decodeByteArray(installedApp.icon, 0, installedApp.icon.size));
                holder.tvStatus.text = context.getString(R.string.medium)
                holder.tvStatus.setTextColor(context.resources.getColor(R.color.firstColor))
            }
        }

        holder.itemView.setOnClickListener {
            clickListener?.onItemClick(pos)
        }

    }


    fun setClickListener(clickListener: ItemClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): InstalledAppViewHolder {
        return InstalledAppViewHolder(LayoutInflater.from(context).inflate(R.layout.app_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }



}

class InstalledAppViewHolder (view: View) : RecyclerView.ViewHolder(view) {

    val tvTitle = view.tvTitle!!
    val tvStatus = view.tvStatus!!
    val ivTask = view.ivTask!!
}