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