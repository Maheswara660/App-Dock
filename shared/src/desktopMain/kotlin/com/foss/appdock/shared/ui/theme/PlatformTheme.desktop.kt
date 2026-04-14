package com.foss.appdock.shared.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.foss.appdock.shared.platform.LocalAwtWindow
import javax.swing.JFrame

@Composable
actual fun PlatformStatusBarTheme(darkTheme: Boolean) {
    val window = LocalAwtWindow.current
    SideEffect {
        val os = System.getProperty("os.name").lowercase()
        val frame = window as? JFrame ?: return@SideEffect

        if (os.contains("mac")) {
            // macOS: force per-window NSAppearance so native title bar follows app theme
            val appearance = if (darkTheme) "NSAppearanceNameDarkAqua" else "NSAppearanceNameAqua"
            frame.rootPane.putClientProperty("apple.awt.windowAppearance", appearance)
            // Keep legacy key too for broader JDK compatibility.
            frame.rootPane.putClientProperty("apple.awt.application.appearance", appearance)
            frame.invalidate()
            frame.repaint()
        } else if (os.contains("windows")) {
            // Windows 11 / 10 20H1+: Toggle immersive dark mode on the title bar via DWM
            // This is done reflectively to avoid hard-dependency on JNA/WinAPI
            try {
                val hwnd = getWindowHandle(frame) ?: return@SideEffect
                val darkValue = if (darkTheme) 1 else 0
                // Newer Windows builds use 20, older ones use 19.
                setDwmWindowAttribute(hwnd, 20, darkValue)
                setDwmWindowAttribute(hwnd, 19, darkValue)
            } catch (ignored: Exception) { /* DWM not available */ }
        }
        // Linux: GTK windows follow the system theme; Compose itself renders the content area,
        // so no programmatic title-bar change is needed here.
    }
}

// ---------------------------------------------------------------------------
// Windows DWM helper via JNA (silently skipped if JNA is not on classpath)
// ---------------------------------------------------------------------------

private fun getWindowHandle(frame: JFrame): Long? {
    return try {
        // sun.awt.windows.WComponentPeer exposes getHWnd()
        val peer = frame.javaClass.getMethod("getPeer").invoke(frame) ?: return null
        val hwnd = peer.javaClass.getMethod("getHWnd").invoke(peer)
        hwnd as? Long
    } catch (e: Exception) { null }
}

private fun setDwmWindowAttribute(hwnd: Long, attribute: Int, value: Int) {
    // Attempt to call DwmSetWindowAttribute via JNA Pointer if available
    // Falls through silently on any error
    try {
        val lib = Class.forName("com.sun.jna.NativeLibrary")
        val getInstance = lib.getMethod("getInstance", String::class.java)
        val dwm = getInstance.invoke(null, "dwmapi")
        val getFunction = lib.getMethod("getFunction", String::class.java)
        val fn = getFunction.invoke(dwm, "DwmSetWindowAttribute")
        // invoke(hwnd, attrId, intValuePtr, sizeOfInt)
        val invoke = fn.javaClass.getMethod("invoke",
            Array<Any>::class.java)
        invoke.invoke(fn, arrayOf(hwnd, attribute, intArrayOf(value), 4))
    } catch (ignored: Exception) { /* JNA not present or call failed */ }
}
