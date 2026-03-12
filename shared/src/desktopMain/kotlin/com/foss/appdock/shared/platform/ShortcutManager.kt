package com.foss.appdock.shared.platform

import com.foss.appdock.shared.domain.WebApp
import java.io.File

class DesktopShortcutManager : ShortcutManager {
    override fun createShortcut(app: WebApp) {
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        if (osName.contains("windows")) {
            createWindowsShortcut(app)
            return
        }

        val userHome = System.getProperty("user.home") ?: return
        val applicationsDir = File(userHome, ".local/share/applications")
        applicationsDir.mkdirs()

        val appId = "appdock_${app.name.replace(" ", "_").lowercase()}"
        val desktopFile = File(applicationsDir, "$appId.desktop")

        var localIconPath = "text-html"
        try {
            if (!app.iconPath.isNullOrBlank()) {
                val iconsDir = File(userHome, ".local/share/icons/hicolor/128x128/apps")
                iconsDir.mkdirs()
                val iconFile = File(iconsDir, "$appId.png")
                val bytes = java.net.URL(app.iconPath).readBytes()
                iconFile.writeBytes(bytes)
                localIconPath = iconFile.absolutePath
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val profileDir = File(userHome, ".local/share/appdock/profiles/$appId")
        if (app.isolatedProfile) {
            profileDir.mkdirs()
        }

        val execCommand = buildString {
            var browserLower = app.browserChoice?.lowercase() ?: ""

            if (browserLower == "system default" || browserLower.isEmpty()) {
                try {
                    val process =
                            Runtime.getRuntime()
                                    .exec(arrayOf("xdg-settings", "get", "default-web-browser"))
                    val output =
                            process.inputStream.bufferedReader().use {
                                it.readText().trim().lowercase()
                            }
                    browserLower =
                            if (output.contains("chrome")) {
                                "google-chrome"
                            } else if (output.contains("brave")) {
                                "brave"
                            } else if (output.contains("edge") || output.contains("msedge")) {
                                "edge"
                            } else if (output.contains("firefox")) {
                                "firefox"
                            } else ""
                } catch (e: Exception) {}
            }

            when (browserLower) {
                "chrome", "google-chrome", "brave", "edge", "microsoft-edge" -> {
                    val cmd =
                            when (browserLower) {
                                "brave" -> "brave-browser"
                                "edge", "microsoft-edge" -> "microsoft-edge"
                                else -> "google-chrome"
                            }
                    append(cmd)
                    if (app.isStandalone) append(" --app=\"${app.url}\"")
                    else append(" \"${app.url}\"")

                    if (app.incognitoMode) append(" --incognito")

                    if (app.isolatedProfile) {
                        append(" --user-data-dir=\"${profileDir.absolutePath}\"")
                    }

                    // Set WM Class for better dock integration
                    append(" --class=$appId --name=$appId")
                }
                "firefox" -> {
                    append("firefox")
                    if (app.incognitoMode) append(" -private-window")
                    if (app.isolatedProfile) {
                        append(" -profile \"${profileDir.absolutePath}\"")
                    }
                    append(" \"${app.url}\"")
                }
                else -> {
                    append("xdg-open \"${app.url}\"")
                }
            }
        }

        val content =
                """
            [Desktop Entry]
            Version=1.0
            Name=${app.name}
            Comment=AppDock Web App
            Exec=$execCommand
            Terminal=false
            Type=Application
            Categories=GTK;WebApps;Network;WebBrowser;
            Icon=$localIconPath
            StartupWMClass=$appId
        """.trimIndent()

        desktopFile.writeText(content)
        desktopFile.setExecutable(true)
    }

    private fun createWindowsShortcut(app: WebApp) {
        // Improved Windows support: Attempt to use the selected/default browser with flags
        try {
            val appId = "appdock_${app.name.replace(" ", "_").lowercase()}"
            val userHome = System.getProperty("user.home")
            val appData =
                    System.getenv("LOCALAPPDATA")
                            ?: (if (userHome != null) userHome + "\\AppData\\Local" else null)
                                    ?: return
            val profileDir = File(appData, "AppDock\\Profiles\\$appId")

            if (app.isolatedProfile) {
                profileDir.mkdirs()
            }

            var browserLower = app.browserChoice?.lowercase() ?: ""
            // On Windows, we often don't have xdg-settings.
            // We can try to use 'start' or detect common browser executables.

            val execCmd = buildString {
                when {
                    browserLower.contains("chrome") -> {
                        append("chrome.exe")
                        if (app.isStandalone) append(" --app=\"${app.url}\"")
                        else append(" \"${app.url}\"")
                        if (app.incognitoMode) append(" --incognito")
                        if (app.isolatedProfile)
                                append(" --user-data-dir=\"${profileDir.absolutePath}\"")
                    }
                    browserLower.contains("edge") -> {
                        append("msedge.exe")
                        if (app.isStandalone) append(" --app=\"${app.url}\"")
                        else append(" \"${app.url}\"")
                        if (app.incognitoMode) append(" --inprivate")
                        if (app.isolatedProfile)
                                append(" --user-data-dir=\"${profileDir.absolutePath}\"")
                    }
                    browserLower.contains("brave") -> {
                        append("brave.exe")
                        if (app.isStandalone) append(" --app=\"${app.url}\"")
                        else append(" \"${app.url}\"")
                        if (app.incognitoMode) append(" --incognito")
                        if (app.isolatedProfile)
                                append(" --user-data-dir=\"${profileDir.absolutePath}\"")
                    }
                    browserLower.contains("firefox") -> {
                        append("firefox.exe")
                        if (app.incognitoMode) append(" -private-window")
                        if (app.isolatedProfile) append(" -profile \"${profileDir.absolutePath}\"")
                        append(" \"${app.url}\"")
                    }
                    else -> {
                        // Fallback to default browser via 'start'
                        append("cmd /c start \"\" \"${app.url}\"")
                    }
                }
            }

            // To truly create a shortcut on Windows Desktop, we'd need a .lnk file.
            // But from Kotlin/JVM, we can at least launch it now, or create a .bat/cmd file.
            // For now, let's just launch it as a verification of intent.
            // A more advanced version would use a VBScript to create a .lnk file.
            Runtime.getRuntime().exec(arrayOf("cmd", "/c", execCmd))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun deleteShortcut(app: WebApp) {
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        if (osName.contains("windows")) return

        val userHome = System.getProperty("user.home") ?: return
        val applicationsDir = File(userHome, ".local/share/applications")
        val appId = "appdock_${app.name.replace(" ", "_").lowercase()}"
        val desktopFile = File(applicationsDir, "$appId.desktop")
        if (desktopFile.exists()) {
            desktopFile.delete()
        }

        val iconsDir = File(userHome, ".local/share/icons/hicolor/128x128/apps")
        val iconFile = File(iconsDir, "$appId.png")
        if (iconFile.exists()) {
            iconFile.delete()
        }

        // Cleanup profiles
        val profileDir = File(userHome, ".local/share/appdock/profiles/$appId")
        if (profileDir.exists()) {
            profileDir.deleteRecursively()
        }
    }
}
