package com.foss.appdock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.foss.appdock.shared.AndroidMainView
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.data.DriverFactory
import com.foss.appdock.shared.platform.AndroidShortcutManager
import com.foss.appdock.shared.settings.SettingsFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsFactory = SettingsFactory(applicationContext)
        val driverFactory = DriverFactory(applicationContext)
        val databaseHelper = DatabaseHelper(driverFactory)
        val shortcutManager = AndroidShortcutManager(applicationContext)
        val browserLauncher =
                com.foss.appdock.shared.platform.BrowserLauncher(applicationContext)

        enableEdgeToEdge()

        setContent {
            AndroidMainView(
                    settingsFactory = settingsFactory,
                    databaseHelper = databaseHelper,
                    shortcutManager = shortcutManager,
                    browserLauncher = browserLauncher,
                    onExitApp = { finish() }
            )
        }
    }
}
