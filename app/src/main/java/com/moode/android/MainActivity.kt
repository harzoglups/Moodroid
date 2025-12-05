package com.moode.android

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moode.android.domain.model.ConnectionState
import com.moode.android.ui.MainScreen
import com.moode.android.ui.SplashScreen
import com.moode.android.ui.theme.MoodeAndroidTheme
import com.moode.android.viewmodel.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Main Activity of the application
 * Refactored following Clean Architecture principles
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MoodeAndroidTheme {
                var showSplash by remember { mutableStateOf(true) }
                
                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = { showSplash = false }
                    )
                } else {
                    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
                    val error by settingsViewModel.error.collectAsStateWithLifecycle()
                    
                    // Test connection when URL changes
                    LaunchedEffect(settings.url) {
                        if (settings.url.isNotEmpty()) {
                            Log.i(TAG, "URL changed to: ${settings.url}")
                            settingsViewModel.testConnection(settings.url)
                            
                            // Add initial URL to history on first load
                            if (settings.url.matches(Regex("^https?://.*")) && settings.url.length > 10) {
                                settingsViewModel.addUrlToHistory(settings.url)
                            }
                        }
                    }
                    
                    // Show error toast when error occurs
                    LaunchedEffect(error) {
                        error?.let { errorMessage ->
                            Toast.makeText(
                                this@MainActivity,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                            settingsViewModel.clearError()
                        }
                    }
                    
                    MainScreen(settingsViewModel = settingsViewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Test connection when returning to the main screen
        val url = settingsViewModel.settings.value.url
        if (url.isNotEmpty()) {
            Log.i(TAG, "onResume: testing connection for URL: $url")
            settingsViewModel.testConnection(url)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                Log.i(TAG, "onKeyDown: lowering volume")
                settingsViewModel.sendVolumeCommand("dn")
                true
            }

            KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.i(TAG, "onKeyDown: increasing volume")
                settingsViewModel.sendVolumeCommand("up")
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }
}
