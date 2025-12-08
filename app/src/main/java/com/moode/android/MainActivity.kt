package com.moode.android

import android.os.Bundle
import android.os.PowerManager
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
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Acquire wake lock to prevent network from sleeping during audio streaming
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "Moodroid::AudioStreamingWakeLock"
        ).apply {
            acquire()
            Log.i(TAG, "Wake lock acquired for audio streaming")
        }
        
        setContent {
            MoodeAndroidTheme {
                var showSplash by remember { mutableStateOf(true) }
                
                if (showSplash) {
                    SplashScreen(
                        onSplashFinished = { showSplash = false }
                    )
                } else {
                    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
                    val resolvedUrl by settingsViewModel.resolvedUrl.collectAsStateWithLifecycle()
                    val error by settingsViewModel.error.collectAsStateWithLifecycle()
                    
                    // Test connection when resolved URL changes
                    LaunchedEffect(resolvedUrl) {
                        resolvedUrl?.let { url ->
                            if (url.isNotEmpty()) {
                                Log.i(TAG, "Resolved URL changed to: $url")
                                settingsViewModel.testConnection(url)
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
        // Test connection with resolved URL when returning to the main screen
        val resolvedUrl = settingsViewModel.resolvedUrl.value
        if (resolvedUrl != null && resolvedUrl.isNotEmpty()) {
            Log.i(TAG, "onResume: testing connection for resolved URL: $resolvedUrl")
            settingsViewModel.testConnection(resolvedUrl)
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
    
    override fun onDestroy() {
        super.onDestroy()
        // Release wake lock when activity is destroyed
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
                Log.i(TAG, "Wake lock released")
            }
        }
    }
}
