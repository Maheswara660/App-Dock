package com.foss.appdock.shared

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
        mutableStateOf(settings.getStringOrNull("theme") ?: "System Adaptive")
    }
    var selectedBrowser by remember {
        mutableStateOf(settings.getStringOrNull("browser") ?: "System Default")
    }
    var accentColor by remember {
        mutableStateOf(settings.getStringOrNull("accentColor") ?: "#2B6CEE")
    }
    var appToDelete by remember { mutableStateOf<WebApp?>(null) }

    val isDark = when (selectedTheme) {
        "Dark", "AMOLED Black" -> true
        "Light" -> false
        else -> isSystemInDarkTheme()
    }

    // ── Navigation stack ─────────────────────────────────────────────────────
    val screenStack = remember { mutableStateListOf<Screen>(Screen.Dashboard) }
    val currentScreen = screenStack.last()

    fun push(screen: Screen) = screenStack.add(screen)

    fun popToDashboard() {
        while (screenStack.size > 1) screenStack.removeAt(screenStack.lastIndex)
    }

    fun pop() {
        if (screenStack.size > 1) {
            val current = screenStack.last()
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

    var backPressTime by remember { mutableLongStateOf(0L) }

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

    var showChangelog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val lastSeen = settings.getStringOrNull("last_seen_version")
        if (lastSeen != com.foss.appdock.shared.utils.Constants.VERSION) {
            showChangelog = true
            settings.putString("last_seen_version", com.foss.appdock.shared.utils.Constants.VERSION)
        }
    }

    LaunchedEffect(handleBack) { onBackReady(handleBack) }

    val showFlyout: (String, androidx.compose.ui.graphics.vector.ImageVector) -> Unit = { _, _ -> }

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
                    BottomNavTab.SEARCH -> if (currentScreen !is Screen.Search) { popToDashboard(); push(Screen.Search) }
                    BottomNavTab.CATEGORY -> if (currentScreen !is Screen.ManageCategories) { popToDashboard(); push(Screen.ManageCategories) }
                    BottomNavTab.SETTINGS -> if (currentScreen !is Screen.Settings) { popToDashboard(); push(Screen.Settings) }
                }
            }

            val activeTab = when (currentScreen) {
                is Screen.Dashboard -> BottomNavTab.APPS
                is Screen.Search -> BottomNavTab.SEARCH
                is Screen.ManageCategories, is Screen.CategoryApps -> BottomNavTab.CATEGORY
                is Screen.Settings, is Screen.SettingsAppearance, is Screen.SettingsPrivacy,
                is Screen.SettingsBackup, is Screen.SettingsAppManagement, is Screen.SettingsAbout,
                is Screen.SettingsChangelog, is Screen.ExportConfig, is Screen.ImportConfig -> BottomNavTab.SETTINGS
                else -> BottomNavTab.APPS
            }

            AppScaffold(
                snackbarHostState = snackbarHostState,
                sideBar = if (!com.foss.appdock.shared.platform.platformIsAndroid) {
                    { AppSidebar(selectedTab = activeTab, onTabSelected = onTabSelected, onFabClick = { push(Screen.AddApp) }) }
                } else null
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (val s = currentScreen) {
                        is Screen.Dashboard -> DashboardScreen(
                            webApps = webApps,
                            currentTab = BottomNavTab.APPS,
                            onAddAppClick = { push(Screen.AddApp) },
                            showChangelog = showChangelog,
                            onChangelogDismiss = { showChangelog = false },
                            onAppClick = { app ->
                                coroutineScope.launch {
                                    try {
                                        databaseHelper.updateWebApp(app.copy(launchCount = app.launchCount + 1))
                                        val success = browserLauncher.openUrlInBrowser(
                                            app.url,
                                            app.browserChoice,
                                            isIncognito = app.incognitoMode,
                                            isStandalone = app.isStandalone,
                                            isIsolated = app.isolatedProfile
                                        )
                                        if (!success) {
                                            if (app.browserChoice != null && app.browserChoice != "System Default") {
                                                val fallback = browserLauncher.openUrlInBrowser(app.url, "System Default")
                                                if (fallback) {
                                                    databaseHelper.updateWebApp(app.copy(browserChoice = "System Default"))
                                                }
                                            } else {
                                                push(Screen.AppDetails(app))
                                            }
                                        }
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Failed to launch ${app.name}")
                                    }
                                }
                            },
                            onAppDetails = { push(Screen.AppDetails(it)) },
                            onDeleteApp = { appToDelete = it },
                            onTabSelected = onTabSelected,
                            snackbarHostState = snackbarHostState,
                            onShowFlyout = showFlyout
                        )

                        is Screen.Search -> SearchScreen(
                            webApps = webApps,
                            currentTab = BottomNavTab.SEARCH,
                            onAppClick = { app ->
                                coroutineScope.launch {
                                    try {
                                        browserLauncher.openUrlInBrowser(
                                            app.url, app.browserChoice,
                                            isIncognito = app.incognitoMode,
                                            isStandalone = app.isStandalone,
                                            isIsolated = app.isolatedProfile
                                        )
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Failed to launch ${app.name}")
                                    }
                                }
                            },
                            onAppDetails = { push(Screen.AppDetails(it)) },
                            onDeleteApp = { appToDelete = it },
                            onTabSelected = onTabSelected,
                            onFabClick = { push(Screen.AddApp) },
                            snackbarHostState = snackbarHostState
                        )

                        is Screen.AddApp -> AddWebAppScreen(
                            availableCategories = categories.map { it.name },
                            onBack = { handleBack() },
                            onSave = { name, url, cat, browser, standalone, notifs, isolated, incognito, icon ->
                                coroutineScope.launch(Dispatchers.Default) {
                                    val t = getSystemTimeMillis()
                                    val domain = try { io.ktor.http.Url(url).host } catch (e: Exception) { "" }
                                    val autoIcon = if (domain.isNotBlank()) "https://www.google.com/s2/favicons?domain=$domain&sz=128" else ""
                                    val newApp = WebApp(
                                        name = name, url = url, iconPath = icon ?: autoIcon.ifBlank { null },
                                        category = cat, browserChoice = browser, isStandalone = standalone,
                                        notificationsEnabled = notifs, isolatedProfile = isolated, incognitoMode = incognito,
                                        createdAt = t
                                    )
                                    databaseHelper.insertWebApp(newApp)
                                    shortcutManager.createShortcut(newApp)
                                }
                                pop()
                            }
                        )

                        is Screen.AppDetails -> WebAppDetailsScreen(
                            app = s.app,
                            availableCategories = categories.map { it.name },
                            onBack = { handleBack() },
                            onSave = { updated ->
                                coroutineScope.launch {
                                    databaseHelper.updateWebApp(updated)
                                }
                                pop()
                            },
                            onDelete = {
                                coroutineScope.launch {
                                    databaseHelper.deleteWebApp(s.app.id)
                                }
                                popToDashboard()
                            },
                            snackbarHostState = snackbarHostState
                        )

                        is Screen.Settings -> SettingsScreen(
                            onAppearanceClick = { push(Screen.SettingsAppearance) },
                            onPrivacyClick = { push(Screen.SettingsPrivacy) },
                            onBackupClick = { push(Screen.SettingsBackup) },
                            onAppManagementClick = { push(Screen.SettingsAppManagement) },
                            onAboutClick = { push(Screen.SettingsAbout) },
                            onWhatNewClick = { push(Screen.SettingsChangelog) },
                            onTabSelected = onTabSelected,
                            onFabClick = { push(Screen.AddApp) },
                            onBack = { handleBack() },
                            snackbarHostState = snackbarHostState
                        )

                        is Screen.SettingsAppearance -> SettingsAppearanceScreen(
                            selectedTheme = selectedTheme,
                            onThemeSelected = { theme -> selectedTheme = theme; settings.putString("theme", theme) },
                            accentColor = accentColor,
                            onAccentColorSelected = { color -> accentColor = color; settings.putString("accentColor", color) },
                            onBack = { handleBack() }
                        )

                        is Screen.SettingsPrivacy -> SettingsPrivacyScreen(
                            onBack = { handleBack() },
                            onShowFlyout = showFlyout
                        )

                        is Screen.SettingsBackup -> SettingsBackupScreen(
                            databaseHelper = databaseHelper,
                            onExportClick = { push(Screen.ExportConfig) },
                            onImportClick = { push(Screen.ImportConfig) },
                            onBack = { handleBack() },
                            onShowFlyout = showFlyout
                        )

                        is Screen.SettingsAppManagement -> SettingsAppManagementScreen(
                            selectedBrowser = selectedBrowser,
                            onBrowserSelected = { browser -> selectedBrowser = browser; settings.putString("browser", browser) },
                            onBack = { handleBack() }
                        )

                        is Screen.SettingsAbout -> SettingsAboutScreen(
                            onOpenUrl = { url -> coroutineScope.launch { browserLauncher.openUrlInBrowser(url, "System Default") } },
                            onBack = { handleBack() }
                        )

                        is Screen.SettingsChangelog -> ChangelogScreen(onBack = { handleBack() })

                        is Screen.ExportConfig -> ExportScreen(
                            databaseHelper = databaseHelper,
                            onBack = { handleBack() },
                            snackbarHostState = snackbarHostState,
                            onShowFlyout = showFlyout
                        )

                        is Screen.ImportConfig -> ImportScreen(
                            databaseHelper = databaseHelper,
                            onBack = { handleBack() },
                            onImportComplete = { pop() },
                            onShowFlyout = showFlyout
                        )

                        is Screen.ManageCategories -> ManageCategoriesScreen(
                            databaseHelper = databaseHelper,
                            onCategoryClick = { push(Screen.CategoryApps(it)) },
                            onTabSelected = onTabSelected,
                            onBack = { handleBack() },
                            snackbarHostState = snackbarHostState,
                            onShowFlyout = showFlyout
                        )

                        is Screen.CategoryApps -> CategoryAppsScreen(
                            category = s.category,
                            databaseHelper = databaseHelper,
                            onAppClick = { app ->
                                coroutineScope.launch {
                                    browserLauncher.openUrlInBrowser(
                                        app.url, app.browserChoice,
                                        isIncognito = app.incognitoMode, isStandalone = app.isStandalone, isIsolated = app.isolatedProfile
                                    )
                                }
                            },
                            onAppDelete = { appToDelete = it },
                            onAppEdit = { push(Screen.AppDetails(it)) },
                            onAddWebApp = { push(Screen.AddApp) },
                            onBack = { handleBack() },
                            onTabSelected = onTabSelected,
                            snackbarHostState = snackbarHostState,
                            onShowFlyout = showFlyout
                        )
                    }

                    if (com.foss.appdock.shared.platform.platformIsAndroid && (
                            currentScreen is Screen.Dashboard ||
                            currentScreen is Screen.Search ||
                            currentScreen is Screen.ManageCategories ||
                            currentScreen is Screen.Settings
                        )) {
                        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                            BottomNavBar(selectedTab = activeTab, onTabSelected = onTabSelected)
                        }
                    }
                }
            }

            appToDelete?.let { app ->
                M3DangerDialog(
                    title = "Delete ${app.name}?",
                    text = "Are you sure you want to delete this app?",
                    confirmText = "Delete",
                    onConfirm = {
                        coroutineScope.launch {
                            shortcutManager.deleteShortcut(app)
                            databaseHelper.deleteWebApp(app.id)
                        }
                        appToDelete = null
                    },
                    onDismiss = { appToDelete = null }
                )
            }

            if (showChangelog) {
                ChangelogDialog(onDismiss = { showChangelog = false })
            }
        }
    }
}
