package com.foss.appdock.shared.platform

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.data.DriverFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * A transparent activity that handles shortcut deep links (appdock://launch?id=...).
 * It handles launch tracking and redirects to the browser without showing a UI.
 * Located in shared module for direct access by ShortcutManager.
 */
class ShortcutHandlerActivity : Activity() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val data = intent?.data
        Log.d("AppDock", "ShortcutHandlerActivity: Received intent with data: $data")
        
        if (data != null && data.scheme == "appdock" && data.host == "launch") {
            val appId = data.getQueryParameter("id")?.toLongOrNull()
            if (appId != null) {
                handleLaunch(appId)
            } else {
                Log.e("AppDock", "ShortcutHandlerActivity: Invalid app ID")
                finish()
            }
        } else {
            Log.e("AppDock", "ShortcutHandlerActivity: Invalid deep link data")
            finish()
        }
    }

    private fun handleLaunch(appId: Long) {
        val databaseHelper = DatabaseHelper(DriverFactory(this))
        val browserLauncher = BrowserLauncher(this)

        scope.launch {
            try {
                val app = databaseHelper.getWebAppById(appId)
                if (app != null) {
                    Log.d("AppDock", "ShortcutHandlerActivity: Launching ${app.name}")
                    // Increment launch count (Tracking)
                    databaseHelper.updateWebApp(app.copy(launchCount = app.launchCount + 1))

                    // Launch browser
                    browserLauncher.openUrlInBrowser(
                        url = app.url,
                        browserName = app.browserChoice,
                        isIncognito = app.incognitoMode,
                        isStandalone = app.isStandalone,
                        isIsolated = app.isolatedProfile
                    )
                } else {
                    Log.e("AppDock", "ShortcutHandlerActivity: App with ID $appId not found")
                }
            } catch (e: Exception) {
                Log.e("AppDock", "ShortcutHandlerActivity: Error handling launch", e)
            } finally {
                finish()
            }
        }
    }
}
