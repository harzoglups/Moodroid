package com.moode.android

import android.os.Bundle
import android.view.KeyEvent
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlin.concurrent.thread
import java.io.IOException

class MainActivity : ComponentActivity() {
    private val client = OkHttpClient()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewContent()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                sendVolumeCommand("http://moode.local/command/?cmd=set_volume%20-dn%201")
                return true
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                sendVolumeCommand("http://moode.local/command/?cmd=set_volume%20-up%201")
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
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

@Preview
@Composable
fun WebViewContent() {
    val url = "http://moode.local"
    val context = LocalContext.current
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = {
                WebView(context).apply {

                    webViewClient = WebViewClient()

                    settings.apply {
                        javaScriptEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        domStorageEnabled = true
                        cacheMode = WebSettings.LOAD_DEFAULT
                        mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        userAgentString = WebSettings.getDefaultUserAgent(context) // Sets default user agent
                        databaseEnabled = true
                        //setAppCacheEnabled(true)
                        mediaPlaybackRequiresUserGesture = false
                        builtInZoomControls = true
                        displayZoomControls = false
                        layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING

                        // Enabling additional settings for better performance and rendering
                        allowFileAccess = true
                        javaScriptCanOpenWindowsAutomatically = true
                        loadsImagesAutomatically = true
                    }
                    // Ensuring the WebView is laid out properly
                    layoutParams = android.view.ViewGroup.LayoutParams(
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                        android.view.ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    loadUrl(url)
                }
        }
    )
}