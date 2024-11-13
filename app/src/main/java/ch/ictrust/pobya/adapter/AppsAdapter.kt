/*
 * This file is part of PObY-A.
 *
 * Copyright (C) 2023 ICTrust Sàrl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package ch.ictrust.pobya.adapter


import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ch.ictrust.pobya.R
import ch.ictrust.pobya.models.ApplicationState
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.repository.ApplicationRepository
import ch.ictrust.pobya.utillies.ApplicationPermissionHelper
import ch.ictrust.pobya.utillies.Utilities
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.CornerFamily
import kotlinx.coroutines.launch


class AppsAdapter(ctx: Context) : ListAdapter<InstalledApplication, AppsAdapter.AppHolder>(
    DIFF_CALLBACK
) {
    private lateinit var listener: OnItemClickListener
    private lateinit var context: Context

    init {

        ApplicationRepository.getInstance(ctx as Application).getThirdPartyApps()
            .observeForever { apps ->
                submitList(apps)
                notifyDataSetChanged()
            }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppHolder {

        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.listview_apps, parent, false)
        context = view.context
        return AppHolder(view)
    }

    override fun submitList(list: List<InstalledApplication>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    override fun onBindViewHolder(
        holder: AppsAdapter.AppHolder,
        position: Int
    ) {

        val currentApp: InstalledApplication = getItem(position)
        holder.imageName.text = currentApp.name

        val radius: Float = 0f
        holder.image.setImageIcon(Icon.createWithData(currentApp.icon, 0, currentApp.icon.size))
        holder.image.shapeAppearanceModel = holder.image.shapeAppearanceModel
            .toBuilder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        holder.btnAppInfo.setOnClickListener {
            listener.onItemClick(currentApp)
        }

        holder.btnAppTrust.text =
            if (currentApp.trusted)
                context.resources.getString(R.string.untrust_app)
            else
                context.resources.getString(R.string.trust_app)

        // Trust action
        holder.btnAppTrust.setOnClickListener {
            if (currentApp.trusted) {
                currentApp.trusted = false
                currentApp.flaggedAsThreat = false
                val pm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    context.applicationContext.packageManager.getPackageInfo(
                        currentApp.packageName, (PackageManager.GET_PERMISSIONS or
                                PackageManager.GET_META_DATA or
                                PackageManager.GET_SIGNING_CERTIFICATES)
                    )
                } else {
                    context.applicationContext.packageManager.getPackageInfo(
                        currentApp.packageName, (PackageManager.GET_PERMISSIONS or
                                PackageManager.GET_META_DATA or
                                PackageManager.GET_SIGNATURES))
                }

                currentApp.flagReason = ""
                currentApp.lastHash = ""
                Utilities.scanAppScope.launch {
                    currentApp.applicationState = ApplicationPermissionHelper(
                        context,
                        (currentApp.isSystemApp == 1)
                    ).getAppConfidence(pm)

                    ApplicationRepository.getInstance(context.applicationContext as Application)
                        .update(currentApp)
                }
                holder.tvState.text = currentApp.applicationState.toString()
                holder.btnAppTrust.text = context.getString(R.string.trust_app)

                notifyItemChanged(position)
            } else {
                currentApp.trusted = true
                currentApp.flaggedAsThreat = false
                currentApp.lastHash = ""
                currentApp.flagReason = ""
                currentApp.applicationState = ApplicationState.TRUSTED
                Utilities.scanAppScope.launch {
                    ApplicationRepository.getInstance(context.applicationContext as Application)
                        .update(currentApp)
                }
                holder.tvState.text = context.resources.getString(R.string.trust_app)
                holder.btnAppTrust.text = context.resources.getString(R.string.untrust_app)
                notifyItemChanged(position)
            }
        }

        // Info action
        holder.btnAppInfo.setOnClickListener {
            listener.onItemClick(currentApp)
        }

        // Threat
        if (currentApp.flaggedAsThreat) {
            holder.tvState.text = context.getString(R.string.malware)
            holder.tvState.setTextColor(context.resources.getColor(R.color.purple))
            holder.tvThreatDetails.text = currentApp.flagReason
            holder.parentLayout.setBackgroundColor(Color.parseColor("#F18E39"))
            holder.containerLayout.background =
                context.getDrawable(R.drawable.shape_application_malware_item)
        } else {
            holder.parentLayout.setBackgroundColor(Color.parseColor("#FFFFFF"))
            holder.containerLayout.background =
                context.getDrawable(R.drawable.shape_application_item)
        }



        val status = ApplicationPermissionHelper(context, (currentApp.isSystemApp == 1))
                            .getAppConfidence(context.packageManager.getPackageInfo(currentApp.packageName, PackageManager.GET_PERMISSIONS or PackageManager.GET_META_DATA))

        if (currentApp.applicationState != status && !currentApp.applicationState.equals(ApplicationState.MALWARE)) {
            currentApp.applicationState = status
            Utilities.scanAppScope.launch {
                ApplicationRepository.getInstance(context.applicationContext as Application)
                    .update(currentApp)
            }
        }
        // Application Status
        when (currentApp.applicationState) {
            ApplicationState.TRUSTED -> {
                holder.tvState.text = "Trusted" //context.getString(R.string.trusted)
                holder.tvState.setTextColor(context.resources.getColor(R.color.doneColor))
            }

            ApplicationState.MALWARE -> {
                holder.tvState.text = "Malware"//context.getString(R.string.malware)
                holder.tvState.setTextColor(context.resources.getColor(R.color.purple))
                holder.tvThreatDetails.text = currentApp.flagReason
            }

            ApplicationState.DANGEROUS -> {
                holder.tvState.text = context.getString(R.string.warning)
                holder.tvThreatDetails.text = ""
                holder.tvState.setTextColor(context.resources.getColor(R.color.warningColor))
            }

            ApplicationState.NORMAL -> {
                holder.tvState.text = context.getString(R.string.normal)
                holder.tvThreatDetails.text = ""
                holder.tvState.setTextColor(context.resources.getColor(R.color.doneColor))
            }

            ApplicationState.MEDIUM -> {
                holder.tvState.text = context.getString(R.string.medium)
                holder.tvThreatDetails.text = ""
                holder.tvState.setTextColor(context.resources.getColor(R.color.warningColor))
            }

            ApplicationState.SUSPICIOUS -> {
                holder.tvState.text = "Suspicious" //context.getString(R.string.unknown)
                holder.tvThreatDetails.text = currentApp.flagReason
                holder.tvState.setTextColor(context.resources.getColor(R.color.warningColor))

            }
        }
    }


    inner class AppHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image: ShapeableImageView
        var imageName: TextView
        var parentLayout: RelativeLayout
        var containerLayout: ConstraintLayout
        var tvState: TextView
        var tvThreatDetails: TextView
        var btnAppInfo: Button
        var btnAppTrust: Button

        init {
            image = itemView.findViewById(R.id.imageApp)
            imageName = itemView.findViewById(R.id.tvAppName)
            parentLayout = itemView.findViewById(R.id.rlCardViewListApps)
            tvState = itemView.findViewById(R.id.tvStatus)
            parentLayout.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(getItem(position))
                }
            }
            tvThreatDetails = itemView.findViewById(R.id.tvThreatDetails)
            containerLayout = itemView.findViewById(R.id.clAppInfo)
            btnAppInfo = itemView.findViewById(R.id.btnAppInfo)
            //btnAppScan = itemView.findViewById(R.id.btnAppScan)
            btnAppTrust = itemView.findViewById(R.id.btnAppTrust)

        }
    }

    interface OnItemClickListener {

        fun onItemClick(app: InstalledApplication)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<InstalledApplication> =
            object : DiffUtil.ItemCallback<InstalledApplication>() {
                override fun areItemsTheSame(
                    oldItem: InstalledApplication,
                    newItem: InstalledApplication
                ): Boolean {
                    return oldItem.packageName == newItem.packageName
                            && oldItem.name == newItem.name
                            && oldItem.isSystemApp == newItem.isSystemApp
                            && oldItem.versionCode == newItem.versionCode
                            && oldItem.applicationState == newItem.applicationState
                            && oldItem.uninstalled == newItem.uninstalled
                            && oldItem.flaggedAsThreat == newItem.flaggedAsThreat
                            && oldItem.flagReason == newItem.flagReason
                            && oldItem.lastHash == newItem.lastHash
                }

                override fun areContentsTheSame(
                    oldItem: InstalledApplication,
                    newItem: InstalledApplication
                ): Boolean {
                    return oldItem.name == newItem.name
                            && oldItem.isSystemApp == newItem.isSystemApp
                            && oldItem.versionCode == newItem.versionCode
                            && oldItem.applicationState == newItem.applicationState
                            && oldItem.uninstalled == newItem.uninstalled
                            && oldItem.flaggedAsThreat == newItem.flaggedAsThreat
                            && oldItem.flagReason == newItem.flagReason
                            && oldItem.lastHash == newItem.lastHash
                }
            }
    }
}