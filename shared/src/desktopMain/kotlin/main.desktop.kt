package com.foss.appdock.shared

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.data.DriverFactory
import com.foss.appdock.shared.platform.BrowserLauncher
import com.foss.appdock.shared.platform.DesktopShortcutManager
import com.foss.appdock.shared.settings.SettingsFactory

@Composable
fun DesktopMainView() {
    val settingsFactory = SettingsFactory()
    val driverFactory = DriverFactory()
    val databaseHelper = DatabaseHelper(driverFactory)
    val shortcutManager = DesktopShortcutManager()
    val browserLauncher = BrowserLauncher(null)
    App(settingsFactory, databaseHelper, shortcutManager, browserLauncher)
}

@Preview
@Composable
fun AppPreview() {
    val settingsFactory = SettingsFactory()
    val driverFactory = DriverFactory()
    val databaseHelper = DatabaseHelper(driverFactory)
    val shortcutManager = DesktopShortcutManager()
    val browserLauncher = BrowserLauncher(null)
    App(settingsFactory, databaseHelper, shortcutManager, browserLauncher)
}
