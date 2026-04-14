package com.foss.appdock.shared.platform

import androidx.compose.runtime.Composable
import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.coroutines.Dispatchers
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.withContext

actual val platformIsDesktop: Boolean = true

actual val platformIsAndroid: Boolean = false

@Composable
actual fun queryInstalledBrowsers(context: Any?): List<String> {
    return androidx.compose.runtime.remember {
        val browsers = mutableListOf<String>("System Default")
        val osName = System.getProperty("os.name")?.lowercase() ?: ""

        if (osName.contains("linux")) {
            val directories = listOf(
                File("/usr/share/applications/"),
                File(System.getProperty("user.home")?.plus("/.local/share/applications/") ?: "")
            )

            directories.forEach { dir ->
                if (dir.exists() && dir.isDirectory) {
                    dir.listFiles { _, name -> name.endsWith(".desktop") }?.forEach { file ->
                        try {
                            val lines = file.readLines()
                            val content = lines.joinToString("\n")
                            val isBrowser = (content.contains("Categories=") && content.contains("WebBrowser")) ||
                                           (content.contains("MimeType=") && (content.contains("text/html") || content.contains("x-scheme-handler/http")))
                            
                            val isExcluded = content.contains("NoDisplay=true") || content.contains("AppDock Web App") || content.contains("--app=")
                            
                            if (isBrowser && !isExcluded) {
                                val prettyName = lines.find { it.startsWith("Name=") }?.substringAfter("Name=")?.trim()
                                if (prettyName != null && !browsers.contains(prettyName)) {
                                    browsers.add(prettyName)
                                }
                            }
                        } catch (_: Exception) {}
                    }
                }
            }
        } else if (osName.contains("windows")) {
            // Check StartMenuInternet registry key - the most reliable way on Windows
            try {
                val process = Runtime.getRuntime().exec(arrayOf("reg", "query", "HKLM\\SOFTWARE\\Clients\\StartMenuInternet"))
                val output = process.inputStream.bufferedReader().readText()
                val keys = output.split("\n").filter { it.trim().startsWith("HKEY_LOCAL_MACHINE") }
                
                keys.forEach { key ->
                    try {
                        val nameProcess = Runtime.getRuntime().exec(arrayOf("reg", "query", key.trim(), "/ve"))
                        val nameOutput = nameProcess.inputStream.bufferedReader().readText()
                        val name = nameOutput.split("REG_SZ").last().trim()
                        if (name.isNotEmpty() && !browsers.contains(name)) {
                            browsers.add(name)
                        }
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {}

            // Fallback for user-level installations
            try {
                val process = Runtime.getRuntime().exec(arrayOf("reg", "query", "HKCU\\SOFTWARE\\Clients\\StartMenuInternet"))
                val output = process.inputStream.bufferedReader().readText()
                val keys = output.split("\n").filter { it.trim().startsWith("HKEY_CURRENT_USER") }
                
                keys.forEach { key ->
                    try {
                        val nameProcess = Runtime.getRuntime().exec(arrayOf("reg", "query", key.trim(), "/ve"))
                        val nameOutput = nameProcess.inputStream.bufferedReader().readText()
                        val name = nameOutput.split("REG_SZ").last().trim()
                        if (name.isNotEmpty() && !browsers.contains(name)) {
                            browsers.add(name)
                        }
                    } catch (_: Exception) {}
                }
            } catch (_: Exception) {}

            // Last resort: common paths
            val roots = listOfNotNull(System.getenv("ProgramFiles"), System.getenv("ProgramFiles(x86)"), System.getenv("LOCALAPPDATA"))
            val commonBrowsers = listOf(
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
                    if (fullPath.exists() && !browsers.contains(name)) browsers.add(name)
                }
            }
        } else if (osName.contains("mac")) {
            val appDirs =
                    listOf("/Applications", (System.getProperty("user.home") ?: "") + "/Applications")
            
            // Known browser keywords to help identify them
            val browserKeywords = listOf("Browser", "Chrome", "Firefox", "Safari", "Edge", "Brave", "Opera", "Vivaldi", "Arc", "Orion", "DuckDuckGo", "Waterfox", "Pale Moon")
            
            appDirs.forEach { dirPath ->
                val dir = File(dirPath)
                if (dir.exists() && dir.isDirectory) {
                    dir.listFiles { _, name -> name.endsWith(".app") }?.forEach { appBundle ->
                        val appName = appBundle.name.removeSuffix(".app")
                        
                        // Check if it's a known browser or contains "Browser"
                        val isLikelyBrowser = browserKeywords.any { appName.contains(it, ignoreCase = true) }
                        
                        if (isLikelyBrowser) {
                            // Map to pretty names for common ones
                            val prettyName = when {
                                appName.contains("Google Chrome", ignoreCase = true) -> "Chrome"
                                appName.contains("Brave Browser", ignoreCase = true) -> "Brave"
                                appName.contains("Microsoft Edge", ignoreCase = true) -> "Edge"
                                else -> appName
                            }
                            if (!browsers.contains(prettyName)) {
                                browsers.add(prettyName)
                            }
                        }
                    }
                }
            }
        }

        browsers
    }
}

@Composable
actual fun ImagePicker(
        onImagePicked: (String?) -> Unit,
        content: @Composable (launchPicker: () -> Unit) -> Unit
) {
    content({
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

@Composable
actual fun VerticalScrollbar(
    modifier: androidx.compose.ui.Modifier,
    scrollState: androidx.compose.foundation.ScrollState
) {
    val style = androidx.compose.foundation.LocalScrollbarStyle.current.copy(
        thickness = 8.dp,
        unhoverColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.4f),
        hoverColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.8f)
    )
    androidx.compose.foundation.VerticalScrollbar(
        modifier = modifier,
        adapter = androidx.compose.foundation.rememberScrollbarAdapter(scrollState),
        style = style
    )
}

@Composable
actual fun VerticalScrollbar(
    modifier: androidx.compose.ui.Modifier,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    val style = androidx.compose.foundation.LocalScrollbarStyle.current.copy(
        thickness = 8.dp,
        unhoverColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.4f),
        hoverColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.8f)
    )
    androidx.compose.foundation.VerticalScrollbar(
        modifier = modifier,
        adapter = androidx.compose.foundation.rememberScrollbarAdapter(listState),
        style = style
    )
}

@Composable
actual fun VerticalScrollbar(
    modifier: androidx.compose.ui.Modifier,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState
) {
    val style = androidx.compose.foundation.LocalScrollbarStyle.current.copy(
        thickness = 8.dp,
        unhoverColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.4f),
        hoverColor = androidx.compose.ui.graphics.Color.Gray.copy(alpha = 0.8f)
    )
    androidx.compose.foundation.VerticalScrollbar(
        modifier = modifier,
        adapter = androidx.compose.foundation.rememberScrollbarAdapter(gridState),
        style = style
    )
}

