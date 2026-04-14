package com.foss.appdock.shared.platform

import androidx.compose.ui.Modifier
import okio.Path

expect fun Modifier.platformBlur(): Modifier

expect fun platformProjectRoot(): Path
