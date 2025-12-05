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
    validator: (String) -> Boolean = { true },
    errorMessage: String = "Invalid input",
) {
    var value by rememberSaveable { mutableStateOf(text) }
    var isError by rememberSaveable { mutableStateOf(false) }
    
    Row {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                value = newValue
                isError = !validator(newValue)
                if (!isError && newValue.isNotEmpty()) {
                    updateSetting(newValue)
                }
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
            isError = isError,
            supportingText = if (isError) {
                { Text(errorMessage) }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.secondary,
                focusedLabelColor = MaterialTheme.colorScheme.secondary,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
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
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
            validator = { url ->
                // URL validation: must start with http:// or https:// and have valid format
                url.isEmpty() || (url.matches(Regex("^https?://.*")) && url.length > 10)
            },
            errorMessage = "URL must start with http:// or https://",
            updateSetting = { url ->
                settingsViewModel.setUrl(url)
            }
        )
        Spacer(modifier = Modifier.size(16.dp))
        TextPreference(
            text = settingsViewModel.volumeStep.value?.toString()
                ?: context.getString(R.string.volume_step),
            label = "Volume Step",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            validator = { volumeStep ->
                // Volume step validation: must be a positive integer between 1 and 100
                volumeStep.isEmpty() || volumeStep.toIntOrNull()?.let { it in 1..100 } ?: false
            },
            errorMessage = "Must be a number between 1 and 100",
            updateSetting = { volumeStep ->
                volumeStep.toIntOrNull()?.let { step ->
                    if (step in 1..100) {
                        settingsViewModel.setVolumeStep(step)
                    }
                }
            }
        )
    }
}