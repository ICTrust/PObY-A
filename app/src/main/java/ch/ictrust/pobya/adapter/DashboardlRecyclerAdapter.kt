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
import ch.ictrust.pobya.models.Category
import kotlinx.android.synthetic.main.dashboard_list_item.view.*

class DashboardRecyclerAdapter (private var items : List<Category>, val context: Context) : RecyclerView.Adapter<DashboardViewHolder>() {

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

        holder.itemView.setOnClickListener {
            clickListener?.onItemClick(pos)
        }
    }

    fun setClickListener(clickListener: ItemClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): DashboardViewHolder {
        return DashboardViewHolder(LayoutInflater.from(context).inflate(R.layout.dashboard_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }
}

class DashboardViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvName : TextView = view.tvDashboardItemName
    val ivMenuIcon: ImageView = view.ivMenuIcon
}