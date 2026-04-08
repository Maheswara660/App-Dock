package com.foss.appdock.shared.platform

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.foss.appdock.shared.domain.WebApp
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AndroidShortcutManager(private val context: Context) : ShortcutManager {
    @OptIn(kotlinx.coroutines.DelicateCoroutinesApi::class)
    override fun createShortcut(app: WebApp) {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse("appdock://launch?id=${app.id}")).apply {
                        setClassName(context.packageName, "com.foss.appdock.MainActivity")
                    }

            val iconCompat =
                    if (!app.iconPath.isNullOrBlank()) {
                        try {
                            val url = URL(app.iconPath)
                            val bitmap = BitmapFactory.decodeStream(url.openStream())
                            if (bitmap != null) {
                                IconCompat.createWithBitmap(bitmap)
                            } else {
                                IconCompat.createWithResource(context, context.applicationInfo.icon)
                            }
                        } catch (e: Exception) {
                            IconCompat.createWithResource(context, context.applicationInfo.icon)
                        }
                    } else {
                        IconCompat.createWithResource(context, context.applicationInfo.icon)
                    }

            val shortcutInfo =
                    ShortcutInfoCompat.Builder(context, "appdock_shortcut_${app.id}")
                            .setShortLabel(app.name)
                            .setLongLabel(app.name)
                            .setIcon(iconCompat)
                            .setIntent(intent)
                            .build()

            withContext(Dispatchers.Main) {
                ShortcutManagerCompat.requestPinShortcut(context, shortcutInfo, null)
            }
        }
    }

    override fun deleteShortcut(app: WebApp) {
        val shortcutIds = listOf("appdock_shortcut_${app.id}")
        ShortcutManagerCompat.disableShortcuts(context, shortcutIds, "App deleted from App Dock")
    }
}
