package com.foss.appdock.shared.platform

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.view.ViewGroup.LayoutParams
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback

class WebViewActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private var isIncognito: Boolean = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val url = intent.getStringExtra("url") ?: return finish()
        if (!android.webkit.URLUtil.isValidUrl(url)) return finish()
        isIncognito = intent.getBooleanExtra("isIncognito", false)
        val isIsolated = intent.getBooleanExtra("isIsolated", false)

        if (isIsolated && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                WebView.setDataDirectorySuffix("appdock_isolated_${url.hashCode()}")
            } catch (e: Exception) {
                // Ignore if it's already set by another instance in this process
            }
        }

        // Immersive Fullscreen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
                    or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }

        if (isIncognito) {
            CookieManager.getInstance().removeAllCookies(null)
        }

        webView = WebView(this).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = !isIncognito
                databaseEnabled = !isIncognito
                allowFileAccess = true
                allowContentAccess = true
                mediaPlaybackRequiresUserGesture = false
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                
                if (isIncognito) {
                    cacheMode = WebSettings.LOAD_NO_CACHE
                }
            }

            webViewClient = object : WebViewClient() {
                @Deprecated("Deprecated in Java", ReplaceWith("false"))
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    return false
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                }
            }
            webChromeClient = WebChromeClient()

            setDownloadListener { downloadUrl, userAgent, contentDisposition, mimetype, _ ->
                try {
                    val request = android.app.DownloadManager.Request(android.net.Uri.parse(downloadUrl)).apply {
                        setMimeType(mimetype)
                        addRequestHeader("User-Agent", userAgent)
                        setDescription("Downloading file...")
                        setTitle(android.webkit.URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype))
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                            @Suppress("DEPRECATION")
                            allowScanningByMediaScanner()
                        }
                        setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(
                            android.os.Environment.DIRECTORY_DOWNLOADS,
                            android.webkit.URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype)
                        )
                    }
                    val dm = getSystemService(android.content.Context.DOWNLOAD_SERVICE) as android.app.DownloadManager
                    dm.enqueue(request)
                    android.widget.Toast.makeText(applicationContext, "Downloading file...", android.widget.Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    android.widget.Toast.makeText(applicationContext, "Download failed to start", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (webView.canGoBack()) {
                    webView.goBack()
                } else {
                    finish()
                }
            }
        })

        setContentView(webView)
        webView.loadUrl(url)
    }

    override fun onDestroy() {
        if (isIncognito) {
            CookieManager.getInstance().removeAllCookies(null)
            webView.clearCache(true)
            webView.clearHistory()
        }
        super.onDestroy()
    }
}
