package com.foss.appdock.shared.platform

expect class BrowserLauncher(context: Any?) {
    fun openUrlInBrowser(
            url: String,
            browserName: String?,
            isIncognito: Boolean = false,
            isStandalone: Boolean = false,
            isIsolated: Boolean = false
    ): Boolean
}
