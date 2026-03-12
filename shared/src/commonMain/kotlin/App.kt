package com.foss.appdock.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.foss.appdock.shared.data.DatabaseHelper
import com.foss.appdock.shared.domain.WebApp
import com.foss.appdock.shared.platform.BrowserLauncher
import com.foss.appdock.shared.platform.ShortcutManager
import com.foss.appdock.shared.settings.SettingsFactory
import com.foss.appdock.shared.ui.navigation.Screen
import com.foss.appdock.shared.ui.screens.*
import com.foss.appdock.shared.ui.theme.AppDockTheme
import com.foss.appdock.shared.utils.getSystemTimeMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun App(
        settingsFactory: SettingsFactory,
        databaseHelper: DatabaseHelper,
        shortcutManager: ShortcutManager,
        browserLauncher: BrowserLauncher,
        onExitApp: () -> Unit = {},
        onBackReady: (() -> Unit) -> Unit = {}
) {
        val settings = remember { settingsFactory.createSettings() }
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }

        val webApps by databaseHelper.getAllWebApps().collectAsState(initial = emptyList())
        val categories by databaseHelper.getAllCategories().collectAsState(initial = emptyList())

        var selectedTheme by remember {
                mutableStateOf(settings.getStringOrNull("theme") ?: "System Default")
        }
        var selectedBrowser by remember {
                mutableStateOf(settings.getStringOrNull("browser") ?: "System Default")
        }
        var iconStyle by remember {
                mutableStateOf(settings.getStringOrNull("iconStyle") ?: "System Adaptive")
        }
        var iconSize by remember {
                mutableStateOf(settings.getStringOrNull("iconSize") ?: "System Adaptive")
        }
        var accentColor by remember {
                mutableStateOf(settings.getStringOrNull("accentColor") ?: "#2B6CEE")
        }
        var appToDelete by remember { mutableStateOf<WebApp?>(null) }

        val isDark =
                when (selectedTheme) {
                        "Dark", "AMOLED Black" -> true
                        "Light" -> false
                        else -> isSystemInDarkTheme()
                }

        // ── Navigation stack ─────────────────────────────────────────────────────
        val screenStack = remember { mutableStateListOf<Screen>(Screen.Dashboard) }
        val currentScreen = screenStack.last()

        // Push any sub-screen
        fun push(screen: Screen) = screenStack.add(screen)

        // Go all the way to Dashboard
        fun popToDashboard() {
                while (screenStack.size > 1) screenStack.removeAt(screenStack.lastIndex)
        }

        // Pop exactly ONE screen (sub-screen back press → parent)
        fun pop() {
                if (screenStack.size > 1) {
                        val current = screenStack.last()
                        // If it's a top-level navbar screen (Search, ManageCategories, Settings),
                        // go back to Dashboard
                        if (current is Screen.Search ||
                                        current is Screen.ManageCategories ||
                                        current is Screen.Settings
                        ) {
                                popToDashboard()
                        } else {
                                screenStack.removeAt(screenStack.lastIndex)
                        }
                }
        }

        // ── "Double press to exit" logic ──
        var backPressTime by remember { mutableStateOf(0L) }

        val handleBack: () -> Unit = {
                if (screenStack.size > 1) {
                        pop()
                } else {
                        val now = getSystemTimeMillis()
                        if (now - backPressTime < 2000) {
                                onExitApp()
                        } else {
                                backPressTime = now
                                coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                                message = "Press back again to exit",
                                                duration = SnackbarDuration.Short
                                        )
                                }
                        }
                }
        }

        LaunchedEffect(handleBack) { onBackReady(handleBack) }

        var lastAddedApp by remember { mutableStateOf<WebApp?>(null) }

        AppDockTheme(
                themeSelection = selectedTheme,
                darkTheme = isDark,
                accentColor = Color(accentColor.removePrefix("#").toLong(16) or 0xFF000000)
        ) {
                Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                ) {
                        val onTabSelected: (BottomNavTab) -> Unit = { tab ->
                                when (tab) {
                                        BottomNavTab.APPS -> popToDashboard()
                                        BottomNavTab.SEARCH ->
                                                if (currentScreen !is Screen.Search) {
                                                        popToDashboard()
                                                        push(Screen.Search)
                                                }
                                        BottomNavTab.CATEGORY ->
                                                if (currentScreen !is Screen.ManageCategories) {
                                                        popToDashboard()
                                                        push(Screen.ManageCategories)
                                                }
                                        BottomNavTab.SETTINGS ->
                                                if (currentScreen !is Screen.Settings) {
                                                        popToDashboard()
                                                        push(Screen.Settings)
                                                }
                                }
                        }

                        when (val screen = currentScreen) {
                                is Screen.Dashboard -> {
                                        DashboardScreen(
                                                webApps = webApps,
                                                onAddAppClick = { push(Screen.AddApp) },
                                                onAppClick = { app ->
                                                        coroutineScope.launch {
                                                                databaseHelper.updateWebApp(
                                                                        app.copy(
                                                                                launchCount =
                                                                                        app.launchCount +
                                                                                                1
                                                                        )
                                                                )
                                                                val success =
                                                                        browserLauncher
                                                                                .openUrlInBrowser(
                                                                                        app.url,
                                                                                        app.browserChoice,
                                                                                        isIncognito =
                                                                                                app.incognitoMode,
                                                                                        isStandalone =
                                                                                                app.isStandalone,
                                                                                        isIsolated =
                                                                                                app.isolatedProfile
                                                                                )
                                                                if (!success) {
                                                                        if (app.browserChoice !=
                                                                                        null &&
                                                                                        app.browserChoice !=
                                                                                                "System Default"
                                                                        ) {
                                                                                val fallbackSuccess =
                                                                                        browserLauncher
                                                                                                .openUrlInBrowser(
                                                                                                        app.url,
                                                                                                        "System Default"
                                                                                                )
                                                                                if (fallbackSuccess
                                                                                ) {
                                                                                        databaseHelper
                                                                                                .updateWebApp(
                                                                                                        app.copy(
                                                                                                                browserChoice =
                                                                                                                        "System Default"
                                                                                                        )
                                                                                                )
                                                                                        snackbarHostState
                                                                                                .showSnackbar(
                                                                                                        "${app.browserChoice} unavailable. Used System Default."
                                                                                                )
                                                                                } else {
                                                                                        snackbarHostState
                                                                                                .showSnackbar(
                                                                                                        "No browser found. Please install a browser."
                                                                                                )
                                                                                }
                                                                        } else {
                                                                                push(
                                                                                        Screen.AppDetails(
                                                                                                app
                                                                                        )
                                                                                )
                                                                        }
                                                                }
                                                        }
                                                },
                                                onAppDetails = { app ->
                                                        push(Screen.AppDetails(app))
                                                },
                                                onDeleteApp = { app -> appToDelete = app },
                                                onTabSelected = onTabSelected,
                                                snackbarHostState = snackbarHostState
                                        )

                                        appToDelete?.let { app ->
                                                M3DangerDialog(
                                                        title = "Delete ${app.name}?",
                                                        text =
                                                                "Are you sure you want to delete this app?",
                                                        confirmText = "Delete",
                                                        onConfirm = {
                                                                coroutineScope.launch {
                                                                        shortcutManager
                                                                                .deleteShortcut(app)
                                                                        databaseHelper.deleteWebApp(
                                                                                app.id
                                                                        )
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        "Deleted ${app.name}"
                                                                                )
                                                                }
                                                                appToDelete = null
                                                        },
                                                        onDismiss = { appToDelete = null }
                                                )
                                        }
                                }
                                is Screen.Search ->
                                        SearchScreen(
                                                webApps = webApps,
                                                availableCategories = categories.map { it.name },
                                                onBack = { handleBack() },
                                                onAppClick = { app ->
                                                        coroutineScope.launch {
                                                                val success =
                                                                        browserLauncher
                                                                                .openUrlInBrowser(
                                                                                        app.url,
                                                                                        app.browserChoice,
                                                                                        isIncognito =
                                                                                                app.incognitoMode,
                                                                                        isStandalone =
                                                                                                app.isStandalone,
                                                                                        isIsolated =
                                                                                                app.isolatedProfile
                                                                                )
                                                                if (!success)
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        "Could not open ${app.name}."
                                                                                )
                                                        }
                                                },
                                                onAddAppClick = { push(Screen.AddApp) },
                                                onTabSelected = onTabSelected,
                                                snackbarHostState = snackbarHostState
                                        )
                                is Screen.AddApp ->
                                        AddWebAppScreen(
                                                availableCategories = categories.map { it.name },
                                                onBack = { handleBack() },
                                                onSave = {
                                                        name,
                                                        url,
                                                        category,
                                                        browser,
                                                        isStandalone,
                                                        notificationsEnabled,
                                                        isolatedProfile,
                                                        incognitoMode,
                                                        customIconUrl ->
                                                        val createdAt: Long =
                                                                getSystemTimeMillis()
                                                        coroutineScope.launch(Dispatchers.Default) {
                                                                val domain =
                                                                        try {
                                                                                io.ktor.http.Url(
                                                                                                url
                                                                                        )
                                                                                        .host
                                                                        } catch (e: Exception) {
                                                                                ""
                                                                        }
                                                                val autoIconUrl =
                                                                        if (domain.isNotBlank())
                                                                                "https://www.google.com/s2/favicons?domain=$domain&sz=128"
                                                                        else ""
                                                                val iconToUse =
                                                                        customIconUrl
                                                                                ?: autoIconUrl
                                                                                        .ifBlank {
                                                                                                null
                                                                                        }
                                                                val newApp =
                                                                        WebApp(
                                                                                name = name,
                                                                                url = url,
                                                                                iconPath =
                                                                                        iconToUse,
                                                                                category = category,
                                                                                browserChoice =
                                                                                        browser,
                                                                                isStandalone =
                                                                                        isStandalone,
                                                                                notificationsEnabled =
                                                                                        notificationsEnabled,
                                                                                isolatedProfile =
                                                                                        isolatedProfile,
                                                                                incognitoMode =
                                                                                        incognitoMode,
                                                                                createdAt =
                                                                                        createdAt
                                                                        )
                                                                databaseHelper.insertWebApp(newApp)
                                                                snackbarHostState.showSnackbar(
                                                                        message =
                                                                                "Created ${newApp.name}",
                                                                        duration =
                                                                                SnackbarDuration
                                                                                        .Short
                                                                )
                                                                shortcutManager.createShortcut(
                                                                        newApp
                                                                )
                                                                lastAddedApp = newApp
                                                        }
                                                        // Ideally wait for coroutine, but UI is
                                                        // eager:
                                                        lastAddedApp?.let {
                                                                popToDashboard()
                                                                push(Screen.AppAdded(it))
                                                        }
                                                                ?: run { popToDashboard() }
                                                }
                                        )
                                is Screen.AppAdded ->
                                        AppAddedSuccessScreen(
                                                app = screen.app,
                                                onGoToDashboard = { popToDashboard() },
                                                onAddAnother = {
                                                        popToDashboard()
                                                        push(Screen.AddApp)
                                                }
                                        )
                                is Screen.AppDetails ->
                                        WebAppDetailsScreen(
                                                app = screen.app,
                                                availableCategories = categories.map { it.name },
                                                onBack = { handleBack() },
                                                onSave = { updatedApp ->
                                                        coroutineScope.launch {
                                                                databaseHelper.updateWebApp(
                                                                        updatedApp
                                                                )
                                                                snackbarHostState.showSnackbar(
                                                                        message =
                                                                                "Updated ${updatedApp.name}",
                                                                        duration =
                                                                                SnackbarDuration
                                                                                        .Short
                                                                )
                                                        }
                                                        pop()
                                                },
                                                onDelete = {
                                                        coroutineScope.launch {
                                                                databaseHelper.deleteWebApp(
                                                                        screen.app.id
                                                                )
                                                                snackbarHostState.showSnackbar(
                                                                        message =
                                                                                "Deleted ${screen.app.name}",
                                                                        duration =
                                                                                SnackbarDuration
                                                                                        .Short
                                                                )
                                                        }
                                                        popToDashboard()
                                                },
                                                snackbarHostState = snackbarHostState
                                        )
                                is Screen.Settings ->
                                        SettingsScreen(
                                                onAppearanceClick = {
                                                        push(Screen.SettingsAppearance)
                                                },
                                                onPrivacyClick = { push(Screen.SettingsPrivacy) },
                                                onBackupClick = { push(Screen.SettingsBackup) },
                                                onAppManagementClick = {
                                                        push(Screen.SettingsAppManagement)
                                                },
                                                onAboutClick = { push(Screen.SettingsAbout) },
                                                onTabSelected = onTabSelected,
                                                onFabClick = { push(Screen.AddApp) },
                                                onBack = { handleBack() },
                                                snackbarHostState = snackbarHostState
                                        )
                                is Screen.SettingsAppearance ->
                                        SettingsAppearanceScreen(
                                                selectedTheme = selectedTheme,
                                                onThemeSelected = { theme ->
                                                        selectedTheme = theme
                                                        settings.putString("theme", theme)
                                                },
                                                accentColor = accentColor,
                                                onAccentColorSelected = { color ->
                                                        accentColor = color
                                                        settings.putString("accentColor", color)
                                                },
                                                selectedIconSize = iconSize,
                                                onIconSizeSelected = { size ->
                                                        iconSize = size
                                                        settings.putString("iconSize", size)
                                                },
                                                selectedIconShape = iconStyle,
                                                onIconShapeSelected = { style ->
                                                        iconStyle = style
                                                        settings.putString("iconStyle", style)
                                                },
                                                onBack = { handleBack() }
                                        )
                                is Screen.SettingsPrivacy ->
                                        SettingsPrivacyScreen(onBack = { handleBack() })
                                is Screen.SettingsBackup ->
                                        SettingsBackupScreen(
                                                databaseHelper = databaseHelper,
                                                onExportClick = { push(Screen.ExportConfig) },
                                                onImportClick = { push(Screen.ImportConfig) },
                                                onBack = { handleBack() },
                                                snackbarHostState = snackbarHostState
                                        )
                                is Screen.SettingsAppManagement ->
                                        SettingsAppManagementScreen(
                                                selectedBrowser = selectedBrowser,
                                                onBrowserSelected = { browser ->
                                                        selectedBrowser = browser
                                                        settings.putString("browser", browser)
                                                },
                                                onBack = { handleBack() }
                                        )
                                is Screen.SettingsAbout ->
                                        SettingsAboutScreen(
                                                onOpenUrl = { url ->
                                                        coroutineScope.launch {
                                                                browserLauncher.openUrlInBrowser(
                                                                        url,
                                                                        "System Default"
                                                                )
                                                        }
                                                },
                                                onBack = { handleBack() }
                                        )
                                is Screen.ExportConfig ->
                                        ExportScreen(
                                                databaseHelper = databaseHelper,
                                                onBack = {
                                                        handleBack()
                                                }, // back → Settings (parent)
                                                snackbarHostState = snackbarHostState
                                        )
                                is Screen.ImportConfig ->
                                        ImportScreen(
                                                databaseHelper = databaseHelper,
                                                onBack = { handleBack() },
                                                onImportComplete = {
                                                        pop()
                                                }, // back → Settings (parent)
                                                snackbarHostState = snackbarHostState
                                        )
                                is Screen.ManageCategories ->
                                        ManageCategoriesScreen(
                                                databaseHelper = databaseHelper,
                                                onCategoryClick = { category ->
                                                        push(Screen.CategoryApps(category))
                                                },
                                                onTabSelected = onTabSelected,
                                                onBack = { handleBack() }, // back → Dashboard
                                                snackbarHostState = snackbarHostState
                                        )
                                is Screen.CategoryApps ->
                                        CategoryAppsScreen(
                                                category = screen.category,
                                                databaseHelper = databaseHelper,
                                                onAppClick = { app ->
                                                        coroutineScope.launch {
                                                                val success =
                                                                        browserLauncher
                                                                                .openUrlInBrowser(
                                                                                        app.url,
                                                                                        app.browserChoice,
                                                                                        isIncognito =
                                                                                                app.incognitoMode,
                                                                                        isStandalone =
                                                                                                app.isStandalone,
                                                                                        isIsolated =
                                                                                                app.isolatedProfile
                                                                                )
                                                                if (!success)
                                                                        snackbarHostState
                                                                                .showSnackbar(
                                                                                        "Could not open ${app.name}."
                                                                                )
                                                        }
                                                },
                                                onAppDelete = { app -> appToDelete = app },
                                                onAppEdit = { app -> push(Screen.AppDetails(app)) },
                                                onAddWebApp = { push(Screen.AddApp) },
                                                onBack = { handleBack() },
                                                onTabSelected = onTabSelected,
                                                snackbarHostState = snackbarHostState
                                        )
                        }
                }
        }
}
