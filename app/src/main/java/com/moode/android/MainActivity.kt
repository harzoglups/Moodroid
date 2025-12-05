package com.moode.android

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.moode.android.ui.MainScreen
import com.moode.android.viewmodel.ConnectionStatus
import com.moode.android.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private val settingsViewModel by viewModels<SettingsViewModel>()
    private val client = OkHttpClient()
    private var url: String = ""
    private var volumeStep: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        settingsViewModel.url.observe(this) { newUrl ->
            Log.i(TAG, "URL observer: URL changed to: $newUrl")
            val previousUrl = url
            url = newUrl
            
            // Add initial URL to history on first load (only once)
            if (previousUrl.isEmpty() && newUrl.matches(Regex("^https?://.*")) && newUrl.length > 10) {
                Log.i(TAG, "Adding initial URL to history: $newUrl")
                settingsViewModel.addUrlToHistory(newUrl)
            }
            
            // Test connection when URL is loaded or changed
            testConnection()
        }

        settingsViewModel.volumeStep.observe(this) { newVolumeStep ->
            volumeStep = newVolumeStep
        }
        setContent {
            MainScreen(settingsViewModel = settingsViewModel)
        }
    }

    override fun onResume() {
        super.onResume()
        // Test connection when returning to the main screen
        // (the URL observer already handles the initial load)
        if (url.isNotEmpty()) {
            Log.i(TAG, "onResume: testing connection for URL: $url")
            testConnection()
        } else {
            Log.i(TAG, "onResume: URL not loaded yet, skipping test")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val volumeCommand: String = "$url/command/?cmd=set_volume%20-"

        return when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                Log.i(TAG, "onKeyDown: lowering volume")
                sendVolumeCommand(volumeCommand + "dn%20" + volumeStep)
                true
            }

            KeyEvent.KEYCODE_VOLUME_UP -> {
                Log.i(TAG, "onKeyDown: increasing volume")
                sendVolumeCommand(volumeCommand + "up%20" + volumeStep)
                true
            }

            else -> super.onKeyDown(keyCode, event)
        }
    }
    
    private fun testConnection() {
        if (url.isEmpty()) {
            Log.i(TAG, "URL is empty, skipping connection test")
            return
        }
        
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url(url)
                        .head() // Use HEAD request to test connectivity without downloading content
                        .build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Server returned error: ${response.code}")
                        }
                        Log.i(TAG, "Connection test successful: ${response.code}")
                    }
                }
                // Update connection status to connected on main thread
                withContext(Dispatchers.Main) {
                    settingsViewModel.updateConnectionStatus(ConnectionStatus.CONNECTED)
                    Log.i(TAG, "Connection status updated to CONNECTED")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Connection test failed", e)
                // Update connection status to disconnected on main thread
                withContext(Dispatchers.Main) {
                    settingsViewModel.updateConnectionStatus(ConnectionStatus.DISCONNECTED)
                    Log.i(TAG, "Connection status updated to DISCONNECTED")
                }
            }
        }
    }

    private fun sendVolumeCommand(url: String) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val request = Request.Builder()
                        .url(url)
                        .build()
                    client.newCall(request).execute().use { response ->
                        if (!response.isSuccessful) {
                            throw IOException("Server returned error: ${response.code}")
                        }
                        Log.i(TAG, "Volume command sent successfully: ${response.code}")
                    }
                }
                // Update connection status to connected on main thread
                withContext(Dispatchers.Main) {
                    settingsViewModel.updateConnectionStatus(ConnectionStatus.CONNECTED)
                    Log.i(TAG, "Volume command status updated to CONNECTED")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to send volume command", e)
                // Update connection status to disconnected on main thread
                withContext(Dispatchers.Main) {
                    settingsViewModel.updateConnectionStatus(ConnectionStatus.DISCONNECTED)
                    Log.i(TAG, "Volume command status updated to DISCONNECTED")
                }
                // Show error feedback on main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to adjust volume: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}