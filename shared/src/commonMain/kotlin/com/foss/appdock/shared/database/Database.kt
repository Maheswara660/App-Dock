package com.foss.appdock.shared.database

import com.foss.appdock.shared.domain.WebApp

class Database(databaseDriverFactory: DatabaseDriverFactory) {
    private val database = AppDockDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDockDatabaseQueries

    fun getAllWebApps(): List<WebApp> {
        return dbQuery.getAllWebApps().executeAsList().map { entity ->
            WebApp(
                    id = entity.id,
                    name = entity.name,
                    url = entity.url,
                    iconPath = entity.iconPath,
                    category = entity.category,
                    browserChoice = entity.browserChoice,
                    isStandalone = entity.isStandalone == 1L,
                    notificationsEnabled = entity.notificationsEnabled == 1L,
                    isolatedProfile = entity.isolatedProfile == 1L,
                    incognitoMode = entity.incognitoMode == 1L,
                    launchCount = entity.launchCount.toInt(),
                    createdAt = entity.createdAt
            )
        }
    }

    fun getWebAppById(id: Long): WebApp? {
        return dbQuery.getWebAppById(id).executeAsOneOrNull()?.let { entity ->
            WebApp(
                    id = entity.id,
                    name = entity.name,
                    url = entity.url,
                    iconPath = entity.iconPath,
                    category = entity.category,
                    browserChoice = entity.browserChoice,
                    isStandalone = entity.isStandalone == 1L,
                    notificationsEnabled = entity.notificationsEnabled == 1L,
                    isolatedProfile = entity.isolatedProfile == 1L,
                    incognitoMode = entity.incognitoMode == 1L,
                    launchCount = entity.launchCount.toInt(),
                    createdAt = entity.createdAt
            )
        }
    }

    fun insertWebApp(
            name: String,
            url: String,
            iconPath: String?,
            category: String?,
            browserChoice: String?,
            isStandalone: Boolean,
            notificationsEnabled: Boolean = false,
            isolatedProfile: Boolean = true,
            incognitoMode: Boolean = false,
            launchCount: Int = 0,
            createdAt: Long
    ) {
        dbQuery.insertWebApp(
                name,
                url,
                iconPath,
                category,
                browserChoice,
                if (isStandalone) 1L else 0L,
                if (notificationsEnabled) 1L else 0L,
                if (isolatedProfile) 1L else 0L,
                if (incognitoMode) 1L else 0L,
                launchCount.toLong(),
                createdAt
        )
    }

    fun updateWebApp(
            id: Long,
            name: String,
            url: String,
            iconPath: String?,
            category: String?,
            browserChoice: String?,
            isStandalone: Boolean,
            notificationsEnabled: Boolean = false,
            isolatedProfile: Boolean = true,
            incognitoMode: Boolean = false,
            launchCount: Int = 0
    ) {
        dbQuery.updateWebApp(
                name,
                url,
                iconPath,
                category,
                browserChoice,
                if (isStandalone) 1L else 0L,
                if (notificationsEnabled) 1L else 0L,
                if (isolatedProfile) 1L else 0L,
                if (incognitoMode) 1L else 0L,
                launchCount.toLong(),
                id
        )
    }

    fun deleteWebAppById(id: Long) {
        dbQuery.deleteWebAppById(id)
    }
}
