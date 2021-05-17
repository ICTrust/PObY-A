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


class CategoryHorizontalRecyclerAdapter (private var items : List<Category>, val context: Context) : RecyclerView.Adapter<CategoryHorizontalViewHolder>() {

    private var clickListener: ItemClickListener? = null


    @SuppressLint("Range")
    override fun onBindViewHolder(holder: CategoryHorizontalViewHolder, pos: Int) {
        holder?.tvName?.text = items[pos].name

        //holder.ivAvatar.borderColor = Color.parseColor(items[pos].color)


        holder.itemView.setOnClickListener {
            clickListener!!.onItemClick(pos)
        }

    }

    fun setClickListener(clickListener: ItemClickListener) {
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): CategoryHorizontalViewHolder {
        return CategoryHorizontalViewHolder(LayoutInflater.from(context).inflate(R.layout.category_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }


}

class CategoryHorizontalViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvName = view.tvName
    val ivAvatar = view.ivCategory
}