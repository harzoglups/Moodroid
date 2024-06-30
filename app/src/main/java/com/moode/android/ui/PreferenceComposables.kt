package com.moode.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.moode.android.R
import com.moode.android.viewmodel.SettingsViewModel

@Composable
fun TextPreference(
    text: String = "Value",
    label: String = "Label",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    updateSetting: (String) -> Unit = {},
) {
    var value by rememberSaveable { mutableStateOf(text) }
    Row {
        TextField(
            value = value,
            onValueChange = { newValue ->
                value = newValue
                updateSetting(newValue)
            },
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions
        )
    }
}

@Composable
fun PreferenceScreen(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    Column() {
        TextPreference(
            text = settingsViewModel.url.value ?: context.getString(R.string.url),
            label = "Moode Audio URL",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        ) { url ->
            settingsViewModel.setUrl(url)
        }
        TextPreference(
            text = settingsViewModel.volumeStep.value?.toString()
                ?: context.getString(R.string.volume_step),
            label = "Volume Step",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        ) { volumeStep ->
            if (volumeStep.isNotEmpty())
                settingsViewModel.setVolumeStep(volumeStep.toInt())
        }
    }
}