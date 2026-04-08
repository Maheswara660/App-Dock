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

        if (!osName.contains("nux") && !osName.contains("nix")) {
            // Early return for macOS or other unsupported platforms
            return
        }

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
                    
                    // Improved Wayland support for Chromium browsers
                    append(" --ozone-platform-hint=auto")
                    
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
            
            // 1. Determine paths
            val desktopDir = File(userHome, "Desktop")
            val startMenuDir = File(System.getenv("APPDATA"), "Microsoft\\Windows\\Start Menu\\Programs")
            // No need to mkdirs if we use the root Programs folder, but safe to keep for consistency
            if (!startMenuDir.exists()) startMenuDir.mkdirs()

            val shortcutName = "${app.name}.lnk"
            val desktopShortcut = File(desktopDir, shortcutName)
            val startMenuShortcut = File(startMenuDir, shortcutName)

            // 2. Determine executable and arguments
            // We want to point to the App Dock executable if possible, to handle proxy launch,
            // or point to the browser directly if we want standalone behavior.
            // On Windows, pointing to the App Dock exe with a deep link is best for consistency.
            
            val appData = System.getenv("LOCALAPPDATA") ?: (userHome + "\\AppData\\Local")
            val profileDir = File(appData, "AppDock\\Profiles\\$appId")
            if (app.isolatedProfile) profileDir.mkdirs()

            // Find our own executable path
            // When running as a packaged app, System.getProperty("compose.application.resources.dir") 
            // is often near the exe. A better way for jpackage is to check the process path.
            // For now, let's build the browser command directly as it's more reliable across environments.
            
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

            // 3. Download icon locally
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
                        // Reserved
                        icoBytes[0] = 0
                        icoBytes[1] = 0
                        // Type (1 = ICO)
                        icoBytes[2] = 1
                        icoBytes[3] = 0
                        // Number of images
                        icoBytes[4] = 1
                        icoBytes[5] = 0
                        
                        // width and height (0 means 256)
                        icoBytes[6] = 0
                        icoBytes[7] = 0
                        // colors
                        icoBytes[8] = 0
                        // reserved
                        icoBytes[9] = 0
                        // color planes
                        icoBytes[10] = 1
                        icoBytes[11] = 0
                        // Bits per pixel (32)
                        icoBytes[12] = 32
                        icoBytes[13] = 0
                        
                        // Size of image data
                        val size = pngBytes.size
                        icoBytes[14] = (size and 0xFF).toByte()
                        icoBytes[15] = ((size shr 8) and 0xFF).toByte()
                        icoBytes[16] = ((size shr 16) and 0xFF).toByte()
                        icoBytes[17] = ((size shr 24) and 0xFF).toByte()
                        
                        // Offset of image data (22)
                        icoBytes[18] = 22
                        icoBytes[19] = 0
                        icoBytes[20] = 0
                        icoBytes[21] = 0
                        
                        System.arraycopy(pngBytes, 0, icoBytes, 22, pngBytes.size)
                        
                        iconFile.writeBytes(icoBytes)
                        localIconPath = iconFile.absolutePath
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 4. Create shortcuts using PowerShell
            val iconPath = localIconPath 
            
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

                Create-Shortcut -path '${desktopShortcut.absolutePath.replace("'", "''")}' -target '${targetPath.replace("'", "''")}' -shortcutArgs '${arguments.replace("'", "''")}' -icon '${iconPath.replace("'", "''")}' -aumid '$appId'
                Create-Shortcut -path '${startMenuShortcut.absolutePath.replace("'", "''")}' -target '${targetPath.replace("'", "''")}' -shortcutArgs '${arguments.replace("'", "''")}' -icon '${iconPath.replace("'", "''")}' -aumid '$appId'
            """.trimIndent()

            val tempPs1 = File.createTempFile("create_shortcut", ".ps1")
            tempPs1.writeText(psScript)
            
            Runtime.getRuntime().exec(arrayOf("powershell.exe", "-ExecutionPolicy", "Bypass", "-File", tempPs1.absolutePath)).waitFor()
            tempPs1.delete()

        } catch (e: Exception) {
            e.printStackTrace()
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
            val profileDir = File(userHome, "Library/Application Support/AppDock/Profiles/$appId")
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
