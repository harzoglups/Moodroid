package com.moode.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                value = newValue
                updateSetting(newValue)
            },
            label = {
                Text(
                    text = label,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
            },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            ),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
            ),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
        )
    }
}

@Composable
fun PreferenceScreen(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(8.dp)
    ) {
        Spacer(modifier = Modifier.size(16.dp))
        TextPreference(
            text = settingsViewModel.url.value ?: context.getString(R.string.url),
            label = "Moode Audio URL",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
        ) { url ->
            settingsViewModel.setUrl(url)
        }
        Spacer(modifier = Modifier.size(16.dp))
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