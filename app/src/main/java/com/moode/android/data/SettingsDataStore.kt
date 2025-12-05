package com.moode.android.data

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

private const val MOODROID_PREFERENCES = "moodroid_preferences"
private const val MAX_URL_HISTORY = 10

class SettingsDataStore(private val context: Context) {
    private val MOODROID_URL_PREFERENCE = stringPreferencesKey("url_preference")
    private val MOODROID_VOLUME_STEP_PREFERENCE = intPreferencesKey("volume_step")
    private val MOODROID_URL_HISTORY_PREFERENCE = stringSetPreferencesKey("url_history")

    val volumeStep: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[MOODROID_VOLUME_STEP_PREFERENCE] ?: context.getString(R.string.volume_step)
            .toInt()
    }

    val url: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[MOODROID_URL_PREFERENCE] ?: context.getString(R.string.url)
    }

    val urlHistory: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[MOODROID_URL_HISTORY_PREFERENCE]?.toList()?.sorted() ?: emptyList()
    }

    suspend fun setVolumeStep(volumeStep: Int) {
        context.dataStore.edit { preferences ->
            preferences[MOODROID_VOLUME_STEP_PREFERENCE] = volumeStep
        }
    }

    suspend fun setUrl(url: String) {
        context.dataStore.edit { preferences ->
            preferences[MOODROID_URL_PREFERENCE] = url
        }
    }

    suspend fun addUrlToHistory(url: String) {
        context.dataStore.edit { preferences ->
            // Add to history (keep last MAX_URL_HISTORY unique URLs)
            val currentHistory = preferences[MOODROID_URL_HISTORY_PREFERENCE]?.toMutableSet() ?: mutableSetOf()
            currentHistory.add(url)
            
            // Keep only the most recent MAX_URL_HISTORY URLs
            if (currentHistory.size > MAX_URL_HISTORY) {
                // Convert to list, sort, and keep last MAX_URL_HISTORY items
                val sortedHistory = currentHistory.toList().sorted().takeLast(MAX_URL_HISTORY)
                preferences[MOODROID_URL_HISTORY_PREFERENCE] = sortedHistory.toSet()
            } else {
                preferences[MOODROID_URL_HISTORY_PREFERENCE] = currentHistory
            }
        }
    }

    suspend fun clearUrlHistory() {
        context.dataStore.edit { preferences ->
            preferences.remove(MOODROID_URL_HISTORY_PREFERENCE)
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = MOODROID_PREFERENCES)