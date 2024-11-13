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
package ch.ictrust.pobya.fragment

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import ch.ictrust.pobya.models.InstalledApplication
import ch.ictrust.pobya.repository.ApplicationRepository
import kotlinx.coroutines.launch


class AppsViewModel(application: Application) : AndroidViewModel(application) {

    private var repository: ApplicationRepository = ApplicationRepository.getInstance(application)
    var allApps: LiveData<List<InstalledApplication>> = repository.getAllApps()
    var userApps: LiveData<List<InstalledApplication>> = repository.getThirdPartyApps()
    var systemApps: LiveData<List<InstalledApplication>> = repository.getSystemApps()


    private val _installedApps: MutableLiveData<List<InstalledApplication>> = MutableLiveData()

    val installedApps: LiveData<List<InstalledApplication>> = _installedApps

    fun getApps() {
        viewModelScope.launch {
            val items = repository.getAllApps()
            items.value?.toMutableList()?.let { installedApps.value?.toMutableList()?.addAll(it) }

        }
    }


    fun addApp(app: InstalledApplication) {

        viewModelScope.launch {
            repository.insert(app)
        }
    }


    fun deleteApp(app: InstalledApplication) {

        viewModelScope.launch {

            repository.delete(app)

            repository.getThirdPartyApps()
            systemApps = repository.getSystemApps()
        }
    }


    fun getSystemApplications(): LiveData<List<InstalledApplication>> {

        viewModelScope.launch {
            systemApps = repository.getSystemApps()
        }
        return systemApps
    }


    fun getDisabledApps(): LiveData<List<InstalledApplication>> {
        return repository.getDisabledApps()
    }


    fun getThirdPartyApps(): LiveData<List<InstalledApplication>> {
        viewModelScope.launch {

            userApps = repository.getThirdPartyApps()
        }
        return userApps
    }

    fun getUninstalledApps(): LiveData<List<InstalledApplication>> {

        return repository.getUninstalledApps()
    }

    fun deleteAllApps() {
        repository.deleteAllApps()
    }
}