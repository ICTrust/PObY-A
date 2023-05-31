package ch.ictrust.pobya.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import ch.ictrust.pobya.R
import ch.ictrust.pobya.activity.AppDetailActivity
import ch.ictrust.pobya.models.InstalledApplication
import java.text.SimpleDateFormat
import java.util.*


class AppInfosFragment : Fragment() {
    private lateinit var app: InstalledApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appsDetailsActivity: AppDetailActivity = activity as AppDetailActivity
        app = appsDetailsActivity.getCurrentApp()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm")

        val view: View = inflater.inflate(R.layout.fragment_app_infos, container, false)

        val appDetailPackageNameTV = view.findViewById(R.id.appDetailPackageName) as TextView
        appDetailPackageNameTV.text = app.packageName

        val appDetailStatusTV = view.findViewById(R.id.appDetailAppStatus) as TextView
        appDetailStatusTV.text = app.applicationState.toString()

        val appDetailInstallDateTV = view.findViewById(R.id.appDetailInstallDate) as TextView
        val installDate = Date(app.installedDate)

        appDetailInstallDateTV.text = dateFormat.format(installDate)

        val appDetailUpdateDateTV = view.findViewById(R.id.appDetailUpdateDate) as TextView
        val updateDate = Date(app.updateDate)
        appDetailUpdateDateTV.text = dateFormat.format(updateDate)

        val appDetailUninstallDate = view.findViewById(R.id.appDetailUninstallDate) as TextView
        val rlUninstallDate = view.findViewById(R.id.rlUninstallDate) as RelativeLayout
        rlUninstallDate.visibility = View.GONE

        if (app.uninstalled) {
            val uninstallDate = Date(app.uninstallDate)
            appDetailUninstallDate.text = dateFormat.format(uninstallDate)
            rlUninstallDate.visibility = View.VISIBLE
        }

        return view
    }
}