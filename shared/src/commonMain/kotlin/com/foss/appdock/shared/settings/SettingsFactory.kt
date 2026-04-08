package com.foss.appdock.shared.settings

import com.russhwolf.settings.Settings

expect class SettingsFactory {
    fun createSettings(): Settings
}
