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

        if (osName.contains("mac")) {
            createMacOSShortcut(app)
            return
        }

        // Linux/Other
        val userHome = System.getProperty("user.home") ?: return
        val applicationsDir = File(userHome, ".local/share/applications")
        if (!applicationsDir.exists()) applicationsDir.mkdirs()

        val appId = "appdock_${app.name.replace(" ", "_").lowercase()}"
        val desktopFile = File(applicationsDir, "$appId.desktop")

        var localIconPath = "text-html"
        try {
            if (!app.iconPath.isNullOrBlank()) {
                val iconsDir = File(userHome, ".local/share/icons/hicolor/128x128/apps")
                if (!iconsDir.exists()) iconsDir.mkdirs()
                val iconFile = File(iconsDir, "$appId.png")
                
                val bytes = if (app.iconPath.startsWith("http")) {
                    java.net.URL(app.iconPath).readBytes()
                } else {
                    val source = File(app.iconPath)
                    if (source.exists()) source.readBytes() else null
                }
                
                if (bytes != null) {
                    iconFile.writeBytes(bytes)
                    localIconPath = iconFile.absolutePath
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val profileDir = File(userHome, ".local/share/appdock/profiles/$appId")
        if (app.isolatedProfile) {
            if (!profileDir.exists()) profileDir.mkdirs()
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
                            when {
                                output.contains("chrome") -> "google-chrome"
                                output.contains("brave") -> "brave"
                                output.contains("edge") || output.contains("msedge") -> "edge"
                                output.contains("firefox") -> "firefox"
                                output.contains("vivaldi") -> "vivaldi"
                                output.contains("opera") -> "opera"
                                else -> ""
                            }
                } catch (e: Exception) {}
            }

            when (browserLower) {
                "chrome", "google-chrome", "google-chrome-stable", "brave", "edge", "microsoft-edge", "vivaldi", "opera" -> {
                    val cmd =
                            when (browserLower) {
                                "brave" -> "brave-browser"
                                "edge", "microsoft-edge" -> "microsoft-edge"
                                "chrome", "google-chrome", "google-chrome-stable" -> "google-chrome"
                                "vivaldi" -> "vivaldi-stable"
                                "opera" -> "opera"
                                else -> "google-chrome"
                            }
                    append(cmd)
                    
                    append(" --no-first-run --no-default-browser-check --ozone-platform-hint=auto")
                    
                    if (app.isStandalone) append(" --app=\"${app.url}\"")
                    else append(" \"${app.url}\"")

                    if (app.incognitoMode) append(" --incognito")

                    if (app.isolatedProfile) {
                        append(" --user-data-dir=\"${profileDir.absolutePath}\"")
                    }

                    // Set WM Class for better dock/shell integration (crucial for Wayland)
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
        try {
            val appId = "appdock_${app.name.replace(" ", "_").lowercase()}"
            val userHome = System.getProperty("user.home") ?: return
            
            val desktopDir = File(userHome, "Desktop")
            val startMenuDir = File(System.getenv("APPDATA"), "Microsoft\\Windows\\Start Menu\\Programs")
            if (!startMenuDir.exists()) startMenuDir.mkdirs()

            val shortcutName = "${app.name}.lnk"
            val desktopShortcut = File(desktopDir, shortcutName)
            val startMenuShortcut = File(startMenuDir, shortcutName)

            val appData = System.getenv("LOCALAPPDATA") ?: (userHome + "\\AppData\\Local")
            val profileDir = File(appData, "AppDock\\Profiles\\$appId")
            if (app.isolatedProfile) profileDir.mkdirs()

            val (targetPath, arguments) = run {
                var browserName = app.browserChoice?.lowercase() ?: "system default"
                var foundPath = ""
                
                if (browserName == "system default" || browserName.isEmpty()) {
                    val defaultPath = getDefaultWindowsBrowserPath()
                    if (defaultPath != null) {
                        foundPath = defaultPath
                        val pLower = defaultPath.lowercase()
                        browserName = when {
                            pLower.contains("edge") || pLower.contains("msedge") -> "edge"
                            pLower.contains("chrome") -> "chrome"
                            pLower.contains("brave") -> "brave"
                            pLower.contains("firefox") || pLower.contains("waterfox") || pLower.contains("librewolf") -> "firefox"
                            pLower.contains("opera") -> "opera"
                            pLower.contains("vivaldi") -> "vivaldi"
                            else -> "unknown"
                        }
                    }
                }

                if (foundPath.isEmpty()) {
                    val roots = listOfNotNull(System.getenv("ProgramFiles"), System.getenv("ProgramFiles(x86)"), System.getenv("LOCALAPPDATA"))
                    val commonPaths = when {
                        browserName.contains("chrome") -> listOf("Google/Chrome/Application/chrome.exe")
                        browserName.contains("edge") -> listOf("Microsoft/Edge/Application/msedge.exe")
                        browserName.contains("brave") -> listOf("BraveSoftware/Brave-Browser/Application/brave.exe")
                        browserName.contains("firefox") -> listOf("Mozilla Firefox/firefox.exe")
                        browserName.contains("opera") -> listOf("Opera/launcher.exe", "Opera GX/launcher.exe")
                        browserName.contains("vivaldi") -> listOf("Vivaldi/Application/vivaldi.exe")
                        else -> emptyList()
                    }

                    for (root in roots) {
                        for (relPath in commonPaths) {
                            val full = File(root, relPath)
                            if (full.exists()) {
                                foundPath = full.absolutePath
                                break
                            }
                        }
                        if (foundPath.isNotEmpty()) break
                    }
                }

                if (foundPath.isNotEmpty()) {
                    val args = buildString {
                        if (browserName.contains("firefox")) {
                            if (app.incognitoMode) append("-private-window ")
                            if (app.isolatedProfile) append("-profile \"${profileDir.absolutePath}\" ")
                            append("\"${app.url}\"")
                        } else if (browserName == "unknown") {
                            append("\"${app.url}\"")
                        } else {
                            append("--no-first-run --no-default-browser-check ")
                            if (app.isStandalone) append("--app=\"${app.url}\" ")
                            else append("\"${app.url}\" ")
                            if (app.incognitoMode) {
                                if (browserName.contains("edge")) append("--inprivate ")
                                else append("--incognito ")
                            }
                            if (app.isolatedProfile) append("--user-data-dir=\"${profileDir.absolutePath}\" ")
                        }
                    }
                    foundPath to args
                } else {
                    "cmd.exe" to "/c start \"\" \"${app.url}\""
                }
            }

            var localIconPath = ""
            try {
                if (!app.iconPath.isNullOrBlank()) {
                    val iconsDir = File(appData, "AppDock\\Icons")
                    iconsDir.mkdirs()
                    val iconFile = File(iconsDir, "$appId.ico")
                    
                    val pngBytes = if (app.iconPath.startsWith("http")) {
                        java.net.URL(app.iconPath).readBytes()
                    } else {
                        val source = File(app.iconPath)
                        if (source.exists()) source.readBytes() else null
                    }
                    
                    if (pngBytes != null) {
                        val icoBytes = ByteArray(22 + pngBytes.size)
                        icoBytes[0] = 0; icoBytes[1] = 0
                        icoBytes[2] = 1; icoBytes[3] = 0
                        icoBytes[4] = 1; icoBytes[5] = 0
                        icoBytes[6] = 0; icoBytes[7] = 0
                        icoBytes[8] = 0; icoBytes[9] = 0
                        icoBytes[10] = 1; icoBytes[11] = 0
                        icoBytes[12] = 32; icoBytes[13] = 0
                        val size = pngBytes.size
                        icoBytes[14] = (size and 0xFF).toByte()
                        icoBytes[15] = ((size shr 8) and 0xFF).toByte()
                        icoBytes[16] = ((size shr 16) and 0xFF).toByte()
                        icoBytes[17] = ((size shr 24) and 0xFF).toByte()
                        icoBytes[18] = 22; icoBytes[19] = 0
                        icoBytes[20] = 0; icoBytes[21] = 0
                        System.arraycopy(pngBytes, 0, icoBytes, 22, pngBytes.size)
                        iconFile.writeBytes(icoBytes)
                        localIconPath = iconFile.absolutePath
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val psScript = """
                ${'$'}WshShell = New-Object -ComObject WScript.Shell
                function Create-Shortcut {
                    param([string]${'$'}path, [string]${'$'}target, [string]${'$'}shortcutArgs, [string]${'$'}icon, [string]${'$'}aumid)
                    ${'$'}Shortcut = ${'$'}WshShell.CreateShortcut(${'$'}path)
                    ${'$'}Shortcut.TargetPath = ${'$'}target
                    ${'$'}Shortcut.Arguments = ${'$'}shortcutArgs
                    if (${'$'}icon -ne "") {
                        ${'$'}Shortcut.IconLocation = ${'$'}icon + ",0"
                    }
                    ${'$'}Shortcut.Save()
                    try {
                        ${'$'}code = @"
                        using System;
                        using System.Runtime.InteropServices;
                        using System.Runtime.InteropServices.ComTypes;
                        [ComImport, Guid("0000010b-0000-0000-C000-000000000046"), InterfaceType(ComInterfaceType.InterfaceIsIUnknown)]
                        public interface IPersistFile {
                            void GetClassID(out Guid pClassID);
                            [PreserveSig] int IsDirty();
                            void Load([MarshalAs(UnmanagedType.LPWStr)] string pszFileName, int dwMode);
                            void Save([MarshalAs(UnmanagedType.LPWStr)] string pszFileName, [MarshalAs(UnmanagedType.Bool)] bool fRemember);
                            void SaveCompleted([MarshalAs(UnmanagedType.LPWStr)] string pszFileName);
                            void GetCurFile([MarshalAs(UnmanagedType.LPWStr)] out string ppszFileName);
                        }
                        [ComImport, Guid("00021401-0000-0000-C000-000000000046")] public class CShellLink {}
                        [ComImport, InterfaceType(ComInterfaceType.InterfaceIsIUnknown), Guid("886D8EEB-8CF2-4446-8D02-CDBA1DBDCF99")]
                        public interface IPropertyStore {
                            void GetCount(out uint cProps);
                            void GetAt(uint iProp, IntPtr pkey);
                            void GetValue(IntPtr key, IntPtr pv);
                            void SetValue(ref PropertyKey key, ref PropVariant pv);
                            void Commit();
                        }
                        [StructLayout(LayoutKind.Sequential)] public struct PropertyKey {
                            public Guid fmtid; public UIntPtr pid;
                        }
                        [StructLayout(LayoutKind.Explicit)] public struct PropVariant {
                            [FieldOffset(0)] public ushort vt; [FieldOffset(8)] public IntPtr ptr;
                        }
                        public class ShortcutHelper {
                            public static void SetAppId(string path, string appId) {
                                CShellLink link = new CShellLink();
                                ((IPersistFile)link).Load(path, 0);
                                PropertyKey key = new PropertyKey { fmtid = new Guid("9F4C2855-9F79-4B39-A8D0-E1D42DE1D5F3"), pid = (UIntPtr)5 };
                                PropVariant pv = new PropVariant { vt = 31, ptr = Marshal.StringToCoTaskMemUni(appId) };
                                ((IPropertyStore)link).SetValue(ref key, ref pv);
                                ((IPropertyStore)link).Commit();
                                ((IPersistFile)link).Save(path, true);
                                Marshal.FreeCoTaskMem(pv.ptr);
                            }
                        }
"@
                        if (-not ([System.Management.Automation.PSTypeName]"ShortcutHelper").Type) {
                            Add-Type -TypeDefinition ${'$'}code
                        }
                        [ShortcutHelper]::SetAppId(${'$'}path, ${'$'}aumid)
                    } catch {}
                }
                Create-Shortcut -path '${desktopShortcut.absolutePath.replace("'", "''")}' -target '${targetPath.replace("'", "''")}' -shortcutArgs '${arguments.replace("'", "''")}' -icon '${localIconPath.replace("'", "''")}' -aumid '$appId'
                Create-Shortcut -path '${startMenuShortcut.absolutePath.replace("'", "''")}' -target '${targetPath.replace("'", "''")}' -shortcutArgs '${arguments.replace("'", "''")}' -icon '${localIconPath.replace("'", "''")}' -aumid '$appId'
            """.trimIndent()

            val tempPs1 = File.createTempFile("create_shortcut", ".ps1")
            tempPs1.writeText(psScript)
            Runtime.getRuntime().exec(arrayOf("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", tempPs1.absolutePath)).waitFor()
            tempPs1.delete()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun createMacOSShortcut(app: WebApp) {
        try {
            val appId = "com.foss.appdock.app_${app.name.replace(" ", "_").lowercase()}"
            val userHome = System.getProperty("user.home") ?: return
            
            val appBundleName = "${app.name}.app"
            val applicationsDir = File(userHome, "Applications")
            if (!applicationsDir.exists()) applicationsDir.mkdirs()
            
            val appBundleDir = File(applicationsDir, appBundleName)
            if (appBundleDir.exists()) appBundleDir.deleteRecursively()
            
            val contentsDir = File(appBundleDir, "Contents")
            val macOsDir = File(contentsDir, "MacOS")
            val resourcesDir = File(contentsDir, "Resources")
            
            macOsDir.mkdirs()
            resourcesDir.mkdirs()
            File(contentsDir, "PkgInfo").writeText("APPL????")

            val profileDir = File(userHome, "Library/Application Support/AppDock/Profiles/$appId")
            if (app.isolatedProfile && !profileDir.exists()) profileDir.mkdirs()

            val browserLower = app.browserChoice?.lowercase() ?: ""
            val browserArgs = mutableListOf<String>()
            when {
                browserLower.contains("chrome") || browserLower.contains("brave") || browserLower.contains("edge") -> {
                    browserArgs.add("--no-first-run")
                    browserArgs.add("--no-default-browser-check")
                    if (app.incognitoMode) browserArgs.add("--incognito")
                    if (app.isolatedProfile) {
                        ensureChromiumProfileInitialized(profileDir)
                        browserArgs.add("--user-data-dir=${profileDir.absolutePath}")
                    }
                    if (app.isStandalone) browserArgs.add("--app=${app.url}")
                    else browserArgs.add(app.url)
                }
                browserLower.contains("firefox") -> {
                    if (app.incognitoMode) browserArgs.add("-private-window")
                    if (app.isolatedProfile) {
                        profileDir.mkdirs()
                        browserArgs.add("-profile")
                        browserArgs.add(profileDir.absolutePath)
                    }
                    browserArgs.add(app.url)
                }
                else -> browserArgs.add(app.url)
            }

            val browserApp = when {
                app.browserChoice?.lowercase()?.contains("chrome") == true -> "Google Chrome"
                app.browserChoice?.lowercase()?.contains("brave") == true -> "Brave Browser"
                app.browserChoice?.lowercase()?.contains("edge") == true -> "Microsoft Edge"
                app.browserChoice?.lowercase()?.contains("firefox") == true -> "Firefox"
                else -> app.browserChoice ?: "Safari"
            }

            fun findMacBinary(appName: String): String {
                val appDirs = listOf("/Applications", (System.getProperty("user.home") ?: "") + "/Applications")
                for (dir in appDirs) {
                    val bundle = File(dir, "$appName.app")
                    if (bundle.exists()) {
                        val innerMacOsDir = File(bundle, "Contents/MacOS")
                        if (innerMacOsDir.exists() && innerMacOsDir.isDirectory) {
                            val binary = File(innerMacOsDir, appName)
                            if (binary.exists()) return binary.absolutePath
                            val files = innerMacOsDir.listFiles()
                            if (!files.isNullOrEmpty()) return files[0].absolutePath
                        }
                    }
                }
                return "/Applications/$appName.app/Contents/MacOS/$appName" // Fallback
            }

            val browserBinaryPath = when {
                browserApp == "Google Chrome" -> "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome"
                browserApp == "Brave Browser" -> "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser"
                browserApp == "Microsoft Edge" -> "/Applications/Microsoft Edge.app/Contents/MacOS/Microsoft Edge"
                browserApp == "Firefox" -> "/Applications/Firefox.app/Contents/MacOS/firefox"
                browserApp == "Safari" -> "/Applications/Safari.app/Contents/MacOS/Safari"
                else -> findMacBinary(browserApp)
            }

            val safeUrl = shellSingleQuote(app.url)
            val scriptArgLines = browserArgs.joinToString("\n") { "  ${shellSingleQuote(it)}" }

            val launcherScript = File(macOsDir, app.name)
            val scriptContent = "#!/bin/bash\n" + """
                if [ "$browserApp" = "Safari" ]; then
                    open -a "Safari" $safeUrl
                    exit 0
                fi
                
                BROWSER_ARGS=(
$scriptArgLines
                )
                
                # Check if the app is already running with this profile
                if ps aux | grep -v grep | grep -q "${profileDir.absolutePath}"; then
                    # Already running: Just bring the browser to the front
                    open -a "$browserApp"
                    exit 0
                fi

                # Not running: Launch new instance
                if [ -f "$browserBinaryPath" ]; then
                    # Direct execution is more reliable for passing --app and other Chromium flags
                    # Use nohup and & to allow the launcher script to exit while the browser stays open
                    nohup "$browserBinaryPath" "${'$'}{BROWSER_ARGS[@]}" > /dev/null 2>&1 &
                else
                    # Fallback if binary path is missing
                    open -a "$browserApp" --args "${'$'}{BROWSER_ARGS[@]}"
                fi
            """.trimIndent()
            launcherScript.writeText(scriptContent)
            launcherScript.setExecutable(true)

            var iconName = "app.icns"
            if (!app.iconPath.isNullOrBlank()) {
                generateMacIcon(app.iconPath, File(resourcesDir, iconName))
            }

            val infoPlist = File(contentsDir, "Info.plist")
            val plistContent = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
                <plist version="1.0">
                <dict>
                    <key>CFBundleDevelopmentRegion</key>
                    <string>en</string>
                    <key>CFBundleExecutable</key>
                    <string>${app.name}</string>
                    <key>CFBundleIconFile</key>
                    <string>$iconName</string>
                    <key>CFBundleIdentifier</key>
                    <string>$appId</string>
                    <key>CFBundleInfoDictionaryVersion</key>
                    <string>6.0</string>
                    <key>CFBundleName</key>
                    <string>${app.name}</string>
                    <key>CFBundlePackageType</key>
                    <string>APPL</string>
                    <key>CFBundleSignature</key>
                    <string>????</string>
                    <key>CFBundleShortVersionString</key>
                    <string>1.0</string>
                    <key>CFBundleVersion</key>
                    <string>1.0</string>
                    <key>LSMinimumSystemVersion</key>
                    <string>12.0</string>
                    <key>NSHighResolutionCapable</key>
                    <true/>
                    <key>NSAppleScriptEnabled</key>
                    <true/>
                    <key>LSEnvironment</key>
                    <dict>
                        <key>MallocNanoZone</key>
                        <string>0</string>
                    </dict>
                    <key>LSArchitecturePriority</key>
                    <array>
                        <string>arm64</string>
                        <string>x86_64</string>
                    </array>
                    <key>CFBundleSupportedPlatforms</key>
                    <array>
                        <string>MacOSX</string>
                    </array>
                </dict>
                </plist>
            """.trimIndent()
            infoPlist.writeText(plistContent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun generateMacIcon(sourcePath: String, targetFile: File) {
        try {
            val bytes = if (sourcePath.startsWith("http")) {
                java.net.URL(sourcePath).readBytes()
            } else {
                val source = File(sourcePath)
                if (source.exists()) source.readBytes() else null
            } ?: return

            val tempPng = File.createTempFile("app_icon", ".png")
            tempPng.writeBytes(bytes)

            val iconsetDir = File(tempPng.parentFile, "icon.iconset")
            iconsetDir.mkdirs()

            val sizes = listOf(16, 32, 128, 256, 512)
            for (size in sizes) {
                Runtime.getRuntime().exec(arrayOf("sips", "-z", "$size", "$size", tempPng.absolutePath, "--out", "${iconsetDir.absolutePath}/icon_${size}x${size}.png")).waitFor()
                val doubleSize = size * 2
                if (doubleSize <= 1024) {
                    Runtime.getRuntime().exec(arrayOf("sips", "-z", "$doubleSize", "$doubleSize", tempPng.absolutePath, "--out", "${iconsetDir.absolutePath}/icon_${size}x${size}@2x.png")).waitFor()
                }
            }
            Runtime.getRuntime().exec(arrayOf("iconutil", "-c", "icns", iconsetDir.absolutePath, "-o", targetFile.absolutePath)).waitFor()
            tempPng.delete()
            iconsetDir.deleteRecursively()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun ensureChromiumProfileInitialized(profileDir: File) {
        profileDir.mkdirs()
        val firstRunMarker = File(profileDir, "First Run")
        if (!firstRunMarker.exists()) {
            try {
                firstRunMarker.createNewFile()
            } catch (_: Exception) {}
        }
    }

    private fun shellSingleQuote(value: String): String {
        return "'" + value.replace("'", "'\"'\"'") + "'"
    }

    private fun getDefaultWindowsBrowserPath(): String? {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("reg", "query", "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\Shell\\Associations\\UrlAssociations\\https\\UserChoice", "/v", "ProgId"))
            val output = process.inputStream.bufferedReader().readText()
            val progId = output.split("REG_SZ").last().trim()
            
            val shellCommandProcess = Runtime.getRuntime().exec(arrayOf("reg", "query", "HKEY_CLASSES_ROOT\\$progId\\shell\\open\\command", "/ve"))
            val shellOutput = shellCommandProcess.inputStream.bufferedReader().readText()
            val fullCommand = shellOutput.split("REG_SZ").last().trim()
            
            // Extract path from "path\to\browser.exe" %1
            if (fullCommand.startsWith("\"")) {
                fullCommand.substring(1).split("\"").first()
            } else {
                fullCommand.split(" ").first()
            }
        } catch (e: Exception) {
            null
        }
    }

    override fun deleteShortcut(app: WebApp) {
        val appId = "appdock_${app.name.replace(" ", "_").lowercase()}"
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        
        if (osName.contains("windows")) {
            val userHome = System.getProperty("user.home") ?: return
            val appData = System.getenv("LOCALAPPDATA") ?: (userHome + "\\AppData\\Local")
            
            // Delete Desktop Shortcut
            val desktopDir = File(userHome, "Desktop")
            val desktopShortcut = File(desktopDir, "${app.name}.lnk")
            if (desktopShortcut.exists()) desktopShortcut.delete()
            
            // Delete Start Menu Shortcut
            val startMenuShortcutRoot = File(System.getenv("APPDATA"), "Microsoft\\Windows\\Start Menu\\Programs\\${app.name}.lnk")
            if (startMenuShortcutRoot.exists()) startMenuShortcutRoot.delete()
            
            val startMenuDirOld = File(System.getenv("APPDATA"), "Microsoft\\Windows\\Start Menu\\Programs\\App Dock")
            val startMenuShortcutOld = File(startMenuDirOld, "${app.name}.lnk")
            if (startMenuShortcutOld.exists()) startMenuShortcutOld.delete()
            
            // Clean up empty old folder
            if (startMenuDirOld.exists() && startMenuDirOld.list()?.isEmpty() == true) {
                startMenuDirOld.delete()
            }

            // Delete Icon
            val iconsDir = File(appData, "AppDock\\Icons")
            val iconFile = File(iconsDir, "$appId.ico")
            if (iconFile.exists()) iconFile.delete()

            // Delete Profile
            val profileDir = File(appData, "AppDock\\Profiles\\$appId")
            if (profileDir.exists()) profileDir.deleteRecursively()
            
            return
        }

        if (osName.contains("mac")) {
            val userHome = System.getProperty("user.home") ?: return
            
            // Delete actual App bundle
            val applicationsDir = File(userHome, "Applications")
            val appBundleDir = File(applicationsDir, "${app.name}.app")
            if (appBundleDir.exists()) {
                appBundleDir.deleteRecursively()
            }
            
            // Cleanup profile
            val appInstanceId = "com.foss.appdock.app_${app.name.replace(" ", "_").lowercase()}"
            val profileDir = File(userHome, "Library/Application Support/AppDock/Profiles/$appInstanceId")
            if (profileDir.exists()) {
                profileDir.deleteRecursively()
            }
            return
        }

        if (!osName.contains("nux") && !osName.contains("nix")) {
            return
        }

        val userHome = System.getProperty("user.home") ?: return
        val applicationsDir = File(userHome, ".local/share/applications")
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
