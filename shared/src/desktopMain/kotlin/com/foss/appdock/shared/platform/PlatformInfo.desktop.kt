package com.foss.appdock.shared.platform

import androidx.compose.ui.Modifier
import okio.Path
import okio.Path.Companion.toPath
import java.io.File

actual fun Modifier.platformBlur(): Modifier = this

actual fun platformProjectRoot(): Path {
    val home = System.getProperty("user.home")
    val root = File(home, ".appdock/projects")
    if (!root.exists()) root.mkdirs()
    return root.absolutePath.toPath()
}
