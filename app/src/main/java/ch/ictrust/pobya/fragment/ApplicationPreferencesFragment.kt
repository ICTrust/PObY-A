package ch.ictrust.pobya.fragment

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import ch.ictrust.pobya.R
import ch.ictrust.pobya.R.layout
import ch.ictrust.pobya.service.ApplicationsService
import ch.ictrust.pobya.utillies.Prefs
import com.google.android.material.switchmaterial.SwitchMaterial


class ApplicationPreferencesFragment : Fragment() {

    private lateinit var toggleButtonMonitoring: SwitchMaterial
    private lateinit var databaseURLSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(layout.fragment_application_settings, container, false)

        toggleButtonMonitoring = view.findViewById(R.id.toggleButtonMonitoring)
        databaseURLSpinner = view.findViewById(R.id.databaseURLSpinner)

        // Live monitoring service
        toggleButtonMonitoring.isChecked = Prefs.getInstance(view.context)?.monitoringServiceStatus == true
        toggleButtonMonitoring.setOnClickListener {
             Prefs.getInstance(view.context)?.mPrefs?.edit()?.putBoolean(Prefs.MONITORING_SERVICE_ENABLED, toggleButtonMonitoring.isChecked)!!.apply()

            val activityManager =
                view.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val serviceIsRunning = activityManager.runningAppProcesses.any {
                it.processName == "ch.ictrust.pobya.ApplicationsService"
            }

            if (toggleButtonMonitoring.isChecked) {
                view.context.startService(Intent(view.context,
                                        ApplicationsService::class.java))
                Toast.makeText(view.context, "The service is loading", Toast.LENGTH_LONG)

            } else if (serviceIsRunning) {
                view.context.stopService(
                    Intent(
                        view.context,
                        ApplicationsService::class.java
                    )
                )
            }
        }

        // Database URLs
        val databaseURLsAdapter = ArrayAdapter(view.context, layout.spinner_urls_item, Prefs.URLs)
        databaseURLsAdapter.setDropDownViewResource(layout.spinner_urls_dropdown_item)

        val spinnerURLsListener: AdapterView.OnItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    Prefs.getInstance(view.context)?.mPrefs?.edit()?.putString(
                                Prefs.BASE_URL, parent.getItemAtPosition(position).toString())!!.apply()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        databaseURLSpinner.adapter = databaseURLsAdapter
        databaseURLSpinner.onItemSelectedListener = spinnerURLsListener

        return view
    }

}