package com.moode.android.ui

import android.webkit.WebView

/**
 * Holds a single [WebView] instance for the lifetime of [com.moode.android.MainActivity].
 *
 * Keeping the instance outside the Compose tree means the loaded page survives
 * recomposition, navigation to the Settings screen and configuration changes
 * (rotation, dark mode, ...). The WebView is only destroyed when the Activity
 * is truly finishing.
 */
class RetainedWebView {
    var webView: WebView? = null
}
