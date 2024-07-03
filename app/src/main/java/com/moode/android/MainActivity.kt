package com.moode.android

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.moode.android.ui.MainScreen
import com.moode.android.viewmodel.SettingsViewModel
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import kotlin.concurrent.thread

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
        setContent {
            MainScreen(settingsViewModel = settingsViewModel)
        }
        settingsViewModel.url.observe(this) { newUrl ->
            url = newUrl
        }

        settingsViewModel.volumeStep.observe(this) { newVolumeStep ->
            volumeStep = newVolumeStep
        }
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

    private fun sendVolumeCommand(url: String) {
        thread {
            try {
                val request = Request.Builder()
                    .url(url)
                    .build()
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}