package com.foss.appdock.shared.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.foss.appdock.shared.domain.WebApp

actual val platformIsDesktop: Boolean = false

actual val platformIsAndroid: Boolean = true

@Composable
actual fun queryInstalledBrowsers(context: Any?): List<String> {
    val androidContext = context as? Context ?: LocalContext.current
    return androidx.compose.runtime.remember(androidContext) {
        val browsers = mutableListOf("System Default", "Chrome", "Firefox", "Edge", "Brave", "Opera", "Vivaldi", "Kiwi Browser", "DuckDuckGo", "Samsung Internet")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
        val packageManager = androidContext.packageManager
        val resolveInfos = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        
        for (info in resolveInfos) {
            val name = info.loadLabel(packageManager).toString()
            if (!browsers.contains(name)) {
                browsers.add(name)
            }
        }
        browsers
    }
}

@Composable
actual fun ImagePicker(
        onImagePicked: (String?) -> Unit,
        content: @Composable (launchPicker: () -> Unit) -> Unit
) {
    val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri
                ->
                onImagePicked(uri?.toString())
            }

    content { launcher.launch("image/*") }
}

@Composable
actual fun VerticalScrollbar(
    modifier: androidx.compose.ui.Modifier,
    scrollState: androidx.compose.foundation.ScrollState
) {}

@Composable
actual fun VerticalScrollbar(
    modifier: androidx.compose.ui.Modifier,
    listState: androidx.compose.foundation.lazy.LazyListState
) {}

@Composable
actual fun VerticalScrollbar(
    modifier: androidx.compose.ui.Modifier,
    gridState: androidx.compose.foundation.lazy.grid.LazyGridState
) {}

actual suspend fun exportData(webApps: List<WebApp>): Boolean = false

actual suspend fun importData(): List<WebApp>? = null

actual fun createShortcut(webApp: WebApp, context: Any?) {
    val androidContext = context as? Context ?: return
    
    val intent = Intent(androidContext, androidContext.javaClass).apply {
        action = Intent.ACTION_VIEW
        data = Uri.parse(webApp.url)
        putExtra("webAppId", webApp.id)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val shortcut = ShortcutInfoCompat.Builder(androidContext, "webapp_${webApp.id}")
        .setShortLabel(webApp.name)
        .setLongLabel(webApp.name)
        .setIcon(IconCompat.createWithResource(androidContext, androidContext.resources.getIdentifier("ic_launcher", "mipmap", androidContext.packageName)))
        .setIntent(intent)
        .build()

    ShortcutManagerCompat.requestPinShortcut(androidContext, shortcut, null)
}
