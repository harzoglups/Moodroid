package com.moode.android.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moode.android.BuildConfig
import com.moode.android.R
import com.moode.android.viewmodel.SettingsViewModel

@Composable
fun TextPreference(
    text: String = "Value",
    label: String = "Label",
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    updateSetting: (String) -> Unit = {},
    onDone: ((String) -> Unit)? = null,
    validator: (String) -> Boolean = { true },
    errorMessage: String = "Invalid input",
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    var value by rememberSaveable { mutableStateOf(text) }
    var isError by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    
    Row {
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->
                value = newValue
                isError = !validator(newValue)
                // Only update setting for numeric fields (like volume step)
                // URL field will be updated only on validation (Done button or focus loss)
                if (!isError && newValue.isNotEmpty() && keyboardOptions.keyboardType == KeyboardType.Number) {
                    updateSetting(newValue)
                }
            },
            label = {
                Text(
                    text = label,
                    style = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
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
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = {
                    if (!isError && value.isNotEmpty()) {
                        // Save the setting when user validates
                        updateSetting(value)
                        onDone?.invoke(value)
                        focusManager.clearFocus()
                    }
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.secondary,
                focusedBorderColor = MaterialTheme.colorScheme.secondary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.onSurface,
                errorBorderColor = MaterialTheme.colorScheme.error,
                errorLabelColor = MaterialTheme.colorScheme.error,
            ),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = if (isLandscape) 4.dp else 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(
            horizontal = 16.dp,
            vertical = if (isLandscape) 12.dp else 16.dp
        )) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = if (isLandscape) 8.dp else 12.dp)
            )
            content()
        }
    }
}

/**
 * Normalizes URL by adding http:// prefix if missing
 */
private fun normalizeUrl(url: String): String {
    return when {
        url.isEmpty() -> url
        url.startsWith("http://") || url.startsWith("https://") -> url
        else -> "http://$url"
    }
}

@Composable
fun PreferenceScreen(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val urlHistory by settingsViewModel.urlHistory.collectAsStateWithLifecycle()
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val currentUrl = settings.url.ifEmpty { context.getString(R.string.url) }
    var showUrlDropdown by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = if (isLandscape) 8.dp else 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Connection Settings Section
        SettingsSection(title = stringResource(R.string.settings_connection_title)) {
            TextPreference(
                text = currentUrl,
                label = stringResource(R.string.settings_url_label),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Done
                ),
                validator = { url ->
                    // Accept empty or valid http/https URLs, OR hostnames that will be normalized
                    url.isEmpty() || url.matches(Regex("^https?://.*")) || url.contains(".")
                },
                errorMessage = stringResource(R.string.settings_url_error),
                updateSetting = { url ->
                    settingsViewModel.setUrl(normalizeUrl(url))
                },
                onDone = { url ->
                    val normalized = normalizeUrl(url)
                    // Add to history when user presses Done/Enter
                    if (normalized.matches(Regex("^https?://.*")) && normalized.length > 10) {
                        settingsViewModel.addUrlToHistory(normalized)
                    }
                },
                trailingIcon = if (urlHistory.isNotEmpty()) {
                    {
                        IconButton(onClick = { showUrlDropdown = !showUrlDropdown }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = stringResource(R.string.history_show_dropdown))
                        }
                        DropdownMenu(
                            expanded = showUrlDropdown,
                            onDismissRequest = { showUrlDropdown = false }
                        ) {
                            urlHistory.forEach { historicUrl ->
                                DropdownMenuItem(
                                    text = { Text(historicUrl) },
                                    onClick = {
                                        settingsViewModel.setUrl(historicUrl)
                                        showUrlDropdown = false
                                    }
                                )
                            }
                        }
                    }
                } else null
            )
            
            Spacer(modifier = Modifier.size(16.dp))
            
            TextPreference(
                text = settings.volumeStep.toString(),
                label = stringResource(R.string.settings_volume_step_label),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                validator = { volumeStep ->
                    volumeStep.isEmpty() || volumeStep.toIntOrNull()?.let { it in 1..100 } ?: false
                },
                errorMessage = stringResource(R.string.settings_volume_step_error),
                updateSetting = { volumeStep ->
                    volumeStep.toIntOrNull()?.let { step ->
                        if (step in 1..100) {
                            settingsViewModel.setVolumeStep(step)
                        }
                    }
                }
            )
        }
        
        // URL History Section
        if (urlHistory.isNotEmpty()) {
            SettingsSection(title = stringResource(R.string.settings_history_title)) {
                Column {
                    Text(
                        text = stringResource(R.string.history_recent_servers),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    urlHistory.forEach { historicUrl ->
                        Text(
                            text = "• $historicUrl",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { settingsViewModel.setUrl(historicUrl) }
                                .padding(vertical = 4.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.size(8.dp))
                    
                    Button(
                        onClick = { settingsViewModel.clearUrlHistory() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(stringResource(R.string.history_clear_button))
                    }
                }
            }
        }
        
        // About Section
        SettingsSection(title = stringResource(R.string.settings_about_title)) {
            Column {
                Text(
                    text = stringResource(R.string.about_version, BuildConfig.VERSION_NAME),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.size(if (isLandscape) 8.dp else 12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.size(if (isLandscape) 8.dp else 12.dp))
                
                Text(
                    text = stringResource(R.string.about_features_title),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.size(if (isLandscape) 4.dp else 8.dp))
                
                val features = listOf(
                    stringResource(R.string.feature_webview),
                    stringResource(R.string.feature_volume_control),
                    stringResource(R.string.feature_connection_status),
                    stringResource(R.string.feature_performance),
                    stringResource(R.string.feature_url_history),
                    stringResource(R.string.feature_input_validation),
                    stringResource(R.string.feature_clean_architecture)
                )
                
                features.forEach { feature ->
                    Text(
                        text = "• $feature",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = if (isLandscape) 1.dp else 2.dp)
                    )
                }
                
                Spacer(modifier = Modifier.size(if (isLandscape) 8.dp else 12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.size(if (isLandscape) 8.dp else 12.dp))
                
                Text(
                    text = stringResource(R.string.about_description),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.size(if (isLandscape) 8.dp else 16.dp))
    }
}
