package com.foss.appdock.shared.platform

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

actual val platformIsDesktop: Boolean = false

actual val platformIsAndroid: Boolean = true

// On Android, we don't query the installed browsers in the same way as desktop,
// as the system's intent chooser handles defaults effectively.
actual fun queryInstalledBrowsers(): List<String> = emptyList()

@Composable
actual fun ImagePicker(
        onImagePicked: (String?) -> Unit,
        content: @Composable (launchPicker: () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri
                ->
                // Return the URI string. Kamel can load "content://" URIs using
                // `asyncPainterResource(uri.toString())`
                onImagePicked(uri?.toString())
            }

    content(launchPicker = { launcher.launch("image/*") })
}
