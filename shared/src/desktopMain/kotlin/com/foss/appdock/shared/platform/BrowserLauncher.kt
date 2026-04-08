package com.foss.appdock.shared.platform

actual class BrowserLauncher actual constructor(@Suppress("UNUSED_PARAMETER") context: Any?) {
    actual fun openUrlInBrowser(
            url: String,
            browserName: String?,
            isIncognito: Boolean,
            isStandalone: Boolean,
            isIsolated: Boolean
    ): Boolean {
        if (isStandalone) {
            DesktopWindowManager.launchStandalone(url, isIncognito, isIsolated)
            return true
        }

        val runtime = Runtime.getRuntime()
        val commands = mutableListOf<Array<String>>()

        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        var browserLower = browserName?.lowercase() ?: ""

        if (browserLower == "system default" || browserLower.isEmpty()) {
            if (osName.contains("linux")) {
                try {
                    val process = runtime.exec(arrayOf("xdg-settings", "get", "default-web-browser"))
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
                        } else {
                            "" 
                        }
                } catch (e: Exception) {}
            }
        }

        if (osName.contains("windows")) {
            val roots = listOfNotNull(System.getenv("ProgramFiles"), System.getenv("ProgramFiles(x86)"), System.getenv("LOCALAPPDATA"))
            
            fun findBrowser(relPaths: List<String>): String? {
                for (root in roots) {
                    for (rel in relPaths) {
                        val f = java.io.File(root, rel)
                        if (f.exists()) return f.absolutePath
                    }
                }
                return null
            }

            var actualBrowserLower = browserLower

            var browserPath = if (actualBrowserLower == "system default" || actualBrowserLower.isEmpty()) {
                val defaultPath = getDefaultWindowsBrowserPath()
                if (defaultPath != null) {
                    val pLower = defaultPath.lowercase()
                    actualBrowserLower = when {
                        pLower.contains("edge") || pLower.contains("msedge") -> "edge"
                        pLower.contains("chrome") -> "chrome"
                        pLower.contains("brave") -> "brave"
                        pLower.contains("firefox") || pLower.contains("waterfox") || pLower.contains("librewolf") -> "firefox"
                        pLower.contains("opera") -> "opera"
                        pLower.contains("vivaldi") -> "vivaldi"
                        else -> "unknown"
                    }
                    defaultPath
                } else {
                    actualBrowserLower = "edge"
                    findBrowser(listOf("Microsoft/Edge/Application/msedge.exe")) ?: findBrowser(listOf("Google/Chrome/Application/chrome.exe"))
                }
            } else {
                when {
                    actualBrowserLower.contains("chrome") -> findBrowser(listOf("Google/Chrome/Application/chrome.exe"))
                    actualBrowserLower.contains("edge") -> findBrowser(listOf("Microsoft/Edge/Application/msedge.exe"))
                    actualBrowserLower.contains("brave") -> findBrowser(listOf("BraveSoftware/Brave-Browser/Application/brave.exe"))
                    actualBrowserLower.contains("firefox") -> findBrowser(listOf("Mozilla Firefox/firefox.exe"))
                    actualBrowserLower.contains("opera") -> findBrowser(listOf("Opera/launcher.exe", "Opera GX/launcher.exe"))
                    actualBrowserLower.contains("vivaldi") -> findBrowser(listOf("Vivaldi/Application/vivaldi.exe"))
                    else -> null
                }
            }

            if (browserPath != null) {
                val args = mutableListOf(browserPath)
                if (actualBrowserLower.contains("firefox")) {
                    if (isIncognito) args.add("-private-window")
                    if (isIsolated) {
                        val appData = System.getenv("LOCALAPPDATA") ?: "${System.getProperty("user.home")}\\AppData\\Local"
                        val profileDir = java.io.File(appData, "AppDock\\Profiles\\${url.hashCode()}")
                        profileDir.mkdirs()
                        args.add("-profile")
                        args.add(profileDir.absolutePath)
                    }
                    args.add(url)
                } else if (actualBrowserLower == "unknown") {
                    args.add(url) // Unknown browser: just launch url
                } else {
                    if (isStandalone) args.add("--app=$url")
                    else args.add(url)

                    if (isIncognito) {
                        if (actualBrowserLower.contains("edge")) args.add("--inprivate")
                        else args.add("--incognito")
                    }
                    if (isIsolated) {
                        val appData = System.getenv("LOCALAPPDATA") ?: "${System.getProperty("user.home")}\\AppData\\Local"
                        val profileDir = java.io.File(appData, "AppDock\\Profiles\\${url.hashCode()}")
                        profileDir.mkdirs()
                        args.add("--user-data-dir=${profileDir.absolutePath}")
                    }
                }
                commands.add(args.toTypedArray())
            }
            
            // Standard Windows fallback
            commands.add(arrayOf("cmd.exe", "/c", "start", "", url))
        } else if (osName.contains("mac")) {
            // macOS implementation
            when (browserLower) {
                "chrome", "google-chrome" -> {
                    commands.add(arrayOf("open", "-a", "Google Chrome", url))
                    if (isIncognito) commands.add(arrayOf("open", "-a", "Google Chrome", "--args", "--incognito", url))
                }
                "brave", "brave-browser" -> {
                    commands.add(arrayOf("open", "-a", "Brave Browser", url))
                    if (isIncognito) commands.add(arrayOf("open", "-a", "Brave Browser", "--args", "--incognito", url))
                }
                "edge", "microsoft-edge" -> {
                    commands.add(arrayOf("open", "-a", "Microsoft Edge", url))
                    if (isIncognito) commands.add(arrayOf("open", "-a", "Microsoft Edge", "--args", "--inprivate", url))
                }
                "firefox" -> {
                    commands.add(arrayOf("open", "-a", "Firefox", url))
                    if (isIncognito) commands.add(arrayOf("open", "-a", "Firefox", "--args", "-private-window", url))
                }
                "safari" -> {
                    commands.add(arrayOf("open", "-a", "Safari", url))
                }
                else -> {
                    commands.add(arrayOf("open", url))
                }
            }
        } else {
            // Linux implementation
            when (browserLower) {
                "chrome", "google-chrome", "google-chrome-stable", "chrome-browser" -> {
                    val args = mutableListOf("google-chrome")
                    if (isIncognito) args.add("--incognito")
                    if (isStandalone) {
                        args.add("--app=$url")
                        args.add("--window-size=1280,720")
                    } else args.add(url)
                    commands.add(args.toTypedArray())
                }
                "brave", "brave-browser" -> {
                    val args = mutableListOf("brave-browser")
                    if (isIncognito) args.add("--incognito")
                    if (isStandalone) {
                        args.add("--app=$url")
                        args.add("--window-size=1280,720")
                    } else args.add(url)
                    commands.add(args.toTypedArray())
                }
                "edge", "microsoft-edge", "msedge" -> {
                    val args = mutableListOf("microsoft-edge")
                    if (isIncognito) args.add("--inprivate")
                    if (isStandalone) {
                        args.add("--app=$url")
                        args.add("--window-size=1280,720")
                    } else args.add(url)
                    commands.add(args.toTypedArray())
                }
                "firefox", "firefox-esr" -> {
                    val args = mutableListOf("firefox")
                    if (isIncognito) args.add("-private-window")
                    if (isStandalone) {
                        args.add("-new-window")
                        args.add(url)
                    } else {
                        args.add(url)
                    }
                    commands.add(args.toTypedArray())
                }
            }
            
            // Fallback for system default or if specific browser failed
            commands.add(arrayOf("xdg-open", url))
        }

        for (command in commands) {
            try {
                val process = runtime.exec(command)
                Thread.sleep(200)
                if (process.isAlive || process.exitValue() == 0) return true
            } catch (e: Exception) {
                continue
            }
        }

        return false
    }
}
