package com.foss.appdock.shared.platform

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.dp

actual fun Modifier.platformBlur(): Modifier = this.blur(32.dp)
