package com.foss.appdock.shared.platform

import androidx.compose.runtime.compositionLocalOf
import java.awt.Window

/**
 * A CompositionLocal that holds a reference to the AWT [Window] for the current Compose hierarchy.
 * This is used so platform-specific theme code (e.g. macOS NSAppearance) can access the JFrame.
 */
val LocalAwtWindow = compositionLocalOf<Window?> { null }
