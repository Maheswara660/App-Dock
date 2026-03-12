package com.foss.appdock.shared.platform

actual class BrowserLauncher actual constructor(@Suppress("UNUSED_PARAMETER") context: Any?) {
    actual fun openUrlInBrowser(
            url: String,
            browserName: String?,
            isIncognito: Boolean,
            isStandalone: Boolean,
            isIsolated: Boolean
    ): Boolean {
        val runtime = Runtime.getRuntime()
        val commands = mutableListOf<Array<String>>()

        var browserLower = browserName?.lowercase() ?: ""

        if (browserLower == "system default" || browserLower.isEmpty()) {
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
                            "" // Fallback to xdg-open explicitly if unhandled
                        }
            } catch (e: Exception) {
                // Ignore and let it fallback to xdg-open
            }
        }

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
                // Firefox doesn't support a true "app" mode like Chrome anymore (SSB was removed)
                // -new-window is the best we can do for standalone-like experience
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
