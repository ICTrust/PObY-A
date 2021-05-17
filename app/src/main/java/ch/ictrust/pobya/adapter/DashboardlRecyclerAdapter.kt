package ch.ictrust.pobya.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.Category
import kotlinx.android.synthetic.main.category_list_item.view.*


class DashboardRecyclerAdapter (private var items : List<Category>, val context: Context) : RecyclerView.Adapter<DashboardViewHolder>() {

    private var clickListener: ItemClickListener? = null

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: DashboardViewHolder, pos: Int) {
        holder?.tvName?.text = items[pos].name

        holder.ivAvatar.setBackgroundResource(items[pos].image)

        holder.itemView.setOnClickListener {
            clickListener!!.onItemClick(pos)
        }

    }

    fun setClickListener(clickListener: ItemClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DashboardViewHolder {
        return DashboardViewHolder(LayoutInflater.from(context).inflate(R.layout.category_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }


}

class DashboardViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val ivAvatar = view.ivCategory
}