package ch.ictrust.pobya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.adapter.SettingsAdapter
import ch.ictrust.pobya.models.SysSettings



class SettingsScanFragment : Fragment() {

    private var settingsList : MutableList<SysSettings> = mutableListOf()
    private lateinit var settingsAdapter: SettingsAdapter
    private lateinit var progressBar : ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view : View = inflater.inflate(R.layout.fragment_settings_scan, container, false)

        getSettings()

        settingsAdapter = SettingsAdapter(settingsList, view.context)
        progressBar = view.findViewById(R.id.loading_spinner_settings)
        progressBar.visibility = View.GONE

        view.findViewById<RecyclerView>(R.id.recyclerViewSettings).apply {
            settingsAdapter = SettingsAdapter(settingsList, view.context)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = settingsAdapter
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        getSettings()
        view?.findViewById<RecyclerView>(R.id.recyclerViewSettings)?.apply {
            settingsAdapter = SettingsAdapter(settingsList, requireView().context)
            layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            adapter = settingsAdapter
        }

    }

    private fun getSettings() {
        val settingsHelper = ch.ictrust.pobya.utillies.SettingsHelper(context)
        settingsList = settingsHelper.scan()
    }

}