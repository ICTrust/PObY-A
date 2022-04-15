package ch.ictrust.pobya.fragment


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.baoyz.actionsheet.ActionSheet
import ch.ictrust.pobya.R
import ch.ictrust.pobya.activity.*
import ch.ictrust.pobya.adapter.CategoryHorizontalRecyclerAdapter
import ch.ictrust.pobya.listener.ItemClickListener
import ch.ictrust.pobya.models.Category
import kotlinx.android.synthetic.main.activity_installed_apps.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment: Fragment(), ItemClickListener,
    ActionSheet.ActionSheetListener {


    private var categoryList: MutableList<Category> = mutableListOf()
    private lateinit var categoryAdapter: CategoryHorizontalRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.fragment_home, container, false)

        categoryAdapter = CategoryHorizontalRecyclerAdapter(categoryList,context!!)
        categoryAdapter.setClickListener(this)
        view.categoryRecyclerView.apply {

            layoutManager = LinearLayoutManager(this.context,LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        activity!!.nav_view.setCheckedItem(R.id.nav_dashboard)
        activity!!.toolbar.toolbarTitle.text= "Dashboard"
    }

    override fun onItemClick(position: Int) {
        when (position) {
            0 -> {
                startActivity(Intent(context, InstalledAppsActivity::class.java))
            }
            1 -> {
                val transaction = fragmentManager!!.beginTransaction()
                transaction.replace(R.id.container, MalwareScanFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }
            3 -> {
                startActivity(Intent(context, InstalledAppsActivity::class.java))
            }
        }
    }

    override fun onDismiss(actionSheet: ActionSheet?, isCancel: Boolean) {
    }

    override fun onOtherButtonClick(actionSheet: ActionSheet?, index: Int) {

    }


}