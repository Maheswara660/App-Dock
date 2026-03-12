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
    override fun createShortcut(app: WebApp) {
        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
            return
        }

        GlobalScope.launch(Dispatchers.IO) {
            val intent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(app.url)).apply {
                        // Apply advanced flags if supported by known browsers
                        val browserLower = app.browserChoice?.lowercase() ?: ""
                        if (app.incognitoMode) {
                            when {
                                browserLower.contains("chrome") -> {
                                    putExtra(
                                            "com.google.android.apps.chrome.EXTRA_IS_INCOGNITO",
                                            true
                                    )
                                }
                                browserLower.contains("firefox") -> {
                                    putExtra("private_browsing_mode", true)
                                }
                                else -> {
                                    putExtra("android.support.customtabs.extra.INCOGNITO", true)
                                }
                            }
                        }

                        if (!app.browserChoice.isNullOrEmpty() &&
                                        app.browserChoice != "System Default"
                        ) {
                            val packageName =
                                    when {
                                        browserLower.contains("chrome") -> "com.android.chrome"
                                        browserLower.contains("firefox") -> "org.mozilla.firefox"
                                        browserLower.contains("edge") -> "com.microsoft.emmx"
                                        browserLower.contains("brave") -> "com.brave.browser"
                                        else -> null
                                    }
                            if (packageName != null) {
                                setPackage(packageName)
                            }
                        }
                    }

            val iconCompat =
                    if (!app.iconPath.isNullOrBlank()) {
                        try {
                            val url = URL(app.iconPath)
                            val bitmap = BitmapFactory.decodeStream(url.openStream())
                            if (bitmap != null) {
                                IconCompat.createWithBitmap(bitmap)
                            } else {
                                IconCompat.createWithResource(
                                        context,
                                        android.R.drawable.ic_menu_compass
                                )
                            }
                        } catch (e: Exception) {
                            IconCompat.createWithResource(
                                    context,
                                    android.R.drawable.ic_menu_compass
                            )
                        }
                    } else {
                        IconCompat.createWithResource(context, android.R.drawable.ic_menu_compass)
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