actual suspend fun exportData(webApps: List<com.foss.appdock.shared.domain.WebApp>): Boolean = withContext(Dispatchers.IO) {
    try {
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = "Export Web Apps"
        fileChooser.fileFilter = FileNameExtensionFilter("JSON Files", "json")
        fileChooser.selectedFile = File("appdock_backup.json")
        
        val result = fileChooser.showSaveDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            var file = fileChooser.selectedFile
            if (!file.name.endsWith(".json")) {
                file = File(file.absolutePath + ".json")
            }
            val json = Json { prettyPrint = true }
            val content = json.encodeToString(webApps)
            file.writeText(content)
            true
        } else {
            false
        }
    } catch (e: Exception) {
        false
    }
}

actual suspend fun importData(): List<com.foss.appdock.shared.domain.WebApp>? = withContext(Dispatchers.IO) {
    try {
        val fileChooser = JFileChooser()
        fileChooser.dialogTitle = "Import Web Apps"
        fileChooser.fileFilter = FileNameExtensionFilter("JSON Files", "json")
        
        val result = fileChooser.showOpenDialog(null)
        if (result == JFileChooser.APPROVE_OPTION) {
            val file = fileChooser.selectedFile
            val content = file.readText()
            Json.decodeFromString<List<com.foss.appdock.shared.domain.WebApp>>(content)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}

actual fun createShortcut(webApp: com.foss.appdock.shared.domain.WebApp, context: Any?) {
    val osName = System.getProperty("os.name")?.lowercase() ?: ""
    if (osName.contains("windows")) {
        try {
            val desktopPath = System.getProperty("user.home") + "\\Desktop"
            val script = """
                ${'$'}s = (New-Object -ComObject WScript.Shell).CreateShortcut("$desktopPath\\${webApp.name}.lnk");
                
                ${'$'}pf = [Environment]::GetEnvironmentVariable("ProgramFiles")
                ${'$'}pf86 = [Environment]::GetEnvironmentVariable("ProgramFiles(x86)")
                ${'$'}localAp = [Environment]::GetEnvironmentVariable("LOCALAPPDATA")
                
                ${'$'}browserPath = ""
                ${'$'}paths = @(
                    "${'$'}pf86\Microsoft\Edge\Application\msedge.exe",
                    "${'$'}pf\Google\Chrome\Application\chrome.exe",
                    "${'$'}pf86\Google\Chrome\Application\chrome.exe",
                    "${'$'}localAp\Google\Chrome\Application\chrome.exe"
                )
                
                foreach (${'$'}p in ${'$'}paths) {
                    if (Test-Path ${'$'}p) {
                        ${'$'}browserPath = ${'$'}p
                        break
                    }
                }
                
                if (${'$'}browserPath) {
                    ${'$'}s.TargetPath = ${'$'}browserPath;
                    ${'$'}s.Arguments = "--app=${webApp.url}";
                } else {
                    ${'$'}s.TargetPath = "cmd.exe";
                    ${'$'}s.Arguments = "/c start `" `" `"${webApp.url}`"";
                }
                ${'$'}s.Save();
            """.trimIndent()
            
            val tempFile = File.createTempFile("create_shortcut", ".ps1")
            tempFile.writeText(script)
            
            ProcessBuilder("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", tempFile.absolutePath).start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    } else if (osName.contains("linux")) {
        try {
            val desktopPath = System.getProperty("user.home") + "/Desktop"
            val content = """
                [Desktop Entry]
                Name=${webApp.name}
                Exec=xdg-open ${webApp.url}
                Type=Application
                Icon=web-browser
            """.trimIndent()
            val file = File(desktopPath, "${webApp.name}.desktop")
            file.writeText(content)
            file.setExecutable(true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun getDefaultWindowsBrowserPath(): String? {
    try {
        val process1 = Runtime.getRuntime().exec(arrayOf("reg", "query", "HKCU\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\http\\UserChoice", "/v", "ProgId"))
        val output1 = process1.inputStream.bufferedReader().readText()
        val progIdMatch = "ProgId\\s+REG_SZ\\s+(\\S+)".toRegex().find(output1)
        val progId = progIdMatch?.groupValues?.get(1) ?: return null

        val process2 = Runtime.getRuntime().exec(arrayOf("reg", "query", "HKCR\\${progId}\\shell\\open\\command", "/ve"))
        val output2 = process2.inputStream.bufferedReader().readText()
        val cmdMatch = "REG_SZ\\s+(.*)".toRegex().find(output2)
        val command = cmdMatch?.groupValues?.get(1) ?: return null
        
        if (command.startsWith("\"")) {
            val endQuote = command.indexOf("\"", 1)
            if (endQuote != -1) return command.substring(1, endQuote)
        } else {
            val space = command.indexOf(" ")
            if (space != -1) return command.substring(0, space)
        }
        return command
    } catch (e: Exception) {
        return null
    }
}
