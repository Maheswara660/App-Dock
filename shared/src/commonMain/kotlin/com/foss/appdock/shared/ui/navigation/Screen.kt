package com.foss.appdock.shared.ui.navigation

import com.foss.appdock.shared.domain.Category
import com.foss.appdock.shared.domain.WebApp

sealed class Screen {
    object Dashboard : Screen()
    object Search : Screen()
    object AddApp : Screen()
    object ManageCategories : Screen()
    data class CategoryApps(val category: Category) : Screen()
    object Settings : Screen()
    object SettingsAppearance : Screen()
    object SettingsPrivacy : Screen()
    object SettingsBackup : Screen()
    object SettingsAppManagement : Screen()
    object SettingsAbout : Screen()
    object SettingsChangelog : Screen()
    object ExportConfig : Screen()
    object ImportConfig : Screen()
    data class AppDetails(val app: WebApp) : Screen()
}
