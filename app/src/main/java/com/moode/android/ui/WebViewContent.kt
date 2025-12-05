package com.moode.android.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moode.android.MainActivity
import com.moode.android.R
import com.moode.android.viewmodel.SettingsViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContent(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val url = settings.url.ifEmpty { context.getString(R.string.url) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var loading by remember { mutableStateOf(true) }
    var currentUrl by remember { mutableStateOf(url) }

    fun createWebView(initialUrl: String): WebView {
        return WebView(context).apply {
            // Enable hardware acceleration for better rendering performance
            setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            
            webViewClient = object : WebViewClient() {
                override fun onPageStarted(
                    view: WebView?,
                    url: String?,
                    favicon: android.graphics.Bitmap?
                ) {
                    super.onPageStarted(view, url, favicon)
                    loading = true
                    Log.d(MainActivity.TAG, "onPageStarted: $url")
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    val progress = view?.progress
                    Log.d(MainActivity.TAG, "onPageFinished - Progress = $progress, URL = $url")
                    // Always set loading to false when page finishes, regardless of progress
                    loading = false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    // Only set loading to false if this is the main frame
                    if (request?.isForMainFrame == true) {
                        loading = false
                        Log.e(MainActivity.TAG, "onReceivedError (main frame) - Error: ${error?.description}")
                    } else {
                        Log.d(MainActivity.TAG, "onReceivedError (subframe) - Error: ${error?.description}")
                    }
                }

                override fun onRenderProcessGone(
                    view: WebView?,
                    detail: RenderProcessGoneDetail?
                ): Boolean {
                    Log.e(MainActivity.TAG, "onRenderProcessGone: $detail")
                    (context as? MainActivity)?.let { activity ->
                        activity.runOnUiThread {
                            view?.destroy()
                            webView = createWebView(initialUrl)
                            webView?.loadUrl(initialUrl)
                        }
                    }
                    return true
                }
            }
            this.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
                
                // Intelligent caching strategy - prefer cache when available to speed up loads
                cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                
                // Enable Safe Browsing for security
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    safeBrowsingEnabled = true
                }
                
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                userAgentString = WebSettings.getDefaultUserAgent(context)
                databaseEnabled = true
                mediaPlaybackRequiresUserGesture = false
                builtInZoomControls = true
                displayZoomControls = false
                layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                allowFileAccess = true
                javaScriptCanOpenWindowsAutomatically = true
                loadsImagesAutomatically = true
            }
            layoutParams = android.view.ViewGroup.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Handle URL changes
    LaunchedEffect(url) {
        if (url != currentUrl) {
            Log.i(MainActivity.TAG, "URL changed from $currentUrl to $url")
            currentUrl = url
            loading = true
            webView?.loadUrl(url)
        }
    }

    // Initialize WebView only once
    if (webView == null) {
        webView = createWebView(url)
        webView?.loadUrl(url)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.i(MainActivity.TAG, "Refreshing URL $url")
                    loading = true
                    webView?.reload()
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.offset(
                    x = if (isLandscape) (-16).dp else 0.dp,
                    y = if (isLandscape) (-80).dp else (-120).dp
                )
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        content = { pv ->
            Box(modifier = Modifier.fillMaxSize()) {
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(pv),
                    factory = {
                        Log.i(MainActivity.TAG, "AndroidView factory creating WebView for URL: $url")
                        webView ?: createWebView(url).also { 
                            webView = it
                            it.loadUrl(url)
                        }
                    },
                    update = {
                        // Do NOT reload here - this is called on every recomposition
                        // URL changes are handled by LaunchedEffect above
                        Log.d(MainActivity.TAG, "AndroidView update block (no action taken)")
                    }
                )
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(50.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )

    DisposableEffect(Unit) {
        onDispose {
            Log.i(MainActivity.TAG, "Disposing WebView")
            webView?.destroy()
            webView = null
        }
    }
}
