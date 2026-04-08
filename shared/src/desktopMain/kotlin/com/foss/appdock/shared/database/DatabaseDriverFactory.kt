package com.foss.appdock.shared.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import java.io.File

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), ".appdock/appdock.db")
        databasePath.parentFile.mkdirs()
        // Initialize SQLite driver
        val driver = JdbcSqliteDriver("jdbc:sqlite:${databasePath.absolutePath}")
        // Note: For production, consider using a DB migration check here.
        // If DB file exists, we probably don't need to re-create schema, but create() handles it or throws if exists
        if (!databasePath.exists() || databasePath.length() == 0L) {
            AppDockDatabase.Schema.create(driver)
        }
        return driver
    }
}
