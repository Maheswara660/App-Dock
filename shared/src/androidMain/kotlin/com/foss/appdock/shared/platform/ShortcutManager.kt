package com.foss.appdock.shared.platform

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.foss.appdock.shared.domain.WebApp
import java.io.File
import java.io.InputStream
import java.net.URL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AndroidShortcutManager(private val context: Context) : ShortcutManager {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun createShortcut(app: WebApp) {
        scope.launch {
            try {
                val shortcutInfo = buildShortcutInfo(app)
                withContext(Dispatchers.Main) {
                    ShortcutManagerCompat.pushDynamicShortcut(context, shortcutInfo)
                    if (ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
                        ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("AppDock", "Failed to create shortcut for ${app.name}", e)
            }
        }
    }

    private fun buildShortcutInfo(app: WebApp): ShortcutInfoCompat {
        val deepLink = Uri.parse("appdock://launch?id=${app.id}")
        val launchIntent =
                Intent(context, ShortcutHandlerActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = deepLink
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

        return ShortcutInfoCompat.Builder(context, "appdock_shortcut_${app.id}")
                .setShortLabel(app.name)
                .setLongLabel(app.name)
                .setIcon(resolveShortcutIcon(app.iconPath))
                .setIntent(launchIntent)
                .build()
    }

    private fun resolveShortcutIcon(iconPath: String?): IconCompat {
        if (iconPath.isNullOrBlank()) {
            return IconCompat.createWithResource(context, context.applicationInfo.icon)
        }

        return try {
            val stream: InputStream? =
                    when {
                        iconPath.startsWith("http://") || iconPath.startsWith("https://") ->
                                URL(iconPath).openStream()
                        iconPath.startsWith("content://") || iconPath.startsWith("file://") ->
                                context.contentResolver.openInputStream(Uri.parse(iconPath))
                        else -> {
                            val localFile = File(iconPath)
                            if (localFile.exists()) localFile.inputStream() else null
                        }
                    }

            val bitmap = stream.use { it?.let(BitmapFactory::decodeStream) }
            if (bitmap != null) IconCompat.createWithBitmap(bitmap)
            else IconCompat.createWithResource(context, context.applicationInfo.icon)
        } catch (_: Exception) {
            IconCompat.createWithResource(context, context.applicationInfo.icon)
        }
    }

    override fun deleteShortcut(app: WebApp) {
        val shortcutIds = listOf("appdock_shortcut_${app.id}")
        ShortcutManagerCompat.removeDynamicShortcuts(context, shortcutIds)
        ShortcutManagerCompat.removeLongLivedShortcuts(context, shortcutIds)
        ShortcutManagerCompat.disableShortcuts(context, shortcutIds, "App deleted from App Dock")
    }
}
