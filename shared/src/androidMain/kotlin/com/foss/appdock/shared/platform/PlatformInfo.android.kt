package com.foss.appdock.shared.platform

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp
import okio.Path
import okio.Path.Companion.toPath

actual fun Modifier.platformBlur(): Modifier = this.blur(32.dp)

actual fun platformProjectRoot(): Path {
    val context = PlatformContext.context ?: throw IllegalStateException("PlatformContext not initialized")
    return context.filesDir.absolutePath.toPath().resolve("projects")
}
