package com.foss.appdock.shared

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.platform.AndroidShortcutManager
import com.foss.appdock.shared.platform.BrowserLauncher
import com.foss.appdock.shared.settings.SettingsFactory

@Composable
fun AndroidMainView(
        settingsFactory: SettingsFactory,
        databaseHelper: DatabaseHelper,
        shortcutManager: com.foss.appdock.shared.platform.AndroidShortcutManager,
        browserLauncher: BrowserLauncher,
        onExitApp: () -> Unit
) {
        var backHandler: (() -> Unit)? by remember { mutableStateOf(null) }

        BackHandler(enabled = true) { backHandler?.invoke() }

        App(
                settingsFactory = settingsFactory,
                databaseHelper = databaseHelper,
                shortcutManager = shortcutManager,
                browserLauncher = browserLauncher,
                onExitApp = onExitApp,
                onBackReady = { backHandler = it }
        )
}
