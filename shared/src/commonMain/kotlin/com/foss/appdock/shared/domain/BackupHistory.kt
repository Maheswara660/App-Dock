package com.foss.appdock.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class BackupHistory(
        val id: Long = 0,
        val filename: String,
        val timestamp: Long,
        val sizeBytes: Long,
        val type: String // 'manual', 'auto'
)
