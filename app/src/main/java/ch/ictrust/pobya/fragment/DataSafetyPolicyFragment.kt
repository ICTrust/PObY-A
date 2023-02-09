package ch.ictrust.pobya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment
import ch.ictrust.pobya.R
import kotlinx.android.synthetic.main.fragment_dashboard.view.*

class DataSafetyPolicyFragment : Fragment() {

    lateinit var wvDataSafetyPolicyFragment : WebView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view : View = inflater.inflate(R.layout.fragment_data_safety_policy, container, false)
        view.categoryRecyclerView.apply {

            wvDataSafetyPolicyFragment = view?.findViewById(R.id.wvDataSafetyPolicy)!!
            wvDataSafetyPolicyFragment.loadUrl("file:///android_res/raw/policy.html")
        }
        return view

    }
}