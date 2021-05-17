package ch.ictrust.pobya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ch.ictrust.pobya.R
import ch.ictrust.pobya.adapter.SettingsAdapter
import ch.ictrust.pobya.models.SysSetting
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.app_bar_main.view.*
import kotlinx.android.synthetic.main.fragment_settings_scan.view.*


class SettingsScanFragment : Fragment() {

    private var settingsList : MutableList<SysSetting> = mutableListOf()
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var progressBar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val view : View = inflater.inflate(R.layout.fragment_settings_scan, container, false)

        getSettings()

        settingsAdapter = SettingsAdapter(settingsList, view.context)
        progressBar = view.findViewById(R.id.loading_spinner_settings)
        progressBar.visibility = View.GONE

        view.recyclerView.apply {
            settingsAdapter = SettingsAdapter(settingsList, view.context)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = settingsAdapter
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        getSettings()
        view?.recyclerView?.apply {
            settingsAdapter = SettingsAdapter(settingsList, view!!.context)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = settingsAdapter
        }

    }

    private fun getSettings() {
        val settingsHelper = ch.ictrust.pobya.Utillies.SettingsHelper(context)
        settingsList = settingsHelper.scan()
    }

    override fun onStart() {
        super.onStart()
        activity!!.nav_view.setCheckedItem(R.id.nav_settings_scan)
        activity!!.toolbar.toolbarTitle.text = "Privacy Settings"
    }


}