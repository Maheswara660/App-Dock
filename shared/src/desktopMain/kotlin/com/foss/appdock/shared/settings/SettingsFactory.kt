package com.foss.appdock.shared.settings

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import java.util.prefs.Preferences

actual class SettingsFactory {
    actual fun createSettings(): Settings {
        val delegate: Preferences = Preferences.userRoot().node("AppDockPrefs")
        return PreferencesSettings(delegate)
    }
}
