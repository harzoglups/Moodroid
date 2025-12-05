package com.moode.android.data.source.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.moode.android.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private const val MOODROID_PREFERENCES = "moodroid_preferences"
private const val MAX_URL_HISTORY = 10

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = MOODROID_PREFERENCES)

/**
 * Local data source for managing settings using DataStore
 */
@Singleton
class SettingsLocalDataSource @Inject constructor(
    private val context: Context
) {
    private val urlPreferenceKey = stringPreferencesKey("url_preference")
    private val volumeStepPreferenceKey = intPreferencesKey("volume_step")
    private val urlHistoryPreferenceKey = stringSetPreferencesKey("url_history")

    fun getUrl(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[urlPreferenceKey] ?: context.getString(R.string.url)
    }

    fun getVolumeStep(): Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[volumeStepPreferenceKey] ?: context.getString(R.string.volume_step).toInt()
    }

    fun getUrlHistory(): Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[urlHistoryPreferenceKey]?.toList()?.sorted() ?: emptyList()
    }

    suspend fun setUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[urlPreferenceKey] = url
        }
    }

    suspend fun setVolumeStep(volumeStep: Int) {
        context.dataStore.edit { preferences ->
            preferences[volumeStepPreferenceKey] = volumeStep
        }
    }

    suspend fun addUrlToHistory(url: String) {
        context.dataStore.edit { preferences ->
            val currentHistory = preferences[urlHistoryPreferenceKey]?.toMutableSet() ?: mutableSetOf()
            currentHistory.add(url)
            
            if (currentHistory.size > MAX_URL_HISTORY) {
                val sortedHistory = currentHistory.toList().sorted().takeLast(MAX_URL_HISTORY)
                preferences[urlHistoryPreferenceKey] = sortedHistory.toSet()
            } else {
                preferences[urlHistoryPreferenceKey] = currentHistory
            }
        }
    }

    suspend fun clearUrlHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(urlHistoryPreferenceKey)
        }
    }
}
