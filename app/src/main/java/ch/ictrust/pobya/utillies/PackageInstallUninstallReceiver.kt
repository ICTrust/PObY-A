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
package ch.ictrust.pobya.utillies

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import ch.ictrust.pobya.repository.ApplicationRepository
import kotlinx.coroutines.launch

class PackageInstallUninstallReceiver : BroadcastReceiver() {
    private val TAG: String = this.javaClass.name

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val packageName = intent.data?.encodedSchemeSpecificPart

        Log.d(TAG, "onReceive called with action: $action and package: $packageName")

        when (action) {
            Intent.ACTION_PACKAGE_CHANGED -> {
                Log.d(TAG, "App changed: $packageName")
                /**
                 * TODO: Handle app changed
                 */
            }

            Intent.ACTION_PACKAGE_ADDED -> {
                Log.d(TAG, "App installed: $packageName")
                context.sendBroadcast(Intent("ch.ictrust.pobya.APP_ADDED"))
            }

            Intent.ACTION_PACKAGE_REMOVED -> {
                Log.d(TAG, "App uninstalled: $packageName")
                Utilities.dbScope.launch {
                    val uninstalledApp =
                        ApplicationRepository.getInstance(context.applicationContext as Application)
                            .getAppByPackageName(packageName!!)
                    uninstalledApp.uninstalled = true
                    ApplicationRepository.getInstance(context.applicationContext as Application)
                        .update(uninstalledApp)
                }
                context.sendBroadcast(Intent("ch.ictrust.pobya.APP_REMOVED"))
            }
        }
    }
}