package com.foss.appdock.shared.platform

import androidx.compose.runtime.mutableStateListOf

object DesktopWindowManager {
    class WebViewAppInstance(
            val id: String,
            val url: String,
            val isIncognito: Boolean,
            val isIsolated: Boolean
    )

    val activeStandaloneApps = mutableStateListOf<WebViewAppInstance>()

    fun launchStandalone(url: String, isIncognito: Boolean, isIsolated: Boolean) {
        val instance =
                WebViewAppInstance(
                        id = System.nanoTime().toString(),
                        url = url,
                        isIncognito = isIncognito,
                        isIsolated = isIsolated
                )
        activeStandaloneApps.add(instance)
    }

    fun closeStandalone(instance: WebViewAppInstance) {
        activeStandaloneApps.remove(instance)
    }
}
