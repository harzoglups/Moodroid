package com.moode.android.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moode.android.domain.model.ConnectionState
import com.moode.android.domain.model.Result
import com.moode.android.domain.model.Settings
import com.moode.android.domain.usecase.AddUrlToHistoryUseCase
import com.moode.android.domain.usecase.ClearUrlHistoryUseCase
import com.moode.android.domain.usecase.GetSettingsUseCase
import com.moode.android.domain.usecase.GetUrlHistoryUseCase
import com.moode.android.domain.usecase.ResolveUrlUseCase
import com.moode.android.domain.usecase.SendVolumeCommandUseCase
import com.moode.android.domain.usecase.TestConnectionUseCase
import com.moode.android.domain.usecase.UpdateUrlUseCase
import com.moode.android.domain.usecase.UpdateVolumeStepUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing settings and Moode server interactions
 * Following Clean Architecture principles with use cases
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getSettingsUseCase: GetSettingsUseCase,
    private val updateUrlUseCase: UpdateUrlUseCase,
    private val updateVolumeStepUseCase: UpdateVolumeStepUseCase,
    private val getUrlHistoryUseCase: GetUrlHistoryUseCase,
    private val addUrlToHistoryUseCase: AddUrlToHistoryUseCase,
    private val clearUrlHistoryUseCase: ClearUrlHistoryUseCase,
    private val testConnectionUseCase: TestConnectionUseCase,
    private val sendVolumeCommandUseCase: SendVolumeCommandUseCase,
    private val resolveUrlUseCase: ResolveUrlUseCase
) : ViewModel() {
    
    companion object {
        private const val TAG = "SettingsViewModel"
    }
    
    // Settings state
    val settings: StateFlow<Settings> = getSettingsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Settings(url = "", volumeStep = 10)
        )
    
    // Resolved URL state (mDNS .local hostnames converted to IPs)
    private val _resolvedUrl = MutableStateFlow<String?>(null)
    val resolvedUrl: StateFlow<String?> = _resolvedUrl.asStateFlow()
    
    // URL history state
    val urlHistory: StateFlow<List<String>> = getUrlHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Connection status state
    private val _connectionStatus = MutableStateFlow(ConnectionState.UNKNOWN)
    val connectionStatus: StateFlow<ConnectionState> = _connectionStatus.asStateFlow()
    
    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        // Resolve URL whenever settings URL changes
        viewModelScope.launch {
            settings.map { it.url }.collect { url ->
                if (url.isNotEmpty()) {
                    Log.i(TAG, "Settings URL changed, resolving: $url")
                    val resolved = resolveUrlUseCase(url)
                    Log.i(TAG, "URL resolved to: $resolved")
                    _resolvedUrl.value = resolved
                    
                    // Test connection with resolved URL
                    if (resolved != null) {
                        testConnection(resolved)
                    }
                }
            }
        }
    }
    
    fun setUrl(url: String) {
        viewModelScope.launch {
            updateUrlUseCase(url)
        }
    }
    
    fun addUrlToHistory(url: String) {
        viewModelScope.launch {
            addUrlToHistoryUseCase(url)
        }
    }
    
    fun setVolumeStep(volumeStep: Int) {
        viewModelScope.launch {
            updateVolumeStepUseCase(volumeStep)
        }
    }
    
    fun clearUrlHistory() {
        viewModelScope.launch {
            clearUrlHistoryUseCase()
        }
    }
    
    fun testConnection(url: String) {
        if (url.isEmpty()) {
            Log.w(TAG, "testConnection called with empty URL")
            return
        }
        
        Log.i(TAG, "Testing connection to: $url")
        viewModelScope.launch {
            when (val result = testConnectionUseCase(url)) {
                is Result.Success -> {
                    Log.i(TAG, "Connection test successful")
                    _connectionStatus.value = ConnectionState.CONNECTED
                    _error.value = null
                }
                is Result.Error -> {
                    Log.e(TAG, "Connection test failed: ${result.exception.message}")
                    _connectionStatus.value = ConnectionState.DISCONNECTED
                    _error.value = result.exception.message
                }
                is Result.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }
    
    fun sendVolumeCommand(command: String) {
        val currentSettings = settings.value
        if (currentSettings.url.isEmpty()) {
            Log.w(TAG, "sendVolumeCommand called with empty URL")
            return
        }
        
        Log.i(TAG, "Sending volume command: $command with step: ${currentSettings.volumeStep}")
        viewModelScope.launch {
            when (val result = sendVolumeCommandUseCase(
                currentSettings.url,
                command,
                currentSettings.volumeStep
            )) {
                is Result.Success -> {
                    Log.i(TAG, "Volume command successful")
                    _connectionStatus.value = ConnectionState.CONNECTED
                    _error.value = null
                }
                is Result.Error -> {
                    Log.e(TAG, "Volume command failed: ${result.exception.message}")
                    _connectionStatus.value = ConnectionState.DISCONNECTED
                    _error.value = result.exception.message
                }
                is Result.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
