package com.moode.android.domain.repository

import com.moode.android.domain.model.Settings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing user settings
 */
interface SettingsRepository {
    fun getSettings(): Flow<Settings>
    suspend fun setUrl(url: String)
    suspend fun setVolumeStep(volumeStep: Int)
    fun getUrlHistory(): Flow<List<String>>
    suspend fun addUrlToHistory(url: String)
    suspend fun clearUrlHistory()
}
