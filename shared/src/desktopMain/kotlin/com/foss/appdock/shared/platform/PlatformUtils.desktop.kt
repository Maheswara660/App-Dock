package com.foss.appdock.shared.platform

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter

actual val platformIsDesktop: Boolean = true

actual val platformIsAndroid: Boolean = false

actual fun queryInstalledBrowsers(): List<String> {
    val browsers = mutableListOf<String>("System Default")
    val osName = System.getProperty("os.name")?.lowercase() ?: ""

    if (osName.contains("linux")) {
        val directories =
                listOf(
                        File("/usr/share/applications/"),
                        @Suppress("UNNECESSARY_SAFE_CALL")
                        File(
                                System.getProperty("user.home")?.plus("/.local/share/applications/")
                                        ?: ""
                        )
                )

        directories.forEach { dir ->
            if (dir.exists() && dir.isDirectory) {
                dir.listFiles { _, name -> name.endsWith(".desktop") }?.forEach { file ->
                    try {
                        val lines = file.readLines()
                        val content = lines.joinToString("\n")

                        // Stricter browser check for Linux
                        val categories =
                                lines
                                        .find { it.startsWith("Categories=") }
                                        ?.substringAfter("Categories=")
                                        ?: ""
                        val mimeTypes =
                                lines
                                        .find { it.startsWith("MimeType=") }
                                        ?.substringAfter("MimeType=")
                                        ?: ""
                        val execLine =
                                lines.find { it.startsWith("Exec=") }?.substringAfter("Exec=") ?: ""
                        val comment =
                                lines.find { it.startsWith("Comment=") }?.substringAfter("Comment=")
                                        ?: ""

                        val isBrowser =
                                categories.contains("WebBrowser") &&
                                        mimeTypes.contains("x-scheme-handler/http")

                        val isExcluded =
                                categories.contains("TextEditor") ||
                                        categories.contains("Development") ||
                                        categories.contains("IDE") ||
                                        categories.contains("WebApps") ||
                                        comment.contains("AppDock Web App") ||
                                        execLine.contains("--app=")

                        val isHidden = content.contains("NoDisplay=true")

                        if (isBrowser && !isExcluded && !isHidden) {
                            val nameLine = lines.find { it.startsWith("Name=") }
                            val prettyName = nameLine?.substringAfter("Name=")?.trim()

                            if (prettyName != null &&
                                            !browsers.contains(prettyName) &&
                                            !prettyName.contains("Private", ignoreCase = true) &&
                                            !prettyName.contains("Incognito", ignoreCase = true)
                            ) {
                                browsers.add(prettyName)
                            }
                        }
                    } catch (e: Exception) {
                        // Skip files we can't read
                    }
                }
            }
        }
    } else if (osName.contains("windows")) {
        val roots =
                listOfNotNull(
                        System.getenv("ProgramFiles"),
                        System.getenv("ProgramFiles(x86)"),
                        System.getenv("LOCALAPPDATA")
                )

        val commonBrowsers =
                listOf(
                        "Google/Chrome/Application/chrome.exe" to "Chrome",
                        "Microsoft/Edge/Application/msedge.exe" to "Edge",
                        "Mozilla Firefox/firefox.exe" to "Firefox",
                        "BraveSoftware/Brave-Browser/Application/brave.exe" to "Brave",
                        "Opera/launcher.exe" to "Opera",
                        "Vivaldi/Application/vivaldi.exe" to "Vivaldi"
                )

        roots.forEach { root ->
            commonBrowsers.forEach { (path, name) ->
                val fullPath = File(root, path)
                if (fullPath.exists()) {
                    if (!browsers.contains(name)) {
                        browsers.add(name)
                    }
                }
            }
        }
    } else if (osName.contains("mac")) {
        val appDirs =
                listOf("/Applications", (System.getProperty("user.home") ?: "") + "/Applications")
        val commonMacBrowsers =
                listOf(
                        "Safari.app" to "Safari",
                        "Google Chrome.app" to "Chrome",
                        "Firefox.app" to "Firefox",
                        "Brave Browser.app" to "Brave",
                        "Microsoft Edge.app" to "Edge"
                )

        appDirs.forEach { dir ->
            commonMacBrowsers.forEach { (appName, prettyName) ->
                if (File(dir, appName).exists()) {
                    if (!browsers.contains(prettyName)) {
                        browsers.add(prettyName)
                    }
                }
            }
        }
    }

    return browsers
}

@Composable
actual fun ImagePicker(
        onImagePicked: (String?) -> Unit,
        content: @Composable (launchPicker: () -> Unit) -> Unit
) {
    content({
        // Use Swing JFileChooser since it's desktop Compose
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = "Select App Icon"
        val filter =
                FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif", "svg", "webp")
        fileChooser.fileFilter = filter

        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = fileChooser.selectedFile
            onImagePicked(selectedFile.absolutePath)
        } else {
            onImagePicked(null)
        }
    })
}
