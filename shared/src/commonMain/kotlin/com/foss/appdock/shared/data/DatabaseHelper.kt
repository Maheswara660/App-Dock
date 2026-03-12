package com.foss.appdock.shared.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.foss.appdock.shared.database.AppDockDatabase
import com.foss.appdock.shared.domain.BackupHistory
import com.foss.appdock.shared.domain.Category
import com.foss.appdock.shared.domain.WebApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DatabaseHelper(driverFactory: DriverFactory) {
    private val database = AppDockDatabase(driverFactory.createDriver())
    private val queries = database.appDockDatabaseQueries

    init {
        // Initialize default categories if none exist
        val existingCategories = queries.getAllCategories().executeAsList()
        if (existingCategories.isEmpty()) {
            val defaults = listOf("Social", "Productivity", "Entertainment", "News", "Utilities")
            defaults.forEachIndexed { index, name ->
                queries.insertCategory(name = name, sortOrder = index.toLong())
            }
        }
    }

    // ─── Web Apps ────────────────────────────────────────────────────────────

    fun getAllWebApps(): Flow<List<WebApp>> {
        return queries.getAllWebApps().asFlow().mapToList(Dispatchers.Default).map { entities ->
            entities.map { entity ->
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
    }

    fun insertWebApp(app: WebApp) {
        queries.insertWebApp(
                name = app.name,
                url = app.url,
                iconPath = app.iconPath,
                category = app.category,
                browserChoice = app.browserChoice,
                isStandalone = if (app.isStandalone) 1L else 0L,
                notificationsEnabled = if (app.notificationsEnabled) 1L else 0L,
                isolatedProfile = if (app.isolatedProfile) 1L else 0L,
                incognitoMode = if (app.incognitoMode) 1L else 0L,
                launchCount = app.launchCount.toLong(),
                createdAt = app.createdAt
        )
    }

    fun updateWebApp(app: WebApp) {
        queries.updateWebApp(
                name = app.name,
                url = app.url,
                iconPath = app.iconPath,
                category = app.category,
                browserChoice = app.browserChoice,
                isStandalone = if (app.isStandalone) 1L else 0L,
                notificationsEnabled = if (app.notificationsEnabled) 1L else 0L,
                isolatedProfile = if (app.isolatedProfile) 1L else 0L,
                incognitoMode = if (app.incognitoMode) 1L else 0L,
                launchCount = app.launchCount.toLong(),
                id = app.id
        )
    }

    fun deleteWebApp(id: Long) {
        queries.deleteWebAppById(id)
    }

    // ─── Categories ──────────────────────────────────────────────────────────

    fun getAllCategories(): Flow<List<Category>> {
        return queries.getAllCategories().asFlow().mapToList(Dispatchers.Default).map { entities ->
            entities.map { entity ->
                Category(id = entity.id, name = entity.name, sortOrder = entity.sortOrder.toInt())
            }
        }
    }

    fun insertCategory(category: Category) {
        queries.insertCategory(name = category.name, sortOrder = category.sortOrder.toLong())
    }

    fun addCategory(name: String) {
        val nextSortOrder =
                (queries.getAllCategories().executeAsList().maxOfOrNull { it.sortOrder }
                        ?: -1L) + 1L
        queries.insertCategory(name = name, sortOrder = nextSortOrder)
    }

    fun updateCategory(category: Category) {
        queries.updateCategory(
                name = category.name,
                sortOrder = category.sortOrder.toLong(),
                id = category.id
        )
    }

    fun deleteCategory(id: Long) {
        queries.deleteCategoryById(id)
    }

    // ─── Backup History ──────────────────────────────────────────────────────

    fun getAllBackupHistory(): Flow<List<BackupHistory>> {
        return queries.getAllBackupHistory().asFlow().mapToList(Dispatchers.Default).map { entities
            ->
            entities.map { entity ->
                BackupHistory(
                        id = entity.id,
                        filename = entity.filename,
                        timestamp = entity.timestamp,
                        sizeBytes = entity.sizeBytes,
                        type = entity.type
                )
            }
        }
    }

    fun insertBackupHistory(history: BackupHistory) {
        queries.insertBackupHistory(
                filename = history.filename,
                timestamp = history.timestamp,
                sizeBytes = history.sizeBytes,
                type = history.type
        )
    }

    fun deleteBackupHistory(id: Long) {
        queries.deleteBackupHistoryById(id)
    }
}
