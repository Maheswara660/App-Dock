package com.foss.appdock.shared.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.foss.appdock.shared.database.AppDockDatabase
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val dbFile = File(System.getProperty("user.home"), ".appdock/appdock.db")
        dbFile.parentFile?.mkdirs()

        val driver = JdbcSqliteDriver("jdbc:sqlite:${dbFile.absolutePath}")

        if (!dbFile.exists() || dbFile.length() == 0L) {
            AppDockDatabase.Schema.create(driver)
        } else {
            // Apply missing schema updates for existing databases
            try {
                // Table: BackupHistoryEntity
                driver.execute(
                        null,
                        "CREATE TABLE IF NOT EXISTS BackupHistoryEntity (id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, filename TEXT NOT NULL, timestamp INTEGER NOT NULL, sizeBytes INTEGER NOT NULL, type TEXT NOT NULL);",
                        0
                )

                // Columns for WebAppEntity
                val columnsToAdd =
                        listOf(
                                "isolatedProfile INTEGER NOT NULL DEFAULT 1",
                                "incognitoMode INTEGER NOT NULL DEFAULT 0",
                                "launchCount INTEGER NOT NULL DEFAULT 0"
                        )

                columnsToAdd.forEach { columnDef ->
                    try {
                        driver.execute(null, "ALTER TABLE WebAppEntity ADD COLUMN $columnDef;", 0)
                    } catch (e: Exception) {
                        // Column might already exist
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return driver
    }
}
