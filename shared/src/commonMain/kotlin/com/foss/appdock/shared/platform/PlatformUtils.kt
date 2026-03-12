package com.foss.appdock.shared.platform

import androidx.compose.runtime.Composable

/** Indicates if the current platform is a Desktop environment (e.g., Windows, macOS, Linux). */
expect val platformIsDesktop: Boolean

/** Indicates if the current platform is Android. */
expect val platformIsAndroid: Boolean

/**
 * Returns a list of installed web browsers. On Desktop, this dynamically queries established
 * browser locations. On Android, this returns an empty list as we defer to the global settings
 * intent picker.
 */
expect fun queryInstalledBrowsers(): List<String>

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
