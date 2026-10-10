package com.moode.android.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.util.Log
import android.view.ViewGroup
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.moode.android.MainActivity
import com.moode.android.R
import com.moode.android.viewmodel.SettingsViewModel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewContent(
    settingsViewModel: SettingsViewModel,
    retainedWebView: RetainedWebView
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val resolvedUrl by settingsViewModel.resolvedUrl.collectAsStateWithLifecycle()
    val displayUrl = settings.url.ifEmpty { context.getString(R.string.url) }
    // Use resolved URL for WebView, fall back to display URL if resolution hasn't completed yet
    val url = resolvedUrl ?: displayUrl
    // Only show the spinner if there is no already-loaded page on screen
    var loading by remember { mutableStateOf(retainedWebView.webView == null) }
    var currentUrl by remember { mutableStateOf(url) }
    // Bumped to force a brand new WebView after the render process dies
    var webViewKey by remember { mutableStateOf(0) }

    fun createWebView(): WebView {
        return WebView(context).apply {
            // Enable hardware acceleration for better rendering and audio performance
            setLayerType(android.view.View.LAYER_TYPE_HARDWARE, null)
            
            // Keep screen on during audio playback to prevent network issues
            keepScreenOn = false // We use wake lock instead to save battery
            
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
                    // The render process died; the WebView must be discarded and rebuilt.
                    view?.let { dying ->
                        (dying.parent as? ViewGroup)?.removeView(dying)
                        dying.destroy()
                    }
                    if (retainedWebView.webView === view) {
                        retainedWebView.webView = null
                    }
                    // Recreate the AndroidView (and therefore the WebView) from scratch
                    webViewKey++
                    loading = true
                    return true
                }
            }
            this.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
                
                // Use default cache mode for audio streaming - LOAD_CACHE_ELSE_NETWORK can cause issues
                cacheMode = WebSettings.LOAD_DEFAULT
                
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
                
                // Optimize for media streaming
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    // Disable offscreen pre-rasterization to save memory and improve streaming
                    offscreenPreRaster = false
                }
                
                // Set appropriate block network loads setting for streaming
                blockNetworkLoads = false
            }
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    // Handle URL changes
    LaunchedEffect(url) {
        if (url != currentUrl) {
            Log.i(MainActivity.TAG, "URL changed from $currentUrl to $url")
            currentUrl = url
            loading = true
            retainedWebView.webView?.loadUrl(url)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.i(MainActivity.TAG, "Refreshing URL $url")
                    loading = true
                    retainedWebView.webView?.reload()
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.offset(
                    x = if (isLandscape) (-16).dp else 0.dp,
                    y = if (isLandscape) (-80).dp else (-120).dp
                )
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.main_refresh_button),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        content = { pv ->
            Box(modifier = Modifier.fillMaxSize()) {
                // The WebView is retained across recomposition and navigation so the page
                // does not reload when opening Settings or returning to the app.
                key(webViewKey) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pv),
                        factory = {
                            retainedWebView.webView?.also { existing ->
                                // Detach from any previous parent before re-attaching
                                (existing.parent as? ViewGroup)?.removeView(existing)
                            } ?: createWebView().also { created ->
                                Log.i(MainActivity.TAG, "Creating WebView for URL: $url")
                                retainedWebView.webView = created
                                created.loadUrl(url)
                            }
                        },
                        update = {
                            // Do NOT reload here - this is called on every recomposition
                            // URL changes are handled by LaunchedEffect above
                            Log.d(MainActivity.TAG, "AndroidView update block (no action taken)")
                        }
                    )
                }
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
}
