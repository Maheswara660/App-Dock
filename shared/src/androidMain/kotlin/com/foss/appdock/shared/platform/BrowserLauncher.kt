package com.foss.appdock.shared.platform

import android.content.Context
import android.content.Intent
import android.net.Uri

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

        // Apply advanced flags if supported by known browsers
        val browserLower = browserName?.lowercase() ?: ""
        if (isIncognito) {
            when {
                browserLower.contains("chrome") -> {
                    intent.putExtra("com.google.android.apps.chrome.EXTRA_IS_INCOGNITO", true)
                }
                browserLower.contains("firefox") -> {
                    intent.putExtra("private_browsing_mode", true)
                }
                else -> {
                    // Try generic custom tabs incognito extra
                    intent.putExtra("android.support.customtabs.extra.INCOGNITO", true)
                }
            }
        }

        // Map AppDock browser names to Android package names
        val packageName =
                when (browserLower) {
                    "chrome" -> "com.android.chrome"
                    "firefox" -> "org.mozilla.firefox"
                    "edge" -> "com.microsoft.emmx"
                    "brave" -> "com.brave.browser"
                    else -> null // System Default
                }

        if (packageName != null) {
            intent.setPackage(packageName)
        }

        return try {
            androidContext.startActivity(intent)
            true
        } catch (e: Exception) {
            // Fallback to system default if package launch fails
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
