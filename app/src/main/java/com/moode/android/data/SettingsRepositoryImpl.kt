package com.moode.android.data

import com.moode.android.data.source.local.SettingsLocalDataSource
import com.moode.android.domain.model.Settings
import com.moode.android.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SettingsRepository
 */
@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val settingsLocalDataSource: SettingsLocalDataSource
) : SettingsRepository {
    
    override fun getSettings(): Flow<Settings> {
        return combine(
            settingsLocalDataSource.getUrl(),
            settingsLocalDataSource.getVolumeStep()
        ) { url, volumeStep ->
            Settings(url = url, volumeStep = volumeStep)
        }
    }
    
    override suspend fun setUrl(url: String) {
        settingsLocalDataSource.setUrl(url)
    }
    
    override suspend fun setVolumeStep(volumeStep: Int) {
        settingsLocalDataSource.setVolumeStep(volumeStep)
    }
    
    override fun getUrlHistory(): Flow<List<String>> {
        return settingsLocalDataSource.getUrlHistory()
    }
    
    override suspend fun addUrlToHistory(url: String) {
        settingsLocalDataSource.addUrlToHistory(url)
    }
    
    override suspend fun clearUrlHistory() {
        settingsLocalDataSource.clearUrlHistory()
    }
}
