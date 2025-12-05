package com.moode.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.moode.android.data.SettingsDataStore
import kotlinx.coroutines.launch

enum class ConnectionStatus {
    UNKNOWN,
    CONNECTED,
    DISCONNECTED
}

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)
    val url: LiveData<String> = settingsDataStore.url.asLiveData()
    val volumeStep: LiveData<Int> = settingsDataStore.volumeStep.asLiveData()
    
    private val _connectionStatus = MutableLiveData<ConnectionStatus>(ConnectionStatus.UNKNOWN)
    val connectionStatus: LiveData<ConnectionStatus> = _connectionStatus

    fun setUrl(url: String) {
        viewModelScope.launch {
            settingsDataStore.setUrl(url)
        }
    }

    fun setVolumeStep(volumeStep: Int) {
        viewModelScope.launch {
            settingsDataStore.setVolumeStep(volumeStep)
        }
    }
    
    fun updateConnectionStatus(status: ConnectionStatus) {
        _connectionStatus.value = status
    }
}