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
private const val HISTORY_SEPARATOR = "\n"

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
    // Legacy unordered history (read-only fallback) and its ordered replacement
    private val urlHistoryPreferenceKey = stringSetPreferencesKey("url_history")
    private val urlHistoryMruPreferenceKey = stringPreferencesKey("url_history_mru")

    fun getUrl(): Flow<String> = context.dataStore.data.map { preferences ->
        preferences[urlPreferenceKey] ?: context.getString(R.string.url)
    }

    fun getVolumeStep(): Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[volumeStepPreferenceKey] ?: context.getString(R.string.volume_step).toInt()
    }

    /** URL history, most recently used first. */
    fun getUrlHistory(): Flow<List<String>> = context.dataStore.data.map { preferences ->
        readHistory(preferences)
    }

    /**
     * Reads the MRU history. Falls back to the legacy unordered set (alphabetical order)
     * until the first write migrates it to the ordered format.
     */
    private fun readHistory(preferences: Preferences): List<String> {
        val ordered = preferences[urlHistoryMruPreferenceKey]
        if (ordered != null) {
            return ordered.split(HISTORY_SEPARATOR).filter { it.isNotEmpty() }
        }
        return preferences[urlHistoryPreferenceKey]?.toList()?.sorted() ?: emptyList()
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
            // Most recently used first; drop the oldest entries beyond the limit
            val updated = (listOf(url) + readHistory(preferences).filter { it != url })
                .take(MAX_URL_HISTORY)
            preferences[urlHistoryMruPreferenceKey] = updated.joinToString(HISTORY_SEPARATOR)
            preferences.remove(urlHistoryPreferenceKey)
        }
    }

    suspend fun clearUrlHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(urlHistoryMruPreferenceKey)
            preferences.remove(urlHistoryPreferenceKey)
        }
    }
}
