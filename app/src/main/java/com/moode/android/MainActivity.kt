package com.moode.android

import android.os.Bundle
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WebViewContent()
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