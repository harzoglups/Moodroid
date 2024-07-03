package com.moode.android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.moode.android.data.SettingsDataStore
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settingsDataStore = SettingsDataStore(application)
    val url: LiveData<String> = settingsDataStore.url.asLiveData()
    val volumeStep: LiveData<Int> = settingsDataStore.volumeStep.asLiveData()

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
}