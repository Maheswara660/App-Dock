package com.foss.appdock.shared.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

actual class BrowserLauncher actual constructor(private val context: Any?) {
    actual fun openUrlInBrowser(
            url: String,
            browserName: String?,
            isIncognito: Boolean,
            isStandalone: Boolean,
            isIsolated: Boolean
    ): Boolean {
        val androidContext = context as? Context ?: return false

        val intent =
                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

        val browserLower = browserName?.lowercase() ?: ""
        
        // Better package mapping
        val packageName = when {
            browserLower.contains("chrome") -> "com.android.chrome"
            browserLower.contains("firefox") -> "org.mozilla.firefox"
            browserLower.contains("edge") -> "com.microsoft.emmx"
            browserLower.contains("brave") -> "com.brave.browser"
            browserLower.contains("samsung") -> "com.sec.android.app.sbrowser"
            browserLower.contains("opera") -> "com.opera.browser"
            browserLower.contains("vivaldi") -> "com.vivaldi.browser"
            else -> null
        }

        if (packageName != null) {
            intent.setPackage(packageName)
        }

        if (isIncognito) {
            when {
                packageName == "com.android.chrome" -> {
                    intent.putExtra("com.google.android.apps.chrome.EXTRA_IS_INCOGNITO", true)
                }
                packageName == "org.mozilla.firefox" -> {
                    intent.putExtra("private_browsing_mode", true)
                }
                else -> {
                    intent.putExtra("android.support.customtabs.extra.INCOGNITO", true)
                }
            }
        }

        if (isStandalone) {
            try {
                val webViewIntent = Intent(androidContext, Class.forName("com.foss.appdock.shared.platform.WebViewActivity")).apply {
                    putExtra("url", url)
                    putExtra("isIncognito", isIncognito)
                    putExtra("isIsolated", isIsolated)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                androidContext.startActivity(webViewIntent)
                return true
            } catch (e: Exception) {
                // Fallback inside BrowserLauncher continues below
            }
        }

        return try {
            androidContext.startActivity(intent)
            true
        } catch (e: Exception) {
            if (packageName != null) {
                intent.setPackage(null)
                try {
                    androidContext.startActivity(intent)
                    true
                } catch (e2: Exception) {
                    false
                }
            } else {
                false
            }
        }
    }
}
