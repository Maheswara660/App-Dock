package com.foss.appdock.shared.domain

import kotlinx.serialization.Serializable

@Serializable
data class WebApp(
        val id: Long = 0,
        val name: String,
        val url: String,
        val iconPath: String? = null,
        val category: String? = null,
        val browserChoice: String? = null,
        val isStandalone: Boolean = true,
        val notificationsEnabled: Boolean = false,
        val isolatedProfile: Boolean = true,
        val incognitoMode: Boolean = false,
        val createdAt: Long = 0,
        val launchCount: Int = 0
)
