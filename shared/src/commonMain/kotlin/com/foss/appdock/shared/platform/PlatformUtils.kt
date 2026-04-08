package com.foss.appdock.shared.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.foss.appdock.shared.domain.WebApp

/** Indicates if the current platform is a Desktop environment (e.g., Windows, macOS, Linux). */
expect val platformIsDesktop: Boolean

/** Indicates if the current platform is Android. */
expect val platformIsAndroid: Boolean

/**
 * Returns a list of installed web browsers. On Desktop, this dynamically queries established
 * browser locations. On Android, this returns an empty list as we defer to the global settings
 * intent picker.
 */
@Composable
expect fun queryInstalledBrowsers(context: Any? = null): List<String>

/**
 * Provides a platform-specific file picker for selecting images.
 *
 * @param onImagePicked Callback invoked with the absolute path or URI of the selected image, or
 * null if cancelled.
 * @param content The UI content that triggers the picker. It provides a `launchPicker` lambda to be
 * called on click.
 */
@Composable
expect fun ImagePicker(
        onImagePicked: (String?) -> Unit,
        content: @Composable (launchPicker: () -> Unit) -> Unit
)

/** Platform-agnostic vertical scrollbar. Moves with the given [scrollState]. */
@Composable
expect fun VerticalScrollbar(
    modifier: Modifier,
    scrollState: androidx.compose.foundation.ScrollState
)

@Composable
expect fun VerticalScrollbar(
    modifier: Modifier,
    listState: androidx.compose.foundation.lazy.LazyListState
)

@Composable
expect fun VerticalScrollbar(
    modifier: Modifier,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState
)

/** Exports the given list of web apps to a JSON file. */
expect suspend fun exportData(webApps: List<WebApp>): Boolean

/** Imports web apps from a JSON file. */
expect suspend fun importData(): List<WebApp>?

/** Creates a system-level shortcut for the given web app. */
expect fun createShortcut(webApp: WebApp, context: Any?)
