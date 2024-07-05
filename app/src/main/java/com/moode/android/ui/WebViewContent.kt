package com.moode.android.ui

import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.moode.android.MainActivity
import com.moode.android.R
import com.moode.android.viewmodel.SettingsViewModel

@Composable
fun WebViewContent(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val url = settingsViewModel.url.value ?: context.getString(R.string.url)
    val webView = remember {
        WebView(context).apply {
            webViewClient = WebViewClient()
            settings.apply {
                javaScriptEnabled = true
                loadWithOverviewMode = true
                useWideViewPort = true
                domStorageEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
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

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Log.i(MainActivity.TAG, "Refreshing URL $url")
                    webView.reload()
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.offset(x = 0.dp, y = (-120).dp)
            ) {
                Icon(
                    Icons.Filled.Refresh,
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        },
        content = { pv ->
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(pv),
                factory = {
                    webView
                },
                update = {
                    Log.i(MainActivity.TAG, "Loading URL $url")
                    it.loadUrl(url)
                }
            )
        }
    )
}