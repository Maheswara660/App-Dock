package com.foss.appdock.shared.platform

import com.foss.appdock.shared.domain.WebApp

interface ShortcutManager {
    fun createShortcut(app: WebApp)
    fun deleteShortcut(app: WebApp)
}
