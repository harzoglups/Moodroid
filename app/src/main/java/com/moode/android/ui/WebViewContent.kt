package com.moode.android.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

/**
 * Owns the WebView showing the Moode web UI.
 *
 * It lives above the navigation graph (see [MainScreen]) so that navigating to the settings
 * screen and back does not destroy the WebView and reload the page.
 */
class MoodeWebViewHolder(private val context: Context) {
    var loading: Boolean by mutableStateOf(true)
        private set

    private var loadedUrl: String? = null

    var webView: WebView by mutableStateOf(createWebView())
        private set

    /** Loads [url] unless it is already the page being displayed. */
    fun loadIfChanged(url: String) {
        if (url == loadedUrl) return
        Log.i(MainActivity.TAG, "Loading URL: $url (previous: $loadedUrl)")
        loadedUrl = url
        loading = true
        webView.loadUrl(url)
    }

    fun reload() {
        loading = true
        webView.reload()
    }

    fun onPause() {
        webView.onPause()
        webView.pauseTimers()
    }

    fun onResume() {
        webView.onResume()
        webView.resumeTimers()
    }

    fun destroy() {
        Log.i(MainActivity.TAG, "Destroying WebView")
        webView.destroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun createWebView(): WebView {
        return WebView(context).apply {
            // Enable hardware acceleration for better rendering
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
                    Log.d(MainActivity.TAG, "onPageFinished - Progress = ${view?.progress}, URL = $url")
                    loading = false
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    // Only stop the spinner if this is the main frame
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
                    // Recreate the WebView and reload the last requested URL
                    view?.destroy()
                    val urlToRestore = loadedUrl
                    loadedUrl = null
                    webView = createWebView()
                    urlToRestore?.let { loadIfChanged(it) }
                    return true
                }
            }
            this.settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
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
                offscreenPreRaster = false
                blockNetworkLoads = false
            }
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
}

@Composable
fun WebViewContent(
    holder: MoodeWebViewHolder,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val settings by settingsViewModel.settings.collectAsStateWithLifecycle()
    val resolvedUrl by settingsViewModel.resolvedUrl.collectAsStateWithLifecycle()
    val displayUrl = settings.url.ifEmpty { context.getString(R.string.url) }
    // Use resolved URL for WebView, fall back to display URL if resolution hasn't completed yet
    val url = resolvedUrl ?: displayUrl

    // No-op when returning from the settings screen with an unchanged URL
    LaunchedEffect(url) {
        holder.loadIfChanged(url)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.i(MainActivity.TAG, "Refreshing URL $url")
                    holder.reload()
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
                // key: swap the AndroidView if the holder had to recreate the WebView
                key(holder.webView) {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(pv),
                        factory = { holder.webView }
                    )
                }
                if (holder.loading) {
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
