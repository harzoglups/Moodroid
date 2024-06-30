package com.moode.android.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.moode.android.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val MOODROID_PREFERENCES = "moodroid_preferences"


class SettingsDataStore(private val context: Context) {
    private val MOODROID_URL_PREFERENCE = stringPreferencesKey("url_preference")
    private val MOODROID_VOLUME_STEP_PREFERENCE = intPreferencesKey("volume_step")

    val volumeStep: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[MOODROID_VOLUME_STEP_PREFERENCE] ?: context.getString(R.string.volume_step)
            .toInt()
    }

    val url: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[MOODROID_URL_PREFERENCE] ?: context.getString(R.string.url)
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
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = MOODROID_PREFERENCES)